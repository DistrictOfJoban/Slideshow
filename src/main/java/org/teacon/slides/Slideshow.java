package org.teacon.slides;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.teacon.slides.packet.OpenMenuPayload;
import org.teacon.slides.packet.UpdatePayload;
import org.teacon.slides.projector.ProjectorBlock;
import org.teacon.slides.projector.ProjectorBlockEntity;
import org.teacon.slides.projector.ProjectorContainerMenu;

import java.util.Objects;

public final class Slideshow implements ModInitializer {
    public static final String ID = "slide_show";
    public static final Logger LOGGER = LogManager.getLogger("SlideShow");

    public static final ProjectorBlock PROJECTOR = new ProjectorBlock();
    public static final BlockEntityType<ProjectorBlockEntity> PROJECTOR_BLOCK_ENTITY = ProjectorBlockEntity.create();
    public static final BlockItem PROJECTOR_BLOCK_ITEM = new BlockItem(PROJECTOR, new Item.Properties());

    public static final ExtendedScreenHandlerType<ProjectorContainerMenu, OpenMenuPayload> PROJECTOR_SCREEN_HANDLER = new ExtendedScreenHandlerType<>((syncId, inventory, data) -> new ProjectorContainerMenu(syncId, data), OpenMenuPayload.OPEN_MENU);

    @Override
    public void onInitialize() {

        Registry.register(BuiltInRegistries.BLOCK, Objects.requireNonNull(ResourceLocation.tryBuild(ID, "projector")), PROJECTOR);
        Registry.register(BuiltInRegistries.ITEM, Objects.requireNonNull(ResourceLocation.tryBuild(ID, "projector")), PROJECTOR_BLOCK_ITEM);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Objects.requireNonNull(ResourceLocation.tryBuild(ID, "projector")), PROJECTOR_BLOCK_ENTITY);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> content.accept(PROJECTOR_BLOCK_ITEM));
        Registry.register(BuiltInRegistries.MENU, Objects.requireNonNull(ResourceLocation.tryBuild(ID, "projector")), PROJECTOR_SCREEN_HANDLER);

        PayloadTypeRegistry.playC2S().register(UpdatePayload.UPDATE_CHANNEL, UpdatePayload.UPDATE);

        ServerPlayNetworking.registerGlobalReceiver(UpdatePayload.UPDATE_CHANNEL, ((payload, context) -> {
            MinecraftServer server = context.player().getServer();
            if (server != null) {
                server.execute(() -> payload.handle(context.player()));
            }
        }));

        LOGGER.info("Slideshow initialized");
    }
}