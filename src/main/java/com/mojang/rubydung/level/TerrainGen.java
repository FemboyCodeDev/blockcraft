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
        return 0;
    }
    public static boolean canHaveTree(int x, int y, int z){

        for (int dx = -8; dx < 8; dx ++){
            for (int dz = -8; dz < 8; dz ++) {
                if (Math.abs(calculateSurfaceHeight(dx + x, z+dz)-y) > 4) {
                    return false;
                }
            }

        }
        return true;
    }

    public static boolean shouldPutTree(int x, int y, int z){
        if (!canHaveTree(x,y,z)){return false;}
        if (perlin.getNoise(x * 10.1254, z * 10.1254) > 0.5){return true;}
        /*
        float value = perlin.getNoise(x * 1, y * 1);
        for (int dx = -8; dx < 8; dx ++){
            for (int dz = -8; dz < 8; dz ++) {
                if (perlin.getNoise((x+dx) * 1, (z+dz) * 1) > value) {
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
