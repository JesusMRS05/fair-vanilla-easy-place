package com.github.jesusmrs05.client.provider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class PlayerProvider {

    private final Minecraft client;

    public PlayerProvider(Minecraft client) {
        this.client = client;
    }

    public LocalPlayer getPlayer() {
        if (client == null) {
            return null;
        }

        return client.player;
    }

    public Vec3 getEyePosition(float partialTick) {
        if (client == null || client.player == null) {
            return null;
        }

        return client.player.getEyePosition(partialTick);
    }

    public BlockPos getBlockPosition() {
        if (client == null || client.player == null) {
            return null;
        }

        return client.player.blockPosition();
    }

    public ItemStack getMainHandItem() {
        if (client == null || client.player == null) {
            return ItemStack.EMPTY;
        }

        return client.player.getItemInHand(InteractionHand.MAIN_HAND);
    }

    public InteractionHand getMainHand() {
        return InteractionHand.MAIN_HAND;
    }
}