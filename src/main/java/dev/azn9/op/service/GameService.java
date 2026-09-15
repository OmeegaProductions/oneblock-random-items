package dev.azn9.op.service;

import dev.azn9.op.data.Game;
import dev.azn9.op.data.GameSettings;
import dev.azn9.op.util.ItemUtil;
import dev.azn9.op.util.WorldUtil;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GameService {

    private final GameSettings gameSettings = new GameSettings();
    private World initialWorld;
    private JavaPlugin plugin;
    private Server server;
    private Game currentGame;
    private ScheduledTask nextDropTask;

    public void initialize(@NotNull World initialWorld, JavaPlugin plugin) {
        this.initialWorld = initialWorld;
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.currentGame = new Game();
    }

    public Game getCurrentGame() {
        return this.currentGame;
    }

    public void startGame() {
        if (this.currentGame.isStarted()) {
            return;
        }

        for (Player player : this.initialWorld.getPlayers()) {
            if (player.getGameMode() != GameMode.SPECTATOR) {
                this.currentGame.getPlayers().add(player.getUniqueId());
            }
        }

        if (this.getCurrentGame().getPlayers().isEmpty()) {
            throw new IllegalStateException("No players in game");
        }

        this.currentGame.setGameMode(this.gameSettings.getGameMode());

        List<UUID> playersToTeleport = new ArrayList<>(this.currentGame.getPlayers());

        World gameWorld = this.currentGame.getGameOverworld();

        List<Location> spawnLocations;
        if (this.gameSettings.getGameMode() == dev.azn9.op.data.GameMode.DUEL) {
            spawnLocations = WorldUtil.generateSpawnPoints(gameWorld, this.currentGame.getPlayers().size(), this.gameSettings.getSpawnDistance());
        } else {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    gameWorld.getBlockAt(x, 192, z).setType(Material.BEDROCK);
                }
            }

            spawnLocations = new ArrayList<>();
            for (int i = 0; i < playersToTeleport.size(); i++) {
                spawnLocations.add(new Location(gameWorld, 0.5, 193, 0.5));
            }
        }

        for (Location spawnLocation : spawnLocations) {
            if (playersToTeleport.isEmpty()) {
                break;
            }

            spawnLocation.getChunk().load();

            UUID playerUuid = playersToTeleport.removeFirst();
            Player player = this.server.getPlayer(playerUuid);
            if (player != null && player.isOnline()) {
                player.teleport(spawnLocation);
                GameService.resetPlayer(player);
            }
        }

        for (Player player : this.initialWorld.getPlayers()) {
            if (player.getGameMode() == GameMode.SPECTATOR) {
                player.teleportAsync(gameWorld.getSpawnLocation());
            }
        }

        for (UUID playerUuid : this.currentGame.getPlayers()) {
            Player player = this.server.getPlayer(playerUuid);
            if (player != null && player.isOnline()) {
                player.setGameMode(GameMode.SURVIVAL);
            }
        }

        this.currentGame.setStarted();

        for (int i = 0; i < this.gameSettings.getStartItems(); i++) {
            this.dropItem();
        }

        this.scheduleItemDrop();
    }

    private void scheduleItemDrop() {
        this.nextDropTask = this.server.getGlobalRegionScheduler().runDelayed(this.plugin, _ -> {
            this.dropItem();
            this.scheduleItemDrop();
        }, this.gameSettings.getSecondsInterval() * 20L);
    }

    private void dropItem() {
        for (UUID playerUuid : this.currentGame.getPlayers()) {
            Player player = this.server.getPlayer(playerUuid);
            if (player == null || !player.isOnline()) {
                continue;
            }

            ItemStack itemStack = ItemUtil.generateRandomItem();
            HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(itemStack);

            if (!remainingItems.isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
            }

            player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 0.1f, 1.0f);
//            player.sendMessage(Component.text("Item reçu : ").append(itemStack.effectiveName()));
        }
    }

    public void checkWin() {
        if (!this.currentGame.isStarted()) {
            return;
        }

        if (this.currentGame.getGameMode() != dev.azn9.op.data.GameMode.DUEL) {
            return;
        }

        Set<UUID> playerUuids = this.currentGame.getPlayers();
        Player winner;

        if (playerUuids.isEmpty()) {
            return;
        } else if (playerUuids.size() == 1) {
            //noinspection OptionalGetWithoutIsPresent
            winner = playerUuids.stream().findFirst().map(this.server::getPlayer).get(); // NOSONAR
        } else {
            return;
        }

        this.server.broadcast(Component.text("\nVictoire de " + winner.getName() + " !\n", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        this.server.playSound(net.kyori.adventure.sound.Sound.sound(Sound.ENTITY_PLAYER_LEVELUP, net.kyori.adventure.sound.Sound.Source.AMBIENT, 1, 1));

        if (this.nextDropTask != null) {
            this.nextDropTask.cancel();
        }
    }

    public void endGame() {
        if (this.currentGame == null || !this.currentGame.isStarted()) {
            return;
        }
        if (this.nextDropTask != null) {
            this.nextDropTask.cancel();
        }

        for (Player player : this.currentGame.getGameOverworld().getPlayers()) {
            player.teleport(this.initialWorld.getSpawnLocation());
            player.setGameMode(GameMode.ADVENTURE);
            GameService.resetPlayer(player);
        }
        for (Player player : this.currentGame.getGameNetherWorld().getPlayers()) {
            player.teleport(this.initialWorld.getSpawnLocation());
            player.setGameMode(GameMode.ADVENTURE);
            GameService.resetPlayer(player);
        }
        for (Player player : this.currentGame.getGameEndWorld().getPlayers()) {
            player.teleport(this.initialWorld.getSpawnLocation());
            player.setGameMode(GameMode.ADVENTURE);
            GameService.resetPlayer(player);
        }

        this.currentGame.reset();
    }

    private static void resetPlayer(Player player) {
        player.getInventory().clear();
        player.setHealth(20.0);
        player.setFireTicks(0);
        player.setFallDistance(0);
        player.setFoodLevel(20);
        player.setSaturation(10);
        player.setExp(0);
        player.setLevel(0);
    }

    public void playCooperationWin() {
        this.server.broadcast(Component.text("\nVictoire !\n", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        this.server.playSound(net.kyori.adventure.sound.Sound.sound(Sound.ENTITY_PLAYER_LEVELUP, net.kyori.adventure.sound.Sound.Source.AMBIENT, 1, 1));

        if (this.nextDropTask != null) {
            this.nextDropTask.cancel();
        }

        for (Player player : this.server.getOnlinePlayers()) {
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    public GameSettings getGameSettings() {
        return this.gameSettings;
    }
}
