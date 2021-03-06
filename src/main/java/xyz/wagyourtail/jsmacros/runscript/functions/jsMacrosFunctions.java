package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.network.ServerAddress;
import net.minecraft.util.Util;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.ConfigManager;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.macros.BaseMacro;
import xyz.wagyourtail.jsmacros.macros.IEventListener;
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

    public IEventListener on(String event, BiConsumer<String, Map<String, Object>> callback) {
        if (callback == null) return null;
        String tname = Thread.currentThread().getName();
        Thread th = Thread.currentThread();
        IEventListener listener = new IEventListener() {
            private LinkedBlockingQueue<Thread> tasks = new LinkedBlockingQueue<>();
            
            @Override
            public Thread trigger(String type, Map<String, Object> args) {
                Thread t = new Thread(() -> {
                    while(th.isAlive());
                    while (tasks.peek() != Thread.currentThread()) {
                        try {
                            tasks.peek().join();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                    Thread.currentThread().setName(this.toString());
                    try {
                        callback.accept(type, args);
                    } catch (Exception e) {
                        Profile.registry.removeListener(this);
                        e.printStackTrace();
                    }
                    try {
                        tasks.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                try {
                    tasks.put(t);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                t.start();
                return t;
            }

            @Override
            public String toString() {
                return String.format("EventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", tname, event);
            }
        };
        Profile.registry.addListener(event, listener);
        return listener;
    }
    
    public IEventListener once(String event, BiConsumer<String, Map<String, Object>> callback) {
        if (callback == null) return null;
        String tname = Thread.currentThread().getName();
        Thread th = Thread.currentThread();
        IEventListener listener = new IEventListener() {
            @Override
            public Thread trigger(String type, Map<String, Object> args) {
                Profile.registry.removeListener(this);
                Thread t = new Thread(() -> {
                    Thread.currentThread().setName(this.toString());
                    try {
                        while(th.isAlive());
                        callback.accept(type, args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                t.start();
                return t;
            }
            @Override
            public String toString() {
                return String.format("OnceEventListener:{\"creator\":\"%s\", \"event\":\"%s\"}", tname, event);
            }
            
        };
        Profile.registry.addListener(event, listener);
        return listener;
    }
    
    public boolean off(String event, IEventListener listener) {
        return Profile.registry.removeListener(event, listener);
    }
    
    public boolean off(IEventListener listener) {
        return Profile.registry.removeListener(listener);
    }
    
    public List<IEventListener> listeners(String event) {
        List<IEventListener> listeners = new ArrayList<>();
        for (IEventListener l : Profile.registry.getListeners(event)) {
            if (!(l instanceof BaseMacro)) listeners.add(l);
        }
        return listeners;
    }
    
    public String getFPS() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.fpsDebugString;
    }
    
    public String getServerAddress() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.getNetworkHandler().getConnection().getAddress().toString();
    }
    
    public void connect(String ip) {
        ServerAddress a = ServerAddress.parse(ip);
        connect(a.getAddress(), a.getPort());
    }
    
    public boolean connect(String ip, int port) {
        return hudFunctions.renderTaskQueue.add(() -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world != null) mc.world.disconnect();
            mc.joinWorld(null);
            mc.openScreen(new ConnectScreen(null, mc, ip, port));
        });
    }
    
    public boolean disconnect() {
        return hudFunctions.renderTaskQueue.add(() -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world != null) mc.world.disconnect();
            mc.joinWorld(null);
            mc.openScreen(new TitleScreen());
        });
    }
}