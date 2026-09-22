package com.github.jesusmrs05.client;

import com.github.jesusmrs05.client.provider.BlockPlacementProvider;
import com.github.jesusmrs05.client.provider.InputProvider;
import com.github.jesusmrs05.client.provider.InteractionProvider;
import com.github.jesusmrs05.client.provider.InventoryActionProvider;
import com.github.jesusmrs05.client.provider.InventoryProvider;
import com.github.jesusmrs05.client.provider.LitematicaProvider;
import com.github.jesusmrs05.client.provider.PlacementStateProvider;
import com.github.jesusmrs05.client.provider.PlayerProvider;
import com.github.jesusmrs05.client.provider.ReachProvider;
import com.github.jesusmrs05.client.provider.TargetProvider;
import com.github.jesusmrs05.client.provider.WorldProvider;
import com.github.jesusmrs05.client.target.SchematicTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Ties the providers together into the automated-but-vanilla-bound
 * placement flow described by the mod objective:
 *
 * 1. Litematica provides the intended block state for a schematic position.
 * 2. We observe the real world and the direction the player is looking.
 * 3. The player must physically aim at the construction area, within reach.
 * 4. We determine which schematic ghost block is relevant along that line of sight.
 * 5. We check that the player has the required block in their inventory.
 * 6. We can select the required block in the hotbar.
 * 7. We perform the equivalent of a normal vanilla right-click against the
 *    real block the player is looking at.
 * 8. Minecraft/the server remains responsible for deciding whether the
 *    placement is actually valid.
 *
 * This class never bypasses reach, line of sight, or vanilla placement
 * rules — it only removes the tedium of repeating the same right-click
 * once per ghost block. If the player isn't aiming at a valid ghost
 * block within normal reach, nothing happens.
 */
public final class AutoPlaceController {

    private final Minecraft client;

    private final InputProvider inputProvider;
    private final LitematicaProvider litematicaProvider;
    private final WorldProvider worldProvider;
    private final PlacementStateProvider placementStateProvider;
    private final ReachProvider reachProvider;
    private final TargetProvider targetProvider;
    private final InventoryActionProvider inventoryActionProvider;
    private final InteractionProvider interactionProvider;
    private final BlockPlacementProvider blockPlacementProvider;

    private boolean enabled = false;

    public AutoPlaceController(Minecraft client) {
        this.client = client;

        this.inputProvider = new InputProvider(client);
        this.litematicaProvider = new LitematicaProvider(client);
        this.worldProvider = new WorldProvider(client);
        this.placementStateProvider =
                new PlacementStateProvider(litematicaProvider, worldProvider);
        this.reachProvider = new ReachProvider(client);
        this.targetProvider =
                new TargetProvider(client, litematicaProvider, reachProvider);
        this.inventoryActionProvider = new InventoryActionProvider(client);
        this.interactionProvider = new InteractionProvider(client);
        this.blockPlacementProvider =
                new BlockPlacementProvider(interactionProvider);
    }

    public void toggle() {
        enabled = !enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * True only while this controller is in the middle of its own,
     * already-checked call to {@code InteractionProvider.useMainHand}.
     * The {@code UseBlockCallback} registered in the client entrypoint
     * uses this to let our own placement through while cancelling every
     * other (i.e. vanilla click-driven) block interaction attempt.
     */


    /**
     * Call once per client tick. While enabled, this method is the sole
     * source of real placements: a {@code UseBlockCallback} registered in
     * the client entrypoint cancels every right-click-on-block attempt
     * that doesn't come from {@link #()},
     * so vanilla's own click-driven placement never goes through on its
     * own — only a placement that has passed every check below
     * (target, ghost state, inventory, selection) does.
     */
    public void tick() {
        if (!enabled) {
            return;
        }

        if (client == null || client.player == null || client.level == null) {
            return;
        }

        if (!inputProvider.isUsePressed()) {
            return;
        }

        // Steps 2-4: find the furthest relevant ghost block along the
        // player's real line of sight, within normal reach.
        SchematicTarget target = targetProvider.findTarget();

        if (target == null) {
            return;
        }

        BlockPos ghostPos = target.ghostPos();

        // Only proceed if that position genuinely differs from the real
        // world right now (i.e. it still needs placing).
        if (!placementStateProvider.isGhost(ghostPos)) {
            return;
        }

        BlockState schematicState =
                litematicaProvider.getSchematicBlockState(ghostPos);

        if (schematicState == null || schematicState.isAir()) {
            return;
        }

        Item requiredItem = schematicState.getBlock().asItem();

        if (requiredItem == null || requiredItem == Items.AIR) {
            // No sensible item maps to this block state (e.g. fluids,
            // fire, or other non-placeable states) — nothing we can do.
            return;
        }

        // Step 5: confirm the player actually has the block.
        InventoryProvider inventoryProvider =
                new InventoryProvider(client.player);

        if (!inventoryProvider.contains(requiredItem)) {
            return;
        }

        // Step 6: make sure the required block is the one in hand.
        if (!inventoryProvider.isSelected(requiredItem)) {
            selectRequiredItem(inventoryProvider, requiredItem);
            // Give the client a tick to catch up with the selection
            // change before attempting to place with it.
            return;
        }

        // Step 7: perform the equivalent of a normal vanilla right-click
        // against the real block the player is looking at. Whether this
        // succeeds is entirely up to InteractionProvider/gameMode, i.e.
        // normal vanilla survival rules (step 8).
        BlockHitResult hitResult = target.interaction();

        blockPlacementProvider.place(hitResult);
    }

    private void selectRequiredItem(
            InventoryProvider inventoryProvider,
            Item requiredItem
    ) {
        int slot = inventoryProvider.findSlot(requiredItem);

        if (slot < 0) {
            return;
        }

        if (slot <= 8) {
            inventoryActionProvider.selectHotbarSlot(slot);
            return;
        }

        // The block is in the main inventory: swap it into the currently
        // selected hotbar slot so it becomes the held item.
        int currentHotbarSlot = inventoryProvider.getSelectedSlot();

        if (currentHotbarSlot < 0) {
            return;
        }

        inventoryActionProvider.swapInventorySlotWithHotbar(
                slot,
                currentHotbarSlot
        );
    }
}