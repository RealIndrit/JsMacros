package xyz.wagyourtail.jsmacros.runscript;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.script.ScriptEngine;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.HostAccess;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.runscript.functions.chatFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.globalVarFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.hudFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.jsMacrosFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.keybindFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.playerFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.timeFunctions;
import xyz.wagyourtail.jsmacros.runscript.functions.worldFunctions;

public class RunScript {
    public static HashMap<RawMacro, ArrayList<Thread>> threads = new HashMap<>();
    
    
    public static Thread exec(RawMacro macro, String event, HashMap<String, Object> args) {
        Thread t = new Thread(() -> {
            threads.putIfAbsent(macro, new ArrayList<>());
            Thread.currentThread().setName(macro.type.toString() + " " + macro.eventkey + " " + macro.scriptFile + ": " + threads.get(macro).size());
            threads.get(macro).add(Thread.currentThread());
            try {
                File file = new File(jsMacros.config.macroFolder, macro.scriptFile);
                if (file.exists()) {
                    Builder context = Context.newBuilder("js");
                    context.allowHostAccess(HostAccess.ALL);
                    context.allowHostClassLookup(s -> true);
                    context.allowAllAccess(true);
                    context.allowExperimentalOptions(true);
                    context.currentWorkingDirectory(Paths.get(file.getParent()));
                    ScriptEngine engine = GraalJSScriptEngine.create(null, context);
                    engine.put("event", event);
                    engine.put("args", args);
                    engine.put("file", file);
                    engine.put("global", new globalVarFunctions());
                    engine.put("jsmacros", new jsMacrosFunctions());
                    engine.put("time", new timeFunctions());
                    engine.put("keybind", new keybindFunctions());
                    engine.put("chat", new chatFunctions());
                    engine.put("world", new worldFunctions());
                    engine.put("player", new playerFunctions());
                    engine.put("hud", new hudFunctions());
                    engine.eval(new FileReader(file));
                }
            } catch (Exception e) {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.inGameHud != null) {
                    LiteralText text = new LiteralText(e.toString());
                    mc.inGameHud.getChatHud().addMessage(text);
                }
                e.printStackTrace();
            }
            threads.get(macro).remove(Thread.currentThread());
        });

        // function files
        
        
        t.start();
        return t;
    }
}