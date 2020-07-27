package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.client.gui.widget.ButtonWidget;

public class ButtonWidgetHelper {
    private ButtonWidget btn;
    
    public ButtonWidgetHelper(ButtonWidget btn) {
        this.btn = btn;
    }
    
    public int getX() {
        return btn.x;
    }
    
    public int getY() {
        return btn.y;
    }
    
    public ButtonWidgetHelper setPos(int x, int y) {
        btn.x = x;
        btn.y = y;
        return this;
    }
    
    public int getWidth() {
        return btn.getWidth();
    }
    
    public ButtonWidgetHelper setText(String message) {
        btn.setMessage(message);
        return this;
    }
    
    public boolean getActive() {
        return btn.active;
    }
    
    public ButtonWidgetHelper setActive(boolean t) {
        btn.active = t;
        return this;
    }
    
    public ButtonWidgetHelper setWidth(int width) {
        btn.setWidth(width);
        return this;
    }
    
    public ButtonWidget getRaw() {
        return btn;
    }
}
