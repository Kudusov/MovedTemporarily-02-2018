package main.websocket;

import javax.validation.constraints.NotNull;

public abstract class MessageHandler<T extends Message> {
    private final @NotNull Class<T> clazz;

    public MessageHandler(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    // TODO : реализовать exceptions в случае ошибки
    public void handleMessage(@NotNull Message message, String userId) throws HandleException {
        try {
            handle(clazz.cast(message), userId);
        } catch (ClassCastException ex) {
            throw new HandleException("Can't read incoming message of type " + message.getClass(), ex);
        }
    }

    public abstract void handle(@NotNull T message, @NotNull String userId);
}
