package main.services;

import main.DAO.UserDAO;
import main.models.User;
import main.views.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceDAO {

    @SuppressWarnings("unused")
    public enum ErrorCodes {
        @SuppressWarnings("EnumeratedConstantNamingConvention") OK,
        INVALID_LOGIN,
        INCORRECT_PASSWORD,
        LOGIN_OCCUPIED,
        EMAIL_OCCUPIED,
        INVALID_AUTH_DATA,
        INVALID_REG_DATA,
        INTERNAL_SERVER_ERROR,
    }
    
    private JdbcTemplate jdbcTemplate;


//    @Autowired
    public UserServiceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    // сделать обертку над этим методом чтобы при ошибке произошел Rollback
    // и монжно было  возвращать коды ошибок
    @Transactional
    public void signUp(User userData) {
        addUserDB(userData);
    }

    public ErrorCodes login(LoginForm loginData) {
        System.out.println("\nStarted\n");
        final LoginForm dbUser = getUserByIdOrEmailDB(loginData);

        if (dbUser == null) {
            System.out.println("dbUser == null");
            return ErrorCodes.INVALID_AUTH_DATA;
        }

        if (!dbUser.getPassword().equals(loginData.getPassword())) {
            System.out.println("incorrect pass");
            return ErrorCodes.INCORRECT_PASSWORD;
        }

        loginData.setLogin(dbUser.getLogin());
        System.out.println("all Ok");
        return ErrorCodes.OK;

    }

    public ErrorCodes getUserInfo(String login, UserInfoForm data) {
        try {
            final UserInfoForm dbUser = getUserInfoDB(login);
            data.setLogin(dbUser.getLogin());
            data.setEmail(dbUser.getEmail());
            return ErrorCodes.OK;
        } catch (EmptyResultDataAccessException ex) {
            return ErrorCodes.INVALID_LOGIN;
        }
    }

    public ErrorCodes changeEmail(String login, MailForm mailData) {
        if (!mailData.isValid()) {
            return ErrorCodes.INVALID_LOGIN;
        }
        try {
            changeMailDB(login, mailData.getNewEmail());
            return ErrorCodes.OK;
        } catch (DuplicateKeyException ex) {
            return ErrorCodes.EMAIL_OCCUPIED;
        }
    }

    public ErrorCodes changePass(String login, PassForm passData) {
        if (!passData.isValid()) {
            return ErrorCodes.INCORRECT_PASSWORD;
        }

        try {
            final LoginForm user = getUserByLoginDB(login);
            if (!passData.getOldPassword().equals(user.getPassword())) {
                return ErrorCodes.INCORRECT_PASSWORD;
            }
            changePassDB(login, passData.getNewPassword());
        } catch (EmptyResultDataAccessException ex) {
            return ErrorCodes.INVALID_LOGIN;
        }

        return ErrorCodes.OK;
    }

    public ErrorCodes getAllScoreBoard(List<ScoreView> res) {
        try {
            final String query = "SELECT login, score FROM Users u JOIN Scores s on u.id = s.user_id Order by s.score";
            res.addAll(jdbcTemplate.query(query, SCORE_VIEW_ROW_MAPPER));
        } catch (DataAccessException ex) {
            return ErrorCodes.INTERNAL_SERVER_ERROR;
        }

        return ErrorCodes.OK;
    }

    public ErrorCodes getPartScoreBoard(ScoreRequest scoreReq, List<ScoreView> res) {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT login, score FROM Users u JOIN Scores s on u.id = s.user_id Order by s.score ");
        final List<Object> args = new ArrayList<>();

        if (scoreReq.getPosition() != null) {
            query.append(" OFFSET ? ");
            args.add(scoreReq.getPosition());
        }

        if (scoreReq.getCount() != null) {
            query.append(" LIMIT ? ");
            args.add(scoreReq.getCount());
        }
        try {
            res.addAll(jdbcTemplate.query(query.toString(), SCORE_VIEW_ROW_MAPPER, args.toArray()));
        } catch (DataAccessException ex) {
            return ErrorCodes.INTERNAL_SERVER_ERROR;
        }

        return ErrorCodes.OK;

    }

    @Transactional
    public void addUserDB(User userData) {
        final String createUserQuery = "INSERT INTO Users (email, login, password) VALUES(?, ?, ?)";
        jdbcTemplate.update(createUserQuery, userData.getEmail(), userData.getLogin(), userData.getPassword());
        final Integer userId = getIdByLoginDB(userData.getLogin());
        final String createScore = "INSERT INTO Scores (user_id) VALUES (?)";
        jdbcTemplate.update(createScore, userId);
    }

    public Integer getIdByLoginDB(String login) {
        try {
            final String query = "SELECT Id FROM Users WHERE login = ?";
            return jdbcTemplate.queryForObject(query, Integer.class, login);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public UserInfoForm getUserInfoDB(String login) {
        final String queryGetUserByLogin = "Select email, login From Users where login = ?";
        return jdbcTemplate.queryForObject(queryGetUserByLogin, USER_INFO_FORM_ROW_MAPPER, login);
    }

    public void changeMailDB(String login, String newMail) {
        final String queryChangeMail = "Update Users Set email = ? Where login = ?";
        jdbcTemplate.update(queryChangeMail, newMail, login);
    }

    public void changePassDB(String login, String newPass) {
        final String queryChangePass = "Update Users Set password = ? WHERE login = ?";
        jdbcTemplate.update(queryChangePass, newPass, login);
    }


    public LoginForm getUserByIdOrEmailDB(LoginForm loginData) {
        try {
            final String getUserByIdOrNickname = "SELECT * From users Where login = ? OR email = ? LIMIT 1";
            return jdbcTemplate.queryForObject(getUserByIdOrNickname, LOGIN_FORM_ROW_MAPPER, loginData.getLogin(), loginData.getEmail());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private LoginForm getUserByLoginDB(String login) {
        final String getUserByIdOrNickname = "SELECT * From users Where login = ? ";
        return jdbcTemplate.queryForObject(getUserByIdOrNickname, LOGIN_FORM_ROW_MAPPER, login);
    }
    
    private static final RowMapper<LoginForm> LOGIN_FORM_ROW_MAPPER = (row, num) ->
            new LoginForm(row.getString("login"),
                    row.getString("email"),
                    row.getString("password"));

    private static final RowMapper<UserInfoForm> USER_INFO_FORM_ROW_MAPPER = (row, num) ->
            new UserInfoForm(row.getString("login"),
                    row.getString("email"));

    private static final  RowMapper<ScoreView> SCORE_VIEW_ROW_MAPPER = (row, num) ->
            new ScoreView(row.getString("login"),
                    row.getInt("score"));
}
