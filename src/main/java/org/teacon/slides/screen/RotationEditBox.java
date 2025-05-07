package org.teacon.slides.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class RotationEditBox extends EditBox {
    public RotationEditBox(int x, int y, int width, int height) {
        super(Minecraft.getInstance().font, x, y, width, height, Component.literal(""));
        super.setResponder(t -> {});
    }

    @Override
    public void setResponder(Consumer<String> listener) {
        super.setResponder(listener);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isFocused() && isVisible()) {
            float val;

            try {
                val = Float.parseFloat(getValue());
            } catch (NumberFormatException e) {
                val = 0f;
            }

            val += (float) scrollY;
            if (val > -360f && val < 360f) {
                setValue(Float.toString(val));
                setCursorPosition(0);
                setHighlightPos(0);
            }
        }
        
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }    
}