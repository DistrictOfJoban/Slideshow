package org.teacon.slides.slide;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.teacon.slides.texture.TextureProvider;

public final class ImageSlide implements Slide {
    private final TextureProvider mTexture;

    ImageSlide(TextureProvider texture) {
        mTexture = texture;
    }

    @Override
    public void render(MultiBufferSource source, Matrix4f matrix, PoseStack.Pose normal, float width, float height, int color, int light, boolean front, boolean back, long tick, float partialTick) {
        var red = (color >> 16) & 255;
        var green = (color >> 8) & 255;
        var blue = color & 255;
        var alpha = color >>> 24;
        var consumer = source.getBuffer(mTexture.updateAndGet(tick, partialTick));
        if (front) {
            consumer.addVertex(matrix, 0, 1 / 192F, 1)
                    .setColor(red, green, blue, alpha).setUv(0, 1)
                    .setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, 1, 1 / 192F, 1)
                    .setColor(red, green, blue, alpha).setUv(1, 1)
                    .setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, 1, 1 / 192F, 0)
                    .setColor(red, green, blue, alpha).setUv(1, 0)
                    .setLight(light)
                    .setNormal(normal, 0, 1, 0);
            consumer.addVertex(matrix, 0, 1 / 192F, 0)
                    .setColor(red, green, blue, alpha).setUv(0, 0)
                    .setLight(light)
                    .setNormal(normal, 0, 1, 0);
        }
        if (back) {
            consumer.addVertex(matrix, 0, -1 / 256F, 0)
                    .setColor(red, green, blue, alpha).setUv(0, 0)
                    .setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, 1, -1 / 256F, 0)
                    .setColor(red, green, blue, alpha).setUv(1, 0)
                    .setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, 1, -1 / 256F, 1)
                    .setColor(red, green, blue, alpha).setUv(1, 1)
                    .setLight(light)
                    .setNormal(normal, 0, -1, 0);
            consumer.addVertex(matrix, 0, -1 / 256F, 1)
                    .setColor(red, green, blue, alpha).setUv(0, 1)
                    .setLight(light)
                    .setNormal(normal, 0, -1, 0);
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