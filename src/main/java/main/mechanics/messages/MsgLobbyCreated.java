package main.mechanics.messages;

import main.websocket.Message;

public class MsgLobbyCreated extends Message{
    private String opponentLogin;

    public MsgLobbyCreated(String opponentLogin) {
        this.opponentLogin = opponentLogin;
    }


    public String getOpponentLogin() {
        return opponentLogin;
    }

    public void setOpponentLogin(String opponentLogin) {
        this.opponentLogin = opponentLogin;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final MsgLobbyCreated other = (MsgLobbyCreated) object;

        if (opponentLogin != null) {
            return opponentLogin.equals(other.opponentLogin);
        } else {
            return other.opponentLogin == null;
        }
    }

    @Override
    public int hashCode() {
        if (opponentLogin != null) {
            return opponentLogin.hashCode();
        } else {
            return 0;
        }
    }
}
