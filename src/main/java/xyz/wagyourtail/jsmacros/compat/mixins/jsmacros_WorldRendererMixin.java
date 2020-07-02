package xyz.wagyourtail.jsmacros.compat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import xyz.wagyourtail.jsmacros.runscript.classes.Draw3D;
import xyz.wagyourtail.jsmacros.runscript.functions.hudFunctions;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;

@Mixin(GameRenderer.class)
public class jsmacros_WorldRendererMixin {
    @Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {"ldc=hand"}), method = "renderCenter")
    public void jsmacros_render(float tickDelta, long endTime, CallbackInfo info) {
        for (Draw3D d : hudFunctions.renders) {
            d.render();
        }
    }
}
