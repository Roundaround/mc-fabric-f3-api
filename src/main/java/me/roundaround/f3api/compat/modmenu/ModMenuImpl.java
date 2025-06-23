package me.roundaround.f3api.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.roundaround.f3api.client.DebugKeyBindingsScreen;
import me.roundaround.gradle.api.annotation.Entrypoint;

@Entrypoint(Entrypoint.MOD_MENU)
public class ModMenuImpl implements ModMenuApi {
  @Override
  public ConfigScreenFactory<?> getModConfigScreenFactory() {
    return DebugKeyBindingsScreen::new;
  }
}
