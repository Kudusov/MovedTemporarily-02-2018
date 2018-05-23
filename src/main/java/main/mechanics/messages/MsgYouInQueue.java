package main.mechanics.messages;

import main.websocket.Message;

@SuppressWarnings("unused")
public class MsgYouInQueue extends Message {
    private String userLogin;

    public MsgYouInQueue(String userLogin) {
        this.userLogin = userLogin;
    }


    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final MsgYouInQueue other = (MsgYouInQueue) object;

        if (userLogin != null) {
            return userLogin.equals(other.userLogin);
        } else {
            return other.userLogin == null;
        }
    }

    @Override
    public int hashCode() {
        if (userLogin != null) {
            return userLogin.hashCode();
        } else {
            return 0;
        }
    }
}
