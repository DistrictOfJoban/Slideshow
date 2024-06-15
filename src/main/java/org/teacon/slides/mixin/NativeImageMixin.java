package org.teacon.slides.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.ByteBuffer;

@Mixin(NativeImage.class)
public abstract class NativeImageMixin {
    @Redirect(method = "read(Lcom/mojang/blaze3d/platform/NativeImage$Format;Ljava/nio/ByteBuffer;)Lcom/mojang/blaze3d/platform/NativeImage;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/PngInfo;validateHeader(Ljava/nio/ByteBuffer;)V"))
    private static void redirectValidateHeader(ByteBuffer byteBuffer) {
    }
}