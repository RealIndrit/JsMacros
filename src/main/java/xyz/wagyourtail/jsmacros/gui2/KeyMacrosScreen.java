package xyz.wagyourtail.jsmacros.gui2;

import xyz.wagyourtail.jsmacros.jsMacros;
import xyz.wagyourtail.jsmacros.config.RawMacro;
import xyz.wagyourtail.jsmacros.gui2.containers.ConfirmOverlay;
import xyz.wagyourtail.jsmacros.gui2.containers.MacroContainer;
import xyz.wagyourtail.jsmacros.macros.MacroEnum;
import xyz.wagyourtail.jsmacros.profile.Profile;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

public class KeyMacrosScreen extends MacroScreen {
    
    public KeyMacrosScreen(Screen parent) {
        super(parent);
    }

    public void init() {
        super.init();
        keyScreen.setColor(0x4FFFFFFF);
        
        eventScreen.onPress = (btn) -> {
            minecraft.openScreen(new EventMacrosScreen(this));
        };

        profileScreen.onPress = (btn) -> {
            minecraft.openScreen(new ProfileScreen(this));
        };
        
        if (Profile.registry.getMacros().containsKey("KEY"))
            for (RawMacro macro : Profile.registry.getMacros().get("KEY").keySet()) {
                if (macro.type != MacroEnum.EVENT) addMacro(macro);
            }
        
        if (jsMacros.jythonFailed) {
            this.openOverlay(new ConfirmOverlay(width / 2 - 100, height / 2 - 50, 200, 100, minecraft.textRenderer, new TranslatableText("jsmacros.jythonfail"), this::addButton, this::removeButton, this::closeOverlay, (conf) -> {
                jsMacros.jythonFailed = false;
            }));
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (MacroContainer macro : macros) {
            if (!macro.keyPressed(keyCode, scanCode, modifiers)) return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (MacroContainer macro : macros) {
            if (!macro.mouseClicked(mouseX, mouseY, button)) return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
