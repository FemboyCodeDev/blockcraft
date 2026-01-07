package com.mojang.rubydung;

import com.mojang.rubydung.level.Tesselator;
import org.lwjgl.opengl.GL11;

public class UIRenderer {
    Tesselator t = new Tesselator();
    private static int texture = Textures.loadTexture("/UI-texture.png", 9728);


    public void renderTex(int tex,int height, int width, int x,int y){
        float u0 = ((float) (tex % 16))/ 16.0F;
        float u1 = u0 + 0.0624375F;
        float v0 = 0.0F;

        v0 = (((float) (tex)-(tex % 16))/16)/16;
        System.out.println((u0*16)+","+(v0*16));

        float v1 = v0 + 0.0624375F;
        float c1 = 1.0F;
        float c2 = 0.8F;
        float c3 = 0.6F;
        float br;


        float tileSizeX = (float) 1 / (float)width;
        float tileSizeY = (float) 1 / (float)height;

        float x1 = tileSizeX*(x+1);
        float y1 = tileSizeY*(y+1);
        float x2 = x1-tileSizeX;
        float y2 = y1-tileSizeY;

        GL11.glBindTexture(3553, texture);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        t.init();
        t.tex(u1, v0);
        t.vertex(x1, y1, 0);
        t.tex(u0, v0);
        t.vertex(x2, y1, 0);
        t.tex(u0, v1);
        t.vertex(x2, y2, 0);
        t.tex(u1, v1);
        t.vertex(x1, y2, 0);
        t.flush();
    }
    public void renderMenuBackground(int buttonPosX,int buttonMargin,int widthTiles,int heightTiles){
        for (int x = buttonMargin; x<widthTiles-buttonMargin; x++) {
            //this.menuRenderer.renderTex(17, heightTiles, widthTiles, x, 5);
            if (x == buttonMargin){
                this.renderTex(0, heightTiles, widthTiles, x, buttonPosX + 1);
                this.renderTex(32, heightTiles, widthTiles, x, buttonPosX);
            }else if(x==widthTiles-buttonMargin-1){
                this.renderTex(2, heightTiles, widthTiles, x, buttonPosX + 1);
                this.renderTex(34, heightTiles, widthTiles, x, buttonPosX);
            }else {
                this.renderTex(1, heightTiles, widthTiles, x, buttonPosX + 1);
                this.renderTex(33, heightTiles, widthTiles, x, buttonPosX);
            }
        }
    }
}
