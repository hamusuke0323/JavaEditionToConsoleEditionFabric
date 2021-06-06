package com.hamusuke.jece.client.gui.screen;

import com.google.common.collect.Lists;
import com.hamusuke.jece.client.jececomparator.JECEComparator;
import com.hamusuke.jece.client.jececomparator.JECEComparators;
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
public class JECESwitcherScreen extends Screen {
    private final Screen parent;
    private SwitcherList switcherList;

    public JECESwitcherScreen(Screen parent) {
        super(new TranslatableText("jece.switcher.screen"));
        this.parent = parent;
    }

    protected void init() {
        super.init();
        this.switcherList = new SwitcherList(this.client);
        this.children.add(this.switcherList);
        this.addButton(new ButtonWidget(this.width / 4, this.height - 20, this.width / 2, 20, ScreenTexts.DONE, (b) -> this.onClose()));
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.switcherList.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 6, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void onClose() {
        this.client.openScreen(this.parent);
    }

    @Environment(EnvType.CLIENT)
    class SwitcherList extends ElementListWidget<SwitcherList.SwitcherEntry> {
        public SwitcherList(MinecraftClient client) {
            super(client, JECESwitcherScreen.this.width, JECESwitcherScreen.this.height, 20, JECESwitcherScreen.this.height - 20, JECESwitcherScreen.this.height - 20 - 32);

            for (JECEComparator defaultComparator : JECEComparators.JECE_COMPARATORS) {
                this.addEntry(new SwitcherEntry(defaultComparator));
            }
        }

        protected int getScrollbarPositionX() {
            return this.width - 5;
        }

        public int getRowWidth() {
            return this.width - 50;
        }

        protected boolean isFocused() {
            return JECESwitcherScreen.this.getFocused() == this;
        }

        protected int addEntry(SwitcherEntry entry) {
            entry.init();
            return super.addEntry(entry);
        }

        @Environment(EnvType.CLIENT)
        class SwitcherEntry extends ElementListWidget.Entry<SwitcherEntry> {
            private final List<AbstractButtonWidget> buttons = Lists.newArrayList();
            private final JECEComparator jeceComparator;
            private ButtonWidget useJE;
            private ButtonWidget useCE;

            private SwitcherEntry(JECEComparator defaultComparator) {
                this.jeceComparator = defaultComparator;
            }

            public void init() {
                this.useJE = this.addButton(new ButtonWidget(0, 0, SwitcherList.this.getRowWidth() / 2, 20, new TranslatableText("jece.switcher.switch.je"), (b) -> {
                    b.active = false;
                    this.useCE.active = true;
                    this.jeceComparator.onPressUseJE();
                }));
                this.useCE = this.addButton(new ButtonWidget(0, 0, SwitcherList.this.getRowWidth() / 2, 20, new TranslatableText("jece.switcher.switch.ce"), (b) -> {
                    b.active = false;
                    this.useJE.active = true;
                    this.jeceComparator.onPressUseCE();
                }));
                this.useJE.active = !this.jeceComparator.isJESelected();
                this.useCE.active = !this.useJE.active;
            }

            private <T extends AbstractButtonWidget> T addButton(T button) {
                this.buttons.add(button);
                return button;
            }

            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                this.jeceComparator.render(JECESwitcherScreen.this.client, matrices, x, y, entryWidth, entryHeight);

                this.useJE.x = x;
                this.useCE.x = x + entryWidth / 2;
                this.useJE.y = this.useCE.y = y + entryHeight - 20;

                for (AbstractButtonWidget abstractButtonWidget : this.buttons) {
                    abstractButtonWidget.render(matrices, mouseX, mouseY, tickDelta);
                }
            }

            public List<? extends Element> children() {
                return this.buttons;
            }
        }
    }
}