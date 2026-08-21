package dev.azn9.op;

import dev.azn9.op.command.ConfigCommand;
import dev.azn9.op.command.GameCommand;
import dev.azn9.op.listener.PlayerConnexionListener;
import dev.azn9.op.listener.PlayerDeathListener;
import dev.azn9.op.listener.WorldEventsListener;
import dev.azn9.op.service.GameService;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RdpEventPlugin extends JavaPlugin {

    private final GameService gameService = new GameService();

    @Override
    public void onEnable() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, this::createCommands);

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerConnexionListener(this.gameService), this);
        pluginManager.registerEvents(new PlayerDeathListener(this.gameService), this);
        pluginManager.registerEvents(new WorldEventsListener(), this);

        this.gameService.initialize(this.getServer().getWorlds().getFirst(), this);
    }

    private void createCommands(ReloadableRegistrarEvent<Commands> registrarEvent) {
        registrarEvent.registrar().register(ConfigCommand.build(this));
        registrarEvent.registrar().register(GameCommand.build(this));
    }

    public GameService getGameService() {
        return this.gameService;
    }
}
