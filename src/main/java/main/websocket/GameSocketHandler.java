package main.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@SuppressWarnings("unused")
public class GameSocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSocketHandler.class);
    private static final CloseStatus ACCESS_DENIED = new CloseStatus(4500, "Not logged in. Access denied");

    public GameSocketHandler() {
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        System.out.println("Websocket client connected");
    }

    @Override
    public void handleTextMessage(WebSocketSession webSocketSession, TextMessage message) {
        if (!webSocketSession.isOpen()) {
            System.out.println("socket closed");
        }

        System.out.println(message.getPayload());
        try {
            webSocketSession.sendMessage(new TextMessage("Hello from server"));

        } catch (IOException e) {
            System.out.println("Sending error");
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        System.out.println("Webcocket closed");
    }
}
