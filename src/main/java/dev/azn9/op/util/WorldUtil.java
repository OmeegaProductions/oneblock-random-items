package dev.azn9.op.util;

import io.papermc.paper.math.Position;
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

    public static World generateWorld() {
        return new WorldCreator("game-" + UUID.randomUUID())
                .hardcore(true)
                .generator(new EmptyWorldGenerator())
                .forcedSpawnPosition(Position.block(0, 192, 0), 0, 0)
                .createWorld();
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
