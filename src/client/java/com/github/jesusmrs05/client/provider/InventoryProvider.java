package com.github.jesusmrs05.client.provider;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class InventoryProvider {

    private final LocalPlayer player;

    public InventoryProvider(LocalPlayer player) {
        this.player = player;
    }

    public boolean contains(Item item) {
        return findSlot(item) >= 0;
    }

    public int findSlot(Item item) {
        if (player == null || item == null) {
            return -1;
        }

        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);

            if (!stack.isEmpty() && stack.is(item)) {
                return slot;
            }
        }

        return -1;
    }

    public boolean isSelected(Item item) {
        if (player == null || item == null) {
            return false;
        }

        ItemStack selected =
                player.getInventory().getSelectedItem();

        return !selected.isEmpty() && selected.is(item);
    }

    public ItemStack getSelectedStack() {
        if (player == null) {
            return ItemStack.EMPTY;
        }

        return player.getInventory().getSelectedItem();
    }

    public int getSelectedSlot() {
        if (player == null) {
            return -1;
        }

        return player.getInventory().getSelectedSlot();
    }
}