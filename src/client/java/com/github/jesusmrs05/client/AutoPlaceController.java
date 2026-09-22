package com.github.jesusmrs05.client;

import com.github.jesusmrs05.client.provider.BlockPlacementProvider;
import com.github.jesusmrs05.client.provider.InputProvider;
import com.github.jesusmrs05.client.provider.InteractionProvider;
import com.github.jesusmrs05.client.provider.InventoryActionProvider;
import com.github.jesusmrs05.client.provider.InventoryProvider;
import com.github.jesusmrs05.client.provider.LitematicaProvider;
import com.github.jesusmrs05.client.provider.PlacementStateProvider;
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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Connects the providers into the automated-but-vanilla-bound
 * placement flow.
 */
public final class AutoPlaceController {

    private static final Item SCAFFOLD_ITEM = Items.OAK_PLANKS;

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

        this.inputProvider =
                new InputProvider(client);

        this.litematicaProvider =
                new LitematicaProvider(client);

        this.worldProvider =
                new WorldProvider(client);

        this.placementStateProvider =
                new PlacementStateProvider(
                        litematicaProvider,
                        worldProvider
                );

        this.reachProvider =
                new ReachProvider(client);

        this.targetProvider =
                new TargetProvider(
                        client,
                        placementStateProvider,
                        reachProvider
                );

        this.inventoryActionProvider =
                new InventoryActionProvider(client);

        this.interactionProvider =
                new InteractionProvider(client);

        this.blockPlacementProvider =
                new BlockPlacementProvider(
                        interactionProvider
                );
    }

    public void toggle() {
        enabled = !enabled;

        Component message = Component.literal(
                        "Fair Vanilla Easy Place "
                ).withStyle(ChatFormatting.WHITE)
                .append(
                        Component.literal(enabled ? "ON" : "OFF")
                                .withStyle(
                                        ChatFormatting.BOLD,
                                        enabled
                                                ? ChatFormatting.GREEN
                                                : ChatFormatting.RED
                                )
                );

        if (client != null && client.player != null) {
            client.player.sendSystemMessage(message);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void tick() {
        if (!enabled) {
            return;
        }

        if (client == null
                || client.player == null
                || client.level == null) {
            return;
        }

        if (!inputProvider.isUsePressed()) {
            return;
        }

        SchematicTarget target =
                targetProvider.findTarget();

        if (target == null) {
            return;
        }

        BlockPos ghostPos =
                target.ghostPos();

        if (!placementStateProvider.isGhost(ghostPos)) {
            return;
        }

        BlockHitResult hitResult =
                target.interaction();

        BlockPos placementPos =
                hitResult.getBlockPos()
                        .relative(hitResult.getDirection());

        Item requiredItem;

        /*
         * Direct placement:
         *
         * The vanilla interaction would place the block
         * directly in the schematic ghost position.
         */
        if (placementPos.equals(ghostPos)) {
            BlockState schematicState =
                    litematicaProvider.getSchematicBlockState(
                            ghostPos
                    );

            if (schematicState == null
                    || schematicState.isAir()) {
                return;
            }

            requiredItem =
                    schematicState.getBlock().asItem();

            if (requiredItem == null
                    || requiredItem == Items.AIR) {
                return;
            }
        } else {
            /*
             * Scaffold placement:
             *
             * The real vanilla interaction would place a block
             * somewhere before the schematic ghost. For now we
             * deliberately use oak planks as the scaffold block.
             */
            requiredItem = SCAFFOLD_ITEM;
        }

        InventoryProvider inventoryProvider =
                new InventoryProvider(client.player);

        /*
         * The required item must exist in the inventory.
         */
        if (!inventoryProvider.contains(requiredItem)) {
            return;
        }

        /*
         * If the required item is not currently held, select/swap it
         * and stop here. The actual placement happens on the next tick.
         */
        if (!inventoryProvider.isSelected(requiredItem)) {
            selectRequiredItem(
                    inventoryProvider,
                    requiredItem
            );

            return;
        }

        /*
         * The actual interaction is still completely vanilla.
         */
        blockPlacementProvider.place(hitResult);
    }

    private void selectRequiredItem(
            InventoryProvider inventoryProvider,
            Item requiredItem
    ) {
        int slot =
                inventoryProvider.findSlot(requiredItem);

        if (slot < 0) {
            return;
        }

        if (slot <= 8) {
            inventoryActionProvider.selectHotbarSlot(slot);
            return;
        }

        int currentHotbarSlot =
                inventoryProvider.getSelectedSlot();

        if (currentHotbarSlot < 0) {
            return;
        }

        inventoryActionProvider.swapInventorySlotWithHotbar(
                slot,
                currentHotbarSlot
        );
    }
}