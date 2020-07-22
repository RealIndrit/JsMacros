package xyz.wagyourtail.jsmacros.reflector;

import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityHelper {
    protected Entity e;
    
    public EntityHelper(Entity e) {
        this.e = e;
    }
    
    public HashMap<String, Double> getPos() {
        if (e == null) return null;
        HashMap<String, Double> r = new HashMap<>();
        Vec3d pos = e.getPos();
        r.put("x", pos.x);
        r.put("y", pos.y);
        r.put("z", pos.z);
        return r;
    }
    
    public double getX() {
        return e.x;
    }

    public double getY() {
        return e.y;
    }
    
    public double getZ() {
        return e.z;
    }
    
    public float getPitch() {
        return e.pitch;
    }
    
    public float getYaw() {
        return e.yaw;
    }
    
    public String getName() {
        if (e == null) return null;
        return e.getName().toString();
    }
    
    public String getType() {
        if (e == null) return null;
        return e.getType().toString();
    }
    
    public void setGlowing(boolean val) {
        e.setGlowing(val);
    }
    
    public Entity getRaw() {
        return e;
    }
}