package dev.azn9.op.util;

import dev.azn9.op.data.WorldContainer;
import io.papermc.paper.math.Position;
import org.bukkit.GameRules;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorldUtil {

    private WorldUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static WorldContainer generateWorld() {
        String worldName = "game-" + UUID.randomUUID();
        WorldContainer container = new WorldContainer(
                new WorldCreator(worldName)
                        .generator(new EmptyWorldGenerator())
                        .environment(World.Environment.NORMAL)
                        .forcedSpawnPosition(Position.block(0, 192, 0), 0, 0)
                        .createWorld(),
                new WorldCreator(worldName + "_nether")
                        .generator(new EmptyWorldGenerator())
                        .environment(World.Environment.NETHER)
                        .createWorld(),
                new WorldCreator(worldName + "_the_end")
                        .generator(new EmptyEndWorldGenerator())
                        .environment(World.Environment.THE_END)
                        .createWorld()
        );

        container.overworld().setGameRule(GameRules.KEEP_INVENTORY, true);
        container.nether().setGameRule(GameRules.KEEP_INVENTORY, true);
        container.end().setGameRule(GameRules.KEEP_INVENTORY, true);

        return container;
    }

    public static List<Location> generateSpawnPoints(World world, int count, double distance) {
        double circleRadius = MathUtil.getCircleRadius(count, distance);
        if (count == 1) {
            circleRadius = 0.1;
        }

        List<Location> locations = new ArrayList<>();

        double angle = Math.toRadians(360D / count);

        for (int i = 0; i < count; i++) {
            Location location = new Location(
                    world,
                    Math.cos(angle * i) * circleRadius,
                    192,
                    Math.sin(angle * i) * circleRadius
            );
            locations.add(location);

            location.setDirection(location.toVector().clone().multiply(-1).setY(0));
            location.getBlock().setType(Material.BEDROCK);
            location.add(0.5, 1.5, 0.5);
        }

        return locations;
    }

}
