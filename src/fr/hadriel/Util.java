package fr.hadriel;

import fr.hadriel.empires.ai.Characteristics;

import java.awt.*;

//https://rosettacode.org/wiki/Perlin_noise
public class Util {
    private static final int p[] = new int[512];
    private static final int permutation[] = {151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36,
            103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219,
            203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71,
            134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46,
            245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196,
            135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38,
            147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213,
            119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110,
            79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12, 191, 179,
            162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115,
            121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215,
            61, 156, 180};
    static {
        for (int i = 0; i < 256; i++) {
            p[256 + i] = p[i] = permutation[i];
        }
    }

    public static float fade(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    public static int clamp(int value, int min, int max) {
        if (value > max) return max;
        if (value < min) return min;
        return value;
    }

    public static Color lerp(float t, Color a, Color b) {
            int red     = lerp(t, a.getRed(), b.getRed());
            int green   = lerp(t, a.getGreen(), b.getGreen());
            int blue    = lerp(t, a.getBlue(), b.getBlue());

            red     = clamp(red, 0, 255);
            green   = clamp(green, 0, 255);
            blue    = clamp(blue, 0, 255);

            return new Color(red, green, blue);
    }

    public static float lerp(float t, float a, float b) {
        return a + t * (b - a);
    }

    public static int lerp(float t, int a, int b) {
        return (int) (a + t * (b - a));
    }


    public static float inverseLerp(float v, float a, float b) {
        return (v - a) / (b - a);
    }

    public static float grad(int hash, float x, float y, float z) {
        switch (hash & 0x000F) {
            case 0x0:
                return x + y;
            case 0x1:
                return -x + y;
            case 0x2:
                return x - y;
            case 0x3:
                return -x - y;
            case 0x4:
                return x + z;
            case 0x5:
                return -x + z;
            case 0x6:
                return x - z;
            case 0x7:
                return -x - z;
            case 0x8:
                return y + z;
            case 0x9:
                return -y + z;
            case 0xA:
                return y - z;
            case 0xB:
                return -y - z;
            case 0xC:
                return y + x;
            case 0xD:
                return -y + z;
            case 0xE:
                return y - x;
            case 0xF:
                return -y - z;
        }
        return 0; // never happens
    }

    public static float OctaveNoise(float x, float y, float z, float scale, int octaves, float persistence, float lacunarity) {
        float height = 0;
        float frequency = 1;
        float amplitude = 1;

        for (int octave = 0; octave < octaves; octave++) {
            float sampleX = x / scale * frequency;
            float sampleY = y / scale * frequency;
            float sampleZ = z / scale * frequency;

            height += (Noise(sampleX, sampleY, sampleZ) * 0.5f + 0.5f) * amplitude;

            amplitude *= persistence;
            frequency *= lacunarity;
        }

        return height;
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @return Perlin Noise in [0;1]
     */
    public static float Noise(float x, float y, float z) {
        int xi = (int) x & 255;
        int yi = (int) y & 255;
        int zi = (int) z & 255;

        float xf = x - (int) x;
        float yf = y - (int) y;
        float zf = z - (int) z;

        float u = fade(xf);
        float v = fade(yf);
        float w = fade(zf);

        int aaa, aba, aab, abb, baa, bba, bab, bbb;
        aaa = p[p[p[    xi ]+    yi ]+    zi ];
        aba = p[p[p[    xi ]+1 + yi ]+    zi ];
        aab = p[p[p[    xi ]+    yi ]+1 + zi ];
        abb = p[p[p[    xi ]+1 + yi ]+1 + zi ];
        baa = p[p[p[1 + xi ]+    yi ]+    zi ];
        bba = p[p[p[1 + xi ]+1 + yi ]+    zi ];
        bab = p[p[p[1 + xi ]+    yi ]+1 + zi ];
        bbb = p[p[p[1 + xi ]+1 + yi ]+1 + zi ];

        float x1, x2;

        x1 = lerp(u, grad(aaa, xf, yf, zf), grad(baa, xf-1, yf, zf));
        x2 = lerp(u, grad(aba, xf, yf-1, zf), grad(bba, xf-1, yf-1, zf));
        float  y1 = lerp(v, x1, x2);
        x1 = lerp(u, grad(aab, xf, yf, zf-1), grad(bab, xf-1, yf, zf-1));
        x2 = lerp(u, grad (abb, xf, yf-1, zf-1), grad(bbb, xf-1, yf-1, zf-1));
        float y2 = lerp (v, x1, x2);
        return lerp(w, y1, y2);
    }
}