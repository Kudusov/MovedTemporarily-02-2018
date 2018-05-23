package main.websocket;

import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Service
public class GameMessageHandlerContainer implements MessageHandlerContainer {

    private final Map<Class<?>, MessageHandler<?>> handlerMap = new HashMap<>();

    @Override
    public void handle(@NotNull Message message, String forUserId) throws HandleException {
        final MessageHandler<?> messageHandler = handlerMap.get(message.getClass());
        if (messageHandler == null) {
            throw new HandleException("no handler for message of " + message.getClass().getName() + " type");
        }

        messageHandler.handleMessage(message, forUserId);

    }

    @Override
    public <T extends Message> void registerHandler(Class<T> clazz, MessageHandler<T> handler) {
        handlerMap.put(clazz, handler);
    }
}
