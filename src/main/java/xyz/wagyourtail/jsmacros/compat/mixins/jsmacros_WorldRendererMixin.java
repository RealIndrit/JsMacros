package xyz.wagyourtail.jsmacros.compat.mixins;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import xyz.wagyourtail.jsmacros.runscript.classes.Draw3D;
import xyz.wagyourtail.jsmacros.runscript.functions.hudFunctions;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public class jsmacros_WorldRendererMixin {
    @Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {"ldc=hand"}), method = "renderCenter")
    public void jsmacros_render(float tickDelta, long endTime, CallbackInfo info) {
        
        ArrayList<Draw3D> renders;
        
        try {
            renders = new ArrayList<>(hudFunctions.renders);
        } catch (Exception e) {
            return;
        }
        
        for (Draw3D d : renders) {
            d.render();
        }
    }
}
