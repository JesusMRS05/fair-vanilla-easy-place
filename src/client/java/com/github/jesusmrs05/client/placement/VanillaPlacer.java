package com.github.jesusmrs05.client.placement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;

public final class VanillaPlacer {

    private VanillaPlacer() {
    }

    /**
     * Performs one normal vanilla block interaction.
     *
     * The supplied hit result must come from the player's real
     * world raycast. Vanilla remains responsible for deciding
     * whether the placement/use is valid.
     */
    public static InteractionResult interact(
            Minecraft client,
            BlockHitResult hitResult
    ) {
        if (client == null
                || client.player == null
                || client.gameMode == null
                || hitResult == null) {
            return InteractionResult.PASS;
        }

        LocalPlayer player = client.player;

        return client.gameMode.useItemOn(
                player,
                InteractionHand.MAIN_HAND,
                hitResult
        );
    }
}