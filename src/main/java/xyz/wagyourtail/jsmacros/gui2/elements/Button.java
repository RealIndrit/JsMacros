package xyz.wagyourtail.jsmacros.gui2.elements;

import java.util.ArrayList;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.text.Text;

public class Button extends AbstractPressableButtonWidget {
    protected int color;
    protected int borderColor;
    protected int hilightColor;
    protected MinecraftClient mc;
    protected int textColor;
    protected ArrayList<String> text;
    protected int lines;
    protected int vcenter;
    public Consumer<Button> onPress;
    public boolean hovering = false;
    
    public Button(int x, int y, int width, int height, int color, int borderColor, int hilightColor, int textColor, Text message, Consumer<Button> onPress) {
        super(x, y, width, height, message.getString());
        this.color = color;
        this.borderColor = borderColor;
        this.hilightColor = hilightColor;
        this.textColor = textColor;
        this.mc = MinecraftClient.getInstance();
        this.setMessage(message);
        this.onPress = onPress;
    }
    
    public void setPos(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void setMessage(Text message) {
        super.setMessage(message.getString());
        this.text = new ArrayList<>(this.mc.textRenderer.wrapStringToWidthAsList(message.getString(), width - 4));
        this.lines = Math.min(Math.max((height - 2) / mc.textRenderer.fontHeight, 1), text.size());
        this.vcenter = ((height - 4) - (lines * mc.textRenderer.fontHeight)) / 2;
    }
    
    public void setColor(int color) {
        this.color = color;
    }
    
    public void setHilightColor(int color) {
        this.hilightColor = color;
    }
    
    protected void renderMessage() {
        for (int i = 0; i < lines; ++i) {
            drawCenteredString(mc.textRenderer, text.get(i), x + width / 2, y + 2 + vcenter + (i * mc.textRenderer.fontHeight), textColor);
        }
    }
    
    public void render(int mouseX, int mouseY, float delta) {
        if (this.visible) {
            // fill
            if (mouseX - x >= 0 && mouseX - x - width <= 0 && mouseY - y >= 0 && mouseY - y - height <= 0 && this.active) {
                hovering = true;
                fill(x + 1, y + 1, x + width - 1, y + height - 1, hilightColor);
            } else {
                hovering = false;
                fill(x + 1, y + 1, x + width - 1, y + height - 1, color);
            }
            // outline
            fill(x, y, x + 1, y + height, borderColor);
            fill(x + width - 1, y, x + width, y + height, borderColor);
            fill(x + 1, y, x + width - 1, y + 1, borderColor);
            fill(x + 1, y + height - 1, x + width - 1, y + height, borderColor);
            this.renderMessage();
        }
    }
    
    public void onClick(double mouseX, double mouseY) {
        //super.onClick(mouseX, mouseY);
    }
    
    public void onRelease(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
    }
    
    @Override
    public void onPress() {
        if (onPress != null) onPress.accept(this);
    }

}
