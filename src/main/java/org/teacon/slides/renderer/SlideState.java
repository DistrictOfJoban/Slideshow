package org.teacon.slides.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.teacon.slides.Slideshow;
import org.teacon.slides.cache.ImageCache;
import org.teacon.slides.slide.IconSlide;
import org.teacon.slides.slide.ImageSlide;
import org.teacon.slides.slide.Slide;
import org.teacon.slides.texture.AnimatedTextureProvider;
import org.teacon.slides.texture.GIFDecoder;
import org.teacon.slides.texture.StaticTextureProvider;
import org.teacon.slides.texture.TextureProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

public final class SlideState {
    private static final Executor RENDER_EXECUTOR = r -> RenderSystem.recordRenderCall(r::run);
    private static final AtomicReference<ConcurrentHashMap<SlideStateProperties, SlideState>> sCache;

    static {
        sCache = new AtomicReference<>(new ConcurrentHashMap<>());
    }

    private static int cleanerTimer = 0;
    private static long animationTick = 0L;

    private static final int RECYCLE_SECONDS = 120; // 2min
    private static final int RETRY_INTERVAL_SECONDS = 30; // 30s
    private static final int CLEANER_INTERVAL_SECONDS = 720; // 12min


    public static void tick(Minecraft minecraft) {
        if (!minecraft.isPaused()) {
            if (++animationTick % 20 == 0) {
                ConcurrentHashMap<SlideStateProperties, SlideState> map = sCache.getAcquire();
                if (!map.isEmpty()) {
                    map.entrySet().removeIf(entry -> entry.getValue().update());
                }
                if (++cleanerTimer > CLEANER_INTERVAL_SECONDS) {
                    int n = ImageCache.getInstance().cleanResources();
                    if (n != 0) {
                        Slideshow.LOGGER.debug("Cleanup {} http cache image resources", n);
                    }
                    cleanerTimer = 0;
                }
            }
        }
    }

    public static void onPlayerLeft() {
        RenderSystem.recordRenderCall(() -> {
            ConcurrentHashMap<SlideStateProperties, SlideState> map = sCache.getAndSet(new ConcurrentHashMap<>());
            map.values().forEach(s -> s.mSlide.close());
            Slideshow.LOGGER.debug("Release {} slide images", map.size());
            map.clear();
        });
    }

    public static long getAnimationTick() {
        return animationTick;
    }

    public static Slide getSlide(@Nonnull SlideStateProperties slideStateProperties) {
        if (slideStateProperties.location().isEmpty()) {
            return null;
        }
        return sCache.getAcquire().computeIfAbsent(slideStateProperties, (loc) -> new SlideState(slideStateProperties)).getWithUpdate();
    }


    private Slide mSlide;
    private State mState;

    private int mCounter;

    private SlideState(SlideStateProperties slideStateProperties) {
        URI uri = createURI(slideStateProperties.location());
        if (uri == null) {
            mSlide = Slide.failed();
            mState = State.FAILED;
            mCounter = RETRY_INTERVAL_SECONDS;
        } else {
            mSlide = Slide.loading();
            mState = State.LOADING;
            mCounter = RECYCLE_SECONDS;
            ImageCache.getInstance().getResource(uri, true).thenCompose((data) -> SlideState.createTexture(data, slideStateProperties.disableLod()))
                    .thenAccept(textureProvider -> {
                        if (mState == State.LOADING) {
                            mSlide = Slide.make(textureProvider);
                            mState = State.LOADED;
                        } else {
                            // timeout
                            assert mState == State.LOADED;
                            textureProvider.close();
                        }
                    }).exceptionally(e -> {
                        RenderSystem.recordRenderCall(() -> {
                            assert mState == State.LOADING;
                            mSlide = Slide.failed();
                            mState = State.FAILED;
                            mCounter = RETRY_INTERVAL_SECONDS;
                        });
                        return null;
                    });
        }
    }

    @Nonnull
    private Slide getWithUpdate() {
        if (mState != State.FAILED) {
            mCounter = RECYCLE_SECONDS;
        }
        return mSlide;
    }

    private boolean update() {
        if (--mCounter < 0) {
            RenderSystem.recordRenderCall(() -> {
                if (mState == State.LOADED) {
                    assert mSlide instanceof ImageSlide;
                    mSlide.close();
                } else if (mState == State.LOADING) {
                    assert mSlide == Slide.loading();
                    // timeout
                    mState = State.LOADED;
                } else {
                    assert mSlide instanceof IconSlide;
                    assert mState == State.FAILED;
                }
            });
            return true;
        }
        return false;
    }

    @Nullable
    public static URI createURI(@Nonnull String location) {
        if (!location.isEmpty()) {
            try {
                return URI.create(location);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Nonnull
    private static CompletableFuture<TextureProvider> createTexture(byte[] data, boolean disableLod) {
        return CompletableFuture.supplyAsync(GIFDecoder.checkMagic(data) ? () -> new AnimatedTextureProvider(data) : () -> new StaticTextureProvider(data, disableLod), RENDER_EXECUTOR);
    }

    public enum State {
        FAILED, LOADING, NORMAL, LOADED;
    }
}