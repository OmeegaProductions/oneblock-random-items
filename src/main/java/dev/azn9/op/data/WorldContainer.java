package dev.azn9.op.data;

import org.bukkit.World;

import java.util.function.Consumer;

public record WorldContainer(
        World overworld,
        World nether,
        World end
) {
    public void apply(Consumer<World> action) {
        action.accept(this.overworld);
        action.accept(this.nether);
        action.accept(this.end);
    }
}
