package org.teacon.slides;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.teacon.slides.packet.UpdatePacket;
import org.teacon.slides.projector.ProjectorBlock;
import org.teacon.slides.projector.ProjectorBlockEntity;
import org.teacon.slides.projector.ProjectorContainerMenu;

public final class Slideshow implements ModInitializer {
    public static final String ID = "slide_show";
    public static final Logger LOGGER = LogManager.getLogger("SlideShow");

    public static final ResourceLocation UPDATE_CHANNEL = new ResourceLocation(ID, "update");

    public static final ProjectorBlock PROJECTOR = new ProjectorBlock();
    public static final BlockEntityType<ProjectorBlockEntity> PROJECTOR_BLOCK_ENTITY = ProjectorBlockEntity.create();
    public static final BlockItem PROJECTOR_BLOCK_ITEM = new BlockItem(PROJECTOR, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
    public static final ExtendedScreenHandlerType<ProjectorContainerMenu> PROJECTOR_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(((syncId, inventory, buf) -> new ProjectorContainerMenu(syncId, buf)));

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new ResourceLocation(ID, "projector"), PROJECTOR);
        Registry.register(Registry.ITEM, new ResourceLocation(ID, "projector"), PROJECTOR_BLOCK_ITEM);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation("projector"), PROJECTOR_BLOCK_ENTITY);
        Registry.register(Registry.MENU, new ResourceLocation(ID, "projector"), PROJECTOR_SCREEN_HANDLER);
        ServerPlayNetworking.registerGlobalReceiver(UPDATE_CHANNEL, ((server, player, handler, buf, responseSender) -> {
            UpdatePacket updatePacket = new UpdatePacket(buf);
            server.execute(() -> updatePacket.handle(player));
        }));
        LOGGER.info("Slideshow initialized");
    }
}