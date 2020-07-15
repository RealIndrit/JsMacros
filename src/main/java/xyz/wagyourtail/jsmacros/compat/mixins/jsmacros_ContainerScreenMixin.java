package xyz.wagyourtail.jsmacros.compat.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Slot;
import xyz.wagyourtail.jsmacros.compat.interfaces.IInventory;

@Mixin(ContainerScreen.class)
class jsmacros_ContainerScreenMixin implements IInventory {

    @Shadow
    private Slot getSlotAt(double x, double y) {
        return null;
    }
    
    @Override
    public Slot getSlotUnder(double x, double y) {
        return getSlotAt(x, y);
    }
    
}