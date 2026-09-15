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

    public Game() {
        this.reset();
    }

    public void reset() {
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

        this.worldContainer = WorldUtil.generateWorld();
        this.started = false;
        this.players.clear();
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

    public Set<UUID> getPlayers() {
        return this.players;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
}
