package src.main.java.com.mojang.rubydung.level;

import src.main.java.com.mojang.rubydung.Textures;
import src.main.java.com.mojang.rubydung.phys.AABB;
import org.lwjgl.opengl.GL11;

public class Chunk {
   public AABB aabb;
   public final Level level;
   public final int x0;
   public final int y0;
   public final int z0;
   public final int x1;
   public final int y1;
   public final int z1;
   private boolean dirty = true;
   private int lists = -1;
   private static int texture = Textures.loadTexture("terrain.png", 9728);
   private static Tesselator t = new Tesselator();
   public static int rebuiltThisFrame = 0;
   public static int updates = 0;

   public final Tile[] blocks = {Tile.air,Tile.grass,Tile.rock,Tile.plank,Tile.dirt, Tile.water};

   public Chunk(Level level, int x0, int y0, int z0, int x1, int y1, int z1) {
      this.level = level;
      this.x0 = x0;
      this.y0 = y0;
      this.z0 = z0;
      this.x1 = x1;
      this.y1 = y1;
      this.z1 = z1;
      this.aabb = new AABB((float)x0, (float)y0, (float)z0, (float)x1, (float)y1, (float)z1);
      this.lists = GL11.glGenLists(2);
   }

   private void rebuild(int layer) {
      if (rebuiltThisFrame != 3) {
         this.dirty = false;
         ++updates;
         ++rebuiltThisFrame;
         GL11.glNewList(this.lists + layer, 4864);
         GL11.glEnable(3553);
         GL11.glBindTexture(3553, texture);
         t.init();
         int tiles = 0;

         for(int x = this.x0; x < this.x1; ++x) {
            for(int y = this.y0; y < this.y1; ++y) {
               for(int z = this.z0; z < this.z1; ++z) {
                  if (this.level.isTile(x, y, z)) {
                     if (this.level.getTile(x, y, z) != -1) {
                        blocks[this.level.getTile(x, y, z)].render(t, this.level, layer, x, y, z);
                     } else{
                        blocks[4].render(t, this.level, layer, x, y, z);
                     }

                     //int tex = y != this.level.depth * 2 / 3;
                     /*
                     int tex = 0;
                     ++tiles;
                     if (tex == 0) {
                        Tile.rock.render(t, this.level, layer, x, y, z);
                     } else {
                        Tile.grass.render(t, this.level, layer, x, y, z);
                     }
                     */
                  }
               }
            }
         }

         t.flush();
         GL11.glDisable(3553);
         GL11.glEndList();
      }
   }

   public void render(int layer) {
      if (this.dirty) {
         this.rebuild(0);
         this.rebuild(1);
         //this.rebuild(2);
      }

      GL11.glCallList(this.lists + layer);
   }

   public void setDirty() {
      this.dirty = true;
   }
}
