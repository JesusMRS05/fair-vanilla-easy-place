package com.github.jesusmrs05.client.inventory;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public final class InventorySelector {

    private InventorySelector() {
    }

    /**
     * Finds the item corresponding to the required schematic block
     * anywhere in the player's inventory.
     *
     * If the item is in the hotbar, that slot is selected.
     *
     * If the item is in the main inventory, it is swapped with the
     * currently selected hotbar slot so the required item becomes
     * the item in the player's hand.
     *
     * @return true if the required item was found and equipped
     */
    public static boolean selectRequiredBlock(
            LocalPlayer player,
            BlockState requiredState
    ) {
        if (player == null || requiredState == null || requiredState.isAir()) {
            return false;
        }

        Item requiredItem = requiredState.getBlock().asItem();

        if (requiredItem == null) {
            return false;
        }

        int selectedSlot = player.getInventory().getSelectedSlot();

        // First search the entire hotbar.
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);

            if (stack.is(requiredItem)) {
                player.getInventory().setSelectedSlot(slot);
                return true;
            }
        }

        // Then search the main inventory.
        for (int slot = 9; slot < 36; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);

            if (stack.is(requiredItem)) {
                // Swap the required block with whatever the player
                // currently has selected in the hotbar.
                ItemStack selectedStack =
                        player.getInventory().getItem(selectedSlot);

                player.getInventory().setItem(selectedSlot, stack);
                player.getInventory().setItem(slot, selectedStack);

                return true;
            }
        }

        return false;
    }
}