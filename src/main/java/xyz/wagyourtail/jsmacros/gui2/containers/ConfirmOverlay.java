package xyz.wagyourtail.jsmacros.gui2.containers;

import java.util.ArrayList;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import xyz.wagyourtail.jsmacros.gui2.elements.Button;
import xyz.wagyourtail.jsmacros.gui2.elements.OverlayContainer;

public class ConfirmOverlay extends OverlayContainer {
    private Consumer<ConfirmOverlay> accept;
    private ArrayList<String> text;
    private int lines;
    private int vcenter;
    private MinecraftClient mc;
    
    public ConfirmOverlay(int x, int y, int width, int height, TextRenderer textRenderer, Text message, Consumer<AbstractButtonWidget> addButton, Consumer<AbstractButtonWidget> removeButton, Consumer<OverlayContainer> close, Consumer<ConfirmOverlay>accept) {
        super(x, y, width, height, textRenderer, addButton, removeButton, close);
        this.mc = MinecraftClient.getInstance();
        this.setMessage(message);
        this.accept = accept;
    }
    
    public void setMessage(Text message) {
        this.text = new ArrayList<>(textRenderer.wrapStringToWidthAsList(message.getString(), width - 6));
        this.lines = Math.min(Math.max((height - 15) / textRenderer.fontHeight, 1), text.size());
        this.vcenter = ((height - 15) - (lines * mc.textRenderer.fontHeight)) / 2;
    }
    
    public void init() {
        super.init();
        
        this.addButton(new Button(x + 2, y+height-12, (width - 4) / 2, 10, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("gui.cancel"), (btn) -> {
            this.close();
        }));
        
        this.addButton(new Button(x + (width - 4) / 2 + 2, y+height-12, (width - 4) / 2, 10, 0, 0, 0x7FFFFFFF, 0xFFFFFF, new TranslatableText("jsmacros.confirm"), (btn) -> {
            if (this.accept != null) this.accept.accept(this);
            this.close();
        }));
        
    }
    
    protected void renderMessage() {
        for (int i = 0; i < lines; ++i) {
            drawCenteredString(textRenderer, text.get(i), x + width / 2, y + 2 + vcenter + (i * mc.textRenderer.fontHeight), 0xFFFFFF);
        }
    }
    
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        fill(x + 1, y + height - 13, x + width - 1, y + height - 12, 0xFFFFFFFF);
        this.renderMessage();
        super.render(mouseX, mouseY, delta);
    }

}
