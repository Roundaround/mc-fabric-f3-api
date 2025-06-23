package me.roundaround.f3api.client;

import me.roundaround.f3api.api.DebugKeyBinding;
import me.roundaround.f3api.roundalib.client.gui.layout.linear.LinearLayoutWidget;
import me.roundaround.f3api.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.f3api.roundalib.client.gui.util.GuiUtil;
import me.roundaround.f3api.roundalib.client.gui.widget.FlowListWidget;
import me.roundaround.f3api.roundalib.client.gui.widget.ParentElementEntryListWidget;
import me.roundaround.f3api.roundalib.client.gui.widget.drawable.LabelWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public class BindingList extends ParentElementEntryListWidget<BindingList.Entry> {
  public BindingList(MinecraftClient client, ThreeSectionLayoutWidget layout) {
    super(client, layout);
  }

  public static class Entry extends ParentElementEntryListWidget.Entry {
    protected static final int HEIGHT = 20;
    protected static final int CONTROL_WIDTH = 80;

    protected final TextRenderer textRenderer;

    public Entry(TextRenderer textRenderer, DebugKeyBinding keyBinding, int index, int x, int y, int width) {
      super(index, x, y, width, HEIGHT);
      this.textRenderer = textRenderer;

      LinearLayoutWidget layout = LinearLayoutWidget.horizontal()
          .spacing(GuiUtil.PADDING)
          .defaultOffAxisContentAlignCenter();

      layout.add(
          LabelWidget.builder(this.textRenderer, keyBinding.getText())
              .alignTextLeft()
              .overflowBehavior(LabelWidget.OverflowBehavior.SCROLL)
              .hideBackground()
              .showShadow()
              .height(this.getContentHeight())
              .build(),
          (parent, self) -> {
            self.setWidth(parent.getUnusedSpace(self));
          });

      layout.add(
          LabelWidget.builder(this.textRenderer, keyBinding.getBoundText())
              .alignTextRight()
              .overflowBehavior(LabelWidget.OverflowBehavior.SCROLL)
              .hideBackground()
              .showShadow()
              .height(this.getContentHeight())
              .width(CONTROL_WIDTH)
              .build());

      this.addLayout(layout, (self) -> {
        self.setPositionAndDimensions(
            this.getContentLeft(),
            this.getContentTop(),
            this.getContentWidth(),
            this.getContentHeight());
      });
      layout.forEachChild(this::addDrawableChild);
    }

    public static FlowListWidget.EntryFactory<Entry> factory(TextRenderer textRenderer, DebugKeyBinding keyBinding) {
      return (index, x, y, width) -> new Entry(
          textRenderer,
          keyBinding,
          index,
          x,
          y,
          width);
    }
  }
}
