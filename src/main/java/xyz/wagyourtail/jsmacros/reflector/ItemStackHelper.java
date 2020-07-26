package xyz.wagyourtail.jsmacros.reflector;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.registry.Registry;

public class ItemStackHelper {
    private ItemStack i;
    
    public ItemStackHelper(ItemStack i) {
        this.i = i;
    }
    
    public int getDamage() {
        return i.getDamage();
    }
    
    public int getMaxDamage() {
        return i.getMaxDamage();
    }
    
    public String getName() {
        return i.getName().getString();
    }
    
    public int getCount() {
        return i.getCount();
    }
    
    public int getMaxCount() {
        return i.getMaxCount();
    }
    
    public String getNBT() {
        CompoundTag tag = i.getTag();
        if (tag != null) return tag.toString();
        else return "";
    }
    
    public String getCreativeTab() {
        ItemGroup g = i.getItem().getGroup();
        if (g != null)
            return g.getId();
        else
            return null;
    }
    
    public String getItemID() {
        return Registry.ITEM.getId(i.getItem()).toString();
    }
    
    public boolean isEmpty() {
        return i.isEmpty();
    }
    
    public ItemStack getRaw() {
        return i;
    }
    
    public String toString() {
        return String.format("ItemStack:{\"id\":\"%s\", \"Count\": %d}", this.getItemID(), this.getCount());
    }
}
