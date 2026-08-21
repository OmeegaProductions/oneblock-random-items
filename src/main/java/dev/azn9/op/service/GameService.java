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
                this.currentGame.getPlayers().add(player);
            }
        }

        if (this.getCurrentGame().getPlayers().isEmpty()) {
            throw new IllegalStateException("No players in game");
        }

        List<Player> playersToTeleport = new ArrayList<>(this.currentGame.getPlayers());

        World gameWorld = this.currentGame.getGameWorld();
        List<Location> spawnLocations = WorldUtil.generateSpawnPoints(gameWorld, this.currentGame.getPlayers().size(), this.gameSettings.getSpawnDistance());

        for (Location spawnLocation : spawnLocations) {
            if (playersToTeleport.isEmpty()) {
                break;
            }

            spawnLocation.getChunk().load();

            Player player = playersToTeleport.removeFirst();
            player.teleport(spawnLocation);
            GameService.resetPlayer(player);
        }

        for (Player player : this.initialWorld.getPlayers()) {
            if (player.getGameMode() == GameMode.SPECTATOR) {
                player.teleportAsync(gameWorld.getSpawnLocation());
            }
        }

        for (Player player : this.currentGame.getPlayers()) {
            player.setGameMode(GameMode.SURVIVAL);
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
        for (Player player : this.currentGame.getPlayers()) {
            ItemStack itemStack = ItemUtil.generateRandomItem();
            HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(itemStack);

            if (!remainingItems.isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
            }

            player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.1f, 1.0f);
            player.sendMessage(Component.text("Item reçu : ").append(itemStack.effectiveName()));
        }
    }

    public void checkWin() {
        if (!this.currentGame.isStarted()) {
            return;
        }

        Set<Player> players = this.currentGame.getPlayers();
        Player winner;

        if (players.isEmpty()) {
            this.endGame();
            return;
        } else if (players.size() == 1) {
            winner = players.stream().findFirst().get(); // NOSONAR
        } else {
            return;
        }

        this.server.broadcast(Component.text("\nVictoire de " + winner.getName() + " !\n", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        this.server.playSound(net.kyori.adventure.sound.Sound.sound(Sound.ENTITY_PLAYER_LEVELUP, net.kyori.adventure.sound.Sound.Source.AMBIENT, 1, 1));

        if (this.nextDropTask != null) {
            this.nextDropTask.cancel();
        }

        this.server.getGlobalRegionScheduler().runDelayed(this.plugin, _ -> {
            this.endGame();
        }, 100L);
    }

    public void endGame() {
        if (this.currentGame == null || !this.currentGame.isStarted()) {
            return;
        }
        if (this.nextDropTask != null) {
            this.nextDropTask.cancel();
        }

        for (Player player : this.currentGame.getGameWorld().getPlayers()) {
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

    public GameSettings getGameSettings() {
        return this.gameSettings;
    }
}
