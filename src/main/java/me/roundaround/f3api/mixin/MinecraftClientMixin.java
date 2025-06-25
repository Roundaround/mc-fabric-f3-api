package me.roundaround.f3api.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.f3api.api.BindingRegistry;
import me.roundaround.f3api.compat.CompatibilityDetector;
import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
  @Inject(method = "<init>", at = @At("RETURN"))
  private void onInit(CallbackInfo ci) {
    CompatibilityDetector.run();
    BindingRegistry.getInstance().load();
  }
}
