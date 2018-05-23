package main.mechanics.game;

import main.views.UserInfoForm;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class UserPlayer implements Player {
    private static final @NotNull AtomicLong PLAYER_ID_GENERATOR = new AtomicLong(0);
    private Long playerId;
    private Integer score = 0;
    private UserInfoForm user;

    private List<Ship> aliveShips = new ArrayList<>();
    private List<Ship> deadShips = new ArrayList<>();
    private Field field;

    public UserPlayer(UserInfoForm user, List<Ship> ships) {
        this.playerId = PLAYER_ID_GENERATOR.getAndIncrement();
        this.user = user;
        aliveShips.addAll(ships);
        field = new Field(ships);
    }

    public UserPlayer() {
        this.playerId = PLAYER_ID_GENERATOR.getAndIncrement();
        this.user = null;
        field = new Field();
    }

    public UserPlayer(List<Ship> ships) {
        this.playerId = PLAYER_ID_GENERATOR.getAndIncrement();
        this.user = null;
        aliveShips.addAll(ships);
        field = new Field(ships);
    }

    public UserPlayer(UserInfoForm user) {
        this.playerId = PLAYER_ID_GENERATOR.getAndIncrement();
        this.user = user;
        field = new Field();
    }
    @Override
    public Long getPlayerId() {
        return playerId;
    }

    @Override
    public String getUserId() {
        return user.getLogin();
    }

    @Override
    public Integer getScore() {
        return score;
    }

    @Override
    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public UserInfoForm getUser() {
        return user;
    }

    @Override
    public void setUser(UserInfoForm user) {
        this.user = user;
    }

    @Override
    public List<Ship> getAliveShips() {
        return aliveShips;
    }

    @Override
    public void setAliveShips(List<Ship> aliveShips) {
        this.aliveShips = aliveShips;
    }

    @Override
    public List<Ship> getDeadShips() {
        return deadShips;
    }

    @Override
    public void setDeadShips(List<Ship> deadShips) {
        this.deadShips = deadShips;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public void setField(Field field) {
        this.field = field;
    }
}
