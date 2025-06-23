package me.roundaround.f3api.api;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

public final class DebugKeyBindings {
  private static DebugKeyBindings instance = null;

  // TODO: add support for F3+Esc (pause)
  // TODO: replace PressAction for the help print out to iterate over helpTranslationKeys

  public final DebugKeyBinding toggleRenderingChart;
  public final DebugKeyBinding toggleRenderingAndTickCharts;
  public final DebugKeyBinding togglePacketSizeAndPingCharts;
  public final DebugKeyBinding reloadChunks;
  public final DebugKeyBinding showHitboxes;
  public final DebugKeyBinding copyLocation;
  public final DebugKeyBinding clearChat;
  public final DebugKeyBinding showChunkBoundaries;
  public final DebugKeyBinding advancedTooltips;
  public final DebugKeyBinding inspect;
  public final DebugKeyBinding toggleDebugProfiler;
  public final DebugKeyBinding toggleSpectator;
  public final DebugKeyBinding togglePauseOnLostFocus;
  public final DebugKeyBinding showHelp;
  public final DebugKeyBinding dumpDynamicTextures;
  public final DebugKeyBinding reloadResourcePacks;
  public final DebugKeyBinding showVersionInfo;
  public final DebugKeyBinding openGameModeSwitcher;
  public final DebugKeyBinding toggleHitboxesKeyBinding;

  private final LinkedHashSet<DebugKeyBinding> allKeyBindings = new LinkedHashSet<>();
  private final HashMap<DebugKeyBinding, String> helpTranslationKeys = new HashMap<>();
  private final HashMap<DebugKeyBinding, PressAction> pressActions = new HashMap<>();

  public DebugKeyBinding register(@NotNull DebugKeyBinding binding, @NotNull PressAction pressAction) {
    return this.register(binding, null, pressAction);
  }

  public DebugKeyBinding register(
      @NotNull DebugKeyBinding binding,
      @Nullable String helpTranslationKey,
      @NotNull PressAction pressAction) {
    this.allKeyBindings.add(binding);
    if (helpTranslationKey != null) {
      this.helpTranslationKeys.put(binding, helpTranslationKey);
    }
    this.pressActions.put(binding, pressAction);
    return binding;
  }

  public List<DebugKeyBinding> getAllKeyBindings() {
    return List.copyOf(this.allKeyBindings);
  }

  public PressAction getPressAction(DebugKeyBinding binding) {
    return this.pressActions.getOrDefault(binding, (client) -> false);
  }

  private DebugKeyBinding vanilla(int code, String translationKey) {
    return this.vanilla(code, translationKey, null);
  }

  private DebugKeyBinding vanilla(int code, String translationKey, @Nullable String helpTranslationKey) {
    return this.register(
        new DebugKeyBinding(translationKey, code),
        helpTranslationKey,
        (client) -> client.keyboard.f3api$processVanillaF3(code));
  }

  public static DebugKeyBindings getInstance() {
    if (instance == null) {
      instance = new DebugKeyBindings();
    }
    return instance;
  }

  public static void init() {
    // Forcing creation is good enough for initialization
    getInstance();
  }

  private DebugKeyBindings() {
    // TODO: i18n for all vanilla bindings

    this.toggleRenderingChart = this.vanilla(
        InputUtil.GLFW_KEY_1,
        "Toggle rendering chart");
    this.toggleRenderingAndTickCharts = this.vanilla(
        InputUtil.GLFW_KEY_2,
        "Toggle rendering and tick charts");
    this.togglePacketSizeAndPingCharts = this.vanilla(
        InputUtil.GLFW_KEY_3,
        "Toggle packet size and ping charts");
    this.reloadChunks = this.vanilla(
        InputUtil.GLFW_KEY_A,
        "Reload chunks",
        "debug.reload_chunks.help");
    this.showHitboxes = this.vanilla(
        InputUtil.GLFW_KEY_B,
        "Show hitboxes",
        "debug.show_hitboxes.help");
    this.copyLocation = this.vanilla(
        InputUtil.GLFW_KEY_C,
        "Copy location as /tp command, hold to crash the game",
        "debug.copy_location.help");
    this.clearChat = this.vanilla(
        InputUtil.GLFW_KEY_D,
        "Clear chat",
        "debug.clear_chat.help");
    this.showChunkBoundaries = this.vanilla(
        InputUtil.GLFW_KEY_G,
        "Show chunk boundaries",
        "debug.chunk_boundaries.help");
    this.advancedTooltips = this.vanilla(
        InputUtil.GLFW_KEY_H,
        "Advanced tooltips",
        "debug.advanced_tooltips.help");
    this.inspect = this.vanilla(
        InputUtil.GLFW_KEY_I,
        "Copy entity or block data to clipboard",
        "debug.inspect.help");
    this.toggleDebugProfiler = this.vanilla(
        InputUtil.GLFW_KEY_L,
        "Start/stop profiling",
        "debug.profiling.help");
    this.toggleSpectator = this.vanilla(
        InputUtil.GLFW_KEY_N,
        "Cycle previous game mode <-> spectator",
        "debug.creative_spectator.help");
    this.togglePauseOnLostFocus = this.vanilla(
        InputUtil.GLFW_KEY_P,
        "Pause on lost focus",
        "debug.pause_focus.help");
    this.showHelp = this.vanilla(
        InputUtil.GLFW_KEY_Q,
        "Show debug list",
        "debug.help.help");
    this.dumpDynamicTextures = this.vanilla(
        InputUtil.GLFW_KEY_S,
        "Dump dynamic textures",
        "debug.dump_dynamic_textures.help");
    this.reloadResourcePacks = this.vanilla(
        InputUtil.GLFW_KEY_T,
        "Reload resource packs",
        "debug.reload_resourcepacks.help");
    this.showVersionInfo = this.vanilla(
        InputUtil.GLFW_KEY_V,
        "Client version info",
        "debug.version.help");
    this.openGameModeSwitcher = this.vanilla(
        InputUtil.GLFW_KEY_F4,
        "Open game mode switcher",
        "debug.gamemodes.help");

    this.toggleHitboxesKeyBinding = this.register(
        new DebugKeyBinding("Toggle keybinding for toggle hitboxes", InputUtil.GLFW_KEY_T, Modifier.SHIFT),
        (client) -> {
          this.showHitboxes.setModifiers(this.showHitboxes.getBoundModifiers().isEmpty()
              ? Set.of(Modifier.SHIFT)
              : Set.of());
          return true;
        });
  }

  public interface PressAction {
    boolean run(MinecraftClient client);
  }
}
