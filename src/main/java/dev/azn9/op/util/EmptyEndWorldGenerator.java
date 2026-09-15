package dev.azn9.op.util;

import org.bukkit.generator.ChunkGenerator;

public class EmptyEndWorldGenerator extends ChunkGenerator {

    @Override
    public boolean shouldGenerateDecorations() {
        return true;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return true;
    }
}
