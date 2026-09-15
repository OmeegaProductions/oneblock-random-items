package dev.azn9.op.util;

import org.bukkit.generator.ChunkGenerator;

public class EmptyWorldGenerator extends ChunkGenerator {

    @Override
    public boolean shouldGenerateMobs() {
        return true;
    }

}
