package com.github.jesusmrs05.client.provider;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public final class InputProvider {

    private final Minecraft client;

    public InputProvider(Minecraft client) {
        this.client = client;
    }

    public boolean isUsePressed() {
        if (client == null || client.options == null) {
            return false;
        }

        KeyMapping use = client.options.keyUse;

        return use.isDown();
    }
}