package me.roundaround.f3api.client;

import me.roundaround.gradle.api.annotation.Entrypoint;
import net.fabricmc.api.ClientModInitializer;

@Entrypoint(Entrypoint.CLIENT)
public class F3ApiMod implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    // TODO: Add persistence layer
  }
}
