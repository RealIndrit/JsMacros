package xyz.wagyourtail.jsmacros;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.python.util.PythonInterpreter;

import jep.JepConfig;
import jep.SharedInterpreter;
import xyz.wagyourtail.jsmacros.config.ConfigManager;
import xyz.wagyourtail.jsmacros.profile.Profile;
import xyz.wagyourtail.jsmacros.gui2.KeyMacrosScreen;

public class jsMacros implements ClientModInitializer {
    public static final String MOD_ID = "jsmacros";
    public static ConfigManager config = new ConfigManager();
    public static Profile profile;
    public static KeyMacrosScreen keyMacrosScreen;
    public static boolean jythonFailed = false;
    public static boolean jepFailed = false;
    public static String pythonFailStack;
    
    @Override
    public void onInitializeClient() {
        
        //janky hack mate
        try {
            if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
                InputStream f = this.getClass().getClassLoader().getResourceAsStream("META-INF/jars/jython-standalone-2.7.2.jar");
                File fi = new File(FabricLoader.getInstance().getGameDirectory(), "mods/jython-standalone-2.7.2.jar");
                if (!fi.exists()) FileUtils.copyInputStreamToFile(f, fi);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        config.loadConfig();
        profile = new Profile(config.options.defaultProfile);
        keyMacrosScreen = new KeyMacrosScreen(null);
        
        if (config.options.JEPSharedLibraryPath == null) config.options.JEPSharedLibraryPath = "./jep.dll";
        
        File f = new File(FabricLoader.getInstance().getGameDirectory(), config.options.JEPSharedLibraryPath);
        if (f.exists()) {
            File fo = new File(System.getProperty("java.library.path"), f.getName());
            if (!fo.exists()) {
                try {
                    FileUtils.copyFile(f, fo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        preInitLanguages();
    }
    
    static public void preInitLanguages() {
        Thread t = new Thread(() -> {
            Builder build = Context.newBuilder("js");
            Context con = build.build();
            con.eval("js", "console.log('js loaded.')");
            con.close();
            try {
                if (config.options.enableJEP) {
                    jepFailed = true;
                    JepConfig c = new JepConfig();
                    c.setRedirectOutputStreams(true);
                    SharedInterpreter.setConfig(c);
                    SharedInterpreter interp = new SharedInterpreter();
                    interp.exec("print('JEP Loaded.')");
                    interp.close();
                    jepFailed = false;
                } else {
                    jythonFailed = true;
                    PythonInterpreter interp = new PythonInterpreter();
                    interp.exec("print('jython loaded.')");
                    interp.close();
                    jythonFailed = false;
                }
            } catch (Exception e) {
                pythonFailStack = e.getStackTrace().toString();
                e.printStackTrace();
            }
        });
        t.start();
    }

    static public Text getKeyText(String translationKey) {
        try  {
            return new LiteralText(getLocalizedName(InputUtil.fromName(translationKey)));
        } catch(Exception e) {
            return new LiteralText(translationKey);
        }
    }
    
    static public String getKeyModifiers(int mods) {
        String s = "";
        if ((mods & 1) == 1) {
            s += "key.keyboard.left.shift";
        }
        if ((mods & 2) == 2) {
            if (s.length() > 0) s += "+";
            s += "key.keyboard.left.control";
        }
        if ((mods & 4) == 4) {
            if (s.length() > 0) s += "+";
            s += "key.keyboard.left.alt";
        }
        return s;
    }
    
    static public int getModInt(String mods) {
        int i = 0;
        String[] modArr = mods.split("\\+");
        for (String mod : modArr) {
            switch (mod) {
                case "key.keyboard.left.shift":
                    i |= 1;
                    break;
                case "key.keyboard.left.control":
                    i |= 2;
                    break;
                case "key.keyboard.left.alt":
                    i |= 4;
                    break;
                default:
            }
        }
        return i;
        
    }
    
    static public String getScreenName(Screen s) {
        if (s == null) return null;
        if (s instanceof ContainerScreen) {
               //add more ?
            if (s instanceof GenericContainerScreen) {
                return String.format("%d Row Chest", ((GenericContainerScreen) s).getContainer().getRows());
            } else if (s instanceof Generic3x3ContainerScreen) {
                return "3x3 Container";
            } else if (s instanceof AnvilScreen) {
                return "Anvil";
            } else if (s instanceof BeaconScreen) {
                return "Beacon";
            } else if (s instanceof BlastFurnaceScreen) {
                return "Blast Furnace";
            } else if (s instanceof BrewingStandScreen) {
                return "Brewing Stand";
            } else if (s instanceof CraftingTableScreen) {
                return "Crafting Table";
            } else if (s instanceof EnchantingScreen) {
                return "Enchanting Table";
            } else if (s instanceof FurnaceScreen) {
                return "Furnace";
            } else if (s instanceof GrindstoneScreen) {
                return "Grindstone";
            } else if (s instanceof HopperScreen) {
                return "Hopper";
            } else if (s instanceof LecternScreen) {
                return "Lectern";
            } else if (s instanceof LoomScreen) {
                return "Loom";
            } else if (s instanceof MerchantScreen) {
                return "Villager";
            } else if (s instanceof ShulkerBoxScreen) {
                return "Shulker Box";
            } else if (s instanceof SmokerScreen) {
                return "Smoker";
            } else if (s instanceof CartographyTableScreen) {
                return "Cartography Table";
            } else if (s instanceof StonecutterScreen) {
                return "Stonecutter";
            } else if (s instanceof InventoryScreen) {
                return "Survival Inventory";
            } else if (s instanceof HorseScreen) {
                return "Horse";
            } else if (s instanceof CreativeInventoryScreen) {
                return "Creative Inventory";
            } else {
                return s.getClass().getName();
            }
        } else if (s instanceof ChatScreen) {
            return "Chat";
        }
        return s.getTitle().getString();
    }
    
    @Deprecated
    static public String getLocalizedName(InputUtil.KeyCode keyCode) {
        String string = keyCode.getName();
        int i = keyCode.getKeyCode();
        String string2 = null;
        switch(keyCode.getCategory()) {
        case KEYSYM:
           string2 = InputUtil.getKeycodeName(i);
           break;
        case SCANCODE:
           string2 = InputUtil.getScancodeName(i);
           break;
        case MOUSE:
           String string3 = I18n.translate(string);
           string2 = Objects.equals(string3, string) ? I18n.translate(InputUtil.Type.MOUSE.getName(), i + 1) : string3;
        }

        return string2 == null ? I18n.translate(string) : string2;
     }
    
    @Deprecated
    static public MinecraftClient getMinecraft() {
        return MinecraftClient.getInstance();
    }

    public static int[] range(int end) {
        return range(0, end, 1);
    }
    
    public static int[] range(int start, int end) {
        return range(start, end, 1);
    }
    
    public static int[] range(int start, int end, int iter) {
        int[] a = new int[end-start];
        for (int i = start; i < end; i+=iter) {
            a[i-start] = i;
        }
        return a;
    }
    
}