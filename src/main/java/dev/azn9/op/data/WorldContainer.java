package dev.azn9.op.data;

import org.bukkit.World;

public record WorldContainer(
        World overworld,
        World nether,
        World end
) {
}
