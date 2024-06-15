package org.teacon.slides.projector;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.teacon.slides.Slideshow;

import javax.annotation.Nonnull;

public final class ProjectorContainerMenu extends AbstractContainerMenu {
    private final BlockPos blockPos;

    public ProjectorContainerMenu(int i, FriendlyByteBuf buf) {
        super(Slideshow.PROJECTOR_SCREEN_HANDLER, i);
        this.blockPos = buf.readBlockPos();
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        Level level = player.level();
        if (!level.isLoaded(this.blockPos)) {
            return false;
        }
        return level.getBlockEntity(this.blockPos) instanceof ProjectorBlockEntity;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }
}