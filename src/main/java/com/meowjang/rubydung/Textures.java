package com.meowjang.rubydung;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class Textures {
    private static HashMap<String, Integer> idMap = new HashMap<>();
    private static int lastId = -9999999;

    /**
     * Loads a texture from an absolute disk path.
     * Example path: "C:/Games/RubyDung/assets/grass.png"
     */
    public static int loadTexture(String absolutePath, int mode) {
        try {
            if (idMap.containsKey(absolutePath)) {
                return idMap.get(absolutePath);
            } else {
                IntBuffer ib = BufferUtils.createIntBuffer(1);
                GL11.glGenTextures(ib);
                int id = ib.get(0);
                bind(id);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, mode);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mode);

                BufferedImage img;
                File file = new File(absolutePath);

                if (file.exists() && file.isFile()) {
                    // Load from disk! ^w^
                    img = ImageIO.read(file);
                } else {
                    // Fallback checkerboard if disk path is invalid :3
                    System.out.println("Texture not found on disk: " + absolutePath + " - Creating placeholder.");
                    img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                    for (int y = 0; y < 16; y++) {
                        for (int x = 0; x < 16; x++) {
                            int color = ((x / 4 + y / 4) % 2 == 0) ? 0xFFFF00FF : 0xFF000000;
                            img.setRGB(x, y, color);
                            //throw new RuntimeException("Texture not found on disk: " + absolutePath + " - Creating placeholder.");
                        }
                    }
                }

                int w = img.getWidth();
                int h = img.getHeight();
                ByteBuffer pixels = BufferUtils.createByteBuffer(w * h * 4);
                int[] rawPixels = new int[w * h];
                img.getRGB(0, 0, w, h, rawPixels, 0, w);

                // Convert ARGB to RGBA for OpenGL
                for (int i = 0; i < rawPixels.length; ++i) {
                    int a = rawPixels[i] >> 24 & 255;
                    int r = rawPixels[i] >> 16 & 255;
                    int g = rawPixels[i] >> 8 & 255;
                    int b = rawPixels[i] & 255;
                    rawPixels[i] = a << 24 | b << 16 | g << 8 | r;
                }

                pixels.asIntBuffer().put(rawPixels);
                GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, 6408, w, h, 6408, 5121, pixels);

                idMap.put(absolutePath, id);
                return id;
            }
        } catch (IOException var14) {
            throw new RuntimeException("Failed to process texture at: " + absolutePath, var14);
        }
    }

    public static void bind(int id) {
        if (id != lastId) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
            lastId = id;
        }
    }
}