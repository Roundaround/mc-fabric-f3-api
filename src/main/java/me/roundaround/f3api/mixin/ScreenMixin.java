package me.roundaround.f3api.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import me.roundaround.f3api.client.F3ApiMod;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Screen.class)
public abstract class ScreenMixin {
  @WrapMethod(method = "hasControlDown")
  private static boolean wrappedHasControlDown(Operation<Boolean> original) {
    if (F3ApiMod.controlSimulated != null) {
      return F3ApiMod.controlSimulated;
    }
    return original.call();
  }

  @WrapMethod(method = "hasShiftDown")
  private static boolean wrappedHasShiftDown(Operation<Boolean> original) {
    if (F3ApiMod.shiftSimulated != null) {
      return F3ApiMod.shiftSimulated;
    }
    return original.call();
  }

  @WrapMethod(method = "hasAltDown")
  private static boolean wrappedHasAltDown(Operation<Boolean> original) {
    if (F3ApiMod.altSimulated != null) {
      return F3ApiMod.altSimulated;
    }
    return original.call();
  }
}
