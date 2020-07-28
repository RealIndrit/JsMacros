package xyz.wagyourtail.jsmacros.reflector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.ClientResourcePackProfile;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.util.Arm;

public class OptionsHelper {
    private GameOptions options;
    private MinecraftClient mc = MinecraftClient.getInstance();
    private ResourcePackManager<ClientResourcePackProfile> rpm = mc.getResourcePackManager();
    
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
    public OptionsHelper setCloudMode(int mode) {
        switch(mode) {
            case 2:
                options.cloudRenderMode = CloudRenderMode.FANCY;
                return this;
            case 1:
                options.cloudRenderMode = CloudRenderMode.FAST;
                return this;
            default:
                options.cloudRenderMode = CloudRenderMode.OFF;
                return this;
        }
    }
    public int getGraphicsMode() {
        return options.fancyGraphics ? 1 : 0;
    }
    public OptionsHelper setGraphicsMode(int mode) {
        options.fancyGraphics = mode == 1;
        return this;
    }
    public List<String> getResourcePacks() {
        return new ArrayList<String>(rpm.getProfiles().stream().map(e -> e.getName()).collect(Collectors.toList()));
    }
    
    public List<String> getEnabledResourcePacks() {
        return new ArrayList<String>(rpm.getEnabledProfiles().stream().map(e -> e.getName()).collect(Collectors.toList()));
    }
    
    public OptionsHelper setEnabledResourcePacks(String[] enabled) {
        Collection<String> en = new ArrayList<String>(Arrays.asList(enabled).stream().distinct().collect(Collectors.toList()));
        List<String> currentRP = ImmutableList.copyOf(options.resourcePacks);
        Collection<ClientResourcePackProfile> prof = new ArrayList<ClientResourcePackProfile>(en.stream().map(e -> rpm.getProfile(e)).filter(e -> e != null).collect(Collectors.toList()));
        rpm.setEnabledProfiles(prof);
        options.resourcePacks.clear();
        options.incompatibleResourcePacks.clear();
        for (ResourcePackProfile p : rpm.getEnabledProfiles()) {
            if (!p.isPinned()) {
                options.resourcePacks.add(p.getName());
                if (!p.getCompatibility().isCompatible()) {
                    options.incompatibleResourcePacks.add(p.getName());
                }
            }
        }
        options.write();
        List<String> newRP = ImmutableList.copyOf(options.resourcePacks);
        if (!currentRP.equals(newRP)) {
            mc.reloadResources();
        }
        return this;
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
    public OptionsHelper setFov(double fov) {
        options.fov = fov;
        return this;
    }
    public int getRenderDistance() {
        return options.viewDistance;
    }
    public void setRenderDistance(int d) {
        options.viewDistance = d;
    }
    
    public GameOptions getRaw() {
        return options;
    }
}
