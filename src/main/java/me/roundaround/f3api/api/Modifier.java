package me.roundaround.f3api.api;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public enum Modifier {
  // TODO: Replace i18n keys (vanilla translates to i.e. "Left Shift" rather than just "Shift")
  CONTROL("key.keyboard.left.control", Screen::hasControlDown),
  SHIFT("key.keyboard.left.shift", Screen::hasShiftDown),
  ALT("key.keyboard.left.alt", Screen::hasAltDown);

  private final String translationKey;
  private final Supplier<Boolean> activeSupplier;

  Modifier(String translationKey, Supplier<Boolean> activeSupplier) {
    this.translationKey = translationKey;
    this.activeSupplier = activeSupplier;
  }

  public Text getText() {
    return Text.translatable(this.translationKey);
  }

  public boolean isActive() {
    return this.activeSupplier.get();
  }
}
