package me.roundaround.f3api.client;

import me.roundaround.f3api.api.DebugKeyBinding;
import me.roundaround.f3api.api.DebugKeyBindings;
import me.roundaround.f3api.roundalib.client.gui.layout.screen.ThreeSectionLayoutWidget;
import me.roundaround.f3api.roundalib.client.gui.screen.BaseScreen;
import me.roundaround.f3api.roundalib.client.gui.screen.ScreenParent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class DebugKeyBindingsScreen extends BaseScreen {
  private final ThreeSectionLayoutWidget layout = new ThreeSectionLayoutWidget(this);

  private BindingList list;

  public DebugKeyBindingsScreen(Screen parent) {
    // TODO: i18n
    super(Text.of("Debug Key Bindings"), new ScreenParent(parent), MinecraftClient.getInstance());
  }

  @Override
  protected void init() {
    // TODO: All sorts of stuff...
    this.layout.addHeader(this.textRenderer, this.title);

    this.list = this.layout.addBody(new BindingList(this.client, this.layout));
    for (DebugKeyBinding keyBinding : DebugKeyBindings.getInstance().getAllKeyBindings()) {
      this.list.addEntry(BindingList.Entry.factory(this.textRenderer, keyBinding));
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
}
