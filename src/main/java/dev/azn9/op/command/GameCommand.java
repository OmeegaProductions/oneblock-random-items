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

package dev.azn9.op.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.azn9.op.RdpEventPlugin;
import dev.azn9.op.service.GameService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class GameCommand {

    private GameCommand() {
        throw new IllegalStateException("Utility class");
    }

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("game")
                .requires(commandSourceStack -> commandSourceStack.getSender().isOp())
                .then(Commands.literal("start")
                        .executes(context -> {
                            GameService gameService = RdpEventPlugin.INSTANCE.getGameService();
                            if (gameService.getCurrentGame().isStarted()) {
                                context.getSource().getSender().sendMessage(Component.text("La partie est déjà en cours", NamedTextColor.RED));
                                return Command.SINGLE_SUCCESS;
                            }

                            gameService.startGame();

                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("reset")
                        .executes(context -> {
                            GameService gameService = RdpEventPlugin.INSTANCE.getGameService();
                            if (!gameService.getCurrentGame().isStarted()) {
                                context.getSource().getSender().sendMessage(Component.text("La partie n'a pas commencé", NamedTextColor.RED));
                                return Command.SINGLE_SUCCESS;
                            }

                            gameService.endGame();

                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }

}
