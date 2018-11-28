package main.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.mechanics.game.UserPlayer;
import main.mechanics.gamegession.GameSessionService;
import main.services.UserServiceDAO;
import main.views.UserInfoForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.validation.constraints.NotNull;
import java.io.IOException;

import static org.springframework.web.socket.CloseStatus.SERVER_ERROR;

@SuppressWarnings("unused")
public class GameSocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSocketHandler.class);
    private static final CloseStatus ACCESS_DENIED = new CloseStatus(4500, "Not logged in. Access denied");

    private final @NotNull UserServiceDAO userService;
    private final @NotNull MessageHandlerContainer messageHandlerContainer;
    private final @NotNull RemotePointService remotePointService;
    private final @NotNull GameSessionService gameSessionService;
    private final ObjectMapper objectMapper;

    public GameSocketHandler(@NotNull UserServiceDAO userService,
                             @NotNull MessageHandlerContainer messageHandlerContainer,
                             @NotNull RemotePointService remotePointService,
                             @NotNull GameSessionService gameSessionService, ObjectMapper objectMapper) {

        this.userService = userService;
        this.messageHandlerContainer = messageHandlerContainer;
        this.remotePointService = remotePointService;
        this.gameSessionService = gameSessionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        System.out.println("Websocket client connected");

        final String userId = (String) webSocketSession.getAttributes().get("userLogin");

        if (userId == null || userService.getIdByLoginDB(userId) == null) {
            closeSessionSilently(webSocketSession, ACCESS_DENIED);
            return;
        }
        final UserInfoForm dbUser = new UserInfoForm();
        userService.getUserInfo(userId, dbUser);
        final UserPlayer userPlayer = new UserPlayer(dbUser);
        gameSessionService.addPlayer(userPlayer);
        remotePointService.registerUser(userId, webSocketSession);
    }

    @Override
    public void handleTextMessage(WebSocketSession webSocketSession, TextMessage message) {
        System.out.println("HandleTextMessage");
        if (!webSocketSession.isOpen()) {
            System.out.println("socket closed");
            return;
        }

        final String userId = (String) webSocketSession.getAttributes().get("userLogin");
        if (userId == null || userService.getIdByLoginDB(userId) == null) {
            System.out.println("Unauthorized user");
            closeSessionSilently(webSocketSession, ACCESS_DENIED);
            return;
        }
        System.out.println(message.getPayload());
        handleMessage(userId, message);

    }

    @SuppressWarnings("OverlyBroadCatchBlock")
    private void handleMessage(String userId, TextMessage text) {
        System.out.println("Handle Message");
        final Message message;
        try {
            message = objectMapper.readValue(text.getPayload(), Message.class);
        } catch (IOException ex) {
            System.out.println("wrong json format: " + ex);
            return;
        }

        try {
            messageHandlerContainer.handle(message, userId);
        } catch (HandleException ex) {
            System.out.println("Can't handle message of type " + message.getClass().getName() + " with content: " + text);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        System.out.println("Webcocket closed");
    }

    @SuppressWarnings("SameParameterValue")
    private void closeSessionSilently(@NotNull WebSocketSession session, @Nullable CloseStatus closeStatus) {
        System.out.println("closeSessionSylently");
        final CloseStatus status = closeStatus == null ? SERVER_ERROR : closeStatus;
        //noinspection OverlyBroadCatchBlock
        try {
            if (session.isOpen()) {
                session.close(status);
            }
        } catch (Exception ignore) {
        }

    }
}
