package org.teacon.slides.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class OpenMenuPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenMenuPayload> OPEN_MENU = StreamCodec.of((buf, data) -> data.writeToBuf(buf), OpenMenuPayload::new);

    private final BlockPos blockPos;

    public OpenMenuPayload(BlockPos blockPos) {
        this.blockPos = blockPos;
    }

    public OpenMenuPayload(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
    }

    public void writeToBuf(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.blockPos);
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }
}