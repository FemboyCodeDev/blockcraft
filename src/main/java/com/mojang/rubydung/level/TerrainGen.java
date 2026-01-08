package src.main.java.com.mojang.rubydung.level;

public class TerrainGen {


    public static int blockAtPos(int x, int y, int z){
        if (y < calculateSurfaceHeight(x,z)-1){
            return 2;
        }else if (y < calculateSurfaceHeight(x,z)){
            return 1;
        }
        return 0;
    }
    public static float calculateSurfaceHeight(int x, int z){
        return 32+(int)(4*Math.sin((double)x*0.1));
    }
}
