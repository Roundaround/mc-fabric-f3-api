package me.roundaround.f3api.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import me.roundaround.f3api.api.DebugKeyBinding;
import me.roundaround.f3api.api.BindingRegistry;
import me.roundaround.f3api.client.KeyboardExtensions;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin implements KeyboardExtensions {
  @Unique
  private BindingRegistry.Messager messager;

  @Shadow
  @Final
  private MinecraftClient client;

  @Shadow
  private long debugCrashStartTime;

  @Shadow
  protected abstract boolean processF3(int key);

  @Shadow
  protected abstract void debugError(Text message);

  @Shadow
  protected abstract void debugLog(Text text);

  @Shadow
  protected abstract void debugLog(String key);

  @Shadow
  protected abstract void debugFormattedLog(String pattern, Object... args);

  @Shadow
  protected abstract void sendMessage(Text message);

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

  @ModifyExpressionValue(
      method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;isKeyPressed(JI)Z")
  )
  private boolean rerouteIsKeyPressed(
      boolean original,
      long handle,
      int code,
      @Local(argsOnly = true, ordinal = 0) int keyCode
  ) {
    if (code != InputUtil.GLFW_KEY_C) {
      return original;
    }

    return BindingRegistry.getInstance().copyLocation.matches(keyCode);
  }

  @Unique
  private boolean altProcessF3(int code) {
    BindingRegistry bindings = BindingRegistry.getInstance();
    for (DebugKeyBinding binding : bindings.getAllKeyBindings()) {
      if (binding.matches(code)) {
        if (bindings.getPressAction(binding).run(this.client, this.getMessager())) {
          return true;
        }
      }
    }
    return false;
  }

  @Unique
  private BindingRegistry.Messager getMessager() {
    if (this.messager != null) {
      return this.messager;
    }

    this.messager = new BindingRegistry.Messager() {
      @Override
      public void debugMessage(Text text) {
        KeyboardMixin.this.debugLog(text);
      }

      @Override
      public void debugMessage(String key) {
        KeyboardMixin.this.debugLog(key);
      }

      @Override
      public void debugError(Text text) {
        KeyboardMixin.this.debugError(text);
      }

      @Override
      public void debugFormatted(String pattern, Object... args) {
        KeyboardMixin.this.debugFormattedLog(pattern, args);
      }

      @Override
      public void sendMessage(Text text) {
        KeyboardMixin.this.sendMessage(text);
      }
    };
    return this.messager;
  }
}
