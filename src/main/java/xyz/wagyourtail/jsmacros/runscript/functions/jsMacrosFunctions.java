package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.ConfigManager;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;
import xyz.wagyourtail.jsmacros.profile.Profile;
import xyz.wagyourtail.jsmacros.reflector.OptionsHelper;
import xyz.wagyourtail.jsmacros.runscript.RunScript;
import xyz.wagyourtail.jsmacros.runscript.RunScript.thread;

public class jsMacrosFunctions extends Functions {

    public jsMacrosFunctions(String libName) {
        super(libName);
    }
    
    public jsMacrosFunctions(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
    public MinecraftClient getMinecraft() {
        return MinecraftClient.getInstance();
    }

    public Profile getProfile() {
        return jsMacros.profile;
    }

    public ConfigManager getConfig() {
        return jsMacros.config;
    }
    
    public OptionsHelper getGameOptions() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return new OptionsHelper(mc.options);
    }

    public Map<RawMacro, List<thread>> getRunningThreads() {
        return RunScript.threads;
    }

    public String mcVersion() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.getGameVersion();
    }

    public void runScript(String file) {
        runScript(file, null);
    }

    public void runScript(String file, Consumer<String> callback) {
        if (callback != null) {
            RunScript.exec(new RawMacro(MacroEnum.EVENT, "", file, true), "", null, () -> {
                callback.accept(null);
            }, callback);
        } else {
            RunScript.exec(new RawMacro(MacroEnum.EVENT, "", file, true), "", null);
        }
    }
    
    public void open(String s) {
        Util.getOperatingSystem().open(s);
    }

    public String getFPS() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.fpsDebugString;
    }
}