package dev.azn9.op;

import dev.azn9.op.command.ConfigCommand;
import dev.azn9.op.command.GameCommand;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class RdpEventPluginBootstrapper implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, this::createCommands);
    }

    private void createCommands(ReloadableRegistrarEvent<Commands> registrarEvent) {
        registrarEvent.registrar().register(ConfigCommand.build());
        registrarEvent.registrar().register(GameCommand.build());
    }

}
