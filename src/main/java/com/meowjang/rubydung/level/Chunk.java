package com.meowjang.rubydung.level;

import com.meowjang.rubydung.Textures;
import com.meowjang.rubydung.phys.AABB;
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
   public boolean updateBlocks = false;

   public final Tile[] blocks = {Tile.air,Tile.grass,Tile.rock,Tile.plank,Tile.dirt, Tile.water,Tile.log,Tile.leaf};
   public final int[] waterIndexes = {5,51,52,53};


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

   public void UpdateFluid(int input,int output) {
       for (int x = this.x0; x < this.x1; ++x) {
           for (int y = this.y0; y < this.y1; ++y) {
               for (int z = this.z0; z < this.z1; ++z) {
                   if (this.level.isTile(x, y, z)) {
                       if (this.level.getTile(x, y, z) == input) { //
                           //wdif (updateBlocks) {
                               if (this.level.getTile(x, y - 1, z) == 0) {this.level.setTile(x, y - 1, z, output);}
                               if (this.level.getTile(x, y, z - 1) == 0) {this.level.setTile(x, y, z - 1, output);}
                               if (this.level.getTile(x - 1, y, z) == 0) {this.level.setTile(x - 1, y, z, output);}
                               if (this.level.getTile(x, y, z + 1) == 0) {this.level.setTile(x, y, z + 1, output);}
                               if (this.level.getTile(x + 1, y, z) == 0) {this.level.setTile(x + 1, y, z, output);}

                               //}
                           }
                       }
                   }
               }
           }
       }
       public void UpdateFluid(){
           for (int i = 0; i < this.waterIndexes.length-1; ++i) {
               UpdateFluid(this.waterIndexes[i],-this.waterIndexes[i+1]);
           }
           for (int i = 0; i < this.waterIndexes.length; ++i) {
               ReplaceBlock(-this.waterIndexes[i],this.waterIndexes[i]);
           }
       }
       public boolean isWaterTile(int index){
       for (int i = 0; i < this.waterIndexes.length; ++i) {
           if (this.waterIndexes[i] == index) { return true; }
       }

       return false;
       }
    public int getWaterTile(int index) {
        for (int i = 0; i < this.waterIndexes.length; ++i) {
            if (this.waterIndexes[i] == index) {
                return i;
            }
        }
        return 5;
    }
    public void ReplaceBlock(int input,int output) {
        for (int x = this.x0; x < this.x1; ++x) {
            for (int y = this.y0; y < this.y1; ++y) {
                for (int z = this.z0; z < this.z1; ++z) {
                    if (this.level.isTile(x, y, z)) {
                        if (this.level.getTile(x, y, z) == input) {this.level.setTile(x, y, z, output);}
                    }
                }
            }
        }
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
                     if (this.level.getTile(x, y, z) >= 0) {

                         if (isWaterTile(this.level.getTile(x, y, z))) {
                             float height = (float) (getWaterTile(this.level.getTile(x, y, z))+1) /(this.waterIndexes.length+1);
                             blocks[5].render(t, this.level, layer, x, y, z, 1-height);

                         }
                         else{
                             blocks[this.level.getTile(x, y, z)].render(t, this.level, layer, x, y, z);
                         }
                        }
                     } else{
                      if (this.level.getTile(x,y,z) == -5){blocks[4].render(t, this.level, layer, x, y, z);}
                        //blocks[4].render(t, this.level, layer, x, y, z);
                     }
                  }
               }
            }
         }

         t.flush();
         GL11.glDisable(3553);
         GL11.glEndList();
          updateBlocks = false;
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
