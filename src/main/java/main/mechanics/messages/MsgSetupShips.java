package main.mechanics.messages;

import main.mechanics.game.Ship;
import main.websocket.Message;

import java.util.List;

public class MsgSetupShips extends Message {
    private List<Ship> ships;

    public List<Ship> getShips() {
        return this.ships;
    }

    public void setShips(List<Ship> ships) {
        this.ships = ships;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final MsgSetupShips other = (MsgSetupShips) object;

        if (ships != null) {
            return ships.equals(other.ships);
        } else {
            return other.ships == null;
        }
    }

    @Override
    public int hashCode() {
        if (ships != null) {
            return ships.hashCode();
        } else {
            return 0;
        }
    }
}
