package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.ArrayList;
import java.util.List;

import xyz.wagyourtail.jsmacros.runscript.classes.Draw3D;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.runscript.classes.Draw2D;
import xyz.wagyourtail.jsmacros.runscript.classes.Screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;

public class hudFunctions extends Functions {
    public static List<Draw2D> overlays = new ArrayList<>();
    public static List<Draw3D> renders = new ArrayList<>();
    
    public hudFunctions(String libName) {
        super(libName);
    }
    
    public hudFunctions(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }
    
    public Screen createScreen(String title, boolean dirtBG) {
        return new Screen(title, dirtBG);
    }
    
    public void openScreen(Screen s) {
        MinecraftClient.getInstance().openScreen(s);
    }
    
    public String getOpenScreen() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return jsMacros.getScreenName(mc.currentScreen);
    }
    
    public boolean isContainer() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.currentScreen instanceof ContainerScreen;
    }
    
    public Draw2D createDraw2D() {
        return new Draw2D();
    }
    
    public void registerDraw2D(Draw2D overlay) {
        overlay.init();
        if (!overlays.contains(overlay)) overlays.add(overlay);
    }
    
    public void unregisterDraw2D(Draw2D overlay) {
        overlays.remove(overlay);
    }
    
    public List<Draw2D> listDraw2Ds() {
        return overlays;
    }
    
    public void clearDraw2Ds() {
        overlays.clear();
    }
    
    public Draw3D createDraw3D() {
        return new Draw3D();
    }
    
    public void registerDraw3D(Draw3D draw) {
        if (!renders.contains(draw)) renders.add(draw);
    }
    
    public void unregisterDraw3D(Draw3D draw) {
        renders.remove(draw);
    }
    
    public List<Draw3D> listDraw3Ds() {
        return renders;
    }
    
    public void clearDraw3Ds() {
        renders.clear();
    }
    
    public double getMouseX() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.mouse.getX() * (double)mc.window.getScaledWidth() / (double)mc.window.getWidth();
    }
    
    public double getMouseY() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.mouse.getY() * (double)mc.window.getScaledHeight() / (double)mc.window.getHeight();
    }
}
