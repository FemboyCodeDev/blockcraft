package com.meowjang.rubydung.level;

import java.util.Random;

public class PerlinNoise {
    private final int[] p = new int[512];

    public PerlinNoise(int seed) {
        Random rand = new Random(seed);

        // Fill permutation table with values 0-255
        for (int i = 0; i < 256; i++) {
            p[i] = i;
        }

        // Shuffle the table
        for (int i = 0; i < 256; i++) {
            int j = rand.nextInt(256);
            int temp = p[i];
            p[i] = p[j];
            p[j] = temp;
            // Duplicate the array to avoid overflow checks
            p[i + 256] = p[i];
            p[j + 256] = p[j];
        }
    }

    public double getNoise(double x, double y) {
        // Find unit grid cell coordinates
        int xi = (int) Math.floor(x) & 255;
        int yi = (int) Math.floor(y) & 255;

        // Relative coordinates in cell
        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);

        // Compute fade curves
        double u = fade(xf);
        double v = fade(yf);

        // Hash coordinates of the 4 corners
        int aa = p[p[xi] + yi];
        int ab = p[p[xi] + yi + 1];
        int ba = p[p[xi + 1] + yi];
        int bb = p[p[xi + 1] + yi + 1];

        // Add blended results from 4 corners
        double x1 = lerp(grad(aa, xf, yf), grad(ba, xf - 1, yf), u);
        double x2 = lerp(grad(ab, xf, yf - 1), grad(bb, xf - 1, yf - 1), u);

        return lerp(x1, x2, v);
    }

    // Perlin's smoothing function: 6t^5 - 15t^4 + 10t^3
    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    // Calculates the dot product of a random gradient vector and the distance vector
    private double grad(int hash, double x, double y) {
        // Take the first 4 bits of the hash (8 directions)
        int h = hash & 7;
        double u = h < 4 ? x : y;
        double v = h < 4 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}