package org.teacon.slides.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.teacon.slides.Slideshow;
import org.teacon.slides.packet.UpdatePayload;
import org.teacon.slides.projector.ProjectorBlock;
import org.teacon.slides.projector.ProjectorBlockEntity;
import org.teacon.slides.projector.ProjectorContainerMenu;
import org.teacon.slides.renderer.SlideState;
import org.teacon.slides.slide.Slide;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class ProjectorScreen extends AbstractContainerScreen<ProjectorContainerMenu> {
    private static final ResourceLocation GUI_TEXTURE = Objects.requireNonNull(ResourceLocation.tryBuild(Slideshow.ID, "textures/gui/projector.png"));

    private static final int URL_MAX_LENGTH = 1 << 9;
    private static final int COLOR_MAX_LENGTH = 1 << 3;

    private static final Component IMAGE_TEXT = Component.translatable("gui.slide_show.section.image");
    private static final Component OFFSET_TEXT = Component.translatable("gui.slide_show.section.offset");
    private static final Component OTHERS_TEXT = Component.translatable("gui.slide_show.section.others");
    private static final Component URL_TEXT = Component.translatable("gui.slide_show.url");
    private static final Component COLOR_TEXT = Component.translatable("gui.slide_show.color");
    private static final Component WIDTH_TEXT = Component.translatable("gui.slide_show.width");
    private static final Component HEIGHT_TEXT = Component.translatable("gui.slide_show.height");
    private static final Component KEEP_ASPECT_RATIO_TEXT = Component.translatable("gui.slide_show.keep_aspect_ratio");
    private static final Component OFFSET_X_TEXT = Component.translatable("gui.slide_show.offset_x");
    private static final Component OFFSET_Y_TEXT = Component.translatable("gui.slide_show.offset_y");
    private static final Component OFFSET_Z_TEXT = Component.translatable("gui.slide_show.offset_z");
    private static final Component FLIP_TEXT = Component.translatable("gui.slide_show.flip");
    private static final Component ROTATE_TEXT = Component.translatable("gui.slide_show.rotate");
    private static final Component SINGLE_DOUBLE_SIDED_TEXT = Component.translatable("gui.slide_show.single_double_sided");

    private final LazyWidget<EditBox> mURLInput;
    private final LazyWidget<EditBox> mColorInput;
    private final LazyWidget<EditBox> mWidthInput;
    private final LazyWidget<EditBox> mHeightInput;
    private final LazyWidget<EditBox> mOffsetXInput;
    private final LazyWidget<EditBox> mOffsetYInput;
    private final LazyWidget<EditBox> mOffsetZInput;

    private final LazyWidget<Button> mFlipRotation;
    private final LazyWidget<Button> mCycleRotation;
    private final LazyWidget<Button> mSwitchSingleSided;
    private final LazyWidget<Button> mSwitchDoubleSided;
    private final LazyWidget<Button> mKeepAspectChecked;
    private final LazyWidget<Button> mKeepAspectUnchecked;

    private String mImgUrl;
    private int mImageColor;
    private Vector2f mImageSize;
    private Vector3f mImageOffset;
    private boolean mDoubleSided;
    private boolean mKeepAspectRatio;
    private SyncAspectRatio mSyncAspectRatio;
    private ProjectorBlock.InternalRotation mRotation;

    private boolean mInvalidColor;
    private boolean mInvalidWidth;
    private boolean mInvalidHeight;
    private boolean mInvalidOffsetX;
    private boolean mInvalidOffsetY;
    private boolean mInvalidOffsetZ;
    private ImageUrlStatus mImageUrlStatus;


    private final BlockPos blockPos;
    private final ProjectorBlockEntity.ProjectorBlockEntityData data;

    public ProjectorScreen(ProjectorContainerMenu projectorContainerMenu, Inventory inventory, Component component) {
        super(projectorContainerMenu, inventory, component);
        this.imageWidth = 176;
        this.imageHeight = 217;
        this.blockPos = projectorContainerMenu.getBlockPos();
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            throw new RuntimeException("Error! Client level is null!");
        }
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        ProjectorBlock.InternalRotation rotation;
        if (blockEntity instanceof ProjectorBlockEntity projectorBlockEntity) {
            this.data = projectorBlockEntity.getProjectorBlockEntityData();
            rotation = projectorBlockEntity.getBlockState().getValue(ProjectorBlock.ROTATION);
            this.mImgUrl = this.data.getLocation();
            this.mImageColor = this.data.getColor();
            this.mImageSize = new Vector2f(this.data.getWidth(), this.data.getHeight());
            this.mImageOffset = new Vector3f(this.data.getOffsetX(), this.data.getOffsetY(), this.data.getOffsetZ());
            this.mDoubleSided = this.data.isDoubleSided();
            this.mKeepAspectRatio = this.data.isKeepAspectRatio();
            this.mSyncAspectRatio = this.mKeepAspectRatio ? SyncAspectRatio.SYNC_WIDTH_WITH_HEIGHT : SyncAspectRatio.SYNCED;
            this.mRotation = rotation;
            this.mInvalidColor = true;
            this.mInvalidWidth = true;
            this.mInvalidHeight = true;
            this.mInvalidOffsetX = true;
            this.mInvalidOffsetY = true;
            this.mInvalidOffsetZ = true;
            this.mImageUrlStatus = ImageUrlStatus.EMPTY;

            this.mURLInput = LazyWidget.of(this.data.getLocation(), EditBox::getValue, value -> {
                EditBox input = new EditBox(this.font, this.leftPos + 30, this.topPos + 29, 136, 16, URL_TEXT);
                input.setMaxLength(URL_MAX_LENGTH);
                input.setResponder(text -> {
                    this.mImgUrl = text;
                    if (StringUtils.isNotBlank(text)) {
                        if (SlideState.createURI(mImgUrl) != null) {
                            this.mImageUrlStatus = ImageUrlStatus.NORMAL;
                        } else {
                            this.mImageUrlStatus = ImageUrlStatus.INVALID;
                        }
                    }
                    input.setTextColor(
                            switch (mImageUrlStatus) {
                                case NORMAL -> 0xE0E0E0;
                                case INVALID -> 0xFF0000;
                                case EMPTY -> 0xFFFFFF;
                            }
                    );
                });
                input.setValue(value);
                return input;
            });

            this.mColorInput = LazyWidget.of(String.format("%08X", this.data.getColor()), EditBox::getValue, value -> {
                EditBox input = new EditBox(this.font, this.leftPos + 55, this.topPos + 155, 56, 16, COLOR_TEXT);
                input.setMaxLength(COLOR_MAX_LENGTH);
                input.setResponder(text -> {
                    try {
                        this.mImageColor = Integer.parseUnsignedInt(text, 16);
                        this.mInvalidColor = false;
                    } catch (Exception ignored) {
                    }
                    input.setTextColor(this.mInvalidColor ? 0xE04B4B : 0xE0E0E0);
                });
                input.setValue(value);
                return input;
            });

            this.mWidthInput = LazyWidget.of(toOptionalSignedString(this.data.getWidth()), EditBox::getValue, value -> {
                EditBox input = new EditBox(this.font, this.leftPos + 30, this.topPos + 51, 46, 16, WIDTH_TEXT);
                input.setResponder(text -> {
                    try {
                        Vector2f newSize = new Vector2f(parseFloatOrDefault(text, 1), this.mImageSize.y);
                        updateOffsetByDimension(newSize);
                        this.mInvalidWidth = false;
                    } catch (Exception e) {
                        this.mInvalidWidth = true;
                    }
                    input.setTextColor(this.mInvalidWidth ? 0xE04B4B : 0xE0E0E0);
                    if (!this.mInvalidWidth && this.mKeepAspectRatio) {
                        this.mSyncAspectRatio = SyncAspectRatio.SYNC_HEIGHT_WITH_WIDTH;
                    }
                });
                input.setValue(value);
                return input;
            });

            this.mHeightInput = LazyWidget.of(toOptionalSignedString(this.data.getHeight()), EditBox::getValue, value -> {
                EditBox input = new EditBox(this.font, this.leftPos + 100, this.topPos + 51, 46, 16, HEIGHT_TEXT);
                input.setResponder(text -> {
                    try {
                        Vector2f newSize = new Vector2f(this.mImageSize.x, parseFloatOrDefault(text, 1));
                        updateOffsetByDimension(newSize);
                        this.mInvalidHeight = false;
                    } catch (Exception e) {
                        this.mInvalidHeight = true;
                    }
                    input.setTextColor(this.mInvalidHeight ? 0xE04B4B : 0xE0E0E0);
                    if (!this.mInvalidHeight && this.mKeepAspectRatio) {
                        this.mSyncAspectRatio = SyncAspectRatio.SYNC_WIDTH_WITH_HEIGHT;
                    }
                });
                input.setValue(value);
                return input;
            });

            this.mOffsetXInput = LazyWidget.of(toSignedString(this.data.getOffsetX()), EditBox::getValue, value -> {
                EditBox input = new EditBox(this.font, this.leftPos + 30, this.topPos + 103, 29, 16, OFFSET_X_TEXT);
                input.setResponder(text -> {
                    try {
                        this.mImageOffset = new Vector3f(parseFloatOrDefault(text, 0), this.mImageOffset.y(), this.mImageOffset.z());
                        this.mInvalidOffsetX = false;
                    } catch (Exception e) {
                        this.mInvalidOffsetX = true;
                    }
                    input.setTextColor(this.mInvalidOffsetX ? 0xE04B4B : 0xE0E0E0);
                });
                input.setValue(value);
                return input;
            });

            this.mOffsetYInput = LazyWidget.of(toSignedString(this.data.getOffsetY()), EditBox::getValue, value -> {
                EditBox input = new EditBox(this.font, this.leftPos + 84, this.topPos + 103, 29, 16, OFFSET_Y_TEXT);
                input.setResponder(text -> {
                    try {
                        this.mImageOffset = new Vector3f(this.mImageOffset.x(), parseFloatOrDefault(text, 0), this.mImageOffset.z());
                        this.mInvalidOffsetY = false;
                    } catch (Exception e) {
                        this.mInvalidOffsetY = true;
                    }
                    input.setTextColor(this.mInvalidOffsetY ? 0xE04B4B : 0xE0E0E0);
                });
                input.setValue(value);
                return input;
            });

            this.mOffsetZInput = LazyWidget.of(toSignedString(this.data.getOffsetZ()), EditBox::getValue, value -> {
                EditBox input = new EditBox(this.font, this.leftPos + 138, this.topPos + 103, 29, 16, OFFSET_Z_TEXT);
                input.setResponder(text -> {
                    try {
                        this.mImageOffset = new Vector3f(this.mImageOffset.x(), this.mImageOffset.y(), parseFloatOrDefault(text, 1));
                        this.mInvalidOffsetZ = false;
                    } catch (Exception e) {
                        this.mInvalidOffsetZ = true;
                    }
                    input.setTextColor(this.mInvalidOffsetZ ? 0xE04B4B : 0xE0E0E0);
                });
                input.setValue(value);
                return input;
            });

            this.mFlipRotation = LazyWidget.of(true, b -> b.visible, value -> {
                Button button = new Button(this.leftPos + 117, this.topPos + 153, 179, 153, 18, 19, FLIP_TEXT, () -> {
                    ProjectorBlock.InternalRotation newRotation = this.mRotation.flip();
                    updateOffsetByRotation(newRotation);
                });
                button.visible = value;
                return button;
            });

            this.mCycleRotation = LazyWidget.of(true, b -> b.visible, value -> {
                Button button = new Button(this.leftPos + 142, this.topPos + 153, 179, 173, 18, 19, ROTATE_TEXT, () -> {
                    ProjectorBlock.InternalRotation newRotation = this.mRotation.compose(Rotation.CLOCKWISE_90);
                    updateOffsetByRotation(newRotation);
                });
                button.visible = value;
                return button;
            });

            this.mSwitchSingleSided = LazyWidget.of(this.data.isDoubleSided(), b -> b.visible, value -> {
                Button button = new Button(this.leftPos + 9, this.topPos + 153, 179, 113, 18, 19, SINGLE_DOUBLE_SIDED_TEXT, () -> {
                    if (this.mDoubleSided) {
                        updateDoubleSided(false);
                    }
                });
                button.visible = value;
                return button;
            });

            this.mSwitchDoubleSided = LazyWidget.of(!this.data.isDoubleSided(), b -> b.visible, value -> {
                Button button = new Button(this.leftPos + 9, this.topPos + 153, 179, 133, 18, 19, SINGLE_DOUBLE_SIDED_TEXT, () -> {
                    if (!this.mDoubleSided) {
                        updateDoubleSided(true);
                    }
                });
                button.visible = value;
                return button;
            });

            this.mKeepAspectChecked = LazyWidget.of(this.mKeepAspectRatio, b -> b.visible, value -> {
                Button button = new Button(leftPos + 149, topPos + 49, 179, 93, 18, 19, KEEP_ASPECT_RATIO_TEXT, () -> {
                    if (this.mKeepAspectRatio) {
                        updateKeepAspectRatio(false);
                    }
                });
                button.visible = value;
                return button;
            });

            this.mKeepAspectUnchecked = LazyWidget.of(!this.mKeepAspectRatio, b -> b.visible, value -> {
                Button button = new Button(this.leftPos + 149, this.topPos + 49, 179, 73, 18, 19, KEEP_ASPECT_RATIO_TEXT, () -> {
                    if (!this.mKeepAspectRatio) {
                        updateKeepAspectRatio(true);
                    }
                });
                button.visible = value;
                return button;
            });

            return;
        }
        this.data = null;
        rotation = null;
        this.mURLInput = null;
        this.mColorInput = null;
        this.mWidthInput = null;
        this.mHeightInput = null;
        this.mOffsetXInput = null;
        this.mOffsetYInput = null;
        this.mOffsetZInput = null;
        this.mFlipRotation = null;
        this.mCycleRotation = null;
        this.mSwitchSingleSided = null;
        this.mSwitchDoubleSided = null;
        this.mKeepAspectChecked = null;
        this.mKeepAspectUnchecked = null;
    }

    @Override
    public void init() {
        super.init();
        if (this.data != null) {
            addRenderableWidget(mURLInput.refresh());
            addRenderableWidget(mColorInput.refresh());
            addRenderableWidget(mWidthInput.refresh());
            addRenderableWidget(mHeightInput.refresh());
            addRenderableWidget(mOffsetXInput.refresh());
            addRenderableWidget(mOffsetYInput.refresh());
            addRenderableWidget(mOffsetZInput.refresh());
            addRenderableWidget(mFlipRotation.refresh());
            addRenderableWidget(mCycleRotation.refresh());
            addRenderableWidget(mSwitchSingleSided.refresh());
            addRenderableWidget(mSwitchDoubleSided.refresh());
            addRenderableWidget(mKeepAspectChecked.refresh());
            addRenderableWidget(mKeepAspectUnchecked.refresh());
            setInitialFocus(mURLInput.get());
        }
    }

    @Override
    protected void containerTick() {
        if (this.data != null) {
            if (!mURLInput.get().isFocused()) {
                if (mSyncAspectRatio != SyncAspectRatio.SYNCED && !mInvalidWidth && !mInvalidHeight) {
                    Slide slide = SlideState.getSlide(data.getLocation());
                    float aspect = slide == null ? Float.NaN : slide.getImageAspectRatio();
                    if (!Float.isNaN(aspect)) {
                        if (mSyncAspectRatio == SyncAspectRatio.SYNC_WIDTH_WITH_HEIGHT) {
                            Vector2f newSizeByHeight = new Vector2f(mImageSize.y * aspect, mImageSize.y);
                            updateOffsetByDimension(newSizeByHeight);
                            if (!mWidthInput.get().isFocused()) {
                                mWidthInput.get().setValue(toOptionalSignedString(newSizeByHeight.x()));
                            }
                        }
                        if (mSyncAspectRatio == SyncAspectRatio.SYNC_HEIGHT_WITH_WIDTH) {
                            Vector2f newSizeByWidth = new Vector2f(mImageSize.x, mImageSize.x / aspect);
                            updateOffsetByDimension(newSizeByWidth);
                            if (!mHeightInput.get().isFocused()) {
                                mHeightInput.get().setValue(toOptionalSignedString(newSizeByWidth.y()));
                            }
                        }
                        mSyncAspectRatio = SyncAspectRatio.SYNCED;
                    }
                }
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifier) {
        if (this.data != null) {
            boolean isEscape = false;
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                Objects.requireNonNull(Objects.requireNonNull(minecraft).player).closeContainer();
                isEscape = true;
            }
            return isEscape
                    || this.mURLInput.get().keyPressed(keyCode, scanCode, modifier) || this.mURLInput.get().canConsumeInput()
                    || this.mColorInput.get().keyPressed(keyCode, scanCode, modifier) || this.mColorInput.get().canConsumeInput()
                    || this.mWidthInput.get().keyPressed(keyCode, scanCode, modifier) || this.mWidthInput.get().canConsumeInput()
                    || this.mHeightInput.get().keyPressed(keyCode, scanCode, modifier) || this.mHeightInput.get().canConsumeInput()
                    || this.mOffsetXInput.get().keyPressed(keyCode, scanCode, modifier) || this.mOffsetXInput.get().canConsumeInput()
                    || this.mOffsetYInput.get().keyPressed(keyCode, scanCode, modifier) || this.mOffsetYInput.get().canConsumeInput()
                    || this.mOffsetZInput.get().keyPressed(keyCode, scanCode, modifier) || this.mOffsetZInput.get().canConsumeInput()
                    || super.keyPressed(keyCode, scanCode, modifier);
        }
        return super.keyPressed(keyCode, scanCode, modifier);
    }

    @Override
    public void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        if (this.data != null) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, GUI_TEXTURE);
            gui.blit(GUI_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
            if (this.mImageUrlStatus == ImageUrlStatus.INVALID) {
                gui.blit(GUI_TEXTURE, this.leftPos + 9, this.topPos + 27, 179, 53, 18, 19);
            }
        }
    }

    @Override
    public void renderLabels(@Nonnull GuiGraphics gui, int mouseX, int mouseY) {
        if (this.data != null) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderTexture(0, GUI_TEXTURE);

            int alpha = this.mImageColor >>> 24;
            if (alpha > 0) {
                int red = (this.mImageColor >> 16) & 255, green = (this.mImageColor >> COLOR_MAX_LENGTH) & 255, blue = this.mImageColor & 255;
                RenderSystem.setShaderColor(red / 255.0F, green / 255.0F, blue / 255.0F, alpha / 255.0F);
                gui.blit(GUI_TEXTURE, 38, 157, 180, 194, 10, 10);
                gui.blit(GUI_TEXTURE, 82, 185, 180, 194, 17, 17);
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            gui.blit(GUI_TEXTURE, 82, 185, 202, 194 - this.mRotation.ordinal() * 20, 17, 17);

            drawCenteredStringWithoutShadow(gui, this.font, IMAGE_TEXT, 12);
            drawCenteredStringWithoutShadow(gui, this.font, OFFSET_TEXT, 86);
            drawCenteredStringWithoutShadow(gui, this.font, OTHERS_TEXT, 138);

            int offsetX = mouseX - this.leftPos, offsetY = mouseY - this.topPos;
            if (offsetX >= 9 && offsetY >= 27 && offsetX < 27 && offsetY < 46) {
                gui.renderTooltip(this.font, URL_TEXT, offsetX, offsetY);
            } else if (offsetX >= 34 && offsetY >= 153 && offsetX < 52 && offsetY < 172) {
                gui.renderTooltip(this.font, COLOR_TEXT, offsetX, offsetY);
            } else if (offsetX >= 9 && offsetY >= 49 && offsetX < 27 && offsetY < 68) {
                gui.renderTooltip(this.font, WIDTH_TEXT, offsetX, offsetY);
            } else if (offsetX >= 79 && offsetY >= 49 && offsetX < 97 && offsetY < 68) {
                gui.renderTooltip(this.font, HEIGHT_TEXT, offsetX, offsetY);
            } else if (offsetX >= 149 && offsetY >= 49 && offsetX < 167 && offsetY < 68) {
                gui.renderTooltip(this.font, KEEP_ASPECT_RATIO_TEXT, offsetX, offsetY);
            } else if (offsetX >= 9 && offsetY >= 101 && offsetX < 27 && offsetY < 120) {
                gui.renderTooltip(this.font, OFFSET_X_TEXT, offsetX, offsetY);
            } else if (offsetX >= 63 && offsetY >= 101 && offsetX < 81 && offsetY < 120) {
                gui.renderTooltip(this.font, OFFSET_Y_TEXT, offsetX, offsetY);
            } else if (offsetX >= 117 && offsetY >= 101 && offsetX < 135 && offsetY < 120) {
                gui.renderTooltip(this.font, OFFSET_Z_TEXT, offsetX, offsetY);
            } else if (offsetX >= 117 && offsetY >= 153 && offsetX < 135 && offsetY < 172) {
                gui.renderTooltip(this.font, FLIP_TEXT, offsetX, offsetY);
            } else if (offsetX >= 142 && offsetY >= 153 && offsetX < 160 && offsetY < 172) {
                gui.renderTooltip(this.font, ROTATE_TEXT, offsetX, offsetY);
            } else if (offsetX >= 9 && offsetY >= 153 && offsetX < 27 && offsetY < 172) {
                gui.renderTooltip(this.font, SINGLE_DOUBLE_SIDED_TEXT, offsetX, offsetY);
            }
        }

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.data != null) {
            if (this.mWidthInput.get().isMouseOver(mouseX, mouseY)) {
                if (!this.mInvalidWidth) {
                    this.mWidthInput.get().setValue(toOptionalSignedString(Math.round(this.mImageSize.x * 2.0 + scrollY) * 0.5f));
                    if (this.mKeepAspectRatio) {
                        this.mSyncAspectRatio = SyncAspectRatio.SYNC_HEIGHT_WITH_WIDTH;
                    }
                    return true;
                }
            } else if (this.mHeightInput.get().isMouseOver(mouseX, mouseY)) {
                if (!this.mInvalidHeight) {
                    this.mHeightInput.get().setValue(toOptionalSignedString(Math.round(this.mImageSize.y * 2.0 + scrollY) * 0.5f));
                    if (this.mKeepAspectRatio) {
                        this.mSyncAspectRatio = SyncAspectRatio.SYNC_WIDTH_WITH_HEIGHT;
                    }
                    return true;
                }
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public void removed() {
        super.removed();
        if (this.data != null) {
            ProjectorBlockEntity.ProjectorBlockEntityData networkData = this.data.copy();
            if (this.mImageUrlStatus == ImageUrlStatus.NORMAL) {
                networkData.setLocation(this.mImgUrl);
            }
            if (!this.mInvalidColor) {
                networkData.setColor(this.mImageColor);
            }
            if (!this.mInvalidWidth) {
                networkData.setWidth(this.mImageSize.x());
            }
            if (!this.mInvalidHeight) {
                networkData.setHeight(this.mImageSize.y());
            }
            if (!this.mInvalidOffsetX) {
                networkData.setOffsetX(this.mImageOffset.x());
            }
            if (!this.mInvalidOffsetY) {
                networkData.setOffsetY(this.mImageOffset.y());
            }
            if (!this.mInvalidOffsetZ) {
                networkData.setOffsetZ(this.mImageOffset.z());
            }
            networkData.setDoubleSided(this.mDoubleSided);
            networkData.setKeepAspectRatio(this.mKeepAspectRatio);
            Level level = Minecraft.getInstance().level;
            if (level != null && level.getBlockEntity(this.blockPos) instanceof ProjectorBlockEntity blockEntity) {
                blockEntity.setProjectorBlockEntityData(networkData);
                BlockState blockState = blockEntity.getBlockState().setValue(ProjectorBlock.ROTATION, mRotation);
                level.setBlock(this.blockPos, blockState, ProjectorBlock.UPDATE_NONE);
            }
            new UpdatePayload(blockPos, networkData, mRotation).sendToServer();
        }
    }

    private void updateOffsetByRotation(ProjectorBlock.InternalRotation newRotation) {
        if (!mInvalidOffsetX && !mInvalidOffsetY && !mInvalidOffsetZ) {
            Vector3f absolute = relativeToAbsolute(mImageOffset, mImageSize, mRotation);
            Vector3f newRelative = absoluteToRelative(absolute, mImageSize, newRotation);
            mOffsetXInput.get().setValue(toSignedString(newRelative.x()));
            mOffsetYInput.get().setValue(toSignedString(newRelative.y()));
            mOffsetZInput.get().setValue(toSignedString(newRelative.z()));
        }
        mRotation = newRotation;
    }

    private void updateOffsetByDimension(Vector2f newDimension) {
        if (!mInvalidOffsetX && !mInvalidOffsetY && !mInvalidOffsetZ) {
            Vector3f absolute = relativeToAbsolute(mImageOffset, mImageSize, mRotation);
            Vector3f newRelative = absoluteToRelative(absolute, newDimension, mRotation);
            mOffsetXInput.get().setValue(toSignedString(newRelative.x()));
            mOffsetYInput.get().setValue(toSignedString(newRelative.y()));
            mOffsetZInput.get().setValue(toSignedString(newRelative.z()));
        }
        mImageSize = newDimension;
    }

    private void updateDoubleSided(boolean doubleSided) {
        mDoubleSided = doubleSided;
        mSwitchSingleSided.get().visible = doubleSided;
        mSwitchDoubleSided.get().visible = !doubleSided;
    }

    private void updateKeepAspectRatio(boolean keepAspectRatio) {
        mKeepAspectRatio = keepAspectRatio;
        mKeepAspectUnchecked.get().visible = !keepAspectRatio;
        mKeepAspectChecked.get().visible = keepAspectRatio;
        mSyncAspectRatio = mKeepAspectRatio ? SyncAspectRatio.SYNC_WIDTH_WITH_HEIGHT : SyncAspectRatio.SYNCED;
    }

    private static void drawCenteredStringWithoutShadow(GuiGraphics gui, Font renderer, Component string, int y) {
        gui.drawString(renderer, string.getString(), (88 - renderer.width(string) / 2), y, 0x404040, false);
    }

    private static float parseFloatOrDefault(String text, float defaultValue) {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static String toOptionalSignedString(float f) {
        return Float.toString(Math.round(f * 1.0E3F) / 1.0E3F);
    }

    private static String toSignedString(float f) {
        return Float.isNaN(f) ? String.valueOf(f) : Math.copySign(1.0F, f) <= 0 ? "-" + Math.round(0.0F - f * 1.0E3F) / 1.0E3F : "+" + Math.round(f * 1.0E3F) / 1.0E3F;
    }

    private static Vector3f relativeToAbsolute(Vector3f relatedOffset, Vector2f size, ProjectorBlock.InternalRotation rotation) {
        Vector4f center = new Vector4f(0.5F * size.x, 0.0F, 0.5F * size.y, 1.0F);
        center.mul(new Matrix4f().translate(relatedOffset.x(), -relatedOffset.z(), relatedOffset.y()));
        center.mul(new Matrix4f().translate(-0.5F, 0.0F, 0.5F - size.y()));
        rotation.transform(center);
        return new Vector3f(center.x() / center.w(), center.y() / center.w(), center.z() / center.w());
    }

    private static Vector3f absoluteToRelative(Vector3f absoluteOffset, Vector2f size, ProjectorBlock.InternalRotation rotation) {
        Vector4f center = new Vector4f(absoluteOffset, 1.0F);
        rotation.invert().transform(center);
        center.mul(new Matrix4f().translate(0.5F, 0.0F, -0.5F + size.y()));
        center.mul(new Matrix4f().translate(-0.5F * size.x, 0.0F, -0.5F * size.y));
        return new Vector3f(center.x() / center.w(), center.z() / center.w(), -center.y() / center.w());
    }

    private static class Button extends AbstractButton {
        private final Runnable callback;
        private final Component msg;
        private final int u;
        private final int v;

        public Button(int x, int y, int u, int v, int width, int height, Component msg, Runnable callback) {
            super(x, y, width, height, msg);
            this.callback = callback;
            this.msg = msg;
            this.u = u;
            this.v = v;
        }

        @Override
        public void onPress() {
            callback.run();
        }

        @Override
        public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            gui.blit(GUI_TEXTURE, getX(), getY(), u, v, width, height);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            output.add(NarratedElementType.TITLE, msg);
        }
    }

    private enum ImageUrlStatus {
        NORMAL, INVALID, EMPTY;
    }

    private enum SyncAspectRatio {
        SYNCED, SYNC_WIDTH_WITH_HEIGHT, SYNC_HEIGHT_WITH_WIDTH
    }
}