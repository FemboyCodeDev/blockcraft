package src.main.java.com.mojang.rubydung.level;

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

      if (!level.isSolidTile(x - 1, y, z,this.render_mode)) {
         br = level.getBrightness(x - 1, y, z) * c3;
         if (br == c3 ^ layer == 1) {
            t.color(br, br, br);
            t.tex(u1, v0);
            t.vertex(x0, y1, z1);
            t.tex(u0, v0);
            t.vertex(x0, y1, z0);
            t.tex(u0, v1);
            t.vertex(x0, y0, z0);
            t.tex(u1, v1);
            t.vertex(x0, y0, z1);
         }
      }

      if (!level.isSolidTile(x + 1, y, z,this.render_mode)) {
         br = level.getBrightness(x + 1, y, z) * c3;
         if (br == c3 ^ layer == 1) {
            t.color(br, br, br);
            t.tex(u0, v1);
            t.vertex(x1, y0, z1);
            t.tex(u1, v1);
            t.vertex(x1, y0, z0);
            t.tex(u1, v0);
            t.vertex(x1, y1, z0);
            t.tex(u0, v0);
            t.vertex(x1, y1, z1);
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
