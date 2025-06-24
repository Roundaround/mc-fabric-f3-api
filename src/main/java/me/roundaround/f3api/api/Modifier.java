package me.roundaround.f3api.api;

import java.util.Locale;
import java.util.function.Supplier;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public enum Modifier {
  CONTROL("control", Screen::hasControlDown),
  SHIFT("shift", Screen::hasShiftDown),
  ALT("alt", Screen::hasAltDown);

  private final String id;
  private final String translationKey;
  private final Supplier<Boolean> activeSupplier;

  Modifier(String id, Supplier<Boolean> activeSupplier) {
    this.id = id;
    // TODO: Replace (vanilla translates to "Left Shift" rather than just "Shift")
    this.translationKey = "key.keyboard.left." + id;
    this.activeSupplier = activeSupplier;
  }

  public String getId() {
    return this.id;
  }

  public Text getText() {
    return Text.translatable(this.translationKey);
  }

  public boolean isActive() {
    return this.activeSupplier.get();
  }

  public static Modifier fromId(String id) {
    id = id.toLowerCase(Locale.ROOT).trim();
    switch (id) {
      case "control":
        return CONTROL;
      case "shift":
        return SHIFT;
      case "alt":
        return ALT;
    }
    throw new IllegalArgumentException("Unknown modifier: " + id);
  }
}
