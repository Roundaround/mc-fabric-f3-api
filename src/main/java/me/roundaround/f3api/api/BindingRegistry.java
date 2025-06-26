package me.roundaround.f3api.api;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import me.roundaround.f3api.client.F3ApiMod;
import me.roundaround.f3api.generated.Constants;
import me.roundaround.f3api.roundalib.util.PathAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class BindingRegistry {
  public static final String PROP_PREFIX = "key.";

  private static BindingRegistry instance = null;

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
  public final DebugKeyBinding pauseGame;
  public final DebugKeyBinding openGameModeSwitcher;
  public final DebugKeyBinding toggleHitboxesKeyBinding;
  public final DebugKeyBinding toggleRenderingChart;
  public final DebugKeyBinding toggleRenderingAndTickCharts;
  public final DebugKeyBinding togglePacketSizeAndPingCharts;

  private final LinkedHashSet<DebugKeyBinding> allKeyBindings = new LinkedHashSet<>();
  private final HashMap<String, DebugKeyBinding> idToKeyBinding = new HashMap<>();
  private final HashMap<DebugKeyBinding, PressAction> pressActions = new HashMap<>();

  private BindingRegistry() {
    // TODO: i18n for all vanilla bindings

    this.reloadChunks = this.registerVanilla(
        "reload_chunks",
        InputUtil.GLFW_KEY_A,
        DebugKeyBinding.withHelpOutput());
    this.showHitboxes = this.registerVanilla(
        "show_hitboxes",
        InputUtil.GLFW_KEY_B,
        DebugKeyBinding.withHelpOutput());
    this.copyLocation = this.registerVanilla(
        "copy_location",
        InputUtil.GLFW_KEY_C,
        DebugKeyBinding.withHelpOutput());
    this.clearChat = this.registerVanilla(
        "clear_chat",
        InputUtil.GLFW_KEY_D,
        DebugKeyBinding.withHelpOutput());
    this.showChunkBoundaries = this.registerVanilla(
        "chunk_boundaries",
        InputUtil.GLFW_KEY_G,
        DebugKeyBinding.withHelpOutput());
    this.advancedTooltips = this.registerVanilla(
        "advanced_tooltips",
        InputUtil.GLFW_KEY_H,
        DebugKeyBinding.withHelpOutput());
    this.inspect = this.registerVanilla(
        "inspect",
        InputUtil.GLFW_KEY_I,
        DebugKeyBinding.withHelpOutput());
    this.toggleDebugProfiler = this.registerVanilla(
        "profiling",
        InputUtil.GLFW_KEY_L,
        DebugKeyBinding.withHelpOutput());
    this.toggleSpectator = this.registerVanilla(
        "creative_spectator",
        InputUtil.GLFW_KEY_N,
        DebugKeyBinding.withHelpOutput());
    this.togglePauseOnLostFocus = this.registerVanilla(
        "pause_focus",
        InputUtil.GLFW_KEY_P,
        DebugKeyBinding.withHelpOutput());
    this.showHelp = this.registerVanilla(
        "help",
        InputUtil.GLFW_KEY_Q,
        (client, messager) -> {
          this.printDebugHelp(messager::debugMessage, messager::sendMessage);
          return true;
        },
        DebugKeyBinding.withHelpOutput());
    this.dumpDynamicTextures = this.registerVanilla(
        "dump_dynamic_textures",
        InputUtil.GLFW_KEY_S,
        DebugKeyBinding.withHelpOutput());
    this.reloadResourcePacks = this.registerVanilla(
        "reload_resourcepacks",
        InputUtil.GLFW_KEY_T,
        DebugKeyBinding.withHelpOutput());
    this.showVersionInfo = this.registerVanilla(
        "version",
        InputUtil.GLFW_KEY_V,
        DebugKeyBinding.withHelpOutput());
    this.pauseGame = this.registerVanilla(
        "pause",
        InputUtil.GLFW_KEY_ESCAPE,
        DebugKeyBinding.withHelpOutput(),
        DebugKeyBinding.withImmutable());
    this.openGameModeSwitcher = this.registerVanilla(
        "gamemodes",
        InputUtil.GLFW_KEY_F4,
        DebugKeyBinding.withHelpOutput());
    this.toggleRenderingChart = this.registerVanilla(
        "toggle_rendering_chart",
        InputUtil.GLFW_KEY_1);
    this.toggleRenderingAndTickCharts = this.registerVanilla(
        "toggle_rendering_and_tick_charts",
        InputUtil.GLFW_KEY_2);
    this.togglePacketSizeAndPingCharts = this.registerVanilla(
        "toggle_packet_size_and_ping_charts",
        InputUtil.GLFW_KEY_3);

    this.toggleHitboxesKeyBinding = this.register(
        new DebugKeyBinding(
            "toggle_hitboxes_binding",
            Constants.MOD_ID,
            DebugKeyBinding.withHelpOutput(),
            DebugKeyBinding.withDefaultBinding(InputUtil.GLFW_KEY_T, Modifier.SHIFT)),
        (client, messager) -> {
          this.showHitboxes
              .setModifiers(this.showHitboxes.getBoundModifiers().isEmpty() ? Set.of(Modifier.SHIFT) : Set.of());
          return true;
        });
  }

  public DebugKeyBinding register(@NotNull DebugKeyBinding binding, @NotNull PressAction pressAction) {
    String id = binding.getId();
    DebugKeyBinding existing = this.idToKeyBinding.put(id, binding);
    if (existing != null) {
      F3ApiMod.LOGGER.warn("Registered duplicate debug key binding; replacing existing binding: {}", id);
      this.allKeyBindings.remove(existing);
      this.pressActions.remove(existing);
    }

    this.allKeyBindings.add(binding);
    this.pressActions.put(binding, pressAction);

    return binding;
  }

  public DebugKeyBinding registerCompatFallThrough(@NotNull DebugKeyBinding binding) {
    return this.register(binding, (client, messager) -> {
      return client.keyboard.f3api$processVanillaF3(binding.getDefaultKey(), binding.getDefaultModifiers());
    });
  }

  public List<DebugKeyBinding> getAllKeyBindings() {
    return List.copyOf(this.allKeyBindings);
  }

  public PressAction getPressAction(DebugKeyBinding binding) {
    return this.pressActions.getOrDefault(binding, (c, m) -> false);
  }

  public void printDebugHelp(Consumer<String> debugLog, Consumer<Text> sendMessage) {
    debugLog.accept("debug.help.message");
    for (DebugKeyBinding binding : this.allKeyBindings) {
      if (binding.hasHelpOutput()) {
        sendMessage.accept(binding.getHelpText());
      }
    }
  }

  public void save() {
    Properties properties = new Properties();

    this.allKeyBindings.forEach((binding) -> {
      if (binding.isDefault()) {
        return;
      }

      String id = binding.getId();
      StringBuilder value = new StringBuilder();
      for (Modifier modifier : binding.getBoundModifiers()) {
        value.append(modifier.getId()).append("+");
      }
      value.append(String.valueOf(binding.getBoundKey().getCode()));
      properties.setProperty(PROP_PREFIX + id, value.toString());
    });
    
    Path file = this.getFilePath();

    if (properties.isEmpty()) {
      try {
        Files.delete(file);
      } catch (NoSuchFileException e) {
        // No-op
      } catch (Exception e) {
        F3ApiMod.LOGGER.error("Failed to delete debug key bindings file", e);
      }
      return;
    }

    try {
      Files.createDirectories(file.getParent());
    } catch (Exception e) {
      F3ApiMod.LOGGER.error("Failed to write debug key bindings file", e);
      return;
    }

    try (OutputStream outputStream = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
      properties.store(outputStream, null);
    } catch (Exception e) {
      F3ApiMod.LOGGER.error("Failed to write debug key bindings file", e);
    }
  }

  public void load() {
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

      if (value.equals("-1")) {
        binding.set(InputUtil.UNKNOWN_KEY, Set.of());
        return;
      }

      if (value.isBlank()) {
        binding.reset();
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

  private DebugKeyBinding registerVanilla(String id, int code, DebugKeyBinding.Option... options) {
    return this.registerVanilla(id, code, (client, messager) -> client.keyboard.f3api$processVanillaF3(code), options);
  }

  private DebugKeyBinding registerVanilla(
      String id,
      int code,
      PressAction pressAction,
      DebugKeyBinding.Option... options) {
    ArrayList<DebugKeyBinding.Option> optionsList = new ArrayList<>(Arrays.asList(options));
    optionsList.add(DebugKeyBinding.withForcedDefaultBinding(code));

    return this.register(new DebugKeyBinding(Identifier.DEFAULT_NAMESPACE, id, optionsList), pressAction);
  }

  public static BindingRegistry getInstance() {
    if (instance == null) {
      instance = new BindingRegistry();
    }
    return instance;
  }

  public static void init() {
    // Forcing creation is good enough for initialization
    getInstance();
  }

  public interface PressAction {
    boolean run(MinecraftClient client, Messager messager);
  }

  public interface Messager {
    void debugMessage(Text text);

    void debugMessage(String key);

    void debugError(Text text);

    void debugFormatted(String pattern, Object... args);

    void sendMessage(Text text);
  }
}
