package org.teacon.slides;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import org.teacon.slides.config.Config;
import org.teacon.slides.renderer.ProjectorRenderer;
import org.teacon.slides.renderer.SlideState;
import org.teacon.slides.screen.ProjectorScreen;

public final class SlideshowClient implements ClientModInitializer {
    public static final ResourceLocation UPDATE_CHANNEL = new ResourceLocation(Slideshow.ID, "update");

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(Slideshow.PROJECTOR, RenderType.cutout());

        BlockEntityRenderers.register(Slideshow.PROJECTOR_BLOCK_ENTITY, context -> new ProjectorRenderer());

        ClientTickEvents.START_CLIENT_TICK.register(SlideState::tick);

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> SlideState.onPlayerLeft());

        MenuScreens.register(Slideshow.PROJECTOR_SCREEN_HANDLER, ProjectorScreen::new);

        Config.refreshProperties();
    }
}