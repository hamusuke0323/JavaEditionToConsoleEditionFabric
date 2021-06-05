package com.hamusuke.jece.client.gui.screen;

import com.google.common.collect.Lists;
import com.hamusuke.jece.client.defaultcomparator.DefaultComparator;
import com.hamusuke.jece.client.defaultcomparator.DefaultComparators;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

import java.util.List;

@Environment(EnvType.CLIENT)
public class SelectDefaultsScreen extends Screen {
    private final Screen parent;
    private DefaultList defaultList;

    public SelectDefaultsScreen(Screen parent) {
        super(new TranslatableText("jece.select.default.screen"));
        this.parent = parent;
    }

    protected void init() {
        super.init();
        this.defaultList = new DefaultList(this.client);
        this.children.add(this.defaultList);
        this.addButton(new ButtonWidget(this.width / 4, this.height - 20, this.width / 2, 20, ScreenTexts.DONE, (b) -> this.onClose()));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.defaultList.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 6, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void onClose() {
        this.client.openScreen(this.parent);
    }

    @Environment(EnvType.CLIENT)
    class DefaultList extends ElementListWidget<DefaultList.DefaultEntry> {
        public DefaultList(MinecraftClient client) {
            super(client, SelectDefaultsScreen.this.width, SelectDefaultsScreen.this.height, 20, SelectDefaultsScreen.this.height - 20, SelectDefaultsScreen.this.height - 20 - 32);

            for (DefaultComparator defaultComparator : DefaultComparators.DEFAULT_COMPARATORS) {
                this.addEntry(new DefaultEntry(defaultComparator));
            }
        }

        protected int getScrollbarPositionX() {
            return this.width - 5;
        }

        public int getRowWidth() {
            return this.width / 2;
        }

        protected boolean isFocused() {
            return SelectDefaultsScreen.this.getFocused() == this;
        }

        protected int addEntry(DefaultEntry entry) {
            entry.init();
            return super.addEntry(entry);
        }

        @Environment(EnvType.CLIENT)
        class DefaultEntry extends ElementListWidget.Entry<DefaultEntry> {
            private final List<AbstractButtonWidget> buttons = Lists.newArrayList();
            private final DefaultComparator defaultComparator;
            private ButtonWidget useDefault;
            private ButtonWidget useConsoleEdition;

            private DefaultEntry(DefaultComparator defaultComparator) {
                this.defaultComparator = defaultComparator;
            }

            public void init() {
                this.useDefault = this.addButton(new ButtonWidget(0, 0, DefaultList.this.getRowWidth() / 2, 20, new TranslatableText("jece.select.default.use.default"), (b) -> {
                    b.active = false;
                    this.useConsoleEdition.active = true;
                    this.defaultComparator.onPressUseDefault();
                }));
                this.useConsoleEdition = this.addButton(new ButtonWidget(0, 0, DefaultList.this.getRowWidth() / 2, 20, new TranslatableText("jece.select.default.use.console.edition"), (b) -> {
                    b.active = false;
                    this.useDefault.active = true;
                    this.defaultComparator.onPressUseConsoleEdition();
                }));
                this.useDefault.active = !this.defaultComparator.isDefaultSelected();
                this.useConsoleEdition.active = !this.useDefault.active;
            }

            private <T extends AbstractButtonWidget> T addButton(T button) {
                this.buttons.add(button);
                return button;
            }

            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                this.defaultComparator.render(SelectDefaultsScreen.this.client, matrices, x, y, entryWidth, entryHeight);

                this.useDefault.x = x;
                this.useConsoleEdition.x = x + entryWidth / 2;
                this.useDefault.y = this.useConsoleEdition.y = y + entryHeight - 20;

                for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
                    abstractButtonWidget.render(matrices, mouseX, mouseY, tickDelta);
                }
            }

            public List<? extends Element> children() {
                return this.buttons;
            }

            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (!super.mouseClicked(mouseX, mouseY, button)) {
                    DefaultList.this.setFocused(this);
                    return true;
                }

                return false;
            }
        }
    }
}
