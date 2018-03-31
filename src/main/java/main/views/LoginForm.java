package main.views;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

@SuppressWarnings("unused")
public final class LoginForm {

    private @NotNull String login;
    private @NotNull String password;
    private @NotNull String email;

    public LoginForm(@JsonProperty("login") String login,
                     @JsonProperty("email") String email,
                     @JsonProperty("password") String password) {
        this.login = login;
        this.email = email;
        this.password = password;
    }

    public boolean isValid() {
        return ((login != null || email != null) && password != null);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
