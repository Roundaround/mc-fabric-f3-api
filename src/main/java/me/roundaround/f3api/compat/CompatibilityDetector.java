package me.roundaround.f3api.compat;

import me.roundaround.f3api.api.BindingRegistry;
import me.roundaround.f3api.api.DebugKeyBinding;
import me.roundaround.f3api.api.Modifier;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.InputUtil;

public final class CompatibilityDetector {
  private static final String BETTERF3 = "betterf3";

  public static void run() {
    BindingRegistry registry = BindingRegistry.getInstance();

    if (FabricLoader.getInstance().isModLoaded(BETTERF3)) {
      registry.registerCompatFallThrough(
          new DebugKeyBinding(
              "betterf3.cycle_renderdistance",
              BETTERF3,
              DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_F),
              DebugKeyBinding.withHelpOutput()));
      registry.registerCompatFallThrough(
          new DebugKeyBinding(
              "betterf3.cycle_renderdistance.invert",
              BETTERF3,
              DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_F, Modifier.SHIFT),
              DebugKeyBinding.withHelpOutput()));
      registry.registerCompatFallThrough(
          new DebugKeyBinding(
              "betterf3.cycle_simulationdistance",
              BETTERF3,
              DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_F, Modifier.CONTROL),
              DebugKeyBinding.withHelpOutput()));
      registry.registerCompatFallThrough(
          new DebugKeyBinding(
              "betterf3.cycle_simulationdistance.invert",
              BETTERF3,
              DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_F, Modifier.CONTROL, Modifier.SHIFT),
              DebugKeyBinding.withHelpOutput()));
      registry.registerCompatFallThrough(
          new DebugKeyBinding(
              "betterf3.modmenu",
              BETTERF3,
              DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_M),
              DebugKeyBinding.withHelpOutput()));
    }
  }

  private CompatibilityDetector() {
  }
}
