package com.github.jesusmrs05.client.provider;

import net.minecraft.client.Minecraft;

public final class ReachProvider {

    private static final double FALLBACK_REACH = 4.5D;

    private final Minecraft client;

    public ReachProvider(Minecraft client) {
        this.client = client;
    }

    public double getBlockInteractionRange() {
        if (client == null || client.player == null) {
            return FALLBACK_REACH;
        }

        return client.player.blockInteractionRange();
    }
}