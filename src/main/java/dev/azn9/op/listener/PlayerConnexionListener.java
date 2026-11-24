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

package dev.azn9.op.listener;

import dev.azn9.op.data.Game;
import dev.azn9.op.data.GameMode;
import dev.azn9.op.service.GameService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnexionListener implements Listener {

    private final GameService gameService;

    public PlayerConnexionListener(GameService gameService) {
        this.gameService = gameService;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Game currentGame = this.gameService.getCurrentGame();
        if (!currentGame.isStarted()) {
            return;
        }

        if (currentGame.getGameMode() == GameMode.DUEL && currentGame.getPlayers().remove(event.getPlayer().getUniqueId())) {
            this.gameService.checkWin();
        }
    }

}
