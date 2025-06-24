package me.roundaround.f3api.api;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.roundaround.f3api.client.F3ApiMod;
import me.roundaround.f3api.generated.Constants;
import me.roundaround.f3api.roundalib.util.PathAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

public final class DebugKeyBindings {
  private static final String PROP_PREFIX = "key.";

  private static DebugKeyBindings instance = null;

  // TODO: add support for F3+Esc (pause)
  // TODO: replace PressAction for the help print out to iterate over
  // helpTranslationKeys

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
  private final HashMap<String, DebugKeyBinding> idToKeyBinding = new HashMap<>();
  private final HashMap<DebugKeyBinding, String> helpTranslationKeys = new HashMap<>();
  private final HashMap<DebugKeyBinding, PressAction> pressActions = new HashMap<>();

  private DebugKeyBindings() {
    // TODO: i18n for all vanilla bindings

    this.toggleRenderingChart = this.vanilla(
        InputUtil.GLFW_KEY_1,
        "toggle_rendering_chart");
    this.toggleRenderingAndTickCharts = this.vanilla(
        InputUtil.GLFW_KEY_2,
        "toggle_rendering_and_tick_charts");
    this.togglePacketSizeAndPingCharts = this.vanilla(
        InputUtil.GLFW_KEY_3,
        "toggle_packet_size_and_ping_charts");
    this.reloadChunks = this.vanilla(
        InputUtil.GLFW_KEY_A,
        "reload_chunks",
        "debug.reload_chunks.help");
    this.showHitboxes = this.vanilla(
        InputUtil.GLFW_KEY_B,
        "show_hitboxes",
        "debug.show_hitboxes.help");
    this.copyLocation = this.vanilla(
        InputUtil.GLFW_KEY_C,
        "copy_location",
        "debug.copy_location.help");
    this.clearChat = this.vanilla(
        InputUtil.GLFW_KEY_D,
        "clear_chat",
        "debug.clear_chat.help");
    this.showChunkBoundaries = this.vanilla(
        InputUtil.GLFW_KEY_G,
        "chunk_boundaries",
        "debug.chunk_boundaries.help");
    this.advancedTooltips = this.vanilla(
        InputUtil.GLFW_KEY_H,
        "advanced_tooltips",
        "debug.advanced_tooltips.help");
    this.inspect = this.vanilla(
        InputUtil.GLFW_KEY_I,
        "inspect",
        "debug.inspect.help");
    this.toggleDebugProfiler = this.vanilla(
        InputUtil.GLFW_KEY_L,
        "profiling",
        "debug.profiling.help");
    this.toggleSpectator = this.vanilla(
        InputUtil.GLFW_KEY_N,
        "creative_spectator",
        "debug.creative_spectator.help");
    this.togglePauseOnLostFocus = this.vanilla(
        InputUtil.GLFW_KEY_P,
        "pause_focus",
        "debug.pause_focus.help");
    this.showHelp = this.vanilla(
        InputUtil.GLFW_KEY_Q,
        "help",
        "debug.help.help");
    this.dumpDynamicTextures = this.vanilla(
        InputUtil.GLFW_KEY_S,
        "dump_dynamic_textures",
        "debug.dump_dynamic_textures.help");
    this.reloadResourcePacks = this.vanilla(
        InputUtil.GLFW_KEY_T,
        "reload_resourcepacks",
        "debug.reload_resourcepacks.help");
    this.showVersionInfo = this.vanilla(
        InputUtil.GLFW_KEY_V,
        "version",
        "debug.version.help");
    this.openGameModeSwitcher = this.vanilla(
        InputUtil.GLFW_KEY_F4,
        "gamemodes",
        "debug.gamemodes.help");

    this.toggleHitboxesKeyBinding = this.register(
        new DebugKeyBinding(
            "toggle_hitboxes_binding",
            "f3api.debug.key.toggle_hitboxes_binding",
            InputUtil.GLFW_KEY_T,
            Modifier.SHIFT),
        (client) -> {
          this.showHitboxes.setModifiers(this.showHitboxes.getBoundModifiers().isEmpty()
              ? Set.of(Modifier.SHIFT)
              : Set.of());
          return true;
        });

    this.load();
  }

  public DebugKeyBinding register(@NotNull DebugKeyBinding binding, @NotNull PressAction pressAction) {
    return this.register(binding, null, pressAction);
  }

  public DebugKeyBinding register(
      @NotNull DebugKeyBinding binding,
      @Nullable String helpTranslationKey,
      @NotNull PressAction pressAction) {
    String id = binding.getId();
    DebugKeyBinding existing = this.idToKeyBinding.put(id, binding);
    if (existing != null) {
      F3ApiMod.LOGGER.warn("Registered duplicate debug key binding; replacing existing binding: {}", id);
      this.allKeyBindings.remove(existing);
      this.helpTranslationKeys.remove(existing);
      this.pressActions.remove(existing);
    }

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

  public void save() {
    Path file = this.getFilePath();

    try {
      Files.createDirectories(file.getParent());
    } catch (Exception e) {
      F3ApiMod.LOGGER.error("Failed to write debug key bindings file", e);
      return;
    }

    Properties properties = new Properties();

    this.allKeyBindings.forEach((binding) -> {
      String id = binding.getId();
      StringBuilder value = new StringBuilder(String.valueOf(binding.getBoundKey().getCode()));
      for (Modifier modifier : binding.getBoundModifiers()) {
        value.append("+").append(modifier.getId());
      }
      properties.setProperty(id, value.toString());
    });

    try (OutputStream outputStream = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
      properties.store(outputStream, null);
    } catch (Exception e) {
      F3ApiMod.LOGGER.error("Failed to write debug key bindings file", e);
    }
  }

  private DebugKeyBinding vanilla(int code, String id) {
    return this.vanilla(code, id, null);
  }

  private DebugKeyBinding vanilla(int code, String id, @Nullable String helpTranslationKey) {
    return this.register(
        new DebugKeyBinding(id, "f3api.debug.key." + id, code),
        helpTranslationKey,
        (client) -> client.keyboard.f3api$processVanillaF3(code));
  }

  private void load() {
    Path file = this.getFilePath();
    if (!Files.exists(file) || !Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
      return;
    }

    Properties properties = new Properties();

    try {
      properties.load(Files.newInputStream(file, LinkOption.NOFOLLOW_LINKS));
    } catch (Exception e) {
      F3ApiMod.LOGGER.error("Failed to load debug key bindings", e);
      return;
    }

    properties.forEach((keyObj, valueObj) -> {
      String key = keyObj.toString().toLowerCase(Locale.ROOT).trim();
      String value = valueObj.toString().toLowerCase(Locale.ROOT).trim();

      if (!key.startsWith(PROP_PREFIX)) {
        return;
      }

      String id = key.substring(PROP_PREFIX.length());
      DebugKeyBinding binding = this.idToKeyBinding.get(id);
      if (binding == null) {
        return;
      }

      if (value.isBlank() || value.equals("-1")) {
        binding.set(InputUtil.UNKNOWN_KEY, Set.of());
        return;
      }

      String[] parts = value.split("\\+");
      Integer code = null;
      Set<Modifier> modifiers = new LinkedHashSet<>();

      for (String part : parts) {
        part = part.trim();
        if (part.isEmpty()) {
          continue;
        }

        try {
          int parsedCode = Integer.parseInt(part);
          if (code != null) {
            F3ApiMod.LOGGER.warn("Multiple key codes; ignoring: {} = {}", key, value);
            return;
          }
          if (DebugKeyBinding.RESERVED_KEYS.contains(parsedCode)) {
            F3ApiMod.LOGGER.warn("Attempted to bind reserved key; ignoring: {} = {}", key, value);
            return;
          }
          code = parsedCode;
        } catch (NumberFormatException e) {
          try {
            Modifier modifier = Modifier.fromId(part);
            modifiers.add(modifier);
          } catch (IllegalArgumentException e2) {
            F3ApiMod.LOGGER.warn("Invalid value; ignoring: {} = {}", key, value);
            return;
          }
        }
      }

      if (code == null) {
        F3ApiMod.LOGGER.warn("No key code; ignoring: {} = {}", key, value);
        return;
      }

      binding.set(code, modifiers);
    });
  }

  private Path getFilePath() {
    return PathAccessor.getInstance().getModConfigFile(Constants.MOD_ID, PathAccessor.ConfigFormat.PROPERTIES);
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

  public interface PressAction {
    boolean run(MinecraftClient client);
  }
}
