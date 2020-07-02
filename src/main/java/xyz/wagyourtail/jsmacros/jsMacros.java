package xyz.wagyourtail.jsmacros;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.io.File;
import java.io.InputStream;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.python.util.PythonInterpreter;

import xyz.wagyourtail.jsmacros.config.ConfigManager;
import xyz.wagyourtail.jsmacros.profile.Profile;
import xyz.wagyourtail.jsmacros.gui2.KeyMacrosScreen;

public class jsMacros implements ClientModInitializer {
    public static final String MOD_ID = "jsmacros";
    public static ConfigManager config = new ConfigManager();
    public static Profile profile;
    public static KeyMacrosScreen keyMacrosScreen;
    public static boolean jythonFailed = true;
    public static String jythonFailStack;
    
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
        
        Thread t = new Thread(() -> {
            Builder build = Context.newBuilder("js");
            Context con = build.build();
            con.eval("js", "console.log('js loaded.')");
            try {
                PythonInterpreter interp = new PythonInterpreter();
                interp.exec("print('py loaded.')");
                interp.close();
                jythonFailed = false;
            } catch (Exception e) {
                jythonFailStack = e.getStackTrace().toString();
                e.printStackTrace();
            }
        });
        t.start();
    }

    static public Text getKeyText(String translationKey) {
        try {
            return new LiteralText(getLocalizedName(InputUtil.fromName(translationKey)));
        } catch (Exception e) {
            return new LiteralText(translationKey);
        }
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
    
    
}