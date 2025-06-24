package me.roundaround.f3api.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.roundaround.f3api.generated.Constants;
import me.roundaround.gradle.api.annotation.Entrypoint;
import net.fabricmc.api.ClientModInitializer;

@Entrypoint(Entrypoint.CLIENT)
public class F3ApiMod implements ClientModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_ID);

  @Override
  public void onInitializeClient() {
    // TODO: Add persistence layer
  }
}
