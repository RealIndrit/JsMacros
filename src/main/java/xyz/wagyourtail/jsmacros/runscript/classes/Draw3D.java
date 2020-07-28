package xyz.wagyourtail.jsmacros.runscript.classes;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;

public class Draw3D {
    public List<box> boxes = new ArrayList<>();
    public List<line> lines = new ArrayList<>();
    
    public box addBox(double x1, double y1, double z1, double x2, double y2, double z2, int color, int fillColor, boolean fill) {
        box b = new box(x1, y1, z1, x2, y2, z2, color, fillColor, fill);
        boxes.add(b);
        return b;
    }
    
    public box addBox(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha, int fillColor, int fillAlpha, boolean fill) {
        box b = new box(x1, y1, z1, x2, y2, z2, color, alpha, fillColor, fillAlpha, fill);
        boxes.add(b);
        return b;
    }
    
    public Draw3D removeBox(box b) {
        boxes.remove(b);
        return this;
    }
    
    public List<box> getBoxes() {
        return boxes;
    }

    public line addLine(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        line l = new line(x1, y1, z1, x2, y2, z2, color);
        lines.add(l);
        return l;
    }
    
    public line addLine(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha) {
        line l = new line(x1, y1, z1, x2, y2, z2, color, alpha);
        lines.add(l);
        return l;
    }
    
    public Draw3D removeLine(line l) {
        lines.remove(l);
        return this;
    }
    
    public List<line> getLines() {
        return lines;
    }

    public void render() {
        MinecraftClient mc = MinecraftClient.getInstance();
        
        List<box> boxes;
        List<line> lines;
        
        try {
            boxes = ImmutableList.copyOf(this.boxes);
            lines = ImmutableList.copyOf(this.lines);
        } catch (Exception e) {
            return;
        }
        
        // setup
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.lineWidth(2.5F);
        GlStateManager.disableTexture();
        GlStateManager.disableDepthTest();
        GlStateManager.matrixMode(5889);

        GlStateManager.pushMatrix();

        // offsetRender
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d camPos = camera.getPos();

        // render
        for (box b : boxes) {
            b.render(camPos);
        }

        for (line l : lines) {
            l.render(camPos);
        }

        GlStateManager.popMatrix();

        // reset
        GlStateManager.matrixMode(5888);
        GlStateManager.enableDepthTest();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }

    public static class box {
        public double x1;
        public double y1;
        public double z1;
        public double x2;
        public double y2;
        public double z2;
        public int color;
        public int fillColor;
        public boolean fill;

        public box(double x1, double y1, double z1, double x2, double y2, double z2, int color, int fillColor, boolean fill) {
            setPos(x1, y1, z1, x2, y2, z2);
            setColor(color);
            setFillColor(fillColor);
            this.fill = fill;
        }
        
        public box(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha, int fillColor, int fillAlpha, boolean fill) {
            setPos(x1, y1, z1, x2, y2, z2);
            setColor(color, alpha);
            setFillColor(fillColor, fillAlpha);
            this.fill = fill;
        }
        
        public void setPos(double x1, double y1, double z1, double x2, double y2, double z2) {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
        }
        
        
        
        public void setColor(int color) {
            if (color <= 0xFFFFFF) color = color | 0xFF000000;
            this.color = color;
        }

        public void setFillColor(int fillColor) {
            this.fillColor = fillColor;
        }
        
        public void setColor(int color, int alpha) {
            this.color = color | (alpha << 24);
        }
        
        public void setAlpha(int alpha) {
            this.color = (color & 0xFFFFFF) | (alpha << 24);
        }
        
        public void setFillColor(int fillColor, int alpha) {
            this.fillColor = fillColor | (alpha << 24);
        }
        
        public void setFillAlpha(int alpha) {
            this.fillColor = (fillColor & 0xFFFFFF) | (alpha << 24);
        }
        
        public void setFill(boolean fill) {
            this.fill = fill;
        }

        public void render(Vec3d camPos) {
            int a = (color >> 24) & 0xFF;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();

            if (this.fill) {
                float fa = ((fillColor >> 24) & 0xFF) / 255F;
                float fr = ((fillColor >> 16) & 0xFF) / 255F;
                float fg = ((fillColor >> 8) & 0xFF) / 255F;
                float fb = (fillColor & 0xFF) / 255F;

                buf.begin(GL11.GL_TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

                WorldRenderer.buildBox(buf, x1 - camPos.x, y1 - camPos.y, z1 - camPos.z, x2 - camPos.x, y2 - camPos.y, z2 - camPos.z, fr, fg, fb, fa);

                tess.draw();
            }

            buf.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);

            buf.vertex(x1 - camPos.x, y1 - camPos.y, z1 - camPos.z).color(r, g, b, a).next();
            buf.vertex(x1 - camPos.x, y1 - camPos.y, z2 - camPos.z).color(r, g, b, a).next();
            buf.vertex(x2 - camPos.x, y1 - camPos.y, z2 - camPos.z).color(r, g, b, a).next();
            buf.vertex(x2 - camPos.x, y1 - camPos.y, z1 - camPos.z).color(r, g, b, a).next();
            buf.vertex(x1 - camPos.x, y1 - camPos.y, z1 - camPos.z).color(r, g, b, a).next();
            buf.vertex(x1 - camPos.x, y2 - camPos.y, z1 - camPos.z).color(r, g, b, a).next();
            buf.vertex(x2 - camPos.x, y2 - camPos.y, z1 - camPos.z).color(r, g, b, a).next();
            buf.vertex(x2 - camPos.x, y2 - camPos.y, z2 - camPos.z).color(r, g, b, a).next();
            buf.vertex(x1 - camPos.x, y2 - camPos.y, z2 - camPos.z).color(r, g, b, a).next();
            buf.vertex(x1 - camPos.x, y2 - camPos.y, z1 - camPos.z).color(r, g, b, a).next();
            buf.vertex(x1 - camPos.x, y1 - camPos.y, z2 - camPos.z).color(r, g, b, 0).next();
            buf.vertex(x1 - camPos.x, y2 - camPos.y, z2 - camPos.z).color(r, g, b, a).next();
            buf.vertex(x2 - camPos.x, y1 - camPos.y, z2 - camPos.z).color(r, g, b, 0).next();
            buf.vertex(x2 - camPos.x, y2 - camPos.y, z2 - camPos.z).color(r, g, b, a).next();
            buf.vertex(x2 - camPos.x, y1 - camPos.y, z1 - camPos.z).color(r, g, b, 0).next();
            buf.vertex(x2 - camPos.x, y2 - camPos.y, z1 - camPos.z).color(r, g, b, a).next();
            
            tess.draw();
        }
    }

    public static class line {
        public double x1;
        public double y1;
        public double z1;
        public double x2;
        public double y2;
        public double z2;
        public int color;

        public line(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
            setPos(x1, y1, z1, x2, y2, z2);
            setColor(color);
        }
        
        public line(double x1, double y1, double z1, double x2, double y2, double z2, int color, int alpha) {
            setPos(x1, y1, z1, x2, y2, z2);
            setColor(color, alpha);
        }
        
        public void setPos(double x1, double y1, double z1, double x2, double y2, double z2) {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
        }

        public void setColor(int color) {
            if (color <= 0xFFFFFF) color = color | 0xFF000000;
            this.color = color;
        }
        
        public void setColor(int color, int alpha) {
            this.color = color | (alpha << 24);
        }
        
        public void setAlpha(int alpha) {
            this.color = (color & 0xFFFFFF) | (alpha << 24);
        }
        
        public void render(Vec3d camPos) {
            int a = (color >> 24) & 0xFF;
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;
            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();
            buf.begin(GL11.GL_LINE_STRIP, VertexFormats.POSITION_COLOR);
            buf.vertex(x1 - camPos.x, y1 - camPos.y, z1 - camPos.z).color(r, g, b, a).next();
            buf.vertex(x1 - camPos.x, y1 - camPos.y, z1 - camPos.z).color(r, g, b, a).next();
            buf.vertex(x2 - camPos.x, y2 - camPos.y, z2 - camPos.z).color(r, g, b, a).next();
            tess.draw();
        }
    }
}
