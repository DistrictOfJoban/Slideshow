package org.teacon.slides.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.teacon.slides.Slideshow;
import org.teacon.slides.projector.ProjectorBlock;
import org.teacon.slides.projector.ProjectorBlockEntity;

import javax.annotation.Nonnull;

public class UpdatePayload implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdatePayload> UPDATE_CHANNEL = CustomPacketPayload.createType(Slideshow.ID + "-update");

    public static final StreamCodec<FriendlyByteBuf, UpdatePayload> UPDATE = StreamCodec.of((buf, data) -> data.writeToBuf(buf), UpdatePayload::new);

    private final BlockPos blockPos;
    private final ProjectorBlockEntity.ProjectorBlockEntityData data;
    private final ProjectorBlock.InternalRotation rotation;

    public UpdatePayload(BlockPos blockPos, ProjectorBlockEntity.ProjectorBlockEntityData data, ProjectorBlock.InternalRotation rotation) {
        this.blockPos = blockPos;
        this.data = data;
        this.rotation = rotation;
    }

    public UpdatePayload(FriendlyByteBuf buf) {
        this.data = new ProjectorBlockEntity.ProjectorBlockEntityData();
        this.blockPos = buf.readBlockPos();
        this.data.setLocation(buf.readUtf());
        this.data.setColor(buf.readInt());
        this.data.setWidth(buf.readFloat());
        this.data.setHeight(buf.readFloat());
        this.data.setOffsetX(buf.readFloat());
        this.data.setOffsetY(buf.readFloat());
        this.data.setOffsetZ(buf.readFloat());
        this.data.setDoubleSided(buf.readBoolean());
        this.data.setKeepAspectRatio(buf.readBoolean());
        this.rotation = Enum.valueOf(ProjectorBlock.InternalRotation.class, buf.readUtf());
    }

    public void writeToBuf(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.blockPos);
        buf.writeUtf(this.data.getLocation());
        buf.writeInt(this.data.getColor());
        buf.writeFloat(this.data.getWidth());
        buf.writeFloat(this.data.getHeight());
        buf.writeFloat(this.data.getOffsetX());
        buf.writeFloat(this.data.getOffsetY());
        buf.writeFloat(this.data.getOffsetZ());
        buf.writeBoolean(this.data.isDoubleSided());
        buf.writeBoolean(this.data.isKeepAspectRatio());
        buf.writeUtf(this.rotation.name());
    }

    public void sendToServer() {
        ClientPlayNetworking.send(this);
    }

    public void handle(ServerPlayer player) {
        Level level = player.getCommandSenderWorld();
        if (level.isLoaded(this.blockPos)) {
            BlockEntity blockEntity = level.getBlockEntity(this.blockPos);
            if (blockEntity instanceof ProjectorBlockEntity projectorBlockEntity) {
                projectorBlockEntity.setProjectorBlockEntityData(this.data, this.rotation);
            }
        }
    }

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return UPDATE_CHANNEL;
    }
}