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
