package main.websocket;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import main.mechanics.messages.MsgError;
import main.mechanics.messages.MsgJoinGame;
import main.mechanics.messages.MsgLobbyCreated;
import main.mechanics.messages.MsgYouInQueue;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({
                @JsonSubTypes.Type(MsgError.class),
                @JsonSubTypes.Type(MsgJoinGame.class),
                @JsonSubTypes.Type(MsgLobbyCreated.class),
                @JsonSubTypes.Type(MsgYouInQueue.class)
})
public abstract class Message {
}
