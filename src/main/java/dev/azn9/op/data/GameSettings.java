/*
 * Copyright 2026 Axel "Azn9" Joly <contact@azn9.dev>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package dev.azn9.op.data;

public class GameSettings {

    private int spawnDistance = 10;
    private double secondsInterval = 15;
    private int startItems = 1;
    private GameMode gameMode = GameMode.COOPERATION;

    public int getSpawnDistance() {
        return this.spawnDistance;
    }

    public void setSpawnDistance(int spawnDistance) {
        this.spawnDistance = spawnDistance;
    }

    public double getSecondsInterval() {
        return this.secondsInterval;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setSecondsInterval(double secondsInterval) {
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
