package me.roundaround.f3api.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.roundaround.f3api.api.BindingRegistry;
import me.roundaround.f3api.api.DebugKeyBinding;
import me.roundaround.f3api.api.Modifier;
import me.roundaround.f3api.generated.Constants;
import me.roundaround.f3api.roundalib.client.gui.icon.BuiltinIcon;
import me.roundaround.f3api.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.f3api.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.f3api.roundalib.client.gui.util.GuiUtil;
import me.roundaround.f3api.roundalib.client.gui.widget.FlowListWidget;
import me.roundaround.f3api.roundalib.client.gui.widget.IconButtonWidget;
import me.roundaround.f3api.roundalib.client.gui.widget.ParentElementEntryListWidget;
import me.roundaround.f3api.roundalib.client.gui.widget.TooltipWidget;
import me.roundaround.f3api.roundalib.client.gui.widget.drawable.LabelWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class BindingListWidget extends ParentElementEntryListWidget<BindingListWidget.Entry> {
  public BindingListWidget(
      MinecraftClient client,
      ThreeSectionLayoutWidget layout) {
    super(client, layout);
    this.setAlternatingRowShading(true);
  }

  public static class Entry extends ParentElementEntryListWidget.Entry {
    private static final int HEIGHT = 20;
    private static final int EDIT_BUTTON_WIDTH = ButtonWidget.DEFAULT_WIDTH_SMALL;

    private final TextRenderer textRenderer;
    private final DebugKeyBinding keyBinding;
    private final ArrayList<DebugKeyBinding> conflicts = new ArrayList<>();
    private final LabelWidget label;
    private final TooltipWidget tooltip;
    private final ButtonWidget editButton;
    private final IconButtonWidget resetButton;

    private boolean selected = false;

    public Entry(
        TextRenderer textRenderer,
        DebugKeyBinding keyBinding,
        Runnable onSelect,
        Runnable onReset,
        int index,
        int x,
        int y,
        int width) {
      super(index, x, y, width, HEIGHT);
      this.textRenderer = textRenderer;
      this.keyBinding = keyBinding;

      LinearLayoutWidget layout = LinearLayoutWidget.horizontal()
          .spacing(GuiUtil.PADDING)
          .defaultOffAxisContentAlignCenter();

      this.label = layout.add(
          LabelWidget.builder(this.textRenderer, keyBinding.getText())
              .alignTextLeft()
              .alignTextCenterY()
              .overflowBehavior(LabelWidget.OverflowBehavior.SCROLL)
              .hideBackground()
              .showShadow()
              .height(this.getContentHeight())
              .build(),
          (parent, self) -> {
            self.setWidth(parent.getUnusedSpace(self));
          });

      this.editButton = layout.add(
          ButtonWidget.builder(keyBinding.getBoundTextWithF3(), (button) -> {
            onSelect.run();
          })
              .width(EDIT_BUTTON_WIDTH)
              .build());

      this.resetButton = layout.add(
          IconButtonWidget.builder(BuiltinIcon.UNDO_18, Constants.MOD_ID)
              .vanillaSize()
              .messageAndTooltip(Text.translatable("f3api.roundalib.reset.tooltip"))
              .onPress((button) -> {
                this.reset();
                onReset.run();
              })
              .build());

      this.update();

      this.tooltip = this.addDrawable(new TooltipWidget(List.of(
          this.getModNameText(keyBinding.getModId()),
          Text.literal(BindingRegistry.PROP_PREFIX + keyBinding.getId()).formatted(Formatting.GRAY))));

      this.addLayout(layout, (self) -> {
        self.setPositionAndDimensions(
            this.getContentLeft(),
            this.getContentTop(),
            this.getContentWidth(),
            this.getContentHeight());
      });
      layout.forEachChild(this::addDrawableChild);
    }

    @Override
    public void refreshPositions() {
      super.refreshPositions();
      this.tooltip.setDimensionsAndPosition(
          this.label.getWidth(),
          this.label.getHeight(),
          this.label.getX(),
          this.label.getY());
    }

    public boolean isDefault() {
      return this.keyBinding.isDefault();
    }

    public DebugKeyBinding getKeyBinding() {
      return this.keyBinding;
    }

    public void reset() {
      this.keyBinding.reset();
    }

    public void setSelected(boolean selected) {
      this.selected = selected;
    }

    public void setKey(InputUtil.Key key) {
      this.keyBinding.setKey(key);
    }

    public void setModifiers(Collection<Modifier> modifiers) {
      this.keyBinding.setModifiers(modifiers);
    }

    public void setConflicts(Collection<DebugKeyBinding> conflicts) {
      this.conflicts.clear();
      this.conflicts.addAll(conflicts);
      this.conflicts.remove(this.keyBinding);
    }

    public void update() {
      MutableText boundText = this.keyBinding.getBoundTextWithF3().copy();

      MutableText conflictsText = null;
      if (!this.conflicts.isEmpty()) {
        conflictsText = Text.empty();
        for (int i = 0; i < this.conflicts.size(); i++) {
          DebugKeyBinding conflict = this.conflicts.get(i);
          conflictsText.append(conflict.getText());
          if (i < this.conflicts.size() - 1) {
            conflictsText.append(", ");
          }
        }

        boundText = Text.literal("[ ")
            .append(boundText.formatted(Formatting.WHITE))
            .append(" ]")
            .formatted(Formatting.RED);
      }

      if (this.selected) {
        boundText = Text.literal("> ")
            .append(boundText.formatted(Formatting.WHITE, Formatting.UNDERLINE))
            .append(" <")
            .formatted(Formatting.YELLOW);
      }

      this.editButton.setMessage(boundText);
      if (conflictsText == null) {
        this.editButton.setTooltip(null);
      } else {
        this.editButton.setTooltip(Tooltip.of(Text.translatable(
            "controls.keybinds.duplicateKeybinds",
            conflictsText)));
      }

      this.editButton.active = this.keyBinding.isMutable();
      if (!this.keyBinding.isMutable()) {
        this.editButton.setTooltip(Tooltip.of(Text.translatable("f3api.immutable.tooltip")));
      }
      this.resetButton.active = !this.keyBinding.isDefault();
    }

    private Text getModNameText(String modId) {
      if (modId.equals(Identifier.DEFAULT_NAMESPACE)) {
        return Text.literal("Minecraft").formatted(
            Formatting.BLUE,
            Formatting.ITALIC);
      }

      ModContainer mod = FabricLoader.getInstance().getModContainer(modId).orElse(null);
      if (mod == null) {
        return Text.literal(modId).formatted(
            Formatting.GRAY,
            Formatting.ITALIC);
      }

      return Text.literal(mod.getMetadata().getName()).formatted(
          Formatting.LIGHT_PURPLE,
          Formatting.ITALIC);
    }

    public static FlowListWidget.EntryFactory<Entry> factory(
        TextRenderer textRenderer,
        DebugKeyBinding keyBinding,
        Runnable onSelect,
        Runnable onReset) {
      return (index, x, y, width) -> new Entry(
          textRenderer,
          keyBinding,
          onSelect,
          onReset,
          index,
          x,
          y,
          width);
    }
  }
}
