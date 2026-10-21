package dev.azn9.op.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.azn9.op.data.Game;
import dev.azn9.op.data.GameSettings;
import dev.azn9.op.data.WorldContainer;
import dev.azn9.op.util.ItemUtil;
import dev.azn9.op.util.WorldContainerTypeAdapter;
import dev.azn9.op.util.WorldUtil;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GameService {

    private static final Logger LOGGER = LogManager.getLogger(GameService.class);
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(WorldContainer.class, new WorldContainerTypeAdapter())
            .create();
    private static final String GAMEDATA_JSON = "gamedata.json";

    private GameSettings gameSettings = new GameSettings();
    private World initialWorld;
    private JavaPlugin plugin;
    private Server server;
    private Game currentGame;
    private ScheduledTask nextDropTask;

    public void initialize(@NotNull Server server, JavaPlugin plugin) {
        this.initialWorld = server.getWorlds().getFirst();
        this.plugin = plugin;
        this.server = server;

        this.loadGame();
    }

    private void loadGame() {
        File dataFolder = this.plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs(); // NOSONAR
        }

        File gameDataFile = new File(dataFolder, GameService.GAMEDATA_JSON);
        if (!gameDataFile.exists()) {
            GameService.LOGGER.info("Starting new game");
            this.currentGame = new Game();
            return;
        }

        try (JsonReader reader = new JsonReader(new FileReader(gameDataFile))) {
            this.currentGame = GameService.GSON.fromJson(reader, Game.class);
        } catch (Exception e) {
            GameService.LOGGER.error("Failed to load game", e);
            this.currentGame = new Game();
            return;
        }

        this.gameSettings = this.currentGame.getSettings();

        GameService.LOGGER.info("Found existing game data, resuming");
        this.scheduleItemDrop();
    }

    public Game getCurrentGame() {
        return this.currentGame;
    }

    public void startGame() {
        if (this.currentGame.isStarted()) {
            return;
        }

        this.currentGame.loadWorlds();

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
            gameWorld.getBlockAt(0, 150, 0).setType(Material.GRASS_BLOCK);

            spawnLocations = new ArrayList<>();
            for (int i = 0; i < playersToTeleport.size(); i++) {
                spawnLocations.add(new Location(gameWorld, 0.5, 151, 0.5));
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

        this.currentGame.setSettings(this.gameSettings);
        this.currentGame.setStarted();

        File gameDataFile = new File(this.plugin.getDataFolder(), GameService.GAMEDATA_JSON);
        try {
            Files.deleteIfExists(gameDataFile.toPath());
        } catch (Exception e) {
            GameService.LOGGER.error("Failed to delete gamedata.json", e);
        }

        try (JsonWriter writer = new JsonWriter(new FileWriter(gameDataFile))) {
            GameService.GSON.toJson(this.currentGame, Game.class, writer);
        } catch (Exception e) {
            GameService.LOGGER.error("Failed to save gamedata.json", e);
        }

        for (int i = 0; i < this.gameSettings.getStartItems(); i++) {
            this.dropItem();
        }

        this.scheduleItemDrop();
    }

    private void scheduleItemDrop() {
        this.nextDropTask = this.server.getGlobalRegionScheduler().runDelayed(this.plugin, _ -> {
            this.dropItem();
            this.scheduleItemDrop();
        }, (long) (this.gameSettings.getSecondsInterval() * 20D));
    }

    private void dropItem() {
        this.currentGame.getWorldContainer().apply(world -> {
            for (Player player : world.getPlayers()) {
                this.dropItemToPlayer(player);
            }
        });
    }

    private void dropItemToPlayer(Player player) {
        if (player == null || !player.isOnline() || player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        ItemStack itemStack = ItemUtil.generateRandomItem();
        HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(itemStack);

        if (!remainingItems.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
        }

        player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 0.1f, 1.0f);
        player.sendActionBar(itemStack.effectiveName());
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

        File gameDataFile = new File(this.plugin.getDataFolder(), GameService.GAMEDATA_JSON);
        try {
            Files.deleteIfExists(gameDataFile.toPath());
        } catch (Exception e) {
            GameService.LOGGER.error("Failed to delete gamedata.json", e);
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
        if (this.currentGame.isEnded()) {
            return;
        }
        this.currentGame.setEnded();

        this.server.broadcast(Component.text("\nVictoire !\n", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        this.server.playSound(net.kyori.adventure.sound.Sound.sound(Sound.ENTITY_PLAYER_LEVELUP, net.kyori.adventure.sound.Sound.Source.AMBIENT, 1, 1));

        if (this.nextDropTask != null) {
            this.nextDropTask.cancel();
        }
    }

    public GameSettings getGameSettings() {
        return this.gameSettings;
    }
}
