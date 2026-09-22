package com.github.jesusmrs05.client.mixin;

import com.github.jesusmrs05.client.FairVanillaEasyPlaceClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ExampleClientMixin {
	@Inject(
			method = "startUseItem",
			at = @At("HEAD"),
			cancellable = true
	)
	private void fairVanillaEasyPlace$blockVanillaUse(
			CallbackInfo ci
	) {
		if (FairVanillaEasyPlaceClient.isPlacementEnabled()) {
			ci.cancel();
		}
	}
}