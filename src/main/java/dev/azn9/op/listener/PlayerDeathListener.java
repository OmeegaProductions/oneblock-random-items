package dev.azn9.op.listener;

import dev.azn9.op.data.Game;
import dev.azn9.op.service.GameService;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

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

        if (event.getFinalDamage() < player.getHealth()) {
            return;
        }

        event.setDamage(0);

        if (!currentGame.getPlayers().remove(player)) {
            event.setCancelled(true);
            return;
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(player.getWorld().getSpawnLocation());
        this.gameService.checkWin();
    }

}
