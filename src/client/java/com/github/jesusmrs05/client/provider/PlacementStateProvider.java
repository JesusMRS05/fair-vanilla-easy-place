package com.github.jesusmrs05.client.provider;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class PlacementStateProvider {

    private final LitematicaProvider litematicaProvider;
    private final WorldProvider worldProvider;

    public PlacementStateProvider(
            LitematicaProvider litematicaProvider,
            WorldProvider worldProvider
    ) {
        this.litematicaProvider = litematicaProvider;
        this.worldProvider = worldProvider;
    }

    public boolean matchesSchematic(BlockPos pos) {
        if (pos == null) {
            return false;
        }

        BlockState schematic =
                litematicaProvider.getSchematicBlockState(pos);

        BlockState world =
                worldProvider.getBlockState(pos);

        if (schematic == null || world == null) {
            return false;
        }

        return world.equals(schematic);
    }

    public boolean isGhost(BlockPos pos) {
        if (pos == null) {
            return false;
        }

        BlockState schematic =
                litematicaProvider.getSchematicBlockState(pos);

        BlockState world =
                worldProvider.getBlockState(pos);

        if (schematic == null || schematic.isAir() || world == null) {
            return false;
        }

        return !world.equals(schematic);
    }
}