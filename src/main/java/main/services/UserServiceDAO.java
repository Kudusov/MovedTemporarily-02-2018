package main.services;

import main.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceDAO {
    private JdbcTemplate jdbcTemplate;

    public UserServiceDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void singUp(User userData) {
        final String createQuery = "INSERT INTO Users (email, login, password) VALUES(?, ?, ?)";
        jdbcTemplate.update(createQuery, userData.getEmail(), userData.getLogin(), userData.getPassword());
    }
}
