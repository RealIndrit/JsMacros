package xyz.wagyourtail.jsmacros.runscript.functions;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import xyz.wagyourtail.jsmacros.reflector.TextHelper;

public class chatFunctions extends Functions {
    
    public chatFunctions(String libName) {
        super(libName);
    }
    
    public chatFunctions(String libName, List<String> excludeLanguages) {
        super(libName, excludeLanguages);
    }

    private void logInternal(String message) {
        if (message != null) {
            MinecraftClient mc = MinecraftClient.getInstance();
            LiteralText text = new LiteralText(message);
            mc.inGameHud.getChatHud().addMessage(text, 0);
        }
    }
    
    private void logInternal(TextHelper text) {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.inGameHud.getChatHud().addMessage(text.getRaw(), 0);
    }
    
    // yay, auto type coercion.
    public void log(Object message) {
        if (message instanceof TextHelper) {
            this.logInternal((TextHelper)message);
        } else if (message != null) {
            this.logInternal(message.toString());
        }
    }
    
    public void say(String message) {
        if (message != null) {
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.player.sendChatMessage(message);
        }
    }
    
    public void title(Object title, Object subtitle, int fadeIn, int remain, int fadeOut) {
        String titlee = null;
        String subtitlee = null;
        if (title instanceof TextHelper) titlee = ((TextHelper) title).getRaw().getString();
        else if (title != null) titlee = title.toString();
        if (subtitle instanceof TextHelper) subtitlee = ((TextHelper) subtitle).getRaw().getString();
        else if (subtitle != null) subtitlee = subtitle.toString();
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.inGameHud.setTitles(titlee, subtitlee, fadeIn, remain, fadeOut);
    }
    
    public void actionbar(Object text, boolean tinted) {
        Text textt = null;
        if (text instanceof TextHelper) textt = ((TextHelper) text).getRaw();
        else if (text != null) textt = new LiteralText(text.toString());
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.inGameHud.setOverlayMessage(textt, tinted);
    }
    
    public TextHelper createTextHelperFromString(String content) {
        return new TextHelper(new LiteralText(content));
    }
    
    public TextHelper createTextHelperFromJSON(String json) {
        return new TextHelper(json);
    }
}
