package main.rowmappers;

import main.views.LoginForm;
import org.springframework.jdbc.core.RowMapper;

@SuppressWarnings("unused")
public class RowMappers {
    public static RowMapper<LoginForm> readLoginForm = (rs, i) ->
            new LoginForm(rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("password"));
}
