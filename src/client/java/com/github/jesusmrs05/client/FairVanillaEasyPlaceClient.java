package com.github.jesusmrs05.client;

import com.github.jesusmrs05.client.config.FairVanillaEasyPlaceConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
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
	private static FairVanillaEasyPlaceConfig config;

	@Override
	public void onInitializeClient() {
		config =
				FairVanillaEasyPlaceConfig.load();

		toggleKey = KeyMappingHelper.registerKeyMapping(
				new KeyMapping(
						TOGGLE_KEY,
						InputConstants.Type.KEYSYM,
						GLFW.GLFW_KEY_V,
						KEY_CATEGORY
				)
		);

		controller = new AutoPlaceController(
				Minecraft.getInstance(),
				config
		);

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleKey.consumeClick()) {
				controller.toggle();
			}

			controller.tick();
		});
	}

	public static boolean isPlacementEnabled() {
		return controller != null
				&& controller.isEnabled();
	}

	public static FairVanillaEasyPlaceConfig getConfig() {
		return config;
	}
}