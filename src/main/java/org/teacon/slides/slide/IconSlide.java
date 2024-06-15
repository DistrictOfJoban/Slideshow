package org.teacon.slides.slide;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.teacon.slides.Slideshow;
import org.teacon.slides.renderer.SlideRenderType;

public enum IconSlide implements Slide {
    DEFAULT_FAILED(new ResourceLocation(Slideshow.ID, "textures/gui/slide_icon_failed.png")),
    DEFAULT_LOADING(new ResourceLocation(Slideshow.ID, "textures/gui/slide_icon_loading.png"));

    private static final RenderType BACKGROUND_RENDER_TYPE = new SlideRenderType(new ResourceLocation(Slideshow.ID, "textures/gui/slide_default.png"));

    private final RenderType iconRenderType;

    IconSlide(ResourceLocation icon) {
        iconRenderType = new SlideRenderType(icon);
    }

    @Override
    public void render(MultiBufferSource source, Matrix4f matrix, Matrix3f normal, float width, float height, int color, int light, boolean front, boolean back, long tick, float partialTick) {
        var alpha = color >>> 24;
        var factor = getFactor(width, height);
        var xSize = Math.round(width / factor);
        var ySize = Math.round(height / factor);
        renderIcon(source, matrix, normal, alpha, light, xSize, ySize, front, back);
        renderBackground(source, matrix, normal, alpha, light, xSize, ySize, front, back);
    }

    private void renderIcon(MultiBufferSource source, Matrix4f matrix, Matrix3f normal, int alpha, int light, int xSize, int ySize, boolean front, boolean back) {
        var builder = source.getBuffer(iconRenderType);
        var x1 = (1F - 19F / xSize) / 2F;
        var y1 = (1F - 16F / ySize) / 2F;
        var x2 = 1F - x1;
        var y2 = 1F - y1;
        if (front) {
            builder.vertex(matrix, x1, 1F / 128F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(0F, 1F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            builder.vertex(matrix, x2, 1F / 128F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(1F, 1F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            builder.vertex(matrix, x2, 1F / 128F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(1F, 0F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            builder.vertex(matrix, x1, 1F / 128F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(0F, 0F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
        }
        if (back) {
            builder.vertex(matrix, x1, -1F / 128F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(0F, 0F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            builder.vertex(matrix, x2, -1F / 128F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(1F, 0F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            builder.vertex(matrix, x2, -1F / 128F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(1F, 1F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            builder.vertex(matrix, x1, -1F / 128F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(0F, 1F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
        }
    }

    private void renderBackground(MultiBufferSource source, Matrix4f matrix, Matrix3f normal, int alpha, int light, int xSize, int ySize, boolean front, boolean back) {
        var consumer = source.getBuffer(BACKGROUND_RENDER_TYPE);
        var x1 = 9F / xSize;
        var y1 = 9F / ySize;
        var u1 = 9F / 19F;
        var x2 = 1F - x1;
        var y2 = 1F - y1;
        var u2 = 1F - u1;
        if (front) {
            consumer.vertex(matrix, 0F, 1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(0F, u1).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x1, 1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u1).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x1, 1F / 256F, 0F)
                    .color(255, 255, 255, alpha)
                    .uv(u1, 0F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, 0F, 1F / 256F, 0F)
                    .color(255, 255, 255, alpha)
                    .uv(0F, 0F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, 0F, 1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(0F, u2).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x1, 1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u2).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x1, 1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u1).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, 0F, 1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(0F, u1).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, 0F, 1F / 256F, 1F)
                    .color(255, 255, 255, alpha)
                    .uv(0F, 1F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x1, 1F / 256F, 1F)
                    .color(255, 255, 255, alpha)
                    .uv(u1, 1F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x1, 1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u2).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, 0F, 1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(0F, u2).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x1, 1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u1).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x2, 1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u1).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x2, 1F / 256F, 0F)
                    .color(255, 255, 255, alpha)
                    .uv(u2, 0F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x1, 1F / 256F, 0F)
                    .color(255, 255, 255, alpha)
                    .uv(u1, 0F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x1, 1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u2).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x2, 1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u2).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x2, 1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u1).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x1, 1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u1).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x1, 1F / 256F, 1F)
                    .color(255, 255, 255, alpha)
                    .uv(u1, 1F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x2, 1F / 256F, 1F)
                    .color(255, 255, 255, alpha)
                    .uv(u2, 1F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x2, 1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u2).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x1, 1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u2).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x2, 1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u1).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, 1F, 1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(1F, u1).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, 1F, 1F / 256F, 0F)
                    .color(255, 255, 255, alpha)
                    .uv(1F, 0F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x2, 1F / 256F, 0F)
                    .color(255, 255, 255, alpha)
                    .uv(u2, 0F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x2, 1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u2).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, 1F, 1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(1F, u2).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, 1F, 1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(1F, u1).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x2, 1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u1).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x2, 1F / 256F, 1F)
                    .color(255, 255, 255, alpha)
                    .uv(u2, 1F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, 1F, 1F / 256F, 1F)
                    .color(255, 255, 255, alpha)
                    .uv(1F, 1F).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, 1F, 1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(1F, u2).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, x2, 1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u2).uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
        }
        if (back) {
            consumer.vertex(matrix, 0F, -1F / 256F, 0F)
                    .color(255, 255, 255, alpha)
                    .uv(0F, 0F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x1, -1F / 256F, 0F)
                    .color(255, 255, 255, alpha)
                    .uv(u1, 0F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x1, -1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u1).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, 0F, -1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(0F, u1).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, 0F, -1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(0F, u1).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x1, -1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u1).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x1, -1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u2).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, 0F, -1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(0F, u2).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, 0F, -1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(0F, u2).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x1, -1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u2).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x1, -1F / 256F, 1F)
                    .color(255, 255, 255, alpha)
                    .uv(u1, 1F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, 0F, -1F / 256F, 1F)
                    .color(255, 255, 255, alpha)
                    .uv(0F, 1F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x1, -1F / 256F, 0F)
                    .color(255, 255, 255, alpha)
                    .uv(u1, 0F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x2, -1F / 256F, 0F)
                    .color(255, 255, 255, alpha)
                    .uv(u2, 0F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x2, -1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u1).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x1, -1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u1).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x1, -1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u1).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x2, -1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u1).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x2, -1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u2).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x1, -1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u2).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x1, -1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u1, u2).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x2, -1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u2).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x2, -1F / 256F, 1F)
                    .color(255, 255, 255, alpha)
                    .uv(u2, 1F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x1, -1F / 256F, 1F)
                    .color(255, 255, 255, alpha)
                    .uv(u1, 1F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x2, -1F / 256F, 0F)
                    .color(255, 255, 255, alpha)
                    .uv(u2, 0F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, 1F, -1F / 256F, 0F)
                    .color(255, 255, 255, alpha)
                    .uv(1F, 0F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, 1F, -1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(1F, u1).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x2, -1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u1).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x2, -1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u1).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, 1F, -1F / 256F, y1)
                    .color(255, 255, 255, alpha)
                    .uv(1F, u1).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, 1F, -1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(1F, u2).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x2, -1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u2).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x2, -1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(u2, u2).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, 1F, -1F / 256F, y2)
                    .color(255, 255, 255, alpha)
                    .uv(1F, u2).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, 1F, -1F / 256F, 1F)
                    .color(255, 255, 255, alpha)
                    .uv(1F, 1F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, x2, -1F / 256F, 1F)
                    .color(255, 255, 255, alpha)
                    .uv(u2, 1F).uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
        }
    }

    private static float getFactor(float width, float height) {
        return Math.min(width, height) / (24 + Mth.fastInvCubeRoot(0.00390625F / (width * width + height * height)));
    }

    @Override
    public void close() {
    }
}