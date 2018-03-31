package main.views;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public final class MailForm {

    private @NotNull String userMail;

    public MailForm(@JsonProperty("newEmail") String userMail) {
        this.userMail = userMail;
    }

    public boolean isValid() {
        return this.userMail != null;
    }

    @SuppressWarnings("unused")
    public String getUserMail() {
        return userMail;
    }

    @SuppressWarnings("unused")
    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }
}
