package com.github.jesusmrs05.client;

import com.github.jesusmrs05.client.inventory.InventorySelector;
import com.github.jesusmrs05.client.litematica.LitematicaBridge;
import com.github.jesusmrs05.client.placement.VanillaPlacer;
import com.github.jesusmrs05.client.target.SchematicTarget;
import com.github.jesusmrs05.client.target.SchematicTargetFinder;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.glfw.GLFW;

public final class FairVanillaEasyPlaceClient
		implements ClientModInitializer {

	private static final String TOGGLE_KEY =
			"key.fair-vanilla-easy-place.toggle";

	private static final KeyMapping.Category KEY_CATEGORY =
			KeyMapping.Category.register(
					Identifier.fromNamespaceAndPath(
							"fair-vanilla-easy-place",
							"general"
					)
			);

	private static KeyMapping toggleKey;

	private static boolean enabled;

	@Override
	public void onInitializeClient() {
		toggleKey = KeyMappingHelper.registerKeyMapping(
				new KeyMapping(
						TOGGLE_KEY,
						InputConstants.Type.KEYSYM,
						GLFW.GLFW_KEY_V,
						KEY_CATEGORY
				)
		);

		ClientTickEvents.END_CLIENT_TICK.register(
				FairVanillaEasyPlaceClient::onClientTick
		);
	}

	private static void onClientTick(Minecraft client) {
		while (toggleKey.consumeClick()) {
			enabled = !enabled;
		}

		if (!enabled
				|| client.level == null
				|| client.player == null
				|| client.gameMode == null) {
			return;
		}

		LitematicaBridge bridge =
				new LitematicaBridge(client);

		SchematicTarget target =
				SchematicTargetFinder.findTarget(
						client,
						bridge
				);

		if (target == null) {
			return;
		}

		BlockState requiredState =
				bridge.getSchematicBlockState(
						target.ghostPos()
				);

		if (requiredState == null
				|| requiredState.isAir()) {
			return;
		}

		if (!InventorySelector.selectRequiredBlock(
				client.player,
				requiredState
		)) {
			return;
		}

		InteractionResult result =
				VanillaPlacer.interact(
						client,
						target.interaction()
				);

		/*
		 * Vanilla remains authoritative here.
		 *
		 * We don't place the schematic block ourselves and we don't
		 * fabricate a hit result at the ghost position.
		 */
	}

	public static boolean isEnabled() {
		return enabled;
	}
}