package com.meowjang.rubydung;

import com.meowjang.rubydung.level.Chunk;
import com.meowjang.rubydung.level.Level;
import com.meowjang.rubydung.level.LevelRenderer;
//import com.mojang.rubydung.*;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.swing.JOptionPane;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
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
   private UIRenderer menuRenderer;
   private Player player;
   private IntBuffer viewportBuffer = BufferUtils.createIntBuffer(16);
   private IntBuffer selectBuffer = BufferUtils.createIntBuffer(2000);
   private HitResult hitResult = null;
   private boolean running;

   private String new_game_name;
   private boolean editing_game_name;

   private TextInput textInput;

   private int select_level_scroll = 0;



   private String[] levels;

   private boolean menu_button_pressed;

   private FontRenderer fontRenderer;
   //private Tesselator t = new Tesselator();
   //private static int texture = Textures.loadTexture("/terrain.png", 9728);

   private int hotbar_slot = 0;
   private int[] hotbar = {1,2,3,4,5,6,7,0,0,0};


   private int game_mode = 0; // 0: Main menu | 1: Save select | 2: Create Save | 10: Settings| 11: Graphics Settings| 100: In game | 101: Paused game
    private boolean dev_command_pause = true;

    private boolean vsync = false;

    float fps;
    final String version = "0.0.1d";

   public void init() throws LWJGLException, IOException {
      int col = 920330;
      float fr = 0.5F;
      float fg = 0.8F;
      float fb = 1.0F;
      this.fogColor.put(new float[]{(float)(col >> 16 & 255) / 255.0F, (float)(col >> 8 & 255) / 255.0F, (float)(col & 255) / 255.0F, 1.0F});
      this.fogColor.flip();
      Display.setDisplayMode(new DisplayMode(1024, 768));
      Display.setResizable(true);



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
      this.game_mode = 100;
       this.game_mode = 0;

      this.level = new Level(256, 256, 256);
       //this.level = new Level(64, 64, 256);
      //this.level.load("level.dat");
      //this.level.load("level.dat");
      this.menu_level = new Level(64, 64, 64);
      this.menu_level.load("titlescreen.dat");

      this.menuRenderer = new UIRenderer();

      this.menu_levelRenderer = new LevelRenderer(this.menu_level);
      this.levelRenderer = new LevelRenderer(this.level);

      this.fontRenderer = new FontRenderer();

      this.player = new Player(this.level);

      this.running = true;

      this.levels = new String[]{"level.dat","level_2.dat"};
      this.levels = GetFilesInFolder.getFilenamesInFolder(new File("levels")).toArray(new String[0]);

       //this.level.load("levels\\"+this.levels[0]);

       this.textInput = new TextInput();

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
       Mouse.setGrabbed(true);

      try {
         while(running && !Display.isCloseRequested()) {
            if (game_mode == 100) {
               this.timer.advanceTime();
               if(!Mouse.isGrabbed()){
                   Mouse.setGrabbed(true);
               }
            }else {
               this.timer.advanceTime();
            }
            for(int i = 0; i < this.timer.ticks; ++i) {
               this.tick();
            }
            if (game_mode == 100) {
               this.render(this.timer.a);
               this.devkeybinds();


                if (Keyboard.isKeyDown(1)) {
                    if (!this.menu_button_pressed) {
                        game_mode = 101;
                        System.out.println("paused!");
                    }
                    this.menu_button_pressed = true;
                }else{
                    this.menu_button_pressed = false;
                }

            } else if (game_mode == 0){
               this.render_mainMenu(this.timer.a);
                if(Mouse.isGrabbed()){Mouse.setGrabbed(false);}

            } else if(game_mode == 1){
               render_save_select(this.timer.a);
                if(Mouse.isGrabbed()){Mouse.setGrabbed(false);}
            } else if (game_mode == 2){
                render_save_new(this.timer.a);
                if(Mouse.isGrabbed()){Mouse.setGrabbed(false);}
            }else if (game_mode == 10){
                render_settings(this.timer.a);
                if(Mouse.isGrabbed()){Mouse.setGrabbed(false);}
            }else if (game_mode == 11){
                render_settings_video(this.timer.a);
                if(Mouse.isGrabbed()){Mouse.setGrabbed(false);}
            }
            else if (game_mode == 101){
                if(Mouse.isGrabbed()){Mouse.setGrabbed(false);}
                System.out.println("game is paused!");
                //render_background(this.timer.a);
                render_paused(this.timer.a);
                if (Keyboard.isKeyDown(1)) {
                    if (!this.menu_button_pressed) {
                        game_mode = 100;
                        System.out.println("unpaused!");
                    }
                    this.menu_button_pressed = true;
                }else{
                    this.menu_button_pressed = false;
                }
                //Display.update();
            }
            else{
               Display.update();
            }
            ++frames;

            while(System.currentTimeMillis() >= lastTime + 1000L) {
               System.out.println(frames + " fps, " + Chunk.updates);
                fps = frames;
               Chunk.updates = 0;
               lastTime += 1000L;
               frames = 0;

                //this.level.generate(this.player.x,this.player.y,this.player.z);
               //this.width = Display.getWidth(); #TODO: Make scaling work
               //this.height = Display.getHeight();
                levelRenderer.allChanged();

                if (game_mode == 100) {
                    levelRenderer.doUpdate(player);

                }
            }

             levelRenderer.setDirty(player.x - 4, player.y - 4, player.z - 4, player.x + 4, player.y + 4, player.z + 4);
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
      }else if (this.game_mode == 101) {
          //this.player.tick();
      }
   }

   public void devkeybinds() {


       if (Keyboard.isKeyDown(38)) {} // 38 = key L
       if (Keyboard.isKeyDown(207)) {

           if (!this.dev_command_pause) {
               System.out.println("Remove layer");
           }
           this.dev_command_pause = true;
           for(int x = 0; x < level.width; ++x) {for(int y = 0; y < level.height; ++y) {level.setTile((int)x, (int) player.y-2, (int)y,0);}}

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

    public void render_paused(float a) {
        float xo = (float)Mouse.getDX();
        float yo = (float)Mouse.getDY();

        while(Keyboard.next()) {
            if (Keyboard.getEventKey() == 28 && Keyboard.getEventKeyState()) {
                this.level.save();
            }
        }

        GL11.glClear(16640);
        this.setupCamera(0);


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
        GL11.glDisable(2912);

        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        float aspectRatio = (float) width / (float) height;
        int heightTiles = 32;
        int widthTiles = (int)((float)heightTiles * aspectRatio);

        GLU.gluOrtho2D(0,10*aspectRatio,0,10);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        this.levelRenderer.renderTex(this.hotbar[this.hotbar_slot]-1);

        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GLU.gluOrtho2D(0,1,0,1);
        this.fontRenderer.renderText(widthTiles-6,heightTiles-1,"Paused",widthTiles,heightTiles);
        this.fontRenderer.renderText(0,heightTiles-1,this.version,widthTiles,heightTiles);
        this.fontRenderer.renderText(0,heightTiles-2,(int)fps+" fps",widthTiles,heightTiles);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        Display.update();
    }

   public void render(float a) {
      float xo = (float)Mouse.getDX();
      float yo = (float)Mouse.getDY();
      this.player.turn(xo, yo);
      this.pick(a);

       float aspectRatio = (float) width / (float) height;
       int heightTiles = 16;
       int widthTiles = (int)((float)heightTiles * aspectRatio);

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
      //float aspectRatio = (float) width / (float) height;
      GLU.gluOrtho2D(0,10*aspectRatio,0,10);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      this.levelRenderer.renderTex(this.hotbar[this.hotbar_slot]-1);


      heightTiles = 32;
      widthTiles = (int)((float)heightTiles * aspectRatio);
       GL11.glMatrixMode(5889);
       GL11.glLoadIdentity();
       GLU.gluOrtho2D(0,1,0,1);
       this.fontRenderer.renderText(0,heightTiles-1,this.version,widthTiles,heightTiles);
       this.fontRenderer.renderText(0,heightTiles-2,(int)fps+" fps",widthTiles,heightTiles);


      GL11.glEnable(GL11.GL_DEPTH_TEST);
      GL11.glDisable(GL11.GL_TEXTURE_2D);

      Display.update();
   }
   public void render_background(float a){

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


   }
   public void render_mainMenu(float a){

      render_background(a);

      float mouseX = (float)Mouse.getX();
      float mouseY = (float)Mouse.getY();



      while(Mouse.next()) {

         //System.out.println(mouseX + "," + mouseY);
         //System.out.println(hotbar_slot);
         //System.out.println("Current index"+this.hotbar[hotbar_slot]);
         if (Mouse.getEventButton() == 0) {
            //System.out.println(mouseX + "," + mouseY);
         }
      }
      int buttonIndex = -1;
      if (mouseX > 100 && mouseX < 929){


         if (mouseY < 478){
            if (mouseY > 384) {
               buttonIndex = 0;

            } else{
               if (mouseY < 333) {

                  if (mouseY > 240){
                     buttonIndex = 1;
                  } else{

                     if (mouseY < 190){
                        if (mouseY > 95){
                           buttonIndex = 2;
                           //System.out.println(mouseX + "," + mouseY);
                        }
                     }
                  }
               }
            }
         }
      }
      if (Mouse.isButtonDown(0)){
         if (!this.menu_button_pressed) {
            System.out.println(buttonIndex);
            if (buttonIndex == 2){
               this.running = false;
            }
            if (buttonIndex == 0){
                this.game_mode = 1;
            }
            if (buttonIndex == 1){
                this.game_mode = 10;
            }
         }
         this.menu_button_pressed = true;
      }else{
         this.menu_button_pressed = false;
      }



      //GL11.glEnable(GL11.GL_ALPHA);



      /*
      for (int x = buttonMargin; x<widthTiles-buttonMargin; x++) {
         //this.menuRenderer.renderTex(17, heightTiles, widthTiles, x, 5);
         if (x == buttonMargin){
            this.menuRenderer.renderTex(0, heightTiles, widthTiles, x, buttonPos + 1);
            this.menuRenderer.renderTex(32, heightTiles, widthTiles, x, buttonPos);
         }else if(x==widthTiles-buttonMargin-1){
            this.menuRenderer.renderTex(2, heightTiles, widthTiles, x, buttonPos + 1);
            this.menuRenderer.renderTex(34, heightTiles, widthTiles, x, buttonPos);
         }else {
            this.menuRenderer.renderTex(1, heightTiles, widthTiles, x, buttonPos + 1);
            this.menuRenderer.renderTex(33, heightTiles, widthTiles, x, buttonPos);
         }
      }

       */
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      float aspectRatio = (float) width / (float) height;
      //GLU.gluOrtho2D(0,1*aspectRatio,0,1);
      int heightTiles = 16;
      int widthTiles = (int)((float)heightTiles * aspectRatio);
      //System.out.println(heightTiles + "," + widthTiles);
      GLU.gluOrtho2D(0,1,0,1);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      GL11.glDisable(GL11.GL_DEPTH_TEST);


      int buttonMargin = 2;
      int buttonPos = 8;
      if (buttonIndex == 0){
          this.menuRenderer.textureOffset = 3;
      } else {
          this.menuRenderer.textureOffset = 0;
      }
      this.menuRenderer.renderMenuBackground(buttonPos,buttonMargin,widthTiles,heightTiles);
      this.fontRenderer.renderText(buttonMargin+0.5f,buttonPos+0.5f,"SINGLEPLAYER",widthTiles,heightTiles);
       if (buttonIndex == 1){
           this.menuRenderer.textureOffset = 3;
       } else {
           this.menuRenderer.textureOffset = 0;
       }
      buttonPos = 5;
      this.menuRenderer.renderMenuBackground(buttonPos,buttonMargin,widthTiles,heightTiles);
      this.fontRenderer.renderText(buttonMargin+0.5f,buttonPos+0.5f,"SETTINGS",widthTiles,heightTiles);
       if (buttonIndex == 2){
           this.menuRenderer.textureOffset = 3;
       } else {
           this.menuRenderer.textureOffset = 0;
       }
      buttonPos = 2;
      this.menuRenderer.renderMenuBackground(buttonPos,buttonMargin,widthTiles,heightTiles);
      this.fontRenderer.renderText(buttonMargin+0.5f,buttonPos+0.5f,"QUIT",widthTiles,heightTiles);

      //this.menu_levelRenderer.renderTex(this.hotbar[this.hotbar_slot]-1);
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      GL11.glDisable(GL11.GL_TEXTURE_2D);

      Display.update();
   }

   public void render_save_select(float a){

      render_background(a);

      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      float aspectRatio = (float) width / (float) height;
      //GLU.gluOrtho2D(0,1*aspectRatio,0,1);
      int heightTiles = 16;
      int widthTiles = (int)((float)heightTiles * aspectRatio);
      //System.out.println(heightTiles + "," + widthTiles);
      GLU.gluOrtho2D(0,1,0,1);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      GL11.glDisable(GL11.GL_DEPTH_TEST);


      int buttonMargin = 1;
      int buttonPos = 2;
      //this.menuRenderer.renderMenuBackground(buttonPos,buttonMargin,widthTiles,heightTiles);
       this.menuRenderer.renderMenuBackground(3,buttonMargin,heightTiles-(2+buttonPos),widthTiles,heightTiles);
       buttonPos = heightTiles-3;
      this.fontRenderer.renderText(1.5f,buttonPos+0.5f,"SELECT SAVE GAME",widthTiles,heightTiles);


    /*
      buttonPos = 5;
      this.menuRenderer.renderMenuBackground(buttonPos,buttonMargin,widthTiles,heightTiles);
      this.fontRenderer.renderText(buttonMargin+0.5f,buttonPos+0.5f,"SETTINGS",widthTiles,heightTiles);
*/
      buttonPos = 4;
      buttonMargin = 2;
      this.menuRenderer.renderMenuBackground(buttonPos,buttonMargin,widthTiles,heightTiles);
      this.fontRenderer.renderText(buttonMargin+0.5f,buttonPos+0.5f,"New game",widthTiles,heightTiles);






       float tileSizeX = (float) 1 / (float)widthTiles;
       float tileSizeY = (float) 1 / (float)heightTiles;



       float mouseX = (float)Mouse.getX();
       float mouseY = (float)Mouse.getY();

       float x1 = tileSizeX*(buttonMargin+1);
       float y1 = tileSizeY*(buttonPos+2);
       float x2 = x1-tileSizeX;
       float y2 = y1-(tileSizeY*2);

       x1 = x1*width;
       y1 = y1*height;
       x2 = x2*width;
       y2 = y2*height;




       buttonPos = heightTiles-5;

       int dwheel  =Mouse.getDWheel();

       if (dwheel> 0){
           this.select_level_scroll += 1;
       } else if (dwheel < 0){
           this.select_level_scroll -= 1;
       }


      for (int i=0; i<levels.length;i++){

          x1 = tileSizeX*(buttonMargin+1);
          y1 = tileSizeY*(buttonPos+2-((i)*2));
          y1 = y1 - (this.select_level_scroll/20f);
          x2 = x1-tileSizeX;
          y2 = y1-(tileSizeY*2);

          x1 = x1*width;
          y1 = y1*height;
          x2 = x2*width;
          y2 = y2*height;



          //System.out.println(y1 + "," + mouseY);
          System.out.println(y1);

          if (mouseY < y1 && mouseY > y2){
              System.out.println(i);
              this.menuRenderer.textureOffset = 3;
              if (Mouse.isButtonDown(0)){
                  if (!this.menu_button_pressed) {
                      if (y1>278) {

                          level.load("levels//" + levels[i]);
                          game_mode = 100;
                          levelRenderer.allChanged();
                          Mouse.setGrabbed(true);
                          return;
                      }
                  }
                  this.menu_button_pressed = true;
              }else{
                  this.menu_button_pressed = false;
              }
          }else {
              this.menuRenderer.textureOffset = 0;
          }


        this.menuRenderer.offset = (float) this.select_level_scroll / -20f;
        this.fontRenderer.offset = (float) this.select_level_scroll / -20f;
        if (y1>278) {
            this.menuRenderer.renderMenuBackground(buttonPos - ((i) * 2), buttonMargin, widthTiles, heightTiles);
            this.fontRenderer.renderText(buttonMargin + 0.5f, buttonPos + 0.5f - ((i) * 2), levels[i], widthTiles, heightTiles);
        }

        this.menuRenderer.offset = 0;
        this.fontRenderer.offset = 0;
          this.menuRenderer.textureOffset = 0;
      }
       buttonMargin = 1;
      //this.menuRenderer.textureOffset = 6;
       this.menuRenderer.renderMenuBackgroundBase(3,buttonMargin,4, widthTiles,heightTiles);
       this.menuRenderer.textureOffset = 0;

       buttonPos = 4;
       buttonMargin = 2;



        x1 = tileSizeX*(buttonMargin+1);
        y1 = tileSizeY*(buttonPos+2);
        x2 = x1-tileSizeX;
        y2 = y1-(tileSizeY*2);

       x1 = x1*width;
       y1 = y1*height;
       x2 = x2*width;
       y2 = y2*height;
       if (mouseY < y1 && mouseY > y2){this.menuRenderer.textureOffset = 3;if (Mouse.isButtonDown(0)){this.game_mode = 2;this.new_game_name = "new game";return;}} else{this.menuRenderer.textureOffset = 0;}

       this.menuRenderer.renderMenuBackground(buttonPos,buttonMargin,widthTiles,heightTiles);
       this.fontRenderer.renderText(buttonMargin+0.5f,buttonPos+0.5f,"New game",widthTiles,heightTiles);
       this.menuRenderer.textureOffset = 0;
      //this.menu_levelRenderer.renderTex(this.hotbar[this.hotbar_slot]-1);
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      GL11.glDisable(GL11.GL_TEXTURE_2D);

      Display.update();
   }

    public void render_save_new(float a){

        render_background(a);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        float aspectRatio = (float) width / (float) height;
        int heightTiles = 16;
        int widthTiles = (int)((float)heightTiles * aspectRatio);
        GLU.gluOrtho2D(0,1,0,1);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        int buttonMargin = 1;
        int buttonPos = 2;
        this.menuRenderer.renderMenuBackground(3,buttonMargin,heightTiles-(2+buttonPos),widthTiles,heightTiles);
        buttonPos = heightTiles-3;
        this.fontRenderer.renderText(1.5f,buttonPos+0.5f,"NEW GAME",widthTiles,heightTiles);

        buttonPos = 4;
        buttonMargin = 2;
        //this.menuRenderer.renderMenuBackground(buttonPos,buttonMargin,widthTiles,heightTiles);
        //this.fontRenderer.renderText(buttonMargin+0.5f,buttonPos+0.5f,"New game",widthTiles,heightTiles);

        //this.menuRenderer.renderMenuBackground(buttonPos,buttonMargin,widthTiles,heightTiles);
        //this.fontRenderer.renderText(buttonMargin+0.5f,buttonPos+0.5f,"New game",widthTiles,heightTiles);


        float tileSizeX = (float) 1 / (float)widthTiles;
        float tileSizeY = (float) 1 / (float)heightTiles;


        String new_game_cursor = "";
        if (editing_game_name){
            new_game_cursor = "_";
        }
        String[] options = {"new_game_title", "game_mode_select", "world_make"};
        String[] option_text = {this.new_game_name+new_game_cursor,"Game Mode: ", "Create World"};
        int[] options_offsets = {6, 0, 0};

        buttonPos = heightTiles-5;
        float mouseX = (float)Mouse.getX();
        float mouseY = (float)Mouse.getY();
        if (Mouse.isButtonDown(0)){if (!this.menu_button_pressed) {editing_game_name = false;}}
        for (int i=0; i<options.length;i++){

            float x1 = tileSizeX*(buttonMargin+1);
            float y1 = tileSizeY*(buttonPos+2-(i*2));
            float x2 = x1-tileSizeX;
            float y2 = y1-(tileSizeY*2);

            x1 = x1*width;
            y1 = y1*height;
            x2 = x2*width;
            y2 = y2*height;

            //System.out.println(y1 + "," + mouseY);


            if (mouseY < y1 && mouseY > y2){
                System.out.println(i);
                this.menuRenderer.textureOffset = 3;
                if (Mouse.isButtonDown(0)){
                    if (!this.menu_button_pressed) {
                        if (options[i] == "new_game_title"){
                            editing_game_name = true;

                        } else{
                            editing_game_name = false;
                        }
                        if (options[i] == "world_make"){
                            editing_game_name = false;
                            //this.level.load("levels//"+this.new_game_name+".dat");
                            this.level.pathname = "levels//"+this.new_game_name+".dat";
                            this.level.generate(this.player.x,this.player.y,this.player.z);
                            this.level.save();
                            this.game_mode = 100;
                            return;

                        }
                    }
                    this.menu_button_pressed = true;
                }else{
                    this.menu_button_pressed = false;
                }
            }else {
                this.menuRenderer.textureOffset = 0;
            }

            if (options[i] == "new_game_title"){
                this.menuRenderer.textureOffset = 0;
            }



            this.menuRenderer.textureOffset += options_offsets[i];

            this.menuRenderer.renderMenuBackground(buttonPos-(i*2),buttonMargin,widthTiles,heightTiles);
            this.fontRenderer.renderText(buttonMargin+0.5f,buttonPos+0.5f-(i*2),option_text[i],widthTiles,heightTiles);
            this.menuRenderer.textureOffset = 0;
        }


        if (editing_game_name){
        char[] PressedKeys = textInput.getKeys();
        for (int i=0; i< PressedKeys.length;i++){
            System.out.println(PressedKeys[i]);
            this.new_game_name = this.new_game_name + PressedKeys[i];
        }
        if (textInput.getBackspace()){
            this.new_game_name = textInput.removeLastCharacter(this.new_game_name);
        }}

        //this.menu_levelRenderer.renderTex(this.hotbar[this.hotbar_slot]-1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        Display.update();
    }

    public void render_settings(float a){

        render_background(a);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        float aspectRatio = (float) width / (float) height;
        int heightTiles = 16;
        int widthTiles = (int)((float)heightTiles * aspectRatio);
        GLU.gluOrtho2D(0,1,0,1);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        int buttonMargin = 1;
        int buttonPos = 2;
        this.menuRenderer.renderMenuBackground(3,buttonMargin,heightTiles-(2+buttonPos),widthTiles,heightTiles);
        buttonPos = heightTiles-3;
        this.fontRenderer.renderText(1.5f,buttonPos+0.5f,"SETTINGS",widthTiles,heightTiles);

        float tileSizeX = (float) 1 / (float)widthTiles;
        float tileSizeY = (float) 1 / (float)heightTiles;



        String[] options = {"settings_video", "settings_sound", "setings_controls", "settings_back"};
        String[] option_text = {"Video Settings","Sound Settings", "Controls", "back"};
        int[] options_offsets = {0, 0, 0, 0};

        buttonPos = heightTiles-5;
        float mouseX = (float)Mouse.getX();
        float mouseY = (float)Mouse.getY();
        //if (Mouse.isButtonDown(0)){if (!this.menu_button_pressed) {editing_game_name = false;}}
        buttonMargin = 2;
        for (int i=0; i<options.length;i++){

            float x1 = tileSizeX*(buttonMargin+1);
            float y1 = tileSizeY*(buttonPos+2-(i*2));
            float x2 = x1-tileSizeX;
            float y2 = y1-(tileSizeY*2);

            x1 = x1*width;
            y1 = y1*height;
            x2 = x2*width;
            y2 = y2*height;

            //System.out.println(y1 + "," + mouseY);


            if (mouseY < y1 && mouseY > y2){
                System.out.println(i);
                this.menuRenderer.textureOffset = 3;
                if (Mouse.isButtonDown(0)){
                    if (!this.menu_button_pressed) {
                        if (options[i] == "settings_video"){
                            this.game_mode = 11;
                            return;

                        }
                    }
                    this.menu_button_pressed = true;
                }else{
                    this.menu_button_pressed = false;
                }
            }else {
                this.menuRenderer.textureOffset = 0;
            }

            if (options[i] == "new_game_title"){
                this.menuRenderer.textureOffset = 0;
            }



            this.menuRenderer.textureOffset += options_offsets[i];

            this.menuRenderer.renderMenuBackground(buttonPos-(i*2),buttonMargin,widthTiles,heightTiles);
            this.fontRenderer.renderText(buttonMargin+0.5f,buttonPos+0.5f-(i*2),option_text[i],widthTiles,heightTiles);
            this.menuRenderer.textureOffset = 0;
        }


        if (editing_game_name){
            char[] PressedKeys = textInput.getKeys();
            for (int i=0; i< PressedKeys.length;i++){
                System.out.println(PressedKeys[i]);
                this.new_game_name = this.new_game_name + PressedKeys[i];
            }
            if (textInput.getBackspace()){
                this.new_game_name = textInput.removeLastCharacter(this.new_game_name);
            }}

        //this.menu_levelRenderer.renderTex(this.hotbar[this.hotbar_slot]-1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        Display.update();
    }

    public void render_settings_video(float a){

        render_background(a);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        float aspectRatio = (float) width / (float) height;
        int heightTiles = 16;
        int widthTiles = (int)((float)heightTiles * aspectRatio);
        GLU.gluOrtho2D(0,1,0,1);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        int buttonMargin = 1;
        int buttonPos = 2;
        this.menuRenderer.renderMenuBackground(3,buttonMargin,heightTiles-(2+buttonPos),widthTiles,heightTiles);
        buttonPos = heightTiles-3;
        this.fontRenderer.renderText(1.5f,buttonPos+0.5f,"SETTINGS",widthTiles,heightTiles);

        float tileSizeX = (float) 1 / (float)widthTiles;
        float tileSizeY = (float) 1 / (float)heightTiles;



        String[] options = {"settings_vsync"};

        String[] option_text = {"VSYNC: "+ this.vsync};
        int[] options_offsets = {0, 0, 0};

        buttonPos = heightTiles-5;
        float mouseX = (float)Mouse.getX();
        float mouseY = (float)Mouse.getY();
        //if (Mouse.isButtonDown(0)){if (!this.menu_button_pressed) {editing_game_name = false;}}
        buttonMargin = 2;
        for (int i=0; i<options.length;i++){

            float x1 = tileSizeX*(buttonMargin+1);
            float y1 = tileSizeY*(buttonPos+2-(i*2));
            float x2 = x1-tileSizeX;
            float y2 = y1-(tileSizeY*2);

            x1 = x1*width;
            y1 = y1*height;
            x2 = x2*width;
            y2 = y2*height;

            //System.out.println(y1 + "," + mouseY);


            if (mouseY < y1 && mouseY > y2){
                System.out.println(i);
                this.menuRenderer.textureOffset = 3;
                if (Mouse.isButtonDown(0)){
                    if (!this.menu_button_pressed) {
                        if (options[i] == "settings_vsync"){
                            this.vsync = !this.vsync;
                            Display.setVSyncEnabled(this.vsync);

                        }
                    }
                    this.menu_button_pressed = true;
                }else{
                    this.menu_button_pressed = false;
                }
            }else {
                this.menuRenderer.textureOffset = 0;
            }

            if (options[i] == "new_game_title"){
                this.menuRenderer.textureOffset = 0;
            }



            this.menuRenderer.textureOffset += options_offsets[i];

            this.menuRenderer.renderMenuBackground(buttonPos-(i*2),buttonMargin,widthTiles,heightTiles);
            this.fontRenderer.renderText(buttonMargin+0.5f,buttonPos+0.5f-(i*2),option_text[i],widthTiles,heightTiles);
            this.menuRenderer.textureOffset = 0;
        }


        if (editing_game_name){
            char[] PressedKeys = textInput.getKeys();
            for (int i=0; i< PressedKeys.length;i++){
                System.out.println(PressedKeys[i]);
                this.new_game_name = this.new_game_name + PressedKeys[i];
            }
            if (textInput.getBackspace()){
                this.new_game_name = textInput.removeLastCharacter(this.new_game_name);
            }}

        //this.menu_levelRenderer.renderTex(this.hotbar[this.hotbar_slot]-1);
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
