package com.github.jesusmrs05.client.placement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public final class MaxReachProvider {

    private static final double FALLBACK_REACH = 4.5D;

    private MaxReachProvider() {
    }

    public static double get(Minecraft client) {
        if (client == null || client.player == null) {
            return FALLBACK_REACH;
        }

        LocalPlayer player = client.player;

        return player.blockInteractionRange();
    }
}