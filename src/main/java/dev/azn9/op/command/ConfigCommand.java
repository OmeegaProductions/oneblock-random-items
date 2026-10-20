package dev.azn9.op.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.azn9.op.RdpEventPlugin;
import dev.azn9.op.data.GameMode;
import dev.azn9.op.data.GameSettings;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ConfigCommand {

    private static final String INTERVAL_ARGUMENT = "interval";
    private static final String DISTANCE_ARGUMENT = "distance";
    private static final String START_ITEMS_ARGUMENT = "startItems";
    private static final String MODE_ARGUMENT = "mode";

    private ConfigCommand() {
        throw new IllegalStateException("Utility class");
    }

    public static LiteralCommandNode<CommandSourceStack> build() {
        return Commands.literal("config")
                .requires(commandSourceStack -> commandSourceStack.getSender().isOp())
                .then(Commands.literal(ConfigCommand.MODE_ARGUMENT)
                        .executes(context -> {return 0;})
                        .then(Commands.literal("cooperation").executes(context -> {
                            GameSettings gameSettings = RdpEventPlugin.INSTANCE.getGameSettings();
                            gameSettings.setGameMode(GameMode.COOPERATION);
                            context.getSource().getSender().sendMessage(Component.text("Le mode de jeu a été configuré à \"Cooperation\"", NamedTextColor.GREEN));
                            return Command.SINGLE_SUCCESS;
                        }))
                        .then(Commands.literal("duel").executes(context -> {
                            GameSettings gameSettings = RdpEventPlugin.INSTANCE.getGameSettings();
                            gameSettings.setGameMode(GameMode.DUEL);
                            context.getSource().getSender().sendMessage(Component.text("Le mode de jeu a été configuré à \"Duel\"", NamedTextColor.GREEN));
                            return Command.SINGLE_SUCCESS;
                        })))
                .then(Commands.literal(ConfigCommand.INTERVAL_ARGUMENT)
                        .executes(context -> {
                            GameSettings gameSettings = RdpEventPlugin.INSTANCE.getGameSettings();
                            context.getSource().getSender().sendMessage(Component.text("L'intervalle de drop est de %.2f seconde(s)".formatted(
                                    gameSettings.getSecondsInterval()
                            )));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument(ConfigCommand.INTERVAL_ARGUMENT, DoubleArgumentType.doubleArg(0.05))
                                .executes(context -> {
                                    GameSettings gameSettings = RdpEventPlugin.INSTANCE.getGameSettings();
                                    double interval = context.getArgument(ConfigCommand.INTERVAL_ARGUMENT, Double.class);
                                    if (interval < 0.05) {
                                        context.getSource().getSender().sendMessage(Component.text("L'intervalle de drop doit être au moins égal à 0.05 secondes", NamedTextColor.RED));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    gameSettings.setSecondsInterval(interval);
                                    context.getSource().getSender().sendMessage(Component.text("L'intervalle de drop est maintenant de %.2f seconde(s)".formatted(interval), NamedTextColor.GREEN));

                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal(ConfigCommand.DISTANCE_ARGUMENT)
                        .executes(context -> {
                            GameSettings gameSettings = RdpEventPlugin.INSTANCE.getGameSettings();
                            context.getSource().getSender().sendMessage(Component.text("La distance des plateformes est de %d block(s)".formatted(
                                    gameSettings.getSpawnDistance()
                            )));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument(ConfigCommand.DISTANCE_ARGUMENT, IntegerArgumentType.integer(1))
                                .executes(context -> {
                                    GameSettings gameSettings = RdpEventPlugin.INSTANCE.getGameSettings();
                                    int distance = context.getArgument(ConfigCommand.DISTANCE_ARGUMENT, Integer.class);
                                    if (distance <= 0) {
                                        context.getSource().getSender().sendMessage(Component.text("La distance des plateformes doit être au moins égale à 1 block", NamedTextColor.RED));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    gameSettings.setSpawnDistance(distance);
                                    context.getSource().getSender().sendMessage(Component.text("La distance des plateformes est maintenant de %d block(s)".formatted(distance), NamedTextColor.GREEN));

                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal(ConfigCommand.START_ITEMS_ARGUMENT)
                        .executes(context -> {
                            GameSettings gameSettings = RdpEventPlugin.INSTANCE.getGameSettings();
                            context.getSource().getSender().sendMessage(Component.text("Les joueurs recevront %d item(s) au début de la partie".formatted(
                                    gameSettings.getStartItems()
                            )));
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument(ConfigCommand.START_ITEMS_ARGUMENT, IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    GameSettings gameSettings = RdpEventPlugin.INSTANCE.getGameSettings();
                                    int items = context.getArgument(ConfigCommand.START_ITEMS_ARGUMENT, Integer.class);
                                    if (items < 0) {
                                        context.getSource().getSender().sendMessage(Component.text("Le nombre d'items au début de la partie doit être au moins 0", NamedTextColor.RED));
                                        return Command.SINGLE_SUCCESS;
                                    }

                                    gameSettings.setStartItems(items);
                                    context.getSource().getSender().sendMessage(Component.text("Les joueurs recevront maintenant %d item(s) au début de la partie".formatted(items), NamedTextColor.GREEN));

                                    return Command.SINGLE_SUCCESS;
                                })))
                .build();
    }

}
