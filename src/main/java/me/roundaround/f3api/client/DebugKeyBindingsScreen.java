package me.roundaround.f3api.client;

import java.util.Arrays;
import java.util.Set;

import me.roundaround.f3api.api.DebugKeyBinding;
import me.roundaround.f3api.api.DebugKeyBindings;
import me.roundaround.f3api.api.Modifier;
import me.roundaround.f3api.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.f3api.roundalib.client.gui.screen.BaseScreen;
import me.roundaround.f3api.roundalib.client.gui.screen.ScreenParent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class DebugKeyBindingsScreen extends BaseScreen {
  private static final Set<Integer> RESERVED_KEYS = Set.of(
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

  private final ThreeSectionLayoutWidget layout = new ThreeSectionLayoutWidget(this);

  private BindingListWidget list;
  private DebugKeyBinding selectedKeyBinding;

  public DebugKeyBindingsScreen(Screen parent) {
    // TODO: i18n
    super(Text.of("Debug Key Bindings"), new ScreenParent(parent), MinecraftClient.getInstance());
  }

  @Override
  protected void init() {
    // TODO: All sorts of stuff...
    this.layout.addHeader(this.textRenderer, this.title);

    this.list = this.layout.addBody(new BindingListWidget(this.client, this.layout));
    for (DebugKeyBinding keyBinding : DebugKeyBindings.getInstance().getAllKeyBindings()) {
      this.list.addEntry(BindingListWidget.Entry.factory(this.textRenderer, keyBinding, () -> {
        this.selectedKeyBinding = keyBinding;
        this.list.forEachEntry((entry) -> {
          entry.setSelected(entry.getKeyBinding().equals(this.selectedKeyBinding));
          entry.update();
        });
      }, () -> {
        this.list.forEachEntry((entry) -> {
          entry.update();
        });
      }));
    }

    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, this::done)
        .width(ButtonWidget.field_49479)
        .build());

    this.layout.forEachChild(this::addDrawableChild);
    this.refreshWidgetPositions();
  }

  @Override
  protected void refreshWidgetPositions() {
    this.layout.refreshPositions();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (this.selectedKeyBinding != null) {
      if (RESERVED_KEYS.contains(keyCode)) {
        return false;
      }

      var isDirty = new Object() {
        boolean value = false;
      };

      if (keyCode == InputUtil.GLFW_KEY_ESCAPE) {
        this.selectedKeyBinding.setKey(InputUtil.UNKNOWN_KEY);
        this.selectedKeyBinding.setModifiers(Set.of());
      } else {
        this.selectedKeyBinding.setKey(InputUtil.fromKeyCode(keyCode, scanCode));
        this.selectedKeyBinding.setModifiers(Arrays.stream(Modifier.values())
            .filter(Modifier::isActive)
            .toList());
      }
      // TODO: Persist change
      this.selectedKeyBinding = null;

      // TODO: Conflict detection
      this.list.forEachEntry((entry) -> {
        entry.setSelected(false);
        entry.update();
        if (!entry.isDefault()) {
          isDirty.value = true;
        }
      });

      if (isDirty.value) {
        // TODO: Reset all button
      }

      return true;
    } else {
      return super.keyPressed(keyCode, scanCode, modifiers);
    }
  }
}
