package com.github.jesusmrs05.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
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
	}
}