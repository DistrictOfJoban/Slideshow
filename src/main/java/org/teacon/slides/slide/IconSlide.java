package org.teacon.slides.slide;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.teacon.slides.Slideshow;
import org.teacon.slides.renderer.SlideRenderType;

public enum IconSlide implements Slide {
    DEFAULT_FAILED(ResourceLocation.tryBuild(Slideshow.ID, "textures/gui/slide_icon_failed.png")),
    DEFAULT_LOADING(ResourceLocation.tryBuild(Slideshow.ID, "textures/gui/slide_icon_loading.png"));

    private static final RenderType BACKGROUND_RENDER_TYPE = new SlideRenderType(ResourceLocation.tryBuild(Slideshow.ID, "textures/gui/slide_default.png"));

    private final RenderType iconRenderType;

    IconSlide(ResourceLocation icon) {
        iconRenderType = new SlideRenderType(icon);
    }

    @Override
    public void render(MultiBufferSource source, Matrix4f matrix, PoseStack.Pose normal, float width, float height, int color, int light, boolean front, boolean back, long tick, float partialTick) {
        var alpha = color >>> 24;
        var factor = getFactor(width, height);
        var xSize = Math.round(width / factor);
        var ySize = Math.round(height / factor);
        renderIcon(source, matrix, normal, alpha, light, xSize, ySize, front, back);
        renderBackground(source, matrix, normal, alpha, light, xSize, ySize, front, back);
    }

    private void renderIcon(MultiBufferSource source, Matrix4f matrix, PoseStack.Pose normal, int alpha, int light, int xSize, int ySize, boolean front, boolean back) {
        var builder = source.getBuffer(iconRenderType);
        var x1 = (1F - 19F / xSize) / 2F;
        var y1 = (1F - 16F / ySize) / 2F;
        var x2 = 1F - x1;
        var y2 = 1F - y1;
        if (front) {
            builder.addVertex(matrix, x1, 1F / 128F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, 1F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            builder.addVertex(matrix, x2, 1F / 128F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, 1F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            builder.addVertex(matrix, x2, 1F / 128F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, 0F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            builder.addVertex(matrix, x1, 1F / 128F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, 0F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
        }
        if (back) {
            builder.addVertex(matrix, x1, -1F / 128F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, 0F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            builder.addVertex(matrix, x2, -1F / 128F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, 0F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            builder.addVertex(matrix, x2, -1F / 128F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, 1F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            builder.addVertex(matrix, x1, -1F / 128F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, 1F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
        }
    }

    private void renderBackground(MultiBufferSource source, Matrix4f matrix, PoseStack.Pose normal, int alpha, int light, int xSize, int ySize, boolean front, boolean back) {
        var consumer = source.getBuffer(BACKGROUND_RENDER_TYPE);
        var x1 = 9F / xSize;
        var y1 = 9F / ySize;
        var u1 = 9F / 19F;
        var x2 = 1F - x1;
        var y2 = 1F - y1;
        var u2 = 1F - u1;
        if (front) {
            consumer.addVertex(matrix, 0F, 1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, u1).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x1, 1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u1).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x1, 1F / 256F, 0F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, 0F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, 0F, 1F / 256F, 0F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, 0F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, 0F, 1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, u2).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x1, 1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u2).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x1, 1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u1).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, 0F, 1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, u1).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, 0F, 1F / 256F, 1F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, 1F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x1, 1F / 256F, 1F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, 1F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x1, 1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u2).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, 0F, 1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, u2).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x1, 1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u1).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x2, 1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u1).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x2, 1F / 256F, 0F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, 0F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x1, 1F / 256F, 0F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, 0F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x1, 1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u2).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x2, 1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u2).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x2, 1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u1).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x1, 1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u1).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x1, 1F / 256F, 1F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, 1F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x2, 1F / 256F, 1F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, 1F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x2, 1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u2).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x1, 1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u2).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x2, 1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u1).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, 1F, 1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, u1).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, 1F, 1F / 256F, 0F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, 0F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x2, 1F / 256F, 0F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, 0F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x2, 1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u2).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, 1F, 1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, u2).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, 1F, 1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, u1).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x2, 1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u1).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x2, 1F / 256F, 1F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, 1F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, 1F, 1F / 256F, 1F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, 1F).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, 1F, 1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, u2).setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, x2, 1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u2).setLight(light)
                    .setNormal(normal, 0, 1, 0);
        }
        if (back) {
            consumer.addVertex(matrix, 0F, -1F / 256F, 0F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, 0F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x1, -1F / 256F, 0F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, 0F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x1, -1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u1).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, 0F, -1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, u1).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, 0F, -1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, u1).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x1, -1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u1).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x1, -1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u2).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, 0F, -1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, u2).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, 0F, -1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, u2).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x1, -1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u2).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x1, -1F / 256F, 1F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, 1F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, 0F, -1F / 256F, 1F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(0F, 1F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x1, -1F / 256F, 0F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, 0F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x2, -1F / 256F, 0F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, 0F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x2, -1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u1).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x1, -1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u1).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x1, -1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u1).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x2, -1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u1).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x2, -1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u2).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x1, -1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u2).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x1, -1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, u2).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x2, -1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u2).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x2, -1F / 256F, 1F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, 1F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x1, -1F / 256F, 1F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u1, 1F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x2, -1F / 256F, 0F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, 0F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, 1F, -1F / 256F, 0F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, 0F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, 1F, -1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, u1).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x2, -1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u1).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x2, -1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u1).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, 1F, -1F / 256F, y1)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, u1).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, 1F, -1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, u2).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x2, -1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u2).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x2, -1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, u2).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, 1F, -1F / 256F, y2)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, u2).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, 1F, -1F / 256F, 1F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(1F, 1F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, x2, -1F / 256F, 1F)
                    .setColor(255, 255, 255, alpha)
                    .setUv(u2, 1F).setLight(light)
                    .setNormal(normal, 0, -1, 0);
        }
    }

    private static float getFactor(float width, float height) {
        return Math.min(width, height) / (24 + Mth.fastInvCubeRoot(0.00390625F / (width * width + height * height)));
    }

    @Override
    public void close() {
    }
}