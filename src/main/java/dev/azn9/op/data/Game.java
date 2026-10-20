package dev.azn9.op.data;

import dev.azn9.op.util.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.codehaus.plexus.util.FileUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Game {

    private final Set<UUID> players = new HashSet<>();
    private WorldContainer worldContainer;
    private boolean started;
    private GameMode gameMode;
    private GameSettings settings;
    private boolean ended;

    public Game() {
        this.reset();
    }

    public Game(WorldContainer worldContainer) {
        this.worldContainer = worldContainer;
        this.started = true;
    }

    public void reset() {
        this.clearWorlds();
        this.started = false;
        this.ended = false;
        this.players.clear();
    }

    public void loadWorlds() {
        this.worldContainer = WorldUtil.generateWorld();
    }

    public void clearWorlds() {
        if (this.worldContainer != null) {
            Bukkit.getServer().unloadWorld(this.worldContainer.overworld(), false);
            Bukkit.getServer().unloadWorld(this.worldContainer.nether(), false);
            Bukkit.getServer().unloadWorld(this.worldContainer.end(), false);
            try {
                FileUtils.deleteDirectory(this.worldContainer.overworld().getWorldFolder());
                FileUtils.deleteDirectory(this.worldContainer.nether().getWorldFolder());
                FileUtils.deleteDirectory(this.worldContainer.end().getWorldFolder());
            } catch (IOException e) {
                throw new IllegalStateException("Failed to delete world!", e);
            }
        }
    }

    public boolean isStarted() {
        return this.started;
    }

    public void setStarted() {
        this.started = true;
    }

    public World getGameOverworld() {
        return this.worldContainer.overworld();
    }

    public World getGameNetherWorld() {
        return this.worldContainer.nether();
    }

    public World getGameEndWorld() {
        return this.worldContainer.end();
    }

    public WorldContainer getWorldContainer() {
        return this.worldContainer;
    }

    public Set<UUID> getPlayers() {
        return this.players;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public GameSettings getSettings() {
        return this.settings;
    }

    public void setSettings(GameSettings settings) {
        this.settings = settings;
    }

    public boolean isEnded() {
        return this.ended;
    }

    public void setEnded() {
        this.ended = true;
    }
}
