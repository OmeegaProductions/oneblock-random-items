/*
 * Copyright 2026 Axel "Azn9" Joly <contact@azn9.dev>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

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
