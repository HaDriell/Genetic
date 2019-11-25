package fr.hadriel.empires;

import fr.hadriel.Perlin;

import java.awt.*;
import java.util.Objects;

public class Location {

    ////////////////////////////////////////////////////////////////////////////////
    // Color Settings //////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    private static final int DESERT_COLOR_R = 200;
    private static final int DESERT_COLOR_G = 200;
    private static final int DESERT_COLOR_B = 128;

    private static final int LOW_GRASS_COLOR_R = 30;
    private static final int LOW_GRASS_COLOR_G = 189;
    private static final int LOW_GRASS_COLOR_B = 38;

    private static final int HIGH_GRASS_COLOR_R = 87;
    private static final int HIGH_GRASS_COLOR_G = 153;
    private static final int HIGH_GRASS_COLOR_B = 91;

    private static final int SNOW_COLOR_R = 255;
    private static final int SNOW_COLOR_G = 255;
    private static final int SNOW_COLOR_B = 255;

    private static final int WATER_COLOR_R = 64;
    private static final int WATER_COLOR_G = 64;
    private static final int WATER_COLOR_B = 255;

    ////////////////////////////////////////////////////////////////////////////////
    // World Settings //////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    public static final float MIN_HEIGHT = 0;
    public static final float MAX_HEIGHT = 100;

    public static final float MIN_ARIDITY = 0;
    public static final float MAX_ARIDITY = 1;

    public static final float OCEAN_HEIGHT = 10;
    public static final float SNOW_HEIGHT = 90;

    public static final float MAX_FOOD_DENSITY = 200;
    public static final float FOOD_GROWTH_EXPONENT = 1.5f;
    public static final float ARIDITY_THRESHOLD = 500;


    ////////////////////////////////////////////////////////////////////////////////
    // Location Definition /////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    public final Terrain terrain;
    public final int x;
    public final int y;
    public final float height;
    public final float aridity;

    private float food = 0.0f;

    public Location(Terrain terrain, int x, int y, float height, float aridity) {
        this.terrain = Objects.requireNonNull(terrain);
        this.x = x;
        this.y = y;
        this.height = height;
        this.aridity = aridity;
    }

    public boolean isWater() {
        return height <= OCEAN_HEIGHT;
    }

    public boolean isSnow() {
        return height >= SNOW_HEIGHT;
    }

    public Color getColor() {
        //Under water => Always
        if (isWater()) return new Color(WATER_COLOR_R, WATER_COLOR_G, WATER_COLOR_B);
        if (isSnow()) return new Color(SNOW_COLOR_R, SNOW_COLOR_G,SNOW_COLOR_B);

        //map t between OCEAN & SNOW
        float t = Perlin.inverseLerp(height, OCEAN_HEIGHT, SNOW_HEIGHT);
        float altitude = Perlin.lerp(t, 0.0f, 1.0f);
        return new Color(altitude, altitude, altitude);
    }

    public float distanceSquared(Location location) {
        float dx = x - location.x;
        float dy = y - location.y;
        return dx * dx + dy * dy;
    }
}