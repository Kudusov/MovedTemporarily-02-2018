package main.views;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public final class MailForm {

    private @NotNull String newEmail;

    public MailForm(@JsonProperty("newEmail") String newEmail) {
        this.newEmail = newEmail;
    }

    public boolean isValid() {
        return this.newEmail != null;
    }

    @SuppressWarnings("unused")
    public String getNewEmail() {
        return newEmail;
    }

    @SuppressWarnings("unused")
    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
