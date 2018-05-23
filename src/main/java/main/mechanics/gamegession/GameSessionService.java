package main.mechanics.gamegession;

import main.mechanics.game.Player;
import main.mechanics.game.UserPlayer;
import main.mechanics.messages.MsgError;
import main.mechanics.messages.MsgLobbyCreated;
import main.mechanics.messages.MsgYouInQueue;
import main.services.UserServiceDAO;
import main.websocket.RemotePointService;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class GameSessionService {
    private final @NotNull UserServiceDAO userServiceDAO;
    private final @NotNull RemotePointService remotePointService;

    private final Map<String, GameSession> gameSessions = new ConcurrentHashMap<>();
    private final Queue<UserPlayer> waiters = new ConcurrentLinkedDeque<>();
    private final Map<String, UserPlayer> playerMap = new ConcurrentHashMap<>();

    public GameSessionService(@NotNull UserServiceDAO userServiceDAO,
                              @NotNull RemotePointService remotePointService) {
        this.userServiceDAO = userServiceDAO;
        this.remotePointService = remotePointService;
    }

    public void addPlayer(@NotNull UserPlayer userPlayer) {
        playerMap.put(userPlayer.getUserId(), userPlayer);
        final MsgYouInQueue msgMsgYouInQueue = new MsgYouInQueue(userPlayer.getUser().getLogin());
        try {
            remotePointService.sendMeassageToUser(userPlayer.getUserId(), msgMsgYouInQueue);
        } catch (IOException ex) {
            try {
                remotePointService.sendMeassageToUser(userPlayer.getUserId(), new MsgError("Could not get in the queue "));
            } catch (IOException sendEx) {
                return;
                //LOGGER.warn("Unable to send message");
            }
            remotePointService.closeConnection(userPlayer.getUserId(), CloseStatus.SERVER_ERROR);
        }
    }

    public void addWaitingPlayer(@NotNull String userId) {
        if (playerMap.containsKey(userId)) {
            waiters.add(playerMap.remove(userId));
            if (waiters.size() >= 2) {
                 createSession(waiters.poll(), waiters.poll());
            }
        } else {
            System.out.println("Some MsgError");
        }
    }

    public void createSession(@NotNull Player player1, @NotNull Player player2) {
        final GameSession gameSession = new GameSession(player1, player2);
        gameSessions.put(player1.getUserId(), gameSession);
        gameSessions.put(player2.getUserId(), gameSession);

        try {
            final MsgLobbyCreated msg1 = new MsgLobbyCreated(player2.getUser().getLogin());
            final MsgLobbyCreated msg2 = new MsgLobbyCreated(player1.getUser().getLogin());
            remotePointService.sendMeassageToUser(player1.getUserId(), msg1);
            remotePointService.sendMeassageToUser(player2.getUserId(), msg2);
        } catch (IOException ex) {
            try {
                final MsgError err = new MsgError("Failed to create session");
                remotePointService.sendMeassageToUser(player2.getUserId(), err);
                remotePointService.sendMeassageToUser(player1.getUserId(), err);
            } catch (IOException ex2) {
                System.out.println("Some MsgError");
            }
            remotePointService.closeConnection(player1.getUserId(), CloseStatus.SERVER_ERROR);
            remotePointService.closeConnection(player2.getUserId(), CloseStatus.SERVER_ERROR);
        }
    }
}
