package net.minestom.server.instance;

import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationRequest;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import org.jetbrains.annotations.NotNull;

/**
 * Provides full compatibility for the deprecated {@link ChunkGenerator}
 */
record ChunkGeneratorCompatibilityLayer(@NotNull ChunkGenerator chunkGenerator) implements Generator {
    @Override
    public void generate(@NotNull GenerationRequest request) {
        GenerationUnit chunk = request.unit();
        ChunkBatch batch = new ChunkBatch() {
            @Override
            public void setBlock(int x, int y, int z, @NotNull Block block) {
                chunk.modifier().setBlock(x, y, z, block);
            }
        };
        chunkGenerator.generateChunkData(batch, -999, -999);
    }
}
