package com.github.jesusmrs05.client.provider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;

public final class InteractionProvider {

    private final Minecraft client;

    public InteractionProvider(Minecraft client) {
        this.client = client;
    }

    public InteractionResult placeBlock(BlockHitResult hitResult) {
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