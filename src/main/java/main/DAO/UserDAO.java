package main.DAO;

import main.views.LoginForm;
import main.views.ScoreView;
import main.views.UserInfoForm;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@SuppressWarnings("unused")
public class UserDAO {

    private JdbcTemplate jdbcTemplate;

    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer getIdByLogin(String login) {
        try {
            final String query = "SELECT Id FROM Users WHERE login = ?";
            return jdbcTemplate.queryForObject(query, Integer.class, login);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public UserInfoForm getUserInfo(String login) {
        final String queryGetUserByLogin = "Select email, login From Users where login = ?";
        return jdbcTemplate.queryForObject(queryGetUserByLogin, USER_INFO_FORM_ROW_MAPPER, login);
    }

    public void changeMail(String login, String newMail) {
        final String queryChangeMail = "Update Users Set email = ? Where login = ?";
        jdbcTemplate.update(queryChangeMail, newMail, login);
    }

    public void changePass(String login, String newPass) {
        final String queryChangePass = "Update Users Set password = ? WHERE login = ?";
        jdbcTemplate.update(queryChangePass, newPass, login);
    }


    public LoginForm getUserByIdOrEmail(LoginForm loginData) {
        try {
            final String getUserByIdOrNickname = "SELECT * From users Where login = ? OR email = ? LIMIT 1";
            return jdbcTemplate.queryForObject(getUserByIdOrNickname, LOGIN_FORM_ROW_MAPPER, loginData.getLogin(), loginData.getEmail());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    private LoginForm getUserByLogin(String login) {
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
