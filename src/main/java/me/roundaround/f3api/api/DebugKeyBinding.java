package me.roundaround.f3api.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.roundaround.f3api.client.F3ApiMod;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class DebugKeyBinding implements Comparable<DebugKeyBinding> {
  public static final Set<Integer> RESERVED_KEYS = Set.of(
      InputUtil.UNKNOWN_KEY.getCode(),
      InputUtil.GLFW_KEY_BACKSPACE,
      InputUtil.GLFW_KEY_DELETE,
      InputUtil.GLFW_KEY_LEFT_SHIFT,
      InputUtil.GLFW_KEY_RIGHT_SHIFT,
      InputUtil.GLFW_KEY_LEFT_CONTROL,
      InputUtil.GLFW_KEY_RIGHT_CONTROL,
      InputUtil.GLFW_KEY_LEFT_ALT,
      InputUtil.GLFW_KEY_RIGHT_ALT,
      InputUtil.GLFW_KEY_LEFT_SUPER,
      InputUtil.GLFW_KEY_RIGHT_SUPER,
      InputUtil.GLFW_KEY_F3);

  private final String modId;
  private final String id;

  private String translationKey;
  private @Nullable String helpTranslationKey;
  private boolean mutable;
  private InputUtil.Key defaultKey;
  private Set<Modifier> defaultModifiers;
  private InputUtil.Key boundKey;
  private Set<Modifier> boundModifiers;

  public DebugKeyBinding(String modId, String id, Option... options) {
    this(modId, id, Arrays.asList(options));
  }

  public DebugKeyBinding(String modId, String id, Collection<Option> options) {
    this.modId = modId;

    if (!id.startsWith(modId + ".")) {
      id = modId + "." + id;
    }
    this.id = id;

    this.translationKey = getDefaultI18nKey(id);
    this.helpTranslationKey = null;
    this.mutable = true;
    this.defaultKey = InputUtil.UNKNOWN_KEY;
    this.defaultModifiers = Set.of();

    for (Option option : options) {
      option.apply(this);
    }

    this.boundKey = this.defaultKey;
    this.boundModifiers = this.defaultModifiers;
  }

  @Override
  public int compareTo(@NotNull DebugKeyBinding other) {
    return I18n.translate(this.translationKey).compareTo(I18n.translate(other.translationKey));
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof DebugKeyBinding other)) {
      return false;
    }
    return Objects.equals(id, other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  public String getId() {
    return this.id;
  }

  public String getModId() {
    return this.modId;
  }

  public String getTranslationKey() {
    return this.translationKey;
  }

  public Text getText() {
    return Text.translatable(this.translationKey);
  }

  public Text getBoundText() {
    if (this.isUnbound()) {
      return this.boundKey.getLocalizedText();
    }
    return this.getKeyText(this.boundKey, this.boundModifiers);
  }

  public Text getDefaultText() {
    return this.getKeyText(this.defaultKey, this.defaultModifiers);
  }

  public boolean hasHelpOutput() {
    return this.helpTranslationKey != null;
  }

  public String getHelpTranslationKey() {
    return this.helpTranslationKey;
  }

  public Text getHelpText() {
    if (!this.hasHelpOutput()) {
      return Text.empty();
    }
    return Text.translatable(this.helpTranslationKey, this.getBoundText(), this.getText());
  }

  public InputUtil.Key getBoundKey() {
    return this.boundKey;
  }

  public Set<Modifier> getBoundModifiers() {
    return this.boundModifiers;
  }

  public boolean isMutable() {
    return this.mutable;
  }

  public void reset() {
    this.set(this.defaultKey, this.defaultModifiers);
  }

  public void set(int code, Modifier... modifiers) {
    this.set(code, Arrays.asList(modifiers));
  }

  public void set(InputUtil.Key boundKey, Modifier... modifiers) {
    this.set(boundKey, Arrays.asList(modifiers));
  }

  public void set(int code, Collection<Modifier> modifiers) {
    this.setKey(code);
    this.setModifiers(modifiers);
  }

  public void set(InputUtil.Key boundKey, Collection<Modifier> modifiers) {
    this.setKey(boundKey);
    this.setModifiers(modifiers);
  }

  public void setKey(int code) {
    this.setKey(InputUtil.Type.KEYSYM.createFromCode(code));
  }

  public void setKey(InputUtil.Key boundKey) {
    if (!this.mutable) {
      return;
    }
    if (RESERVED_KEYS.contains(boundKey.getCode())) {
      F3ApiMod.LOGGER.warn("Attempted to bind reserved key; ignoring: {}", boundKey.getTranslationKey());
      return;
    }
    this.boundKey = boundKey;
  }

  public void setModifiers(Collection<Modifier> modifiers) {
    if (!this.mutable) {
      return;
    }
    this.boundModifiers = Set.copyOf(modifiers);
  }

  public boolean conflicts(DebugKeyBinding other) {
    return this.boundKey.equals(other.boundKey);
  }

  public boolean isUnbound() {
    return this.boundKey.equals(InputUtil.UNKNOWN_KEY);
  }

  public boolean isDefault() {
    return this.boundKey.equals(this.defaultKey) && this.boundModifiers.equals(this.defaultModifiers);
  }

  public InputUtil.Key getDefaultKey() {
    return this.defaultKey;
  }

  public Set<Modifier> getDefaultModifiers() {
    return this.defaultModifiers;
  }

  public boolean matches(int code) {
    if (code == InputUtil.UNKNOWN_KEY.getCode()) {
      return this.isUnbound();
    }

    return this.boundKey.getCode() == code && Arrays.stream(Modifier.values())
        .allMatch((modifier) -> modifier.isActive() == this.boundModifiers.contains(modifier));
  }

  private Text getKeyText(InputUtil.Key key, Collection<Modifier> modifiers) {
    MutableText text = InputUtil.Type.KEYSYM.createFromCode(InputUtil.GLFW_KEY_F3)
        .getLocalizedText()
        .copy()
        .append(" + ");
    modifiers.stream().sorted().forEachOrdered(
        (modifier) -> text.append(modifier.getText()).append(" + "));
    text.append(key.getLocalizedText());
    return text;
  }

  private static String getDefaultI18nKey(String id) {
    return "f3api.key." + id;
  }

  private static String getDefaultHelpI18nKey(String id) {
    return getDefaultI18nKey(id) + ".help";
  }

  public static Option withDefaultBinding(int code, Modifier... modifiers) {
    return withDefaultBinding(InputUtil.Type.KEYSYM.createFromCode(code), modifiers);
  }

  public static Option withDefaultBinding(int code, Collection<Modifier> modifiers) {
    return withDefaultBinding(InputUtil.Type.KEYSYM.createFromCode(code), modifiers);
  }

  public static Option withDefaultBinding(InputUtil.Key key, Modifier... modifiers) {
    return withDefaultBinding(key, Arrays.asList(modifiers));
  }

  public static Option withDefaultBinding(InputUtil.Key key, Collection<Modifier> modifiers) {
    return (binding) -> {
      if (RESERVED_KEYS.contains(key.getCode())) {
        throw new IllegalArgumentException(String.format(
            "Attempted to bind reserved key for %s: %s",
            binding.id,
            key.getLocalizedText().getString()));
      }
      binding.defaultKey = key;
      binding.defaultModifiers = modifiers == null ? Set.of() : Set.copyOf(modifiers);
    };
  }

  // Package-private to allow setting i.e. "pause game" binding internally
  static Option withForcedDefaultBinding(int code, Modifier... modifiers) {
    return withForcedDefaultBinding(InputUtil.Type.KEYSYM.createFromCode(code), modifiers);
  }

  // Package-private to allow setting i.e. "pause game" binding internally
  static Option withForcedDefaultBinding(int code, Collection<Modifier> modifiers) {
    return withForcedDefaultBinding(InputUtil.Type.KEYSYM.createFromCode(code), modifiers);
  }

  // Package-private to allow setting i.e. "pause game" binding internally
  static Option withForcedDefaultBinding(InputUtil.Key key, Modifier... modifiers) {
    return withForcedDefaultBinding(key, Arrays.asList(modifiers));
  }

  // Package-private to allow setting i.e. "pause game" binding internally
  static Option withForcedDefaultBinding(InputUtil.Key key, Collection<Modifier> modifiers) {
    return (binding) -> {
      binding.defaultKey = key;
      binding.defaultModifiers = modifiers == null ? Set.of() : Set.copyOf(modifiers);
    };
  }

  public static Option withI18nKey(String i18nKey) {
    return (binding) -> {
      binding.translationKey = i18nKey;
    };
  }

  public static Option withHelpOutput() {
    return (binding) -> {
      binding.helpTranslationKey = getDefaultHelpI18nKey(binding.id);
    };
  }

  public static Option withHelpOutput(String i18nKey) {
    return (binding) -> {
      binding.helpTranslationKey = i18nKey;
    };
  }

  public static Option withImmutable() {
    return (binding) -> {
      binding.mutable = false;
    };
  }

  @FunctionalInterface
  public interface Option {
    void apply(DebugKeyBinding binding);
  }
}
