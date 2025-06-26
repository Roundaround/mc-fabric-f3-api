package me.roundaround.f3api.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import me.roundaround.f3api.api.BindingRegistry;
import me.roundaround.f3api.api.DebugKeyBinding;
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
  private final ThreeSectionLayoutWidget layout = new ThreeSectionLayoutWidget(this);

  private BindingListWidget list;
  private ButtonWidget resetAllButton;
  private BindingListWidget.Entry selectedEntry;

  public DebugKeyBindingsScreen(Screen parent) {
    super(
        Text.translatable("f3api.keybinds.title"),
        new ScreenParent(parent),
        MinecraftClient.getInstance());
  }

  @Override
  protected void init() {
    this.layout.addHeader(this.textRenderer, this.title);

    this.list = this.layout.addBody(new BindingListWidget(this.client, this.layout));
    for (DebugKeyBinding keyBinding : BindingRegistry.getInstance().getAllKeyBindings()) {
      this.list.addEntry(BindingListWidget.Entry.factory(
          this.textRenderer,
          keyBinding,
          (selectedEntry) -> {
            this.selectedEntry = selectedEntry;
            this.list.forEachEntry((entry) -> {
              entry.setSelected(entry.equals(selectedEntry));
            });
            this.update();
          },
          this::update));
    }

    this.resetAllButton = this.layout.addFooter(ButtonWidget.builder(
        Text.translatable("controls.resetAll"),
        (button) -> {
          this.list.forEachEntry((entry) -> {
            entry.setSelected(false);
            entry.reset();
          });
          this.update();
        })
        .build());
    this.layout.addFooter(ButtonWidget.builder(ScreenTexts.DONE, this::done)
        .build());

    this.update();

    this.layout.forEachChild(this::addDrawableChild);
    this.refreshWidgetPositions();
  }

  @Override
  protected void refreshWidgetPositions() {
    this.layout.refreshPositions();
  }

  @Override
  public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
    if (this.selectedEntry != null) {
      if (DebugKeyBinding.RESERVED_KEYS.contains(keyCode)) {
        return false;
      }

      if (keyCode == InputUtil.GLFW_KEY_ESCAPE) {
        this.selectedEntry.set(InputUtil.UNKNOWN_KEY, Set.of());
      } else {
        this.selectedEntry.set(InputUtil.fromKeyCode(keyCode, scanCode), Arrays.stream(Modifier.values())
            .filter(Modifier::isActive)
            .toList());
      }
      this.selectedEntry = null;

      this.list.forEachEntry((entry) -> {
        entry.setSelected(false);
      });

      this.update();

      return true;
    } else {
      return super.keyPressed(keyCode, scanCode, modifiers);
    }
  }

  @Override
  public void removed() {
    BindingRegistry.getInstance().save();
    super.removed();
  }

  private void update() {
    HashMap<String, ArrayList<DebugKeyBinding>> buckets = new HashMap<>();
    this.list.forEachEntry((entry) -> {
      buckets.computeIfAbsent(entry.getBoundString(), (key) -> new ArrayList<>()).add(entry.getKeyBinding());
    });

    var isDirty = new Object() {
      boolean value = false;
    };

    this.list.forEachEntry((entry) -> {
      entry.setConflicts(buckets.computeIfAbsent(entry.getBoundString(), (key) -> new ArrayList<>()));

      entry.update();
      if (!entry.isDefault()) {
        isDirty.value = true;
      }
    });

    this.resetAllButton.active = isDirty.value;
  }
}
