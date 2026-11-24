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
import dev.azn9.op.service.GameService;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerDeathListener implements Listener {

    private final GameService gameService;

    public PlayerDeathListener(GameService gameService) {
        this.gameService = gameService;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        Game currentGame = this.gameService.getCurrentGame();
        if (!currentGame.isStarted()) {
            event.setCancelled(true);
            return;
        }

        if (currentGame.getGameMode() != dev.azn9.op.data.GameMode.DUEL) {
            return;
        }

        if (event.getFinalDamage() < player.getHealth()) {
            return;
        }

        event.setDamage(0);

        if (!currentGame.getPlayers().remove(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(player.getWorld().getSpawnLocation());
        this.gameService.checkWin();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Game currentGame = this.gameService.getCurrentGame();

        if (event.getRespawnReason() == PlayerRespawnEvent.RespawnReason.END_PORTAL) {
            if (currentGame.getGameMode() == dev.azn9.op.data.GameMode.COOPERATION) {
                this.gameService.playCooperationWin();
            }
            return;
        }

        if (event.isMissingRespawnBlock() || (!event.isBedSpawn() && !event.isAnchorSpawn())) {
            event.setRespawnLocation(currentGame.getGameOverworld().getSpawnLocation());
        }
    }

}
