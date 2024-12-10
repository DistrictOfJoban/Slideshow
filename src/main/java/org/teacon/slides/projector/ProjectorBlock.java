package org.teacon.slides.projector;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.teacon.slides.Slideshow;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Locale;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED;

@FieldsAreNonnullByDefault
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class ProjectorBlock extends Block implements EntityBlock {
    public static final EnumProperty<InternalRotation> ROTATION = EnumProperty.create("rotation", InternalRotation.class);
    public static final EnumProperty<Direction> BASE = EnumProperty.create("base", Direction.class, Direction.Plane.VERTICAL);
    private static final VoxelShape SHAPE_WITH_BASE_UP = Block.box(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape SHAPE_WITH_BASE_DOWN = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    public ProjectorBlock() {
        super(BlockBehaviour.Properties.of().strength(20F).lightLevel(blockState -> 15).noCollission());
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.EAST).setValue(POWERED, false).setValue(BASE, Direction.DOWN).setValue(ROTATION, InternalRotation.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, BASE, ROTATION);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return switch (blockState.getValue(BASE)) {
            case DOWN -> SHAPE_WITH_BASE_DOWN;
            case UP -> SHAPE_WITH_BASE_UP;
            default -> throw new AssertionError();
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        Direction facing = blockPlaceContext.getNearestLookingDirection().getOpposite();
        Direction horizontalFacing = blockPlaceContext.getHorizontalDirection().getOpposite();
        Direction base = Arrays.stream(blockPlaceContext.getNearestLookingDirections()).filter(Direction.Plane.VERTICAL).findFirst().orElse(Direction.DOWN);
        InternalRotation rotation = InternalRotation.VALUES[4 + Math.floorMod(facing.getStepY() * horizontalFacing.get2DDataValue(), 4)];
        return defaultBlockState().setValue(BASE, base).setValue(FACING, facing).setValue(POWERED, Boolean.FALSE).setValue(ROTATION, rotation);
    }

    @Override
    public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, Orientation orientation, boolean bl) {
        boolean powered = level.hasNeighborSignal(blockPos);
        if (powered != blockState.getValue(POWERED)) {
            level.setBlock(blockPos, blockState.setValue(POWERED, powered), Block.UPDATE_ALL);
        }
    }

    @Override
    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState oldBlockState, boolean bl) {
        if (!oldBlockState.is(blockState.getBlock())) {
            boolean powered = level.hasNeighborSignal(blockPos);
            if (powered != blockState.getValue(POWERED)) {
                level.setBlock(blockPos, blockState.setValue(POWERED, powered), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        Direction direction = state.getValue(FACING);
        return switch (direction) {
            case DOWN, UP -> state.setValue(ROTATION, state.getValue(ROTATION).compose(Rotation.CLOCKWISE_180));
            default -> state.setValue(FACING, mirror.getRotation(direction).rotate(direction));
        };
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        Direction direction = state.getValue(FACING);
        return switch (direction) {
            case DOWN, UP -> state.setValue(ROTATION, state.getValue(ROTATION).compose(rotation));
            default -> state.setValue(FACING, rotation.rotate(direction));
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (!level.isClientSide() && level.isLoaded(blockPos)) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof ProjectorBlockEntity projectorBlockEntity) {
                player.openMenu(projectorBlockEntity);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return Slideshow.PROJECTOR_BLOCK_ENTITY.create(blockPos, blockState);
    }

    public enum InternalRotation implements StringRepresentable {
        NONE(1F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, 1F, 0F),
        CLOCKWISE_90(0F, 0F, -1F, 0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, 0F),
        CLOCKWISE_180(-1F, 0F, 0F, 0F, 0F, 1F, 0F, 0F, 0F, 0F, -1F, 0F),
        COUNTERCLOCKWISE_90(0F, 0F, 1F, 0F, 0F, 1F, 0F, 0F, -1F, 0F, 0F, 0F),
        HORIZONTAL_FLIPPED(-1F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, 1F, 0F),
        DIAGONAL_FLIPPED(0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, -1F, 0F, 0F, 0F),
        VERTICAL_FLIPPED(1F, 0F, 0F, 0F, 0F, -1F, 0F, 0F, 0F, 0F, -1F, 0F),
        ANTI_DIAGONAL_FLIPPED(0F, 0F, 1F, 0F, 0F, -1F, 0F, 0F, 1F, 0F, 0F, 0F);

        public static final InternalRotation[] VALUES = values();

        private static final int[]
                INV_INDICES = {0, 3, 2, 1, 4, 5, 6, 7},
                FLIP_INDICES = {4, 7, 6, 5, 0, 3, 2, 1};
        private static final int[][] ROTATION_INDICES = {
                {0, 1, 2, 3, 4, 5, 6, 7},
                {1, 2, 3, 0, 5, 6, 7, 4},
                {2, 3, 0, 1, 6, 7, 4, 5},
                {3, 0, 1, 2, 7, 4, 5, 6}
        };

        private final String mSerializedName;
        private final Matrix4f mMatrix;
        private final Matrix3f mNormal;

        InternalRotation(float m00, float m10, float m20, float m30,
                         float m01, float m11, float m21, float m31,
                         float m02, float m12, float m22, float m32) {
            mMatrix = new Matrix4f(m00, m01, m02, 0F, m10, m11, m12, 0F, m20, m21, m22, 0F, m30, m31, m32, 1F);
            mNormal = new Matrix3f(m00, m01, m02, m10, m11, m12, m20, m21, m22);
            mSerializedName = name().toLowerCase(Locale.ROOT);
        }

        public InternalRotation compose(Rotation rotation) {
            return VALUES[ROTATION_INDICES[rotation.ordinal()][ordinal()]];
        }

        public InternalRotation flip() {
            return VALUES[FLIP_INDICES[ordinal()]];
        }

        public InternalRotation invert() {
            return VALUES[INV_INDICES[ordinal()]];
        }

        public boolean isFlipped() {
            return ordinal() >= 4;
        }

        public void transform(Vector4f vector) {
            vector.mul(mMatrix);
        }

        public void transform(Matrix4f poseMatrix) {
            poseMatrix.mul(mMatrix);
        }

        public void transform(Matrix3f normalMatrix) {
            normalMatrix.mul(mNormal);
        }

        @Override
        public final String getSerializedName() {
            return mSerializedName;
        }
    }
}