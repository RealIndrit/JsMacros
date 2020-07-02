package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.ArrayList;

import xyz.wagyourtail.jsmacros.runscript.classes.Draw3D;
import xyz.wagyourtail.jsmacros.runscript.classes.Draw2D;
import xyz.wagyourtail.jsmacros.runscript.classes.Screen;

import net.minecraft.client.MinecraftClient;

public class hudFunctions {
    public static ArrayList<Draw2D> overlays = new ArrayList<>();
    public static ArrayList<Draw3D> renders = new ArrayList<>();
    
    
    public Screen createScreen(String title, boolean dirtBG) {
        return new Screen(title, dirtBG);
    }
    
    public void openScreen(Screen s) {
        MinecraftClient.getInstance().openScreen(s);
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
    
    public ArrayList<Draw2D> listDraw2Ds() {
        return overlays;
    }
    
    public void clearDraw2Ds() {
        overlays.clear();
    }
    
    public Draw3D createDraw3D() {
        return null;
    }
    
    public void registerDraw3D(Draw3D draw) {
        if (!renders.contains(draw)) renders.add(draw);
    }
    
    public void unregisterDraw3D(Draw3D draw) {
        renders.remove(draw);
    }
    
    public ArrayList<Draw3D> listDraw3Ds() {
        return renders;
    }
    
    public void clearDraw3Ds() {
        renders.clear();
    }
}
