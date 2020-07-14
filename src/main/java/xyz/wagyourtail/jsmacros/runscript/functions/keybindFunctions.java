package xyz.wagyourtail.jsmacros.runscript.functions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.KeyCode;

public class keybindFunctions {
    
    
    public KeyCode getKeyCode(String keyName) {
        try {
            return InputUtil.fromName(keyName);
        } catch (Exception e) {
            return InputUtil.UNKNOWN_KEYCODE;
        }
    }
    
    public boolean isKeyPressed(String keyName) {
        KeyCode key = getKeyCode(keyName);
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), key.getKeyCode());
    }
    
    public void key(String keyName, boolean keyState) {
        key(getKeyCode(keyName), keyState);
    }
    
    public void key(KeyCode keyBind, boolean keyState) {
        KeyBinding.setKeyPressed(keyBind, keyState);
    }
}
