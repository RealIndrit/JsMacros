package xyz.wagyourtail.jsmacros.reflector;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.GameOptions;
import net.minecraft.util.Arm;

public class OptionsHelper {
    GameOptions options;
    public OptionsHelper(GameOptions options) {
        this.options = options;
    }
    public int getCloudMode() {
        switch (options.getCloudRenderMode()) {
            case FANCY:
                return 2;
            case FAST:
                return 1;
            default:
                return 0;
        }
    }
    public void setCloudMode(int mode) {
        switch(mode) {
            case 2:
                options.cloudRenderMode = CloudRenderMode.FANCY;
                return;
            case 1:
                options.cloudRenderMode = CloudRenderMode.FAST;
                return;
            default:
                options.cloudRenderMode = CloudRenderMode.OFF;
        }
    }
    public int getGraphicsMode() {
        return options.fancyGraphics ? 1 : 0;
    }
    public void setGraphicsMode(int mode) {
        options.fancyGraphics = mode == 1;
    }
    public List<String> getResourcePacks() {
        return new ArrayList<>(options.resourcePacks);
    }
    public boolean isRightHanded() {
        return options.mainArm == Arm.RIGHT;
    }
    public void setRightHanded(boolean val) {
        if (val) {
            options.mainArm = Arm.RIGHT;
        } else {
            options.mainArm = Arm.LEFT;
        }
    }
    public double getFov() {
        return options.fov;
    }
    public void setFov(double fov) {
        options.fov = fov;
    }
    public int getRenderDistance() {
        return options.viewDistance;
    }
    public void setRenderDistance(int d) {
        options.viewDistance = d;
    }
}
