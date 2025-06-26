package me.roundaround.f3api.compat;

import me.roundaround.f3api.api.BindingRegistry;
import me.roundaround.f3api.api.DebugKeyBinding;
import me.roundaround.f3api.api.Modifier;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.SemanticVersion;
import net.minecraft.client.util.InputUtil;

public final class CompatAutoRegistrar {
  private static final String BETTERF3 = "betterf3";
  private static final String XP_PROGRESS = "experienceprogress";
  private static final SemanticVersion XP_PROGRESS_LEGACY_VERSION = mustParseVersion("1.1.0");

  public static void run() {
    BindingRegistry registry = BindingRegistry.getInstance();
    registerBetterF3(registry);
    registerExperienceProgress(registry);
  }

  // https://modrinth.com/mod/betterf3/versions
  private static void registerBetterF3(BindingRegistry registry) {
    if (!FabricLoader.getInstance().isModLoaded(BETTERF3)) {
      return;
    }

    registry.registerCompatFallThrough(
        new DebugKeyBinding(
            BETTERF3,
            "cycle_renderdistance",
            DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_F),
            DebugKeyBinding.withHelpOutput()));
    registry.registerCompatFallThrough(
        new DebugKeyBinding(
            BETTERF3,
            "cycle_renderdistance.invert",
            DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_F, Modifier.SHIFT),
            DebugKeyBinding.withHelpOutput()));
    registry.registerCompatFallThrough(
        new DebugKeyBinding(
            BETTERF3,
            "cycle_simulationdistance",
            DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_F, Modifier.CONTROL),
            DebugKeyBinding.withHelpOutput()));
    registry.registerCompatFallThrough(
        new DebugKeyBinding(
            BETTERF3,
            "cycle_simulationdistance.invert",
            DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_F, Modifier.CONTROL, Modifier.SHIFT),
            DebugKeyBinding.withHelpOutput()));
    registry.registerCompatFallThrough(
        new DebugKeyBinding(
            BETTERF3,
            "modmenu",
            DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_M),
            DebugKeyBinding.withHelpOutput()));
  }

  // https://modrinth.com/mod/experience-progress
  private static void registerExperienceProgress(BindingRegistry registry) {
    ModContainer mod = FabricLoader.getInstance().getModContainer(XP_PROGRESS).orElse(null);
    if (mod == null) {
      return;
    }

    if (mod.getMetadata().getVersion().compareTo(XP_PROGRESS_LEGACY_VERSION) > 0) {
      return;
    }

    registry.registerCompatFallThrough(
        new DebugKeyBinding(
            XP_PROGRESS,
            "toggle",
            DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_X),
            DebugKeyBinding.withHelpOutput()));
  }

  private static SemanticVersion mustParseVersion(String version) {
    try {
      return SemanticVersion.parse(version);
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse version " + version, e);
    }
  }

  private CompatAutoRegistrar() {
  }
}
