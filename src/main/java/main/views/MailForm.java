package main.views;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public final class MailForm {

    private @NotNull String newMail;

    public MailForm(@JsonProperty("newEmail") String newMail) {
        this.newMail = newMail;
    }

    public boolean isValid() {
        return this.newMail != null;
    }

    @SuppressWarnings("unused")
    public String getNewMail() {
        return newMail;
    }

    @SuppressWarnings("unused")
    public void setNewMail(String newMail) {
        this.newMail = newMail;
    }
}
