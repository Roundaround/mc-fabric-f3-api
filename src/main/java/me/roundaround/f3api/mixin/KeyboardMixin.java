package me.roundaround.f3api.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import me.roundaround.f3api.api.DebugKeyBinding;
import me.roundaround.f3api.api.DebugKeyBindings;
import me.roundaround.f3api.client.KeyboardExtensions;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin implements KeyboardExtensions {
  @Shadow
  @Final
  private MinecraftClient client;

  @Shadow
  private long debugCrashStartTime;

  @Shadow
  protected abstract boolean processF3(int key);

  @Override
  public boolean f3api$processVanillaF3(int code) {
    return this.processF3(code);
  }

  @WrapOperation(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Keyboard;processF3(I)Z"))
  private boolean rerouteProcessF3(Keyboard instance, int code, Operation<Boolean> original) {
    // Vanilla debug crash start time check
    if (this.debugCrashStartTime > 0L && this.debugCrashStartTime < Util.getMeasuringTimeMs() - 100L) {
      return original.call(instance, code);
    }
    return this.altProcessF3(code);
  }

  @Unique
  private boolean altProcessF3(int code) {
    DebugKeyBindings bindings = DebugKeyBindings.getInstance();
    for (DebugKeyBinding binding : bindings.getAllKeyBindings()) {
      if (binding.matches(code)) {
        if (bindings.getPressAction(binding).run(this.client)) {
          return true;
        }
      }
    }
    return false;
  }
}
