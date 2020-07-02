package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import xyz.wagyourtail.jsmacros.events.DeathCallback;

@Mixin(ClientPlayNetworkHandler.class)
class jsmacros_ClientPlayNetworkHandler {
    
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V"), method="onCombatEvent", cancellable = true)
    private void jsmacros_onDeath(final CombatEventS2CPacket packet, CallbackInfo info) {
        DeathCallback.EVENT.invoker().interact();
    }
}

