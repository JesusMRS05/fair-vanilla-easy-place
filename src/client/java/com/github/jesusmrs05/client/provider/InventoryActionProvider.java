package com.github.jesusmrs05.client.provider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.ContainerInput;

public final class InventoryActionProvider {

    private final Minecraft client;

    public InventoryActionProvider(Minecraft client) {
        this.client = client;
    }

    public boolean selectHotbarSlot(int hotbarSlot) {
        if (client == null
                || client.player == null
                || client.gameMode == null
                || hotbarSlot < 0
                || hotbarSlot > 8) {
            return false;
        }

        LocalPlayer player = client.player;

        if (player.getInventory().getSelectedSlot() == hotbarSlot) {
            return true;
        }

        player.getInventory().setSelectedSlot(hotbarSlot);

        return true;
    }

    public void swapInventorySlotWithHotbar(
            int menuSlot,
            int hotbarSlot
    ) {
        if (client == null
                || client.player == null
                || client.gameMode == null
                || hotbarSlot < 0
                || hotbarSlot > 8) {
            return;
        }

        client.gameMode.handleContainerInput(
                client.player.containerMenu.containerId,
                menuSlot,
                hotbarSlot,
                ContainerInput.SWAP,
                client.player
        );
    }
}