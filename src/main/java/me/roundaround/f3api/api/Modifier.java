package me.roundaround.f3api.api;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Locale;
import java.util.function.Supplier;

public enum Modifier {
  CONTROL("control", Screen::hasControlDown),
  SHIFT("shift", Screen::hasShiftDown),
  ALT("alt", Screen::hasAltDown);

  private final String id;
  private final String translationKey;
  private final Supplier<Boolean> activeSupplier;

  Modifier(String id, Supplier<Boolean> activeSupplier) {
    this.id = id;
    this.translationKey = "f3api.modifier." + id;
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
    return switch (id) {
      case "control" -> CONTROL;
      case "shift" -> SHIFT;
      case "alt" -> ALT;
      default -> throw new IllegalArgumentException("Unknown modifier: " + id);
    };
  }
}
