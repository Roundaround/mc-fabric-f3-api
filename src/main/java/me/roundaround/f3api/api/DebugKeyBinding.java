package me.roundaround.f3api.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import me.roundaround.f3api.client.F3ApiMod;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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

  private final String id;
  private final String translationKey;
  private final InputUtil.Key defaultKey;
  private final Set<Modifier> defaultModifiers;

  private InputUtil.Key boundKey;
  private Set<Modifier> boundModifiers;

  public DebugKeyBinding(String id, String translationKey, int code, Modifier... modifiers) {
    this(id, translationKey, code, Arrays.asList(modifiers));
  }

  public DebugKeyBinding(String id, String translationKey, int code, Collection<Modifier> modifiers) {
    if (RESERVED_KEYS.contains(code)) {
      throw new IllegalArgumentException(String.format(
          "Attempted to bind reserved key for %s: %s",
          id,
          InputUtil.fromKeyCode(code, 0).getLocalizedText().getString()));
    }

    this.id = id;
    this.translationKey = translationKey;
    this.defaultKey = InputUtil.Type.KEYSYM.createFromCode(code);
    this.defaultModifiers = Set.copyOf(modifiers);
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
    MutableText text = Text.empty();
    this.boundModifiers.stream().sorted().forEachOrdered(
        (modifier) -> text.append(modifier.getText()).append(Text.literal(" + ").formatted(Formatting.GRAY)));
    text.append(this.boundKey.getLocalizedText());
    return text;
  }

  public Text getBoundTextWithF3() {
    if (this.isUnbound()) {
      return this.boundKey.getLocalizedText();
    }
    MutableText text = InputUtil.Type.KEYSYM.createFromCode(InputUtil.GLFW_KEY_F3)
        .getLocalizedText()
        .copy()
        .append("+");
    this.boundModifiers.stream().sorted().forEachOrdered(
        (modifier) -> text.append(modifier.getText()).append("+"));
    text.append(this.boundKey.getLocalizedText());
    return text;
  }

  public InputUtil.Key getBoundKey() {
    return this.boundKey;
  }

  public Set<Modifier> getBoundModifiers() {
    return this.boundModifiers;
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
    if (RESERVED_KEYS.contains(boundKey.getCode())) {
      F3ApiMod.LOGGER.warn("Attempted to bind reserved key; ignoring: {}", boundKey.getTranslationKey());
      return;
    }
    this.boundKey = boundKey;
  }

  public void setModifiers(Collection<Modifier> modifiers) {
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
}
