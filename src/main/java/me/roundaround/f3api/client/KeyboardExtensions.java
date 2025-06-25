package me.roundaround.f3api.client;

import me.roundaround.f3api.api.Modifier;
import net.minecraft.client.util.InputUtil;

import java.util.Arrays;
import java.util.Collection;

public interface KeyboardExtensions {
  default boolean f3api$processVanillaF3(InputUtil.Key key, Modifier... modifiers) {
    return this.f3api$processVanillaF3(key.getCode(), Arrays.asList(modifiers));
  }

  default boolean f3api$processVanillaF3(InputUtil.Key key, Collection<Modifier> modifiers) {
    return this.f3api$processVanillaF3(key.getCode(), modifiers);
  }

  default boolean f3api$processVanillaF3(int code, Modifier... modifiers) {
    return this.f3api$processVanillaF3(code, Arrays.asList(modifiers));
  }

  default boolean f3api$processVanillaF3(int code, Collection<Modifier> modifiers) {
    return false;
  }
}
