package me.roundaround.f3api.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.roundaround.f3api.client.DebugKeyBindingsScreen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Mixin(ControlsOptionsScreen.class)
public abstract class ControlsOptionsScreenMixin extends GameOptionsScreen {
  @Inject(method = "addOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/OptionListWidget;addWidgetEntry(Lnet/minecraft/client/gui/widget/ClickableWidget;Lnet/minecraft/client/gui/widget/ClickableWidget;)V", shift = At.Shift.AFTER))
  private void addDebugKeyBindingsButton(CallbackInfo ci) {
    // TODO: i18n
    this.body.addWidgetEntry(ButtonWidget.builder(
        Text.of("Debug Key Binds..."),
        (button) -> {
          this.client.setScreen(new DebugKeyBindingsScreen(this));
        }).build(),
        null);
  }

  private ControlsOptionsScreenMixin() {
    super(null, null, null);
  }
}
