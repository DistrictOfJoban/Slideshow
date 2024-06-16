package org.teacon.slides.slide;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import org.teacon.slides.texture.TextureProvider;

public final class ImageSlide implements Slide {
    private final TextureProvider mTexture;

    ImageSlide(TextureProvider texture) {
        mTexture = texture;
    }

    @Override
    public void render(MultiBufferSource source, Matrix4f matrix, Matrix3f normal, float width, float height, int color, int light, boolean front, boolean back, long tick, float partialTick) {
        var red = (color >> 16) & 255;
        var green = (color >> 8) & 255;
        var blue = color & 255;
        var alpha = color >>> 24;
        var consumer = source.getBuffer(mTexture.updateAndGet(tick, partialTick));
        if (front) {
            consumer.vertex(matrix, 0, 1 / 192F, 1)
                    .color(red, green, blue, alpha).uv(0, 1)
                    .uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, 1, 1 / 192F, 1)
                    .color(red, green, blue, alpha).uv(1, 1)
                    .uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, 1, 1 / 192F, 0)
                    .color(red, green, blue, alpha).uv(1, 0)
                    .uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
            consumer.vertex(matrix, 0, 1 / 192F, 0)
                    .color(red, green, blue, alpha).uv(0, 0)
                    .uv2(light)
                    .normal(normal, 0, 1, 0).endVertex();
        }
        if (back) {
            consumer.vertex(matrix, 0, -1 / 256F, 0)
                    .color(red, green, blue, alpha).uv(0, 0)
                    .uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, 1, -1 / 256F, 0)
                    .color(red, green, blue, alpha).uv(1, 0)
                    .uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, 1, -1 / 256F, 1)
                    .color(red, green, blue, alpha).uv(1, 1)
                    .uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
            consumer.vertex(matrix, 0, -1 / 256F, 1)
                    .color(red, green, blue, alpha).uv(0, 1)
                    .uv2(light)
                    .normal(normal, 0, -1, 0).endVertex();
        }
    }

    @Override
    public int getWidth() {
        return mTexture.getWidth();
    }

    @Override
    public int getHeight() {
        return mTexture.getHeight();
    }

    @Override
    public float getImageAspectRatio() {
        return (float) getWidth() / getHeight();
    }

    @Override
    public void close() {
        mTexture.close();
    }
}