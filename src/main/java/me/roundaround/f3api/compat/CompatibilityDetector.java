package me.roundaround.f3api.compat;

import me.roundaround.f3api.api.BindingRegistry;
import me.roundaround.f3api.api.DebugKeyBinding;
import me.roundaround.f3api.api.Modifier;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.InputUtil;

public final class CompatibilityDetector {
  public static void run() {
    BindingRegistry registry = BindingRegistry.getInstance();

    if (FabricLoader.getInstance().isModLoaded("betterf3")) {
      registry.registerCompatFallThrough(
          new DebugKeyBinding(
              "betterf3.cycle_renderdistance",
              DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_F),
              DebugKeyBinding.withHelpOutput()));
      registry.registerCompatFallThrough(
          new DebugKeyBinding(
              "betterf3.cycle_renderdistance.invert",
              DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_F, Modifier.SHIFT),
              DebugKeyBinding.withHelpOutput()));
      registry.registerCompatFallThrough(
          new DebugKeyBinding(
              "betterf3.cycle_simulationdistance",
              DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_F, Modifier.CONTROL),
              DebugKeyBinding.withHelpOutput()));
      registry.registerCompatFallThrough(
          new DebugKeyBinding(
              "betterf3.cycle_simulationdistance.invert",
              DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_F, Modifier.CONTROL, Modifier.SHIFT),
              DebugKeyBinding.withHelpOutput()));
      registry.registerCompatFallThrough(
          new DebugKeyBinding(
              "betterf3.modmenu",
              DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_M),
              DebugKeyBinding.withHelpOutput()));
    }
  }

  private CompatibilityDetector() {
  }
}
