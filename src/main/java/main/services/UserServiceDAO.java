package main.services;

import main.models.User;
import main.views.LoginForm;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static main.rowmappers.RowMappers.readLoginForm;

@Service
public class UserServiceDAO {
    private JdbcTemplate jdbcTemplate;

    public UserServiceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // сделать обертку над этим методом чтобы при ошибке произошел Rollback
    // и монжно было  возвращать коды ошибок
    @Transactional
    public void singUp(User userData) {
        final Integer userId = getNextId();

        final String createUserQuery = "INSERT INTO Users (id, email, login, password) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(createUserQuery, userId, userData.getEmail(), userData.getLogin(), userData.getPassword());

        final String createScore = "INSERT INTO Scores (user_id) VALUES (?)";
        jdbcTemplate.update(createScore, userId);

    }

    public UserService.ErrorCodes login(LoginForm loginData) {
        final LoginForm dbUser = getUserByIdOrEmail(loginData);
        if (dbUser == null) {
            return UserService.ErrorCodes.INVALID_AUTH_DATA;
        }

        if (!dbUser.getPassword().equals(loginData.getPassword())) {
            return UserService.ErrorCodes.INCORRECT_PASSWORD;
        }

        return UserService.ErrorCodes.OK;

    }

    private Integer getNextId() {
        final String sqlGetNext = "SELECT nextval(pg_get_serial_sequence('Users', 'id'))";
        return jdbcTemplate.queryForObject(sqlGetNext, Integer.class);
    }

    private LoginForm getUserByIdOrEmail(LoginForm loginData) {
        try {
            final String getUserByIdOrNickname = "SELECT * From users Where login = ? OR email = ?";
            return jdbcTemplate.queryForObject(getUserByIdOrNickname, readLoginForm, loginData.getLogin(), loginData.getEmail());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }
}
