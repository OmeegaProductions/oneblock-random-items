package dev.azn9.op.data;

public class GameSettings {

    private int spawnDistance = 10;
    private int secondsInterval = 15;
    private int startItems = 3;
    private GameMode gameMode = GameMode.COOPERATION;

    public int getSpawnDistance() {
        return this.spawnDistance;
    }

    public void setSpawnDistance(int spawnDistance) {
        this.spawnDistance = spawnDistance;
    }

    public int getSecondsInterval() {
        return this.secondsInterval;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setSecondsInterval(int secondsInterval) {
        this.secondsInterval = secondsInterval;
    }

    public int getStartItems() {
        return this.startItems;
    }

    public void setStartItems(int startItems) {
        this.startItems = startItems;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
}
