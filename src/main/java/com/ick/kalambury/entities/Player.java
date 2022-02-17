package com.ick.kalambury.entities;

public class Player implements Comparable<Player> {

    public enum Status {
        KICKED,
        DISCONNECTED,
        CONNECTED,      //connection fully established
        IN_GAME,        //received initialisation confirmation
        VOTING
    }

    private User user;
    private Status status;
    private boolean isOperator;
    private boolean isWinner;
    private int points;
    private int roundsSinceLastDraw;

    public Player(User user) {
        this.user = user;
    }

    public void reset() {
        this.points = 0;
        this.roundsSinceLastDraw = 0;
        this.isWinner = false;
    }

    public void updatePoints(int point) {
        this.points += point;
    }

    public String getId() {
        return user.getId();
    }

    public String getUsername() {
        return user.getNickname();
    }

    public User getUser() {
        return user;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isActive() {
        return status.ordinal() >= Status.IN_GAME.ordinal();
    }

    public boolean isOperator() {
        return isOperator;
    }

    public void setOperator(boolean operator) {
        isOperator = operator;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getRoundsSinceLastDraw() {
        return roundsSinceLastDraw;
    }

    public void setRoundsSinceLastDraw(int roundsSinceLastDraw) {
        this.roundsSinceLastDraw = roundsSinceLastDraw;
    }

    public void updateRoundsSinceLastDraw(int roundsSinceLastDraw) {
        this.roundsSinceLastDraw += roundsSinceLastDraw;
    }

    public GameDataProtos.Player toProto() {
        return GameDataProtos.Player.newBuilder()
                .setId(user.getUserId())
                .setNickname(user.getNickname())
                .setPoints(points)
                .setActive(isActive())
                .setOperator(isOperator)
                .setWinner(isWinner)
                .build();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Player) {
            return this.getUser().getId().equals(((Player) obj).getUser().getId());
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Player other) {
        if ((this.isActive() && other.isActive()) || (!this.isActive() && !other.isActive())) {
            return Integer.compare(other.getPoints(), this.getPoints());
        } else {
            return (!this.isActive() && other.isActive()) ? 1 : -1;
        }
    }

    @Override
    public String toString() {
        return "Player{" +
                "user=" + user +
                ", status=" + status +
                ", isActive=" + isActive() +
                ", isOperator=" + isOperator +
                ", isWinner=" + isWinner +
                ", points=" + points +
                ", roundsSinceLastDraw=" + roundsSinceLastDraw +
                '}';
    }

    public static Player EMPTY = new Player(new User());
}
