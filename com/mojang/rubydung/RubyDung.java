package com.mojang.rubydung;

import com.mojang.rubydung.level.Chunk;
import com.mojang.rubydung.level.Level;
import com.mojang.rubydung.level.LevelRenderer;
import java.awt.Component;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.swing.JOptionPane;

import com.mojang.rubydung.level.Tesselator;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class RubyDung implements Runnable {
   private static final boolean FULLSCREEN_MODE = false;
   private int width;
   private int height;
   private FloatBuffer fogColor = BufferUtils.createFloatBuffer(4);
   private Timer timer = new Timer(60.0F);
   private Level level;
   private Level menu_level;
   private LevelRenderer levelRenderer;
   private LevelRenderer menu_levelRenderer;
   private Player player;
   private IntBuffer viewportBuffer = BufferUtils.createIntBuffer(16);
   private IntBuffer selectBuffer = BufferUtils.createIntBuffer(2000);
   private HitResult hitResult = null;

   //private Tesselator t = new Tesselator();
   //private static int texture = Textures.loadTexture("/terrain.png", 9728);

   private int hotbar_slot = 0;
   private int[] hotbar = {1,2,3,0,0,0,0,0,0,0};

   private int game_mode = 0; // 0: Main menu | 100: In game | 101: Paused game
    private boolean dev_command_pause = true;

   public void init() throws LWJGLException, IOException {
      int col = 920330;
      float fr = 0.5F;
      float fg = 0.8F;
      float fb = 1.0F;
      this.fogColor.put(new float[]{(float)(col >> 16 & 255) / 255.0F, (float)(col >> 8 & 255) / 255.0F, (float)(col & 255) / 255.0F, 1.0F});
      this.fogColor.flip();
      Display.setDisplayMode(new DisplayMode(1024, 768));
      Display.create();
      Display.setTitle("BlockCraft");
      Keyboard.create();
      Mouse.create();

      this.width = Display.getDisplayMode().getWidth();
      this.height = Display.getDisplayMode().getHeight();
      GL11.glEnable(3553);
      GL11.glShadeModel(7425);
      GL11.glClearColor(fr, fg, fb, 0.0F);
      GL11.glClearDepth(1.0D);
      GL11.glEnable(2929);
      GL11.glDepthFunc(515);
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      GL11.glMatrixMode(5888);

      this.game_mode = 0;
      //this.game_mode = 100;

      this.level = new Level(64, 64, 64);
      //this.level.load("level.dat");
      this.level.load("level.dat");
      this.menu_level = new Level(64, 64, 64);
      this.menu_level.load("titlescreen.dat");

      this.menu_levelRenderer = new LevelRenderer(this.menu_level);
      this.levelRenderer = new LevelRenderer(this.level);
      this.player = new Player(this.level);

      Mouse.setGrabbed(true);


   }

   public void destroy() {
      this.level.save();
      Mouse.destroy();
      Keyboard.destroy();
      Display.destroy();
   }

   public void run() {
      try {
         this.init();
      } catch (Exception var9) {
         JOptionPane.showMessageDialog((Component)null, var9.toString(), "Failed to start RubyDung", 0);
         System.exit(0);
      }

      long lastTime = System.currentTimeMillis();
      int frames = 0;
      this.timer.advanceTime();

      try {
         while(!Keyboard.isKeyDown(1) && !Display.isCloseRequested()) {
            if (game_mode == 100) {
               this.timer.advanceTime();
            }else {
               this.timer.advanceTime();
            }
            for(int i = 0; i < this.timer.ticks; ++i) {
               this.tick();
            }
            if (game_mode == 100) {
               this.render(this.timer.a);
               this.devkeybinds();
               Mouse.setGrabbed(true);
            } else if (game_mode == 0){
               this.render_mainMenu(this.timer.a);
                Mouse.setGrabbed(false);

            }else{
               Display.update();
            }
            ++frames;

            while(System.currentTimeMillis() >= lastTime + 1000L) {
               System.out.println(frames + " fps, " + Chunk.updates);
               Chunk.updates = 0;
               lastTime += 1000L;
               frames = 0;
            }
         }
      } catch (Exception var10) {
         var10.printStackTrace();
      } finally {
         this.destroy();
      }

   }

   public void tick() {
      if (this.game_mode == 100) {
         this.player.tick();
      }else if (this.game_mode == 0) {
         this.player.tick();
      }
   }

   public void devkeybinds() {


       if (Keyboard.isKeyDown(38)) {} // 38 = key L
       if (Keyboard.isKeyDown(207)) {

           if (!this.dev_command_pause) {
               System.out.println("Remove layer");
           }
           this.dev_command_pause = true;
           for(int x = 0; x < 64; ++x) {for(int y = 0; y < 64; ++y) {level.setTile((int)x, (int) player.y-2, (int)y,0);}}

       } // Key_END
       else {
           this.dev_command_pause = false;
       }
   }

   private void moveCameraToPlayer(float a) {
      GL11.glTranslatef(0.0F, 0.0F, -0.3F);
      GL11.glRotatef(this.player.xRot, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(this.player.yRot, 0.0F, 1.0F, 0.0F);
      float x = this.player.xo + (this.player.x - this.player.xo) * a;
      float y = this.player.yo + (this.player.y - this.player.yo) * a;
      float z = this.player.zo + (this.player.z - this.player.zo) * a;
      GL11.glTranslatef(-x, -y, -z);

   }

   private void setupCamera(float a) {
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      GLU.gluPerspective(70.0F, (float)this.width / (float)this.height, 0.05F, 1000.0F);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      this.moveCameraToPlayer(a);
   }

   private void setupPickCamera(float a, int x, int y) {
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      this.viewportBuffer.clear();
      GL11.glGetInteger(2978, this.viewportBuffer);
      this.viewportBuffer.flip();
      this.viewportBuffer.limit(16);
      GLU.gluPickMatrix((float)x, (float)y, 5.0F, 5.0F, this.viewportBuffer);
      GLU.gluPerspective(70.0F, (float)this.width / (float)this.height, 0.05F, 1000.0F);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      this.moveCameraToPlayer(a);
   }

   private void pick(float a) {
      this.selectBuffer.clear();
      GL11.glSelectBuffer(this.selectBuffer);
      GL11.glRenderMode(7170);
      this.setupPickCamera(a, this.width / 2, this.height / 2);
      this.levelRenderer.pick(this.player);
      int hits = GL11.glRenderMode(7168);
      this.selectBuffer.flip();
      this.selectBuffer.limit(this.selectBuffer.capacity());
      long closest = 0L;
      int[] names = new int[10];
      int hitNameCount = 0;

      for(int i = 0; i < hits; ++i) {
         int nameCount = this.selectBuffer.get();
         long minZ = (long)this.selectBuffer.get();
         this.selectBuffer.get();
         int j;
         if (minZ >= closest && i != 0) {
            for(j = 0; j < nameCount; ++j) {
               this.selectBuffer.get();
            }
         } else {
            closest = minZ;
            hitNameCount = nameCount;

            for(j = 0; j < nameCount; ++j) {
               names[j] = this.selectBuffer.get();
            }
         }
      }

      if (hitNameCount > 0) {
         this.hitResult = new HitResult(names[0], names[1], names[2], names[3], names[4]);
      } else {
         this.hitResult = null;
      }

   }

   public void render(float a) {
      float xo = (float)Mouse.getDX();
      float yo = (float)Mouse.getDY();
      this.player.turn(xo, yo);
      this.pick(a);

      while(Mouse.next()) {

         int wheel = Mouse.getDWheel();
         if (wheel > 0){
            hotbar_slot += 1;
         }else if (wheel < 0){
            hotbar_slot -= 1;
         }
         if (hotbar_slot > 9){
            hotbar_slot = 0;
         }
         if (hotbar_slot < 0){
            hotbar_slot = 9;
         }
         //System.out.println(hotbar_slot);
         //System.out.println("Current index"+this.hotbar[hotbar_slot]);
         if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.hitResult != null) {
            this.level.setTile(this.hitResult.x, this.hitResult.y, this.hitResult.z, 0);
         }

         if (Mouse.getEventButton() == 1 && Mouse.getEventButtonState() && this.hitResult != null) {
            int x = this.hitResult.x;
            int y = this.hitResult.y;
            int z = this.hitResult.z;
            if (this.hitResult.f == 0) {
               --y;
            }

            if (this.hitResult.f == 1) {
               ++y;
            }

            if (this.hitResult.f == 2) {
               --z;
            }

            if (this.hitResult.f == 3) {
               ++z;
            }

            if (this.hitResult.f == 4) {
               --x;
            }

            if (this.hitResult.f == 5) {
               ++x;
            }

            this.level.setTile(x, y, z, this.hotbar[hotbar_slot]);
         }
      }

      while(Keyboard.next()) {
         if (Keyboard.getEventKey() == 28 && Keyboard.getEventKeyState()) {
            this.level.save();
         }
      }

      GL11.glClear(16640);
      this.setupCamera(a);


      GL11.glEnable(2884);
      GL11.glEnable(2912);
      GL11.glFogi(2917, 2048);
      GL11.glFogf(2914, 0.2F);
      GL11.glFog(2918, this.fogColor);
      GL11.glDisable(2912);


      this.levelRenderer.render(this.player, 0);



      GL11.glEnable(2912);
      this.levelRenderer.render(this.player, 1);
      GL11.glDisable(3553);
      if (this.hitResult != null) {

         this.levelRenderer.renderHit(this.hitResult);
      }

      GL11.glDisable(2912);

      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      float aspectRatio = (float) width / (float) height;
      GLU.gluOrtho2D(0,10*aspectRatio,0,10);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      this.levelRenderer.renderTex(this.hotbar[this.hotbar_slot]-1);
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      GL11.glDisable(GL11.GL_TEXTURE_2D);

      Display.update();
   }

   public void render_mainMenu(float a){


      //System.out.println(this.player.x + ","  + this.player.y + ", " + this.player.z);
      this.player.z = 33;
      this.player.y = 39.4f;
      this.player.x = 30;
       //System.out.println(this.player.xRot + ","  + this.player.yRot);
       this.player.xRot = 15;
       this.player.yRot = 90;
       this.player.xo = this.player.x;
       this.player.yo = this.player.y;
       this.player.zo = this.player.z;



     this.pick(a);

       float mouseX = (float)Mouse.getX();
       float mouseY = (float)Mouse.getY();

       while(Mouse.next()) {
           System.out.println(mouseX + "," + mouseY);
           //System.out.println(hotbar_slot);
           //System.out.println("Current index"+this.hotbar[hotbar_slot]);
           if (Mouse.getEventButton() == 0) {
               System.out.println(mouseX + "," + mouseY);
           }
       }


      GL11.glClear(16640);
      this.setupCamera(a);


      GL11.glEnable(2884);
      GL11.glEnable(2912);
      GL11.glFogi(2917, 2048);
      GL11.glFogf(2914, 0.2F);
      GL11.glFog(2918, this.fogColor);
      GL11.glDisable(2912);


      this.menu_levelRenderer.render(this.player, 0);



      GL11.glEnable(2912);
      this.menu_levelRenderer.render(this.player, 1);
      GL11.glDisable(3553);
      if (this.hitResult != null) {

         this.menu_levelRenderer.renderHit(this.hitResult);
      }

      GL11.glDisable(2912);

      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      float aspectRatio = (float) width / (float) height;
      GLU.gluOrtho2D(0,10*aspectRatio,0,10);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      this.menu_levelRenderer.renderTex(this.hotbar[this.hotbar_slot]-1);
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      GL11.glDisable(GL11.GL_TEXTURE_2D);

      Display.update();
   }

   public static void checkError() {
      int e = GL11.glGetError();
      if (e != 0) {
         throw new IllegalStateException(GLU.gluErrorString(e));
      }
   }

   public static void main(String[] args) throws LWJGLException {
      (new Thread(new RubyDung())).start();
   }
}
