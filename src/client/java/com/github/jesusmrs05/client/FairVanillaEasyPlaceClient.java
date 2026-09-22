package com.github.jesusmrs05.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
	private static AutoPlaceController controller;

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

		controller = new AutoPlaceController(Minecraft.getInstance());

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleKey.consumeClick()) {
				controller.toggle();
			}

			controller.tick();
		});

		// While the mod is enabled, this is the sole gatekeeper for real
		// block placement: any right-click-on-block attempt that did not
		// come from the controller's own checked flow is cancelled here,
		// so vanilla's own click-driven placement never fires on its own.
		// The controller's automated placement also goes through
		// gameMode.useItemOn (and therefore this same callback), so it is
		// explicitly let through via isIssuingControlledInteraction().
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (controller == null || !controller.isEnabled()) {
				return InteractionResult.PASS;
			}

			if (hand != InteractionHand.MAIN_HAND) {
				return InteractionResult.PASS;
			}

			if (controller.isIssuingControlledInteraction()) {
				return InteractionResult.PASS;
			}

			return InteractionResult.FAIL;
		});
	}
}