package com.github.jesusmrs05.client.provider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;

public final class InteractionProvider {

    private final Minecraft client;

    /**
     * True only for the duration of a {@link #useMainHand(BlockHitResult)}
     * call issued by us. UseBlockCallback fires for every attempted block
     * interaction, including this one (since it wraps
     * {@code gameMode.useItemOn} itself), so this flag is how the
     * registered callback tells "our own controlled placement" apart from
     * vanilla's own click-driven attempt and lets it through instead of
     * cancelling it.
     */
    private volatile boolean issuingControlledInteraction = false;

    public InteractionProvider(Minecraft client) {
        this.client = client;
    }

    public boolean isIssuingControlledInteraction() {
        return issuingControlledInteraction;
    }

    public InteractionResult useMainHand(BlockHitResult hitResult) {
        if (client == null
                || client.player == null
                || client.gameMode == null
                || hitResult == null) {
            return InteractionResult.PASS;
        }

        LocalPlayer player = client.player;

        issuingControlledInteraction = true;

        try {
            return client.gameMode.useItemOn(
                    player,
                    InteractionHand.MAIN_HAND,
                    hitResult
            );
        } finally {
            issuingControlledInteraction = false;
        }
    }
}