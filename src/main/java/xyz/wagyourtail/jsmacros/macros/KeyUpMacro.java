package xyz.wagyourtail.jsmacros.macros;

import java.util.Map;

import net.minecraft.client.util.InputUtil;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;

public class KeyUpMacro extends BaseMacro {
    private int mods;
    private String key;
    private boolean prevKeyState = false;
    
    public KeyUpMacro(RawMacro macro) {
        super(macro);
        String mods = "";
        this.mods = 0;
        try {
            String[] comb = macro.eventkey.split("\\+");
            int i = 0;
            boolean notfirst = false;
            for (String key : comb) {
                if (++i == comb.length) this.key = key;
                else {
                    if (notfirst) mods += "+";
                    mods += key;
                }
            }
            this.mods = jsMacros.getModInt(mods);
        } catch(Exception e) {
            key = InputUtil.UNKNOWN_KEYCODE.getName();
        }
    }
    
    @Override
    public Thread trigger(String type, Map<String, Object> args) {
        if (check(args)) {
            return runMacro(type, args);
        }
        return null;
    }
    
    private boolean check(Map<String, Object> args) {
        boolean keyState = false;
        if ((int)args.get("action") > 0) keyState = true;
        if (args.get("key").equals(key) && (jsMacros.getModInt((String)args.get("mods")) & mods) == mods)
            if (keyState && !prevKeyState) {
                prevKeyState = true;
                return false;
            } else if (!keyState && prevKeyState) {
                prevKeyState = false;
                return true;
            }
        return false;
    }
}