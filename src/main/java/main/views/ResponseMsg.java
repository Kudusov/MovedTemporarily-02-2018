package main.views;

import com.fasterxml.jackson.annotation.JsonFormat;

@SuppressWarnings("unused")
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ResponseMsg {
    NOT_LOGGED_IN(0, "You are not autorized"),
    @SuppressWarnings("EnumeratedConstantNamingConvention") OK(1, "OK"),
    CREATED(2, "User Created"),
    FORBIDDEN(3, "Invalid auth data"),
    BAD_REQUEST(4, "Not all Json fields are filled"),
    CONFLICT(5, "Login or Email are already exist"),
    INVALID_LOGIN(6, "User with such auth data is not registered"),
    INCORRECT_PASSWORD(7, "Incorrect password"),
    INTERNAL_SERVER_ERROR(8, "Server error. Sorry:(");

    private final Integer status;
    private final String msg;

    ResponseMsg(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

}