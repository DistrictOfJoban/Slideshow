package org.teacon.slides.projector;

import com.mojang.datafixers.DSL;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.teacon.slides.Slideshow;
import org.teacon.slides.packet.OpenMenuPayload;

import javax.annotation.Nonnull;
import java.util.Set;

public final class ProjectorBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    private ProjectorBlockEntityData projectorBlockEntityData;

    public static BlockEntityType<ProjectorBlockEntity> create() {
        return new BlockEntityType<>(ProjectorBlockEntity::new, Set.of(Slideshow.PROJECTOR));
    }

    private ProjectorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Slideshow.PROJECTOR_BLOCK_ENTITY, blockPos, blockState);
        this.projectorBlockEntityData = new ProjectorBlockEntityData();
    }

    public void setProjectorBlockEntityData(ProjectorBlockEntityData projectorBlockEntityData) {
        this.projectorBlockEntityData = projectorBlockEntityData;
    }

    public void setProjectorBlockEntityData(ProjectorBlockEntityData projectorBlockEntityData, ProjectorBlock.InternalRotation rotation) {
        if (level != null && !level.isClientSide()) {
            this.projectorBlockEntityData = projectorBlockEntityData;
            this.setChanged();
            BlockState blockState = this.getBlockState().setValue(ProjectorBlock.ROTATION, rotation);
            this.level.setBlockAndUpdate(this.getBlockPos(), blockState);
            this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, ProjectorBlock.UPDATE_CLIENTS);
        }
    }

    public ProjectorBlockEntityData getProjectorBlockEntityData() {
        return this.projectorBlockEntityData;
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        compoundTag.putString("ImageLocation", this.projectorBlockEntityData.getLocation());
        compoundTag.putInt("Color", this.projectorBlockEntityData.getColor());
        compoundTag.putFloat("Width", this.projectorBlockEntityData.getWidth());
        compoundTag.putFloat("Height", this.projectorBlockEntityData.getHeight());
        compoundTag.putFloat("OffsetX", this.projectorBlockEntityData.getOffsetX());
        compoundTag.putFloat("OffsetY", this.projectorBlockEntityData.getOffsetY());
        compoundTag.putFloat("OffsetZ", this.projectorBlockEntityData.getOffsetZ());
        compoundTag.putBoolean("DoubleSided", this.projectorBlockEntityData.isDoubleSided());
        compoundTag.putBoolean("KeepAspectRatio", this.projectorBlockEntityData.isKeepAspectRatio());
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        this.projectorBlockEntityData.setLocation(compoundTag.getString("ImageLocation"));
        this.projectorBlockEntityData.setColor(compoundTag.getInt("Color"));
        this.projectorBlockEntityData.setWidth(compoundTag.getFloat("Width"));
        this.projectorBlockEntityData.setHeight(compoundTag.getFloat("Height"));
        this.projectorBlockEntityData.setOffsetX(compoundTag.getFloat("OffsetX"));
        this.projectorBlockEntityData.setOffsetY(compoundTag.getFloat("OffsetY"));
        this.projectorBlockEntityData.setOffsetZ(compoundTag.getFloat("OffsetZ"));
        this.projectorBlockEntityData.setDoubleSided(compoundTag.getBoolean("DoubleSided"));
        this.projectorBlockEntityData.setKeepAspectRatio(compoundTag.getBoolean("KeepAspectRatio"));
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithFullMetadata(provider);
    }

    @Override
    @Nonnull
    public Component getDisplayName() {
        return Component.literal("gui.slide_show.title");
    }

    @Override
    public AbstractContainerMenu createMenu(int i, @Nonnull Inventory inventory, @Nonnull Player player) {
        return Slideshow.PROJECTOR_SCREEN_HANDLER.create(i, inventory, new OpenMenuPayload(this.getBlockPos()));
    }

    @Override
    public Object getScreenOpeningData(ServerPlayer player) {
        return new OpenMenuPayload(this.getBlockPos());
    }

    public void transformToSlideSpace(Matrix4f pose, Matrix3f normal) {
        var state = getBlockState();
        var direction = state.getValue(BlockStateProperties.FACING);
        var rotation = state.getValue(ProjectorBlock.ROTATION);
        pose.translate(0.5f, 0.5f, 0.5f);
        pose.rotate(direction.getRotation());
        normal.rotate(direction.getRotation());
        pose.translate(0.0f, 0.5f, 0.0f);
        rotation.transform(pose);
        rotation.transform(normal);
        pose.translate(-0.5F, 0.0F, 0.5F - this.projectorBlockEntityData.getHeight());
        pose.translate(this.getProjectorBlockEntityData().getOffsetX(), -this.getProjectorBlockEntityData().getOffsetZ(), this.projectorBlockEntityData.getOffsetY());
        pose.scale(this.projectorBlockEntityData.getWidth(), 1.0F, this.projectorBlockEntityData.getHeight());
    }

    public static class ProjectorBlockEntityData {
        private String mLocation = "";
        private int mColor = 0xFFFFFFFF;
        private float mWidth = 1;
        private float mHeight = 1;
        private float mOffsetX = 0;
        private float mOffsetY = 0;
        private float mOffsetZ = 0;
        private boolean mKeepAspectRatio = true;
        private boolean mDoubleSided = true;

        public ProjectorBlockEntityData() {
        }

        public ProjectorBlockEntityData copy() {
            ProjectorBlockEntityData copy = new ProjectorBlockEntityData();
            copy.setLocation(mLocation);
            copy.setColor(mColor);
            copy.setWidth(mWidth);
            copy.setHeight(mHeight);
            copy.setOffsetX(mOffsetX);
            copy.setOffsetY(mOffsetY);
            copy.setOffsetZ(mOffsetZ);
            copy.setKeepAspectRatio(mKeepAspectRatio);
            copy.setDoubleSided(mDoubleSided);
            return copy;
        }

        public String getLocation() {
            return mLocation;
        }

        public int getColor() {
            return mColor;
        }

        public float getWidth() {
            return mWidth;
        }

        public float getHeight() {
            return mHeight;
        }

        public float getOffsetX() {
            return mOffsetX;
        }

        public float getOffsetY() {
            return mOffsetY;
        }

        public float getOffsetZ() {
            return mOffsetZ;
        }

        public boolean isKeepAspectRatio() {
            return mKeepAspectRatio;
        }

        public boolean isDoubleSided() {
            return mDoubleSided;
        }

        public void setLocation(String mLocation) {
            this.mLocation = mLocation;
        }

        public void setColor(int mColor) {
            this.mColor = mColor;
        }

        public void setWidth(float mWidth) {
            this.mWidth = mWidth;
        }

        public void setHeight(float mHeight) {
            this.mHeight = mHeight;
        }

        public void setOffsetX(float mOffsetX) {
            this.mOffsetX = mOffsetX;
        }

        public void setOffsetY(float mOffsetY) {
            this.mOffsetY = mOffsetY;
        }

        public void setOffsetZ(float mOffsetZ) {
            this.mOffsetZ = mOffsetZ;
        }

        public void setKeepAspectRatio(boolean mKeepAspectRatio) {
            this.mKeepAspectRatio = mKeepAspectRatio;
        }

        public void setDoubleSided(boolean mDoubleSided) {
            this.mDoubleSided = mDoubleSided;
        }
    }
}