package com.mojang.rubydung.level;

import java.util.Random;

public class PerlinNoise {

    // Maximum size of the octaves
    public static final int MAX_OCTAVES = 6;
    public static final double MIN_OCTAVE_SIZE = 1.0;
    public static final double MAX_OCTAVE_SIZE = 10.0;

    // Seed for the random number generator
    private int seed;
    private double[] octaveSizes;
    private double[][][] gradients;

    // Perlin noise function
    public double getNoise(double x, double y) {
        return perlin(x / octaveSizes[0], y / octaveSizes[0]);
    }

    // Initializes the Perlin noise with a random seed and octave sizes.
    public PerlinNoise(int seed) {
        this.seed = seed;
        octaveSizes = new double[MAX_OCTAVES];
        gradients = new double[MAX_OCTAVES][256][64];

        Random rand = new Random(seed);
        for (int i = 0; i < MAX_OCTAVES; i++) {
            octaveSizes[i] = Math.random() * (MAX_OCTAVE_SIZE - MIN_OCTAVE_SIZE) + MIN_OCTAVE_SIZE;

            for (int j = 0; j < 256; j++) {
                int index = (j % 16) * 3;
                System.out.println(index);
                //index = 0;

                gradients[i][j][index] = rand.nextDouble();

                // Generate custom gradients based on octaves and indices
                if (i == 0 && j >= 128) {
                    gradients[i][j][index + 1] += rand.nextBoolean() ? Math.sin(j / 16.0 * Math.PI) : -Math.sin(j / 16.0 * Math.PI);
                }
            }
        }
    }

    // Perlin noise function
    private double perlin(double x, double y) {
        int xi = (int)x;
        int yi = (int)y;

        double xf = x - xi;
        double yf = y - yi;

        double n00 = gradient(xi, yi, 0);
        double n01 = gradient((xi + 1), yi, 0);
        double n10 = gradient(xi, (yi + 1), 0);
        double n11 = gradient((xi + 1), (yi + 1), 0);

        double interpo01 = lerp(n00, n01, xf);
        double interpo10 = lerp(n10, n11, xf);

        return lerp(interpo01, interpo10, yf);
    }

    // Gradient function
    private double gradient(int x, int y, int octave) {
        int index = (x + y * 257) % 256;
        return gradients[octave][index][0];
    }

    // Linear interpolation function
    private double lerp(double a, double b, double t) {
        return a * (1 - t) + b * t;
    }


}
