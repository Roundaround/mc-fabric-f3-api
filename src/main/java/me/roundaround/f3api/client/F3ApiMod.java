package me.roundaround.f3api.client;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.roundaround.f3api.api.Modifier;
import me.roundaround.f3api.generated.Constants;
import me.roundaround.gradle.api.annotation.Entrypoint;
import net.fabricmc.api.ClientModInitializer;

@Entrypoint(Entrypoint.CLIENT)
public class F3ApiMod implements ClientModInitializer {
  public static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_ID);

  public static Boolean controlSimulated = null;
  public static Boolean shiftSimulated = null;
  public static Boolean altSimulated = null;

  @Override
  public void onInitializeClient() {
  }

  public static void simulateModifiers(Collection<Modifier> modifiers) {
    controlSimulated = false;
    shiftSimulated = false;
    altSimulated = false;

    for (Modifier modifier : modifiers) {
      switch (modifier) {
        case CONTROL -> controlSimulated = true;
        case SHIFT -> shiftSimulated = true;
        case ALT -> altSimulated = true;
      }
    }
  }

  public static void clearSimulatedModifiers() {
    controlSimulated = null;
    shiftSimulated = null;
    altSimulated = null;
  }
}
