package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Entry;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import xyz.wagyourtail.jsmacros.compat.interfaces.IBossBarHud;
import xyz.wagyourtail.jsmacros.events.BlockUpdateCallback;
import xyz.wagyourtail.jsmacros.events.BossBarCallback;
import xyz.wagyourtail.jsmacros.events.ChunkLoadCallback;
import xyz.wagyourtail.jsmacros.events.ChunkUnloadCallback;
import xyz.wagyourtail.jsmacros.events.DeathCallback;
import xyz.wagyourtail.jsmacros.events.JoinCallback;
import xyz.wagyourtail.jsmacros.events.PlayerJoinCallback;
import xyz.wagyourtail.jsmacros.events.PlayerLeaveCallback;
import xyz.wagyourtail.jsmacros.events.TitleCallback;
import xyz.wagyourtail.jsmacros.reflector.BlockDataHelper;
import xyz.wagyourtail.jsmacros.reflector.BossBarHelper;
import xyz.wagyourtail.jsmacros.reflector.ClientPlayerEntityHelper;
import xyz.wagyourtail.jsmacros.reflector.TextHelper;

@Mixin(ClientPlayNetworkHandler.class)
class jsmacros_ClientPlayNetworkHandler {
    
    @Shadow
    private MinecraftClient client;
    @Shadow
    private ClientWorld world;
    @Shadow
    @Final
    private ClientConnection connection;
    
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;showsDeathScreen()Z"), method="onCombatEvent", cancellable = true)
    private void jsmacros_onDeath(final CombatEventS2CPacket packet, CallbackInfo info) {
        DeathCallback.EVENT.invoker().interact();
    }
    
    @Inject(at = @At("HEAD"), method = "onPlayerList")
    public void jsmacros_onPlayerList(PlayerListS2CPacket packet, CallbackInfo info) {
        switch (packet.getAction()) {
            case ADD_PLAYER:
                for (Entry e : packet.getEntries()) {
                    PlayerJoinCallback.EVENT.invoker().interact(e.getProfile().getId(), e.getProfile().getName());
                }
            case REMOVE_PLAYER:
                for (Entry e : packet.getEntries()) {
                    PlayerLeaveCallback.EVENT.invoker().interact(e.getProfile().getId(), e.getProfile().getName());
                }
            default:
                break;
        }
    }
    
    @Inject(at = @At("HEAD"), method = "onTitle")
    public void jsmacros_onTitle(TitleS2CPacket packet, CallbackInfo info) {
        String type = null;
        switch(packet.getAction()) {
            case TITLE:
                type = "TITLE";
                break;
            case SUBTITLE:
                type = "SUBTITLE";
                break;
            case ACTIONBAR:
                type = "ACTIONBAR";
                break;
            default:
                break;
        }
        if (type != null) {
            TitleCallback.EVENT.invoker().interact(type, new TextHelper(packet.getText()));
        }
    }
    
    @Inject(at = @At("TAIL"), method="onBossBar")
    public void jsmacros_onBossBar(BossBarS2CPacket packet, CallbackInfo info) {
        String type = null;
        switch(packet.getType()) {
        case ADD:
            type = "ADD";
            break;
        case REMOVE:
            type = "REMOVE";
            break;
        case UPDATE_NAME:
            type = "UPDATE_NAME";
            break;
        case UPDATE_PCT:
            type = "UPDATE_PERCENT";
            break;
        case UPDATE_PROPERTIES:
            type = "UPDATE_PROPERTIES";
            break;
        case UPDATE_STYLE:
            type = "UPDATE_STYLE";
            break;
        default:
            break;
        }
        
        BossBarCallback.EVENT.invoker().interact(type, packet.getUuid().toString(), packet.getType() == BossBarS2CPacket.Type.REMOVE ? null : new BossBarHelper(((IBossBarHud) client.inGameHud.getBossBarHud()).getBossBars().get(packet.getUuid())));
    }
    
    @Inject(at = @At("TAIL"), method="onGameJoin")
    public void jsmacros_onGameJoin(GameJoinS2CPacket packet, CallbackInfo info) {
        JoinCallback.EVENT.invoker().interact(connection.getAddress().toString(), new ClientPlayerEntityHelper(client.player));
    }
    
    @Inject(at = @At("TAIL"), method="onChunkData")
    public void jsmacros_onChunkData(ChunkDataS2CPacket packet, CallbackInfo info) {
        if (packet.isFullChunk()) {
            ChunkLoadCallback.EVENT.invoker().interact(packet.getX(), packet.getZ());
        }
    }
    
    @Inject(at = @At("TAIL"), method="onBlockUpdate")
    public void jsmacros_onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo info) {
        BlockUpdateCallback.EVENT.invoker().interact(new BlockDataHelper(packet.getState(), world.getBlockEntity(packet.getPos()), packet.getPos()));
    }
    
    @Inject(at = @At("TAIL"), method="onUnloadChunk")
    public void jsmacros_onUnloadChunk(UnloadChunkS2CPacket packet, CallbackInfo info) {
        ChunkUnloadCallback.EVENT.invoker().interact(packet.getX(), packet.getZ());
    }
    
}

