package org.teacon.slides.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.teacon.slides.Slideshow;

/**
 * @author BloCamLimb
 */
public class SlideRenderType extends RenderType.CompositeRenderType {
    public SlideRenderType(int texture) {
        super(Slideshow.ID, DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.QUADS, 256, false, true,
                CompositeState.builder()
                        .setShaderState(RENDERTYPE_TEXT_SEE_THROUGH_SHADER)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .setCullState(CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(NO_OVERLAY)
                        .setLayeringState(NO_LAYERING)
                        .setOutputState(MAIN_TARGET)
                        .setTexturingState(DEFAULT_TEXTURING)
                        .setWriteMaskState(COLOR_DEPTH_WRITE)
                        .setLineState(DEFAULT_LINE)
                        .createCompositeState(true)
        );
        var baseSetup = this.setupState;
        this.setupState = () -> {
            baseSetup.run();
            RenderSystem.setShaderTexture(0, texture);
        };
    }

    public SlideRenderType(ResourceLocation texture) {
        super(Slideshow.ID + "_icon", DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.QUADS, 256, false, true,
                CompositeState.builder()
                        .setShaderState(RENDERTYPE_TEXT_SEE_THROUGH_SHADER)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .setCullState(CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(NO_OVERLAY)
                        .setLayeringState(NO_LAYERING)
                        .setOutputState(MAIN_TARGET)
                        .setTexturingState(DEFAULT_TEXTURING)
                        .setWriteMaskState(COLOR_DEPTH_WRITE)
                        .setLineState(DEFAULT_LINE)
                        .createCompositeState(true));
        var baseSetup = this.setupState;
        this.setupState = () -> {
            baseSetup.run();
            RenderSystem.setShaderTexture(0, texture);
        };
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
