package org.teacon.slides.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.teacon.slides.config.Config;
import org.teacon.slides.projector.ProjectorBlock;
import org.teacon.slides.projector.ProjectorBlockEntity;
import org.teacon.slides.slide.Slide;

import javax.annotation.Nonnull;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

public final class ProjectorRenderer implements BlockEntityRenderer<ProjectorBlockEntity> {

    public ProjectorRenderer() {
    }

    @Override
    public void render(@Nonnull ProjectorBlockEntity blockEntity, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource source, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        BlockState blockState = blockEntity.getBlockState();
        Slide slide = SlideState.getSlide(blockEntity.getProjectorBlockEntityData().getLocation());
        if (slide != null) {
            float width = blockEntity.getProjectorBlockEntityData().getWidth();
            float height = blockEntity.getProjectorBlockEntityData().getHeight();
            int color = blockEntity.getProjectorBlockEntityData().getColor();
            boolean isTransparent = (color & 0xFF000000) == 0;
            boolean isPowered = blockState.getValue(POWERED);
            boolean doubleSided = blockEntity.getProjectorBlockEntityData().isDoubleSided();
            if (!isTransparent && !isPowered) {
                PoseStack.Pose lastPose = poseStack.last();
                Matrix4f pose = new Matrix4f(lastPose.pose());
                Matrix3f normal = new Matrix3f(lastPose.normal());
                blockEntity.transformToSlideSpace(pose, normal);
                boolean flipped = blockState.getValue(ProjectorBlock.ROTATION).isFlipped();
                slide.render(source, pose, normal, width, height, color, LightTexture.FULL_BRIGHT, flipped || doubleSided, !flipped || doubleSided, SlideState.getAnimationTick(), partialTick);
            }
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@Nonnull ProjectorBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return Config.getViewDistance();
    }
}