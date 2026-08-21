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

    public static LiteralCommandNode<CommandSourceStack> build(RdpEventPlugin plugin) {
        GameService gameService = plugin.getGameService();

        return Commands.literal("game")
                .requires(commandSourceStack -> commandSourceStack.getSender().isOp())
                .then(Commands.literal("start")
                        .executes(context -> {
                            if (gameService.getCurrentGame().isStarted()) {
                                context.getSource().getSender().sendMessage(Component.text("La partie est déjà en cours", NamedTextColor.RED));
                                return Command.SINGLE_SUCCESS;
                            }

                            gameService.startGame();

                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("stop")
                        .executes(context -> {
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
