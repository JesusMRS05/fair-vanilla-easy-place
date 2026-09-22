package com.github.jesusmrs05.client.provider;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class WorldProvider {

    private final Minecraft client;

    public WorldProvider(Minecraft client) {
        this.client = client;
    }

    public BlockState getBlockState(BlockPos pos) {
        if (client == null || client.level == null || pos == null) {
            return null;
        }

        return client.level.getBlockState(pos);
    }

    public boolean isAir(BlockPos pos) {
        BlockState state = getBlockState(pos);

        return state == null || state.isAir();
    }
}