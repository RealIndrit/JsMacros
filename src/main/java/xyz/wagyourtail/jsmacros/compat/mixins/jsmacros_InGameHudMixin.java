package xyz.wagyourtail.jsmacros.compat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.GlStateManager;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.hud.InGameHud;

import xyz.wagyourtail.jsmacros.runscript.classes.Draw2D;
import xyz.wagyourtail.jsmacros.runscript.functions.hudFunctions;

@Mixin(InGameHud.class)
class jsmacros_InGameHudMixin {
    @Inject(at = @At("HEAD"), method = "render")
    public void jsMacros_renderHud(float f, final CallbackInfo info) {
        
        for (Draw2D h : hudFunctions.overlays) {
            h.render();
        }
        
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlphaTest();
    }
}
