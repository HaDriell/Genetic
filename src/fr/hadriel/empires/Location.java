package fr.hadriel.empires;

import fr.hadriel.Perlin;

import java.awt.*;
import java.util.Objects;

public class Location {

    ////////////////////////////////////////////////////////////////////////////////
    // Color Settings //////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    private static final Color LOW_GRASS_COLOR = new Color(24, 48, 26);
    private static final Color HIGH_GRASS_COLOR = new Color(103, 160, 85);

    private static final Color DESERT_COLOR = new Color(220, 220, 150);

    private static final Color SNOW_COLOR = new Color(255, 255, 255);

    private static final Color HIGH_WATER_COLOR = new Color(128, 128, 225);
    private static final Color MEDIUM_WATER_COLOR = new Color(64, 64, 164);
    private static final Color LOW_WATER_COLOR = new Color(32,32, 64);

    ////////////////////////////////////////////////////////////////////////////////
    // World Settings //////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    public static final float MIN_HEIGHT = 0;
    public static final float MAX_HEIGHT = 100;

    public static final float OCEAN_HEIGHT = 45;
    public static final float SNOW_HEIGHT = 85;

    public static final float FOOD_GROWTH_SPEED = 10;
    public static final float FOOD_MAX_CAPACITY = 100;

    public static final float ARIDITY_MIN_ATTENUATION = 1f;
    public static final float ARIDITY_MAX_ATTENUATION = 100f;
    public static final float ARIDITY_DESERT_THRESHOLD = 32;


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

    public boolean isDesert() { return aridity > 0.95f; }

    public Color getColor() {
        if (isSnow()) return SNOW_COLOR;
        if (isDesert()) return DESERT_COLOR;
        if (height * 1.7 < OCEAN_HEIGHT) return LOW_WATER_COLOR;
        if (height * 1.2 < OCEAN_HEIGHT) return MEDIUM_WATER_COLOR;
        if (height * 1.0 < OCEAN_HEIGHT) return HIGH_WATER_COLOR;


        //map t between OCEAN & SNOW
        float t = Perlin.inverseLerp(height, OCEAN_HEIGHT, SNOW_HEIGHT);
        float altitude = Perlin.lerp(t, 0.0f, 1.0f);

        //Rock Color (grayscale)
        int rock = (int) Perlin.lerp(altitude, 128, 255);

        //Food Color (green shade)
        int foodR = (int) Perlin.lerp(altitude, LOW_GRASS_COLOR.getRed(), HIGH_GRASS_COLOR.getRed());
        int foodG = (int) Perlin.lerp(altitude, LOW_GRASS_COLOR.getGreen(), HIGH_GRASS_COLOR.getGreen());
        int foodB = (int) Perlin.lerp(altitude, LOW_GRASS_COLOR.getBlue(), HIGH_GRASS_COLOR.getBlue());

        //Mix Colors
        float tt = Perlin.inverseLerp(food, 0.0f, FOOD_MAX_CAPACITY);
        int r = (int) Perlin.lerp(tt, rock, foodR);
        int g = (int) Perlin.lerp(tt, rock, foodG);
        int b = (int) Perlin.lerp(tt, rock, foodB);

        return new Color(r, g, b);
    }

    public void update(float deltaTime) {
        //Nothing to do on Water & Snow
        if(isSnow()) return;
        if (isWater()) return;
        if (isDesert()) return;

        float growthFactor = 1f / Perlin.lerp(aridity, ARIDITY_MIN_ATTENUATION, ARIDITY_MAX_ATTENUATION);
        food += FOOD_GROWTH_SPEED * growthFactor * deltaTime;

        //Cap if too much food is present
        if (food >= FOOD_MAX_CAPACITY)
            food = FOOD_MAX_CAPACITY;
    }
}