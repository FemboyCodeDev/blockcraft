package src.main.java.com.mojang.rubydung.level;

import com.mojang.rubydung.level.PerlinNoise;

public class TerrainGen {
    public static float frequency = 0.05f;
    public static PerlinNoise perlin = new PerlinNoise(100);
    public static int blockAtPos(int x, int y, int z){
        if (y < calculateSurfaceHeight(x,z)-1){

            return 1;
        }else if (y < calculateSurfaceHeight(x,z)){
            if (shouldPutTree(x,y,z)){
                return 4;
            }
            return 1;
        }else if (y < 128){
            return 5;
        }
        if (shouldPutTree(x, (int) calculateSurfaceHeight(x,z),z)){
            if (y < calculateSurfaceHeight(x,z)+4) {
                return 7;
            }
            //return 3;
        }
        if (y < calculateSurfaceHeight(x,z)+32) {
            if (withinTree(x, y, z)) {
                return 6;
            }
        }
        return 0;
    }
    public static boolean offsetInTree(int x, int y, int z){
        //if (y < 5){return true;}
        //if (y > 6){return true;}
        if (x == 0){return true;}
        if (z == 0){return true;}
        if (y < 5){return true;}
        return false;
    }
    public static boolean withinTree(int x, int y, int z){
        for (int dx = -1; dx <= 1; dx ++){
            for (int dz = -1; dz <= 1; dz ++) {
                int height = (int) calculateSurfaceHeight(x+dx, z+dz);
                if (offsetInTree(dx,y-height,dz)){
                if (y <= height + 5 ) {
                    if (y > height + 2) {
                        //if (height >=128){
                        if (shouldPutTree(x + dx, height, z + dz)) {
                            return true;
                        }
                        //}

                    }
                }
                }
            }

        }
        return false;
    }
    public static boolean canHaveTree(int x, int y, int z){
        if (y < 128){return false;}

        for (int dx = -4; dx < 4; dx ++){
            for (int dz = -4; dz < 4; dz ++) {
                if (Math.abs(calculateSurfaceHeight(dx + x, z+dz)-y) > 4) {
                    return false;
                }
            }

        }
        return true;
    }
    public static boolean shouldPutTree(int x, int y, int z){
        return shouldPutTree(x,y,z,false);
    }
    public static boolean shouldPutTree(int x, int y, int z, boolean ignoreCan){
        if (ignoreCan == false) {
            if (!canHaveTree(x, y, z)) {
                return false;
            }
        }

        if (perlin.getNoise(x * 10.1254, z * 10.1254) > 0.5){
            for (int dx = -2; dx < 2; dx ++) {
                for (int dz = -2; dz < 2; dz++) {
                    if (perlin.getNoise((x+dx) * 10.1254, (z+dz) * 10.1254) > 0.8){
                        return false;
                    }
                }
            }
            return true;
        }



        /*
        float value = (float) perlin.getNoise(x * 10.1254, y * 10.1254);
        for (int dx = -2; dx < 2; dx ++){
            for (int dz = -2; dz < 2; dz ++) {
                if (perlin.getNoise((x+dx) * 10.1254, (z+dz) * 10.1254) > value) {
                    return false;
                }
            }

        }

         */

        return false;
    }
    public static float calculateSurfaceHeight(int x, int z){
        return (float) (128+getFractalNoise(x*frequency,z*frequency,4,0.5,2.0)*64);
        //return 32+(int)(4*Math.sin((double)x*0.1));
    }

    public static double getFractalNoise(double x, double y, int octaves, double persistence, double lacunarity) {
        double total = 0;
        double frequency = 0.1;
        double amplitude = 160.0;
        double maxValue = 0; // Used for normalizing the result to [0, 1] or [-1, 1]

        for (int i = 0; i < octaves; i++) {
            // We use our existing getNoise method
            total += perlin.getNoise(x * frequency, y * frequency) * amplitude;

            maxValue += amplitude;

            amplitude *= persistence; // Amplitude gets smaller (e.g., 1.0 -> 0.5 -> 0.25)
            frequency *= lacunarity;  // Frequency gets larger (e.g., 1.0 -> 2.0 -> 4.0)
        }

        return total / maxValue;
    }

}
