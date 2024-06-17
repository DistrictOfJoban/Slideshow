package org.teacon.slides.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class SlideRenderType extends RenderType {
    private static final ImmutableList<RenderStateShard> GENERAL_STATES;

    static {
        GENERAL_STATES = ImmutableList.of(
                TRANSLUCENT_TRANSPARENCY,
                LEQUAL_DEPTH_TEST,
                CULL,
                LIGHTMAP,
                NO_OVERLAY,
                NO_LAYERING,
                MAIN_TARGET,
                DEFAULT_TEXTURING,
                COLOR_DEPTH_WRITE,
                DEFAULT_LINE
        );
    }

    private final int mHashCode;

    public SlideRenderType(int texture) {
        super("slide_show", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS, 256, false, true,
                () -> {
                    GENERAL_STATES.forEach(RenderStateShard::setupRenderState);
                    RenderSystem.enableTexture();
                    RenderSystem.bindTexture(texture);
                },
                () -> GENERAL_STATES.forEach(RenderStateShard::clearRenderState));
        mHashCode = Objects.hash(super.hashCode(), GENERAL_STATES, texture);
    }

    public SlideRenderType(ResourceLocation texture) {
        super("slide_show", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS, 256, false, true,
                () -> {
                    GENERAL_STATES.forEach(RenderStateShard::setupRenderState);
                    RenderSystem.enableTexture();
                    Minecraft.getInstance().getTextureManager().bindForSetup(texture);
                },
                () -> GENERAL_STATES.forEach(RenderStateShard::clearRenderState));
        mHashCode = Objects.hash(super.hashCode(), GENERAL_STATES, texture);
    }

    @Override
    public int hashCode() {
        return mHashCode;
    }
}