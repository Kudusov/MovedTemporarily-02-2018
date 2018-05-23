package main.mechanics.game;

import main.views.UserInfoForm;

import java.util.List;

public interface  Player {
    Long getPlayerId();
    String getUserId();
    Integer getScore();
    void setScore(Integer score);
    UserInfoForm getUser();
    void setUser(UserInfoForm user);
    List<Ship> getAliveShips();
    void setAliveShips(List<Ship> aliveShips);
    List<Ship> getDeadShips();
    void setDeadShips(List<Ship> deadShips);
    Field getField();
    void setField(Field field);
}
