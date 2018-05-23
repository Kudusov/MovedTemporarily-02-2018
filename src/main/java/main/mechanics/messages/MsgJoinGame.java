package main.mechanics.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.websocket.Message;

import javax.validation.constraints.NotNull;

@SuppressWarnings("unused")
public class MsgJoinGame extends Message {

    private @NotNull Boolean playWithBot;

    public MsgJoinGame(@NotNull @JsonProperty("playWithBot") Boolean playWithBot) {
        this.playWithBot = playWithBot;
    }

    public Boolean getPlayWithBot() {
        return playWithBot;
    }

    public void setPlayWithBot(Boolean playWithBot) {
        this.playWithBot = playWithBot;
    }

}
