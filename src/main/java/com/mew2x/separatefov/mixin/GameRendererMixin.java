package com.example.separatefov.mixin;

import com.example.separatefov.FovConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	/**
	 * Vanilla computes the final FOV here (slider value, sprint/speed effects, spyglass, underwater, ...).
	 * We rescale the result so that the slider value is shifted by the per-perspective offset,
	 * while all vanilla FOV effects keep working proportionally.
	 */
	@Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
	private void separatefov$applyOffset(Camera camera, float tickProgress, boolean changingFov, CallbackInfoReturnable<Float> cir) {
		int baseFov = this.client.options.getFov().getValue();
		float adjusted = FovConfig.adjust(cir.getReturnValueF(), baseFov, camera.isThirdPerson());
		cir.setReturnValue(adjusted);
	}
}
