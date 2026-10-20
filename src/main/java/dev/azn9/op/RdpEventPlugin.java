package dev.azn9.op;

import dev.azn9.op.data.Game;
import dev.azn9.op.data.GameSettings;
import dev.azn9.op.listener.PlayerConnexionListener;
import dev.azn9.op.listener.PlayerDeathListener;
import dev.azn9.op.listener.WorldEventsListener;
import dev.azn9.op.service.GameService;
import dev.azn9.op.util.EmptyWorldGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class RdpEventPlugin extends JavaPlugin {

    public static RdpEventPlugin INSTANCE;

    private final GameService gameService = new GameService();

    public RdpEventPlugin() {
        RdpEventPlugin.INSTANCE = this;
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerConnexionListener(this.gameService), this);
        pluginManager.registerEvents(new PlayerDeathListener(this.gameService), this);
        pluginManager.registerEvents(new WorldEventsListener(this.gameService), this);

        this.getServer().getScheduler().runTask(this, () -> {
            World world = this.getServer().getWorlds().getFirst();
            //noinspection removal
            world.dropItemNaturally(new Location(world, 0, 0, 0), new ItemStack(Material.LEGACY_ACACIA_DOOR)); // Force legacy material init

            this.gameService.initialize(this.getServer(), this);
        });
    }

    @Override
    public void onDisable() {
        Game game = this.getGameService().getCurrentGame();
        if (!game.isStarted()) {
            game.clearWorlds();
        }
    }

    @Override
    public @Nullable ChunkGenerator getDefaultWorldGenerator(@NonNull String worldName, @Nullable String id) {
        return new EmptyWorldGenerator();
    }

    public GameService getGameService() {
        return this.gameService;
    }

    public GameSettings getGameSettings() {
        return this.gameService.getGameSettings();
    }
}
