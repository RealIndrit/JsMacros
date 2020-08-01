package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.KeyCode;

public class keybindFunctions extends Functions {
    
    public keybindFunctions(String libName) {
        super(libName);
    }
    
    public keybindFunctions(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
    public KeyCode getKeyCode(String keyName) {
        try {
            return InputUtil.fromName(keyName);
        } catch (Exception e) {
            return InputUtil.UNKNOWN_KEYCODE;
        }
    }
    
    public Map<String, String> getKeyBindings() {
        MinecraftClient mc = MinecraftClient.getInstance();
        Map<String, String> keys = new HashMap<>();
        for (KeyBinding key : mc.options.keysAll) {
            keys.put(key.getId(), key.getName());
        }
        return keys;
    }
    
    public void setKeyBind(String bind, String key) {
        MinecraftClient mc = MinecraftClient.getInstance();
        for (KeyBinding keybind : mc.options.keysAll) {
            if (keybind.getName().equals(bind)) {
                keybind.setKeyCode(InputUtil.fromName(key));
                return;
            }
        }
    }
    
    public void key(String keyName, boolean keyState) {
        key(getKeyCode(keyName), keyState);
    }
    
    public void key(Key keyBind, boolean keyState) {
        if (keyState) KeyBinding.onKeyPressed(keyBind);
        KeyBinding.setKeyPressed(keyBind, keyState);
    }
    
    public void keyBind(String keyBind, boolean keyState) {
        MinecraftClient mc = MinecraftClient.getInstance();
        for (KeyBinding key : mc.options.keysAll) {
            if (key.getName().equals(keyBind)) {
                if (keyState) KeyBinding.onKeyPressed(InputUtil.fromName(key.getName()));
                KeyBinding.setKeyPressed(InputUtil.fromName(key.getName()), keyState);
                return;
            }
        }
    }
    
    public void key(KeyBinding keyBind, boolean keyState) {
        if (keyState) KeyBinding.onKeyPressed(InputUtil.fromName(keyBind.getName()));
        KeyBinding.setKeyPressed(InputUtil.fromName(keyBind.getName()), keyState);
    }
}
