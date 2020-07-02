package xyz.wagyourtail.jsmacros.gui2.containers;

import java.util.function.Consumer;

import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.gui2.elements.Button;
import xyz.wagyourtail.jsmacros.gui2.elements.MultiElementContainer;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;
import xyz.wagyourtail.jsmacros.profile.Profile;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;

public class MacroListTopbar extends MultiElementContainer {
    public MacroEnum deftype;
    private Consumer<RawMacro> addMacro;
    private String enabled;
    private String type;
    private String file;
    
    public MacroListTopbar(int x, int y, int width, int height, TextRenderer textRenderer, MacroEnum deftype, Consumer<AbstractButtonWidget> addButton, Consumer<RawMacro> addMacro) {
        super(x, y, width, height, textRenderer, addButton);
        this.deftype = deftype;
        this.addMacro = addMacro;
        init();
    }

    public void init() {
        super.init();
        enabled = I18n.translate("Enabled");
        type = I18n.translate(deftype == MacroEnum.EVENT ? "jsmacros.events" : "jsmacros.keys");
        file = I18n.translate("jsmacros.file");
        
        addButton(new Button(x + width - 13, y+1, 12, height - 3, 0, 0xFF000000, 0x7F7F7F7F, 0xFFFFFFFF, new LiteralText("+"), (btn) -> {
            RawMacro macro = new RawMacro(deftype, "", "", false);
            Profile.registry.addMacro(macro);
            addMacro.accept(macro);
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        fill(x, y, x + width, y + 1, 0xFFFFFFFF);
        fill(x, y + height - 2, x + width, y + height - 1, 0xFFFFFFFF);
        fill(x, y + height - 1, x + width, y + height, 0xFF7F7F7F);
        fill(x, y + 1, x + 1, y + height - 1, 0xFFFFFFFF);
        fill(x + width - 1, y + 1, x + width, y + height - 1, 0xFFFFFFFF);
        int w = this.width - 12;
        
        drawCenteredString(this.textRenderer, textRenderer.trimToWidth(enabled, w/12), x + (w / 24), y + 2, 0xFFFFFF);
        fill(x + (w / 12), y + 1, x + (w / 12) + 1, y + height - 1, 0xFFFFFFFF);
        drawCenteredString(this.textRenderer, type, x + (w / 6), y + 2, 0xFFFFFF);
        fill(x + (w / 4), y + 1, x + (w / 4) + 1, y + height - 1, 0xFFFFFFFF);
        drawCenteredString(this.textRenderer, file, x + (w * 15 / 24), y + 2, 0xFFFFFF);
        fill(x + width - 14, y + 1, x + width - 13, y + height - 1, 0xFFFFFFFF);
    }
}
