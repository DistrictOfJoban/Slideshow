package org.teacon.slides.cache;

import org.teacon.slides.Slideshow;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

public final class WebpToPng {
    private static boolean isWebP(byte[] webp) {
        if (webp.length < 12) {
            return false;
        }
        boolean isRiff = webp[0] == 'R' && webp[1] == 'I' && webp[2] == 'F' && webp[3] == 'F';
        boolean isWebp = webp[8] == 'W' && webp[9] == 'E' && webp[10] == 'B' && webp[11] == 'P';
        return isRiff && isWebp;
    }

    public static byte[] webpToPng(byte[] webp) {
        try {
            if (!isWebP(webp)) {
                return webp;
            }
            Iterator<ImageReader> imageReader = ImageIO.getImageReadersByFormatName("webp");
            if (!imageReader.hasNext()) {
                return webp;
            }
            ImageReader webpReader = imageReader.next();
            try (ByteArrayInputStream bais = new ByteArrayInputStream(webp); MemoryCacheImageInputStream inputStream = new MemoryCacheImageInputStream(bais)) {
                webpReader.setInput(inputStream);
                BufferedImage bufferedImage = webpReader.read(0);
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); MemoryCacheImageOutputStream outputStream = new MemoryCacheImageOutputStream(baos)) {
                    ImageIO.write(bufferedImage, "png", outputStream);
                    outputStream.flush();
                    return baos.toByteArray();
                }
            }
        } catch (Exception e) {
            Slideshow.LOGGER.warn("Failed to convert webp to PNG", e);
            return webp;
        }
    }
}