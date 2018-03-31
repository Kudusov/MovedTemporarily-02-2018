package main.views;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class PassForm {

    private @NotNull String oldPassword;
    private @NotNull String newPassword;

    public PassForm(@JsonProperty("oldPassword") String oldPassword,
                    @JsonProperty("newPassword") String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public boolean isValid() {
        return (this.oldPassword != null && this.newPassword != null);
    }

    @SuppressWarnings("unused")
    public String getOldPassword() {
        return oldPassword;
    }

    @SuppressWarnings("unused")
    public String getNewPassword() {
        return newPassword;
    }
}
