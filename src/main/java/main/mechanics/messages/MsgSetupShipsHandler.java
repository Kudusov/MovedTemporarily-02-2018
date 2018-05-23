package main.mechanics.messages;

import main.mechanics.gamegession.GameSession;
import main.mechanics.gamegession.GameSessionService;
import main.websocket.MessageHandler;
import main.websocket.MessageHandlerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@SuppressWarnings("unused")
@Component
public class MsgSetupShipsHandler extends MessageHandler<MsgSetupShips> {

    private @NotNull GameSessionService gameSessionService;
    private  @NotNull MessageHandlerContainer messageHandlerContainer;

    public MsgSetupShipsHandler(@NotNull GameSessionService gameSessionService,
                                @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(MsgSetupShips.class);
        this.gameSessionService = gameSessionService;
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(MsgSetupShips.class, this);
    }

    @Override
    public void handle(@NotNull MsgSetupShips message, String userId) {

        // TODO: добавить проверку на валидацию кораблей

        if (gameSessionService.isPlaying(userId)) {
            final GameSession gameSession = gameSessionService.getGameSessionByUserId(userId);
            if (gameSession.getPlayer1().getUserId().equals(userId)) {
                gameSession.getPlayer1().setAliveShips(message.getShips());
            } else if (gameSession.getPlayer2().getUserId().equals(userId)) {
                gameSession.getPlayer2().setAliveShips(message.getShips());
            } else {
                throw new IllegalArgumentException("Player isn't in this session");
            }

            gameSessionService.tryStartGame(gameSession);

        } else {
            throw new IllegalArgumentException("Player not playing");
        }
    }
}
