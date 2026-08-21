package dev.azn9.op.data;

public class GameSettings {

    private int spawnDistance = 10;
    private int secondsInterval = 15;
    private int startItems = 3;

    public int getSpawnDistance() {
        return this.spawnDistance;
    }

    public void setSpawnDistance(int spawnDistance) {
        this.spawnDistance = spawnDistance;
    }

    public int getSecondsInterval() {
        return this.secondsInterval;
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
}
