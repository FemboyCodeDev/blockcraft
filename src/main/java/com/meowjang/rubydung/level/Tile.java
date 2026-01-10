package com.meowjang.rubydung.level;

public class Tile {
   public static Tile air = new Tile(-1);
   public static Tile grass = new Tile(0);
   public static Tile rock = new Tile(1);
   public static Tile plank = new Tile(2);
   public static Tile dirt = new Tile(3);
   public static Tile water = new Tile(4,0);
   public static Tile log = new Tile(5);
   public static Tile leaf = new Tile(6);

    public final int[] waterIndexes = {5,51,52,53};



   private int tex = 0;
   private int render_mode = 1;

   private Tile(int tex, int mode) {
      this.tex = tex;
      this.render_mode = mode;
   }
   private Tile(int tex) {
      this.tex = tex;
   }

    public int getWaterTile(int index) {
        for (int i = 0; i < this.waterIndexes.length; ++i) {
            if (this.waterIndexes[i] == index) {
                return i;
            }
        }
        return -1;
    }

    public float getFluidHeight(int x, int y, int z, Level level) {
       int tile = getWaterTile(level.getTile(x, y, z));
       if (tile == -1) {return 1;}
        float height = (float) (tile+1) /(this.waterIndexes.length+1);
       return 1-height;
        //return height;
    }



   public void render_flat(Tesselator t, Level level, int layer, int x, int y, int z) {
      float u0 = (float) this.tex / 16.0F;
      float u1 = u0 + 0.0624375F;
      float v0 = 0.0F;
      float v1 = v0 + 0.0624375F;
      float c1 = 1.0F;
      float c2 = 0.8F;
      float c3 = 0.6F;
      float x0 = (float) x + 0.0F;
      float x1 = (float) x + 1.0F;
      float y0 = (float) y + 0.0F;
      float y1 = (float) y + 1.0F;
      float z0 = (float) z + 0.0F;
      float z1 = (float) z + 1.0F;
      float br;
      br = level.getBrightness(x, y - 1, z) * c1;
      t.color(br, br, br);
      t.tex(u0, v1);
      t.vertex(0, 0, 1);
      t.tex(u0, v0);
      t.vertex(10, 0, 1);
      t.tex(u1, v0);
      t.vertex(10, 10, 1);
      t.tex(u1, v1);
      t.vertex(0, 10, z1);

   }
    public void render(Tesselator t, Level level, int layer, int x, int y, int z){render(t,level,layer,x,y,z,1.0F);}
   public void render(Tesselator t, Level level, int layer, int x, int y, int z, float height) {
      float u0 = (float)this.tex / 16.0F;
      float u1 = u0 + 0.0624375F;
      float v0 = 0.0F;
      float v1 = v0 + 0.0624375F;
      float c1 = 1.0F;
      float c2 = 0.8F;
      float c3 = 0.6F;
      float x0 = (float)x + 0.0F;
      float x1 = (float)x + 1.0F;
      float y0 = (float)y + 0.0F;
      float y1 = (float)y + height;
      float z0 = (float)z + 0.0F;
      float z1 = (float)z + 1.0F;
      float br;

      float[] p1 = {x0, y0, z1}; // 0;0;1
      float[] p2 = {x0, y0, z0}; // 0;0;0
      float[] p3 = {x1, y0, z0}; // 1;0;0
      float[] p4 = {x1, y0, z1}; // 1;0;1

       float[] p5 = {x1, y1, z1}; //1;1;1
       float[] p6 = {x1, y1, z0}; //1;1;0
       float[] p7 = {x0, y1, z0}; //0;1;0
       float[] p8 = {x0, y1, z1}; //0;1;1

       float[] p9 = {x0, y1, z0}; //0;1;0
       float[] p10 = {x1, y1, z0}; //1;1;0
       float[] p11 = {x1, y0, z0}; //1;0;0
       float[] p12 = {x0, y0, z0}; //0;0;0

       float[] p13 = {x0, y1, z1}; //0;1;1
       float[] p14 = {x0, y0, z1}; //0;0;1
       float[] p15 = {x1, y0, z1}; //1;0;1
       float[] p16 = {x1, y1, z1}; //1;1;1

       // Left Face (X-) points
       float[] p17 = {x0, y1, z1}; // 0;1;1
       float[] p18 = {x0, y1, z0}; // 0;1;0
       float[] p19 = {x0, y0, z0}; // 0;0;0
       float[] p20 = {x0, y0, z1}; // 0;0;1

       // Right Face (X+) points
       float[] p21 = {x1, y0, z1}; // 1;0;1
       float[] p22 = {x1, y0, z0}; // 1;0;0
       float[] p23 = {x1, y1, z0}; // 1;1;0
       float[] p24 = {x1, y1, z1}; // 1;1;1


// --- Top Vertex Height Recalculation ---

       // Helper to get neighbor height or default to current height if not water
       float h_curr = height;

       float h_xN = getWaterTile(level.getTile(x - 1, y, z)) != -1 ? getFluidHeight(x - 1, y, z, level) : h_curr;
       float h_xP = getWaterTile(level.getTile(x + 1, y, z)) != -1 ? getFluidHeight(x + 1, y, z, level) : h_curr;
       float h_zN = getWaterTile(level.getTile(x, y, z - 1)) != -1 ? getFluidHeight(x, y, z - 1, level) : h_curr;
       float h_zP = getWaterTile(level.getTile(x, y, z + 1)) != -1 ? getFluidHeight(x, y, z + 1, level) : h_curr;

       // Diagonal helpers (treating as current height if not water)
       float h_xNzN = getWaterTile(level.getTile(x - 1, y, z - 1)) != -1 ? getFluidHeight(x - 1, y, z - 1, level) : h_curr;
       float h_xNzP = getWaterTile(level.getTile(x - 1, y, z + 1)) != -1 ? getFluidHeight(x - 1, y, z + 1, level) : h_curr;
       float h_xPzN = getWaterTile(level.getTile(x + 1, y, z - 1)) != -1 ? getFluidHeight(x + 1, y, z - 1, level) : h_curr;
       float h_xPzP = getWaterTile(level.getTile(x + 1, y, z + 1)) != -1 ? getFluidHeight(x + 1, y, z + 1, level) : h_curr;

       // Corner Averages
       float h00 = (h_curr + h_xN + h_zN + h_xNzN) / 4.0F; // X- Z-
       float h01 = (h_curr + h_xN + h_zP + h_xNzP) / 4.0F; // X- Z+
       float h10 = (h_curr + h_xP + h_zN + h_xPzN) / 4.0F; // X+ Z-
       float h11 = (h_curr + h_xP + h_zP + h_xPzP) / 4.0F; // X+ Z+

       // Apply to TOP vertices only
       p5[1] = (float)y + h11; p6[1] = (float)y + h10; p7[1] = (float)y + h00; p8[1] = (float)y + h01;
       p9[1] = (float)y + h00; p10[1] = (float)y + h10;
       p13[1] = (float)y + h01; p16[1] = (float)y + h11;
       p17[1] = (float)y + h01; p18[1] = (float)y + h00;
       p23[1] = (float)y + h10; p24[1] = (float)y + h11;

       //c1 = 2.0f;
      //layer = 0;
      if (!level.isSolidTile(x, y - 1, z,this.render_mode)) {
         br = level.getBrightness(x, y - 1, z) * c1;
         if (br == c1 ^ layer == 1) {
            t.color(br, br, br);
            t.tex(u0, v1);
            t.vertex(p1[0],p1[1],p1[2]);
            t.tex(u0, v0);
            t.vertex(p2[0],p2[1],p2[2]);
            t.tex(u1, v0);
            t.vertex(p3[0],p3[1],p3[2]);
            t.tex(u1, v1);
            t.vertex(p4[0],p4[1],p4[2]);
         }
      }


      if (!level.isSolidTile(x, y + 1, z,this.render_mode)) {
         br = level.getBrightness(x, y, z) * c1;
         if (br == c1 ^ layer == 1) {
            t.color(br, br, br);
            t.tex(u1, v1);
            t.vertex(p5[0],p5[1],p5[2]);
            t.tex(u1, v0);
            t.vertex(p6[0],p6[1],p6[2]);
            t.tex(u0, v0);
            t.vertex(p7[0],p7[1],p7[2]);
            t.tex(u0, v1);
            t.vertex(p8[0],p8[1],p8[2]);
         }
      }




      if (!level.isSolidTile(x, y, z - 1,this.render_mode)) {
         br = level.getBrightness(x, y, z - 1) * c2;
         if (br == c2 ^ layer == 1) {
            t.color(br, br, br);
            t.tex(u1, v0);
            t.vertex(p9[0],p9[1],p9[2]);
            t.tex(u0, v0);
            t.vertex(p10[0],p10[1],p10[2]);
            t.tex(u0, v1);
            t.vertex(p11[0],p11[1],p11[2]);
            t.tex(u1, v1);
            t.vertex(p12[0],p12[1],p12[2]);
         }
      }

      if (!level.isSolidTile(x, y, z + 1,this.render_mode)) {
         br = level.getBrightness(x, y, z + 1) * c2;
         if (br == c2 ^ layer == 1) {
            t.color(br, br, br);
            t.tex(u0, v0);
            t.vertex(p13[0],p13[1],p13[2]);
            t.tex(u0, v1);
            t.vertex(p14[0],p14[1],p14[2]);
            t.tex(u1, v1);
            t.vertex(p15[0],p15[1],p15[2]);
            t.tex(u1, v0);
            t.vertex(p16[0],p16[1],p16[2]);
         }
      }

// Left Face (X-)
       if (!level.isSolidTile(x - 1, y, z, this.render_mode)) {
           br = level.getBrightness(x - 1, y, z) * c3;
           if (br == c3 ^ layer == 1) {
               t.color(br, br, br);
               t.tex(u1, v0);
               t.vertex(p17[0], p17[1], p17[2]);
               t.tex(u0, v0);
               t.vertex(p18[0], p18[1], p18[2]);
               t.tex(u0, v1);
               t.vertex(p19[0], p19[1], p19[2]);
               t.tex(u1, v1);
               t.vertex(p20[0], p20[1], p20[2]);
           }
       }

       // Right Face (X+)
       if (!level.isSolidTile(x + 1, y, z, this.render_mode)) {
           br = level.getBrightness(x + 1, y, z) * c3;
           if (br == c3 ^ layer == 1) {
               t.color(br, br, br);
               t.tex(u0, v1);
               t.vertex(p21[0], p21[1], p21[2]);
               t.tex(u1, v1);
               t.vertex(p22[0], p22[1], p22[2]);
               t.tex(u1, v0);
               t.vertex(p23[0], p23[1], p23[2]);
               t.tex(u0, v0);
               t.vertex(p24[0], p24[1], p24[2]);
           }
       }
   }

   public void renderFace(Tesselator t, int x, int y, int z, int face) {
      float x0 = (float)x + 0.0F;
      float x1 = (float)x + 1.0F;
      float y0 = (float)y + 0.0F;
      float y1 = (float)y + 1.0F;
      float z0 = (float)z + 0.0F;
      float z1 = (float)z + 1.0F;
      if (face == 0) {
         t.vertex(x0, y0, z1);
         t.vertex(x0, y0, z0);
         t.vertex(x1, y0, z0);
         t.vertex(x1, y0, z1);
      }

      if (face == 1) {
         t.vertex(x1, y1, z1);
         t.vertex(x1, y1, z0);
         t.vertex(x0, y1, z0);
         t.vertex(x0, y1, z1);
      }

      if (face == 2) {
         t.vertex(x0, y1, z0);
         t.vertex(x1, y1, z0);
         t.vertex(x1, y0, z0);
         t.vertex(x0, y0, z0);
      }

      if (face == 3) {
         t.vertex(x0, y1, z1);
         t.vertex(x0, y0, z1);
         t.vertex(x1, y0, z1);
         t.vertex(x1, y1, z1);
      }

      if (face == 4) {
         t.vertex(x0, y1, z1);
         t.vertex(x0, y1, z0);
         t.vertex(x0, y0, z0);
         t.vertex(x0, y0, z1);
      }

      if (face == 5) {
         t.vertex(x1, y0, z1);
         t.vertex(x1, y0, z0);
         t.vertex(x1, y1, z0);
         t.vertex(x1, y1, z1);
      }

   }
}
