package main.mechanics.gamegession;

import main.mechanics.game.Field;
import main.mechanics.game.Player;

import javax.validation.constraints.NotNull;

public class GameSession {
    private final @NotNull Player player1;
    private final @NotNull Player player2;
    private Player attackingPlayer;
    private GameSessionState gameSessionState;

    public GameSession(@NotNull Player player1, @NotNull Player player2) {
        this.attackingPlayer = null;
        this.player1 = player1;
        this.player2 = player2;
        this.gameSessionState = GameSessionState.SETUP;
    }

    public void setAttackingSide(Player player) {
        if (player.equals(player1)) {
            attackingPlayer = player1;
        } else if (player.equals(player2)) {
            attackingPlayer = player2;
        } else {
            // TODO make exception
            return;
        }
    }


    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public String getPlayer2Id() {
        return player2.getUserId();
    }

    public String getPlayer1Id() {
        return player1.getUserId();
    }

    public GameSessionState getGameSessionState() {
        return gameSessionState;
    }
}
