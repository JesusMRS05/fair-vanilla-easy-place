package com.github.jesusmrs05.client.provider;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;

public final class BlockPlacementProvider {

    private final InteractionProvider interactionProvider;

    public BlockPlacementProvider(
            InteractionProvider interactionProvider
    ) {
        this.interactionProvider = interactionProvider;
    }

    public InteractionResult place(BlockHitResult hitResult) {
        return interactionProvider.placeBlock(hitResult);
    }
}