package dev.azn9.op.listener;

import dev.azn9.op.data.Game;
import dev.azn9.op.service.GameService;
import org.bukkit.Location;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class WorldEventsListener implements Listener {

    private final GameService gameService;

    public WorldEventsListener(GameService gameService) {
        this.gameService = gameService;
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Game currentGame = this.gameService.getCurrentGame();
        Location fromLocation = event.getFrom();
        World fromWorld = fromLocation.getWorld();

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            if (fromWorld.getEnvironment() == World.Environment.NORMAL) { // overworld -> nether
                Location toLocation = event.getTo();
                toLocation.setWorld(currentGame.getGameNetherWorld());
                event.setTo(toLocation);
            } else { // nether -> overworld
                Location toLocation = event.getTo();
                toLocation.setWorld(currentGame.getGameOverworld());
                event.setTo(toLocation);
            }
        } else if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            if (fromWorld.getEnvironment() == World.Environment.NORMAL) { // overworld -> end
                Location toLocation = event.getTo();
                toLocation.setWorld(currentGame.getGameEndWorld());
                event.setTo(toLocation);
            } else { // end -> overworld
                Location toLocation = event.getTo();
                toLocation.setWorld(currentGame.getGameOverworld());
                event.setTo(toLocation);
            }
        }
    }

    @EventHandler
    public void onPlayerPortal(EntityPortalEvent event) {
        Game currentGame = this.gameService.getCurrentGame();
        Location fromLocation = event.getFrom();

        if (event.getPortalType() == PortalType.NETHER) {
            World fromWorld = fromLocation.getWorld();

            if (fromWorld.getEnvironment() == World.Environment.NORMAL) { // overworld -> nether
                Location toLocation = event.getTo();

                if (toLocation == null) {
                    toLocation = fromLocation.clone();
                }

                toLocation.setWorld(currentGame.getGameNetherWorld());

                event.setTo(toLocation);
            } else { // nether -> overworld
                Location toLocation = event.getTo();

                if (toLocation == null) {
                    toLocation = fromLocation.clone();
                }

                toLocation.setWorld(currentGame.getGameOverworld());

                event.setTo(toLocation);
            }
        } else if (event.getPortalType() == PortalType.ENDER) { // overworld -> end
            Location toLocation = event.getTo();

            if (toLocation == null) {
                toLocation = fromLocation.clone();
            }

            toLocation.setWorld(currentGame.getGameEndWorld());

            event.setTo(toLocation);
        }
    }

}
