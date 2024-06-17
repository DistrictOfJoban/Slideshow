package org.teacon.slides.slide;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.teacon.slides.texture.TextureProvider;

public interface Slide extends AutoCloseable {
    void render(MultiBufferSource source, Matrix4f matrix, PoseStack.Pose normal, float width, float height, int color, int light, boolean front, boolean back, long tick, float partialTick);

    @Override
    void close();

    default int getWidth() {
        return 1;
    }

    default int getHeight() {
        return 1;
    }

    default float getImageAspectRatio() {
        return Float.NaN;
    }

    static Slide make(TextureProvider texture) {
        return new ImageSlide(texture);
    }

    static Slide failed() {
        return IconSlide.DEFAULT_FAILED;
    }

    static Slide loading() {
        return IconSlide.DEFAULT_LOADING;
    }
}