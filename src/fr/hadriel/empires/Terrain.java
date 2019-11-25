package fr.hadriel.empires;

import fr.hadriel.Perlin;

import java.awt.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public class Terrain {

    public final int seed;
    public final int width;
    public final int height;
    private final Location[] locations;

    public Terrain(int seed, int width, int height, float scale, int octaves, float persistence, float lacunarity) {
        this.seed = seed;
        this.width = width;
        this.height = height;
        this.locations = new Location[width * height];

        System.out.println("Generating Height Map");
        float[] heightmap = CreateHeightMap(scale, octaves, persistence, lacunarity);

        System.out.println("Generating Aridity Map");
        float[] ariditymap = CreateAridityMap(heightmap);

        System.out.println("Initializing Locations");
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float h = heightmap[x + y * width];
                float a = ariditymap[x + y * width];
                locations[x + y * width] = new Location(this, x, y, h, a);
            }
        }
    }

    private float[] CreateHeightMap(float scale, int octaves, float persistence, float lacunarity) {
        float[] map = new float[width * height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map[x + y * width] = Perlin.OctaveNoise(x, y, 0, scale, octaves, persistence, lacunarity);
            }
        }

        //Find min & max Height for normalization
        float minHeight = Float.MAX_VALUE;
        float maxHeight = Float.MIN_VALUE;
        for (float h : map) {
            if (minHeight > h) minHeight = h; // find min
            if (maxHeight < h) maxHeight = h; // find max
        }

        //Remap HeightMap to [Location.MIN_HEIGHT; Location.MAX_HEIGHT]
        for (int i = 0; i < width * height; i++) {
            float t = Perlin.inverseLerp(map[i], minHeight, maxHeight);
            map[i] = Perlin.lerp(t, Location.MIN_HEIGHT, Location.MAX_HEIGHT);
        }

        return map;
    }

    private float[] CreateAridityMap(float[] heightMap) {
        float[] map = new float[width * height];

        int checkDistance = (int) Math.sqrt(Location.ARIDITY_THRESHOLD) + 1;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                //Aridity is 0.0 by default
                map[x + y * width] = 0.0f;

                //Skip checking when Water OR Snow
                float locationHeight = heightMap[x + y * width];
                if (locationHeight <= Location.OCEAN_HEIGHT) continue;
                if (locationHeight > Location.SNOW_HEIGHT) continue;

                //Find min Distance to water
                float minDistance = Location.ARIDITY_THRESHOLD;
                for (int dx =  -checkDistance; dx <= +checkDistance; dx++) {
                    for (int dy =  -checkDistance; dy <= +checkDistance; dy++) {
                        //Identifiy Target
                        int tx = x + dx;
                        int ty = y + dx;

                        //Avoid ArrayOutOfBoundException
                        if (tx < 0 || tx >= width) continue;
                        if (ty < 0 || ty >= height) continue;
                        float tHeight = heightMap[tx + ty * width];

                        //Fitler away non Water tiles
                        if (tHeight > Location.OCEAN_HEIGHT) continue;

                        //Check for shorter source of water
                        float d = dx*dx + dy*dy;
                        if (d < minDistance) minDistance = d;
                    }
                }

                //Setup map
                map[x + y * width] = minDistance;
            }
        }
        return map;
    }

    public Location at(int x, int y) {
        if (x < 0 || x >= width) return null;
        if (y < 0 || y >= height) return null;
        return locations[x + y * width];
    }

    public void render(Graphics2D g) {
        for (Location location : locations) {
            g.setColor(location.getColor());
            g.fillRect(location.x, location.y, 1, 1);
        }
    }
}
