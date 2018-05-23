package main.mechanics.messages;

import main.mechanics.gamegession.GameSessionService;
import main.websocket.MessageHandler;
import main.websocket.MessageHandlerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Component
public class MsgJoinGameHandler extends MessageHandler<MsgJoinGame> {

    private final GameSessionService gameSessionService;
    private final @NotNull MessageHandlerContainer messageHandlerContainer;
    public MsgJoinGameHandler(@NotNull MessageHandlerContainer messageHandlerContainer,
                              @NotNull GameSessionService gameSessionService) {
        super(MsgJoinGame.class);
        this.messageHandlerContainer = messageHandlerContainer;
        this.gameSessionService = gameSessionService;

    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(MsgJoinGame.class, this);
    }

    @Override
    public void handle(@NotNull MsgJoinGame message, String userId) {
        if (!message.getPlayWithBot()) {
            gameSessionService.addWaitingPlayer(userId);
        }
    }
}
