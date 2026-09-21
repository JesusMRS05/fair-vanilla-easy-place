package com.github.jesusmrs05.client.litematica;

import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class LitematicaBridge {

    private final Minecraft client;

    public LitematicaBridge(Minecraft client) {
        this.client = client;
    }

    /**
     * Returns the block state that Litematica currently has at the given
     * world-space position in its schematic world.
     *
     * This uses Litematica's transformed schematic world, so placement
     * origin, rotation, mirroring, and sub-region transforms are already
     * handled by Litematica itself.
     */
    public BlockState getSchematicBlockState(BlockPos worldPos) {
        if (client == null
                || client.level == null
                || worldPos == null) {
            return null;
        }

        WorldSchematic schematicWorld =
                SchematicWorldHandler.getSchematicWorld();

        if (schematicWorld == null) {
            return null;
        }

        return schematicWorld.getBlockState(worldPos);
    }

    /**
     * Returns true when the schematic contains a non-air block at the
     * given position and the real world does not already contain that
     * exact block state.
     */
    public boolean isSchematicGhost(BlockPos worldPos) {
        if (client == null
                || client.level == null
                || worldPos == null) {
            return false;
        }

        BlockState schematicState =
                getSchematicBlockState(worldPos);

        if (schematicState == null || schematicState.isAir()) {
            return false;
        }

        BlockState worldState =
                client.level.getBlockState(worldPos);

        return !worldState.equals(schematicState);
    }
}