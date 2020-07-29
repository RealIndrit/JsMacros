package xyz.wagyourtail.jsmacros.gui2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.LiteralText;
import xyz.wagyourtail.jsmacros.gui2.containers.RunningThreadContainer;
import xyz.wagyourtail.jsmacros.gui2.elements.Button;
import xyz.wagyourtail.jsmacros.gui2.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.runscript.RunScript;
import xyz.wagyourtail.jsmacros.runscript.RunScript.thread;

public class CancelScreen extends Screen {
    protected Screen parent;
    private int topScroll;
    private Scrollbar s;
    private List<RunningThreadContainer> running = new ArrayList<>();

    public CancelScreen(Screen parent) {
        super(new LiteralText("Cancel"));
        this.parent = parent;
    }

    public void init() {
        super.init();
        topScroll = 10;
        running.clear();
        s = this.addButton(new Scrollbar(width - 12, 5, 8, height-10, 0, 0xFF000000, 0xFFFFFFFF, 2, this::onScrollbar));
    }

    public void addContainer(thread t) {
        running.add(new RunningThreadContainer(10, topScroll + running.size() * 15, width - 26, 13, minecraft.textRenderer, this::addButton, this::removeContainer, t));
        Collections.sort(running, new RTCSort());
        s.setScrollPages(running.size() * 15 / (double)(height - 20));
    }

    public void removeContainer(RunningThreadContainer t) {
        for (AbstractButtonWidget b : t.getButtons()) {
            buttons.remove(b);
            children.remove(b);
        }
        running.remove(t);
        s.setScrollPages(running.size() * 15 / (double)(height - 20));
        updatePos();
    }

    private void onScrollbar(double page) {
        topScroll = 10 - (int) (page * (height - 20));
        updatePos();
    }

    public void updatePos() {
        for (int i = 0; i < running.size(); ++i) {
            if (topScroll + i * 15 < 10 || topScroll + i * 15 > height - 10) running.get(i).setVisible(false);
            else {
                running.get(i).setVisible(true);
                running.get(i).setPos(10, topScroll + i * 15, width - 26, 13);
            }
        }
    }
    
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        s.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground(0);
        
        List<thread> tl;
        List<RunningThreadContainer> running;
        List<AbstractButtonWidget> buttons;
        
        try {
            tl = RunScript.getThreads();
            running = ImmutableList.copyOf(this.running);
            buttons = ImmutableList.copyOf(this.buttons);
        } catch (Exception e) {
            return;
        }
        
        for (RunningThreadContainer r : running) {
            tl.remove(r.t);
            r.render(mouseX, mouseY, delta);
        }
        
        for (thread t : tl) {
            addContainer(t);
        }

        for (AbstractButtonWidget b : buttons) {
            ((Button) b).render(mouseX, mouseY, delta);
        }
    }

    public void removed() {
        minecraft.keyboard.enableRepeatEvents(false);
    }

    public void onClose() {
        minecraft.openScreen(parent);
    }

    public static class RTCSort implements Comparator<RunningThreadContainer> {
        public int compare(RunningThreadContainer arg0, RunningThreadContainer arg1) {
            try {
            return arg0.t.t.getName().compareTo(arg1.t.t.getName());
            } catch(NullPointerException e) {
                return 0;
            }
        }

    }
}
