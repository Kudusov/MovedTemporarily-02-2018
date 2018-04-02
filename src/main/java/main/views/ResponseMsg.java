package main.views;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@SuppressWarnings("unused")
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ResponseMsg {
    @SuppressWarnings("EnumeratedConstantNamingConvention") OK("OK"),
    CREATED("User Created"),
    FORBIDDEN("Invalid auth data"),
    BAD_REQUEST("Not all Json fields are filled"),
    NOT_LOGGED_IN("You are not autorized"),
    CONFLICT("Login or Email are already exist"),
    INVALID_LOGIN("User with such auth data is not registered"),
    INCORRECT_PASSWORD("Incorrect password"),
    INTERNAL_SERVER_ERROR("Server error. Sorry:(");

    private final String msg;

    ResponseMsg(String msg) {
        this.msg = msg;
    }

    @JsonValue
    public String getMsg() {
        return msg;
    }

}