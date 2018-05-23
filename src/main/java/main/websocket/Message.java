package main.websocket;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import main.mechanics.messages.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({
                @JsonSubTypes.Type(MsgError.class),
                @JsonSubTypes.Type(MsgJoinGame.class),
                @JsonSubTypes.Type(MsgLobbyCreated.class),
                @JsonSubTypes.Type(MsgYouInQueue.class),
                @JsonSubTypes.Type(MsgSetupShips.class),
                @JsonSubTypes.Type(MsgGameStarted.class)
})
public abstract class Message {
}
