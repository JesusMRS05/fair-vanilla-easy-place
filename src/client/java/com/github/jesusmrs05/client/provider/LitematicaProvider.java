package com.github.jesusmrs05.client.provider;

import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class LitematicaProvider {

    private final Minecraft client;

    public LitematicaProvider(Minecraft client) {
        this.client = client;
    }

    public BlockState getSchematicBlockState(BlockPos worldPos) {
        if (client == null || worldPos == null) {
            return null;
        }

        WorldSchematic schematicWorld =
                SchematicWorldHandler.getSchematicWorld();

        if (schematicWorld == null) {
            return null;
        }

        return schematicWorld.getBlockState(worldPos);
    }

    public boolean hasSchematicBlock(BlockPos worldPos) {
        BlockState state = getSchematicBlockState(worldPos);

        return state != null && !state.isAir();
    }
}