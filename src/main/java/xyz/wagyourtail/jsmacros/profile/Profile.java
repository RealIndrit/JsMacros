package xyz.wagyourtail.jsmacros.profile;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.ActionResult;
import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.*;
import xyz.wagyourtail.jsmacros.events.AirChangeCallback;
import xyz.wagyourtail.jsmacros.events.DamageCallback;
import xyz.wagyourtail.jsmacros.events.DeathCallback;
import xyz.wagyourtail.jsmacros.events.DimensionChangeCallback;
import xyz.wagyourtail.jsmacros.events.DisconnectCallback;
import xyz.wagyourtail.jsmacros.events.HeldItemCallback;
import xyz.wagyourtail.jsmacros.events.HungerChangeCallback;
import xyz.wagyourtail.jsmacros.events.ItemDamageCallback;
import xyz.wagyourtail.jsmacros.events.JoinCallback;
import xyz.wagyourtail.jsmacros.events.KeyCallback;
import xyz.wagyourtail.jsmacros.events.OpenScreenCallback;
import xyz.wagyourtail.jsmacros.events.PlayerJoinCallback;
import xyz.wagyourtail.jsmacros.events.PlayerLeaveCallback;
import xyz.wagyourtail.jsmacros.events.RecieveMessageCallback;
import xyz.wagyourtail.jsmacros.events.SendMessageCallback;
import xyz.wagyourtail.jsmacros.events.SoundCallback;
import xyz.wagyourtail.jsmacros.events.TitleCallback;
import xyz.wagyourtail.jsmacros.macros.*;
import xyz.wagyourtail.jsmacros.reflector.ItemStackHelper;
import xyz.wagyourtail.jsmacros.reflector.PlayerEntityHelper;
import xyz.wagyourtail.jsmacros.reflector.TextHelper;

public class Profile {
    public String profileName;
    public static MacroRegistry registry = new MacroRegistry();
    private static KeyBinding keyBinding;

    public Profile(String defaultProfile) {
        loadOrCreateProfile(defaultProfile);

        keyBinding = new KeyBinding("jsmacros.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "JS Macros");
        KeyBindingHelper.registerKeyBinding(keyBinding);

        initEventHandlerCallbacks();
    }

    public void loadOrCreateProfile(String pName) {
        registry.clearMacros();
        if (jsMacros.config.options.profiles.containsKey(pName)) {
            loadProfile(pName);
        } else {
            jsMacros.config.options.profiles.put(pName, new ArrayList<>());
            loadProfile(pName);
            jsMacros.config.saveConfig();
        }
    }

    private boolean loadProfile(String pName) {
        registry.clearMacros();
        ArrayList<RawMacro> rawProfile = jsMacros.config.options.profiles.get(pName);
        if (rawProfile == null) {
            System.out.println("profile \"" + pName + "\" does not exist or is broken/null");
            return false;
        }
        profileName = pName;
        for (RawMacro rawmacro : rawProfile) {
            registry.addMacro(rawmacro);
        }

        HashMap<String, Object> args = new HashMap<>();
        args.put("profile", pName);
        if (registry.macros.containsKey("PROFILE_LOAD")) for (BaseMacro macro : registry.macros.get("PROFILE_LOAD").values()) {
            macro.trigger("PROFILE_LOAD", args);
        }
        return true;
    }

    public ArrayList<RawMacro> toRawProfile() {
        ArrayList<RawMacro> rawProf = new ArrayList<>();
        for (HashMap<RawMacro, BaseMacro> eventMacros : registry.macros.values()) {
            for (RawMacro macro : eventMacros.keySet()) {
                rawProf.add(macro);
            }
        }
        return rawProf;
    }

    public void saveProfile() {
        jsMacros.config.options.profiles.put(profileName, toRawProfile());
        jsMacros.config.saveConfig();
    }

    @Deprecated
    public void addMacro(RawMacro rawmacro) {
        registry.addMacro(rawmacro);
    }

    @Deprecated
    public void removeMacro(RawMacro rawmacro) {
        if (toRawProfile().contains(rawmacro) && rawmacro != null) registry.removeMacro(rawmacro);
    }

    @Deprecated
    public BaseMacro getMacro(RawMacro rawMacro) {
        return registry.getMacro(rawMacro);
    }

    @Deprecated
    public HashMap<String, HashMap<RawMacro, BaseMacro>> getMacros() {
        return registry.macros;
    }

    private void initEventHandlerCallbacks() {

        // -------- JOIN ---------- //
        registry.addEvent("JOIN_SERVER");
        JoinCallback.EVENT.register((conn, player) -> {
            HashMap<String, Object> args = new HashMap<>();
            args.put("address", conn.getAddress().toString());
            args.put("player", new PlayerEntityHelper(player));
            if (registry.macros.containsKey("JOIN_SERVER")) for (BaseMacro macro : registry.macros.get("JOIN_SERVER").values()) {
                macro.trigger("JOIN_SERVER", args);
            }
        });
        
        // ----- DISCONNECT ------ // 
        registry.addEvent("DISCONNECT");
        DisconnectCallback.EVENT.register(() -> {
            HashMap<String, Object> args = new HashMap<>();
            if (registry.macros.containsKey("DISCONNECT")) for (BaseMacro macro : registry.macros.get("DISCONNECT").values()) {
                macro.trigger("DISCONNECT", args);
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                macro.trigger("DISCONNECT", args);
            }
        });
        
        // ----- SEND_MESSAGE -----//
        registry.addEvent("SEND_MESSAGE");
        SendMessageCallback.EVENT.register((message) -> {
            HashMap<String, Object> args = new HashMap<>();
            args.put("message", message);
            if (registry.macros.containsKey("SEND_MESSAGE")) for (BaseMacro macro : registry.macros.get("SEND_MESSAGE").values()) {
                try {
                    Thread t = macro.trigger("SEND_MESSAGE", args);
                    if (t != null) t.join();
                } catch (InterruptedException e1) {
                }
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                try {
                    Thread t = macro.trigger("SEND_MESSAGE", args);
                    if (t != null) t.join();
                } catch (InterruptedException e1) {
                }
            }

            message = (String) args.get("message");
            return message;
        });

        // ---- RECV_MESSAGE ---- //
        registry.addEvent("RECV_MESSAGE");
        RecieveMessageCallback.EVENT.register((message) -> {
            HashMap<String, Object> args = new HashMap<>();
            args.put("message", message);
            if (registry.macros.containsKey("RECV_MESSAGE")) for (BaseMacro macro : registry.macros.get("RECV_MESSAGE").values()) {
                try {
                    Thread t = macro.trigger("RECV_MESSAGE", args);
                    if (t != null) t.join();
                } catch (InterruptedException e1) {
                }
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                try {
                    Thread t = macro.trigger("RECV_MESSAGE", args);
                    if (t != null) t.join();
                } catch (InterruptedException e1) {
                }
            }

            message = (TextHelper) args.get("message");
            return message;
        });

        // ----- PLAYER JOIN ----- //
        registry.addEvent("PLAYER_JOIN");
        PlayerJoinCallback.EVENT.register((uuid, pName) -> {
            HashMap<String, Object> args = new HashMap<>();
            args.put("uuid", uuid.toString());
            args.put("player", pName);
            if (registry.macros.containsKey("PLAYER_JOIN")) for (BaseMacro macro : registry.macros.get("PLAYER_JOIN").values()) {
                macro.trigger("PLAYER_JOIN", args);
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                macro.trigger("PLAYER_JOIN", args);
            }
        });
        
        // ---- PLAYER LEAVE ----- //
        registry.addEvent("PLAYER_LEAVE");
        PlayerLeaveCallback.EVENT.register((uuid, pName) -> {
            HashMap<String, Object> args = new HashMap<>();
            args.put("uuid", uuid.toString());
            args.put("player", pName);
            if (registry.macros.containsKey("PLAYER_LEAVE")) for (BaseMacro macro : registry.macros.get("PLAYER_LEAVE").values()) {
                macro.trigger("PLAYER_LEAVE", args);
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                macro.trigger("PLAYER_LEAVE", args);
            }
        });
        
        // -------- TICK --------- //
        registry.addEvent("TICK");
        ClientTickEvents.END_CLIENT_TICK.register(e -> {
            if (registry.macros.containsKey("TICK")) for (BaseMacro macro : registry.macros.get("TICK").values()) {
                macro.trigger("TICK", new HashMap<>());
            }
        });

        // -------- KEY ----------- //
        registry.addEvent("KEY");
        KeyCallback.EVENT.register((window, key, scancode, action, mods) -> {
            InputUtil.KeyCode keycode;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.currentScreen != null && jsMacros.config.options.disableKeyWhenScreenOpen) return ActionResult.PASS;
            if (key == -1 || action == 2) return ActionResult.PASS;

            if (key <= 7) keycode = InputUtil.Type.MOUSE.createFromCode(key);
            else keycode = InputUtil.Type.KEYSYM.createFromCode(key);

            if (keycode == InputUtil.UNKNOWN_KEYCODE) return ActionResult.PASS;
            if (keyBinding.matchesKey(key, scancode) && action == 1 && mc.currentScreen == null) mc.openScreen(jsMacros.keyMacrosScreen);

            HashMap<String, Object> args = new HashMap<>();
            args.put("rawkey", keycode);
            if (action == 1) {
                if (key == 340 || key == 344) mods -= 1;
                else if (key == 341 || key == 345) mods -= 2;
                else if (key == 342 || key == 346) mods -= 4;
            }
            args.put("mods", jsMacros.getKeyModifiers(mods));
            args.put("key", keycode.getName());
            args.put("action", action);
            if (registry.macros.containsKey("KEY")) for (BaseMacro macro : registry.macros.get("KEY").values()) {
                macro.trigger("KEY", args);
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                macro.trigger("KEY", args);
            }

            return ActionResult.PASS;
        });
        
        // ------ AIR CHANGE ------ //
        registry.addEvent("AIR_CHANGE");
        AirChangeCallback.EVENT.register((air) -> {
            HashMap<String, Object> args = new HashMap<>();
            args.put("air", air);
            if (registry.macros.containsKey("AIR_CHANGE")) for (BaseMacro macro : registry.macros.get("AIR_CHANGE").values()) {
                macro.trigger("AIR_CHANGE", args);
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                macro.trigger("AIR_CHANGE", args);
            }
        });

        // ------ DAMAGE -------- //
        registry.addEvent("DAMAGE");
        DamageCallback.EVENT.register((source, health, change) -> {
            HashMap<String, Object> args = new HashMap<>();
            args.put("source", source.getName());
            args.put("health", health);
            args.put("change", change);
            if (registry.macros.containsKey("DAMAGE")) for (BaseMacro macro : registry.macros.get("DAMAGE").values()) {
                macro.trigger("DAMAGE", args);
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                macro.trigger("DAMAGE", args);
            }
        });

        // ------ DEATH -------- //
        registry.addEvent("DEATH");
        DeathCallback.EVENT.register(() -> {
            HashMap<String, Object> args = new HashMap<>();
            if (registry.macros.containsKey("DEATH")) for (BaseMacro macro : registry.macros.get("DEATH").values()) {
                macro.trigger("DEATH", args);
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                macro.trigger("DEATH", args);
            }
        });

        // ----- ITEM DAMAGE ----- //
        registry.addEvent("ITEM_DAMAGE");
        ItemDamageCallback.EVENT.register((stack, damage) -> {
            HashMap<String, Object> args = new HashMap<>();
            args.put("stack", new ItemStackHelper(stack));
            args.put("damage", damage);
            if (registry.macros.containsKey("ITEM_DAMAGE")) for (BaseMacro macro : registry.macros.get("ITEM_DAMAGE").values()) {
                macro.trigger("ITEM_DAMAGE", args);
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                macro.trigger("ITEM_DAMAGE", args);
            }
        });

        // ----- HUNGER CHANGE ------ //
        registry.addEvent("HUNGER_CHANGE");
        HungerChangeCallback.EVENT.register((foodLevel) -> {
            HashMap<String, Object> args = new HashMap<>();
            args.put("foodLevel", foodLevel);
            if (registry.macros.containsKey("HUNGER_CHANGE")) for (BaseMacro macro : registry.macros.get("HUNGER_CHANGE").values()) {
                macro.trigger("HUNGER_CHANGE", args);
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                macro.trigger("HUNGER_CHANGE", args);
            }
        });

        // ----- DIMENSION CHANGE --- //
        registry.addEvent("DIMENSION_CHANGE");
        DimensionChangeCallback.EVENT.register((dim) -> {
            HashMap<String, Object> args = new HashMap<>();
            args.put("dimension", dim);
            if (registry.macros.containsKey("DIMENSION_CHANGE")) for (BaseMacro macro : registry.macros.get("DIMENSION_CHANGE").values()) {
                macro.trigger("DIMENSION_CHANGE", args);
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                macro.trigger("DIMENSION_CHANGE", args);
            }
        });

        
        // ------ SOUND ------ //
        registry.addEvent("SOUND");
        SoundCallback.EVENT.register((sound) -> {
            HashMap<String, Object> args = new HashMap<>();
            args.put("sound", sound);
            if (registry.macros.containsKey("SOUND")) for (BaseMacro macro : registry.macros.get("SOUND").values()) {
                macro.trigger("SOUND", args);
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                macro.trigger("SOUND", args);
            }
        });

        
        // ------- OPEN SCREEN ------ //
        registry.addEvent("OPEN_SCREEN");
        OpenScreenCallback.EVENT.register((screen) -> {
            HashMap<String, Object> args = new HashMap<>();
            args.put("screen", screen);
            if (registry.macros.containsKey("OPEN_SCREEN")) for (BaseMacro macro : registry.macros.get("OPEN_SCREEN").values()) {
                macro.trigger("OPEN_SCREEN", args);
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                macro.trigger("OPEN_SCREEN", args);
            }
        });
        
        // ------- TITLE ------- //
        registry.addEvent("TITLE");
        TitleCallback.EVENT.register((type, message) -> {
            HashMap<String, Object> args = new HashMap<>();
            args.put("type", type);
            args.put("message", message);
            if (registry.macros.containsKey("TITLE")) for (BaseMacro macro : registry.macros.get("TITLE").values()) {
                macro.trigger("TITLE", args);
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                macro.trigger("TITLE", args);
            }
        });
        
        // ----- HELD ITEM ----- //
        registry.addEvent("HELD_ITEM");
        HeldItemCallback.EVENT.register((item) -> {
            HashMap<String, Object> args = new HashMap<>();
            args.put("item", item);
            if (registry.macros.containsKey("HELD_ITEM")) for (BaseMacro macro : registry.macros.get("HELD_ITEM").values()) {
                macro.trigger("HELD_ITEM", args);
            }

            if (registry.macros.containsKey("ANYTHING")) for (BaseMacro macro : registry.macros.get("ANYTHING").values()) {
                macro.trigger("HELD_ITEM", args);
            }
        });

    }
}