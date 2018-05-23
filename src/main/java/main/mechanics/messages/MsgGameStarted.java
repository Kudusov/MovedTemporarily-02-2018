package main.mechanics.messages;

import main.websocket.Message;

import javax.validation.constraints.NotNull;

@SuppressWarnings("unused")
public class MsgGameStarted extends Message {
    private @NotNull Boolean first;

    public MsgGameStarted(@NotNull Boolean first) {
        this.first = first;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final MsgGameStarted other = (MsgGameStarted) object;

        if (first != null) {
            return first.equals(other.first);
        } else {
            return other.first == null;
        }

    }

    @Override
    public int hashCode() {
        if (first != null) {
            return first.hashCode();
        } else {
            return 0;
        }
    }
}
