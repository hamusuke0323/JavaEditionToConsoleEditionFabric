package com.hamusuke.jece.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.hamusuke.jece.client.JECEClient;
import com.hamusuke.jece.client.joystick.JoystickInputUtil;
import com.hamusuke.jece.client.joystick.JoystickKeybinding;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ControlsOptionsScreen extends Screen {
    public JoystickKeybinding focusedBinding;
    public long time;
    private ControlsListWidget keyBindingListWidget;
    private ButtonWidget resetButton;
    private final Screen parent;

    public ControlsOptionsScreen(Screen parent) {
        super(new TranslatableText("controls.title"));
        this.parent = parent;
    }

    protected void init() {
        this.keyBindingListWidget = new ControlsListWidget(this.client);
        this.children.add(this.keyBindingListWidget);
        this.resetButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, new TranslatableText("controls.resetAll"), (button) -> {
            for (JoystickKeybinding joystickKeybinding : JECEClient.jeceOptions.keysAll) {
                joystickKeybinding.setBoundKey(joystickKeybinding.getDefaultKey());
            }

            JoystickKeybinding.updateKeysByCode();
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, ScreenTexts.DONE, (button) -> this.onClose()));
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.focusedBinding != null) {
            if (keyCode == 256) {
                JECEClient.jeceOptions.setKeyCode(this.focusedBinding, JoystickInputUtil.UNKNOWN_KEY);
            }

            this.focusedBinding = null;
            this.time = Util.getMeasuringTimeMs();
            JoystickKeybinding.updateKeysByCode();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public boolean joystickButtonPressed(int key) {
        if (this.focusedBinding != null) {
            JECEClient.jeceOptions.setKeyCode(this.focusedBinding, JoystickInputUtil.fromKeyCode(key));
            this.focusedBinding = null;
            this.time = Util.getMeasuringTimeMs();
            JoystickKeybinding.updateKeysByCode();
            return true;
        } else {
            return false;
        }
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.keyBindingListWidget.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
        boolean b = false;
        for (JoystickKeybinding joystickKeybinding : JECEClient.jeceOptions.keysAll) {
            if (!joystickKeybinding.isDefault()) {
                b = true;
                break;
            }
        }

        this.resetButton.active = b;
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void onClose() {
        this.client.openScreen(this.parent);
    }

    @Environment(EnvType.CLIENT)
    class ControlsListWidget extends ElementListWidget<ControlsListWidget.Entry> {
        private int maxKeyNameLength;

        public ControlsListWidget(MinecraftClient client) {
            super(client, ControlsOptionsScreen.this.width + 45, ControlsOptionsScreen.this.height, 43, ControlsOptionsScreen.this.height - 32, 20);
            JoystickKeybinding[] joystickKeybindings = ArrayUtils.clone(JECEClient.jeceOptions.keysAll);
            Arrays.sort(joystickKeybindings);
            String string = null;
            for (JoystickKeybinding joystickKeybinding : joystickKeybindings) {
                String string2 = joystickKeybinding.getCategory();
                if (!string2.equals(string)) {
                    string = string2;
                    this.addEntry(new ControlsListWidget.CategoryEntry(new TranslatableText(string2)));
                }

                Text text = new TranslatableText(joystickKeybinding.getTranslationKey());
                int i = client.textRenderer.getWidth(text);
                if (i > this.maxKeyNameLength) {
                    this.maxKeyNameLength = i;
                }

                this.addEntry(new JoystickKeyBindingEntry(joystickKeybinding, text));
            }

        }

        protected int getScrollbarPositionX() {
            return super.getScrollbarPositionX() + 15;
        }

        public int getRowWidth() {
            return super.getRowWidth() + 32;
        }

        @Environment(EnvType.CLIENT)
        class JoystickKeyBindingEntry extends ControlsListWidget.Entry {
            private final JoystickKeybinding binding;
            private final Text bindingName;
            private final ButtonWidget editButton;
            private final ButtonWidget resetButton;

            private JoystickKeyBindingEntry(final JoystickKeybinding binding, final Text text) {
                this.binding = binding;
                this.bindingName = text;
                this.editButton = new ButtonWidget(0, 0, 75, 20, text, (button) -> ControlsOptionsScreen.this.focusedBinding = binding) {
                    protected MutableText getNarrationMessage() {
                        return binding.isUnbound() ? new TranslatableText("narrator.controls.unbound", text) : new TranslatableText("narrator.controls.bound", text, super.getNarrationMessage());
                    }
                };
                this.resetButton = new ButtonWidget(0, 0, 50, 20, new TranslatableText("controls.reset"), (button) -> {
                    JECEClient.jeceOptions.setKeyCode(binding, binding.getDefaultKey());
                    KeyBinding.updateKeysByCode();
                }) {
                    protected MutableText getNarrationMessage() {
                        return new TranslatableText("narrator.controls.reset", text);
                    }
                };
            }

            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                boolean b = ControlsOptionsScreen.this.focusedBinding == this.binding;
                TextRenderer textRenderer = ControlsListWidget.this.client.textRenderer;
                float f = (float) (x + 90 - ControlsListWidget.this.maxKeyNameLength);
                int i = y + entryHeight / 2;
                textRenderer.draw(matrices, this.bindingName, f, (float) (i - 9 / 2), 16777215);
                this.resetButton.x = x + 190;
                this.resetButton.y = y;
                this.resetButton.active = !this.binding.isDefault();
                this.resetButton.render(matrices, mouseX, mouseY, tickDelta);
                this.editButton.x = x + 105;
                this.editButton.y = y;
                this.editButton.setMessage(this.binding.getBoundKeyLocalizedText());
                boolean b1 = false;
                if (!this.binding.isUnbound()) {
                    for (JoystickKeybinding joystickKeybinding : JECEClient.jeceOptions.keysAll) {
                        if (joystickKeybinding != this.binding && this.binding.equals(joystickKeybinding)) {
                            b1 = true;
                            break;
                        }
                    }
                }

                if (b) {
                    this.editButton.setMessage((new LiteralText("> ")).append(this.editButton.getMessage().shallowCopy().formatted(Formatting.YELLOW)).append(" <").formatted(Formatting.YELLOW));
                } else if (b1) {
                    this.editButton.setMessage(this.editButton.getMessage().shallowCopy().formatted(Formatting.RED));
                }

                this.editButton.render(matrices, mouseX, mouseY, tickDelta);
            }

            public List<? extends Element> children() {
                return ImmutableList.of(this.editButton, this.resetButton);
            }

            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (this.editButton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                } else {
                    return this.resetButton.mouseClicked(mouseX, mouseY, button);
                }
            }

            public boolean mouseReleased(double mouseX, double mouseY, int button) {
                return this.editButton.mouseReleased(mouseX, mouseY, button) || this.resetButton.mouseReleased(mouseX, mouseY, button);
            }
        }

        @Environment(EnvType.CLIENT)
        class CategoryEntry extends ControlsListWidget.Entry {
            private final Text text;
            private final int textWidth;

            public CategoryEntry(Text text) {
                this.text = text;
                this.textWidth = ControlsListWidget.this.client.textRenderer.getWidth(this.text);
            }

            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                TextRenderer textRenderer = ControlsListWidget.this.client.textRenderer;
                Text text = this.text;
                float f = (float) (ControlsListWidget.this.client.currentScreen.width / 2 - this.textWidth / 2);
                int i = y + entryHeight;
                textRenderer.draw(matrices, text, f, (float) (i - 9 - 1), 16777215);
            }

            public boolean changeFocus(boolean lookForwards) {
                return false;
            }

            public List<? extends Element> children() {
                return Collections.emptyList();
            }
        }

        @Environment(EnvType.CLIENT)
        abstract class Entry extends net.minecraft.client.gui.widget.ElementListWidget.Entry<ControlsListWidget.Entry> {
            public Entry() {
            }
        }
    }
}
