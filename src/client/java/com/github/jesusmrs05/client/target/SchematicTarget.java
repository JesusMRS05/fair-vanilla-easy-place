package com.github.jesusmrs05.client.target;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;

public record SchematicTarget(
        BlockPos ghostPos,
        BlockHitResult interaction
) {
}