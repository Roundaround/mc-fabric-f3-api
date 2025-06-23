package me.roundaround.f3api.client;

public interface KeyboardExtensions {
  default boolean f3api$processVanillaF3(int code) {
    return false;
  }
}
