package dev.azn9.op.data;

import dev.azn9.op.util.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.FileUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Game {

    private final Set<Player> players = new HashSet<>();
    private World world;
    private boolean started;

    public Game() {
        this.reset();
    }

    public void reset() {
        if (this.world != null) {
            Bukkit.getServer().unloadWorld(this.world, false);
            try {
                FileUtils.deleteDirectory(this.world.getWorldFolder());
            } catch (IOException e) {
                throw new IllegalStateException("Failed to delete world!", e);
            }
        }

        this.world = WorldUtil.generateWorld();
        this.started = false;
        this.players.clear();
    }

    public boolean isStarted() {
        return this.started;
    }

    public void setStarted() {
        this.started = true;
    }

    public World getGameWorld() {
        return this.world;
    }

    public Set<Player> getPlayers() {
        return this.players;
    }
}
