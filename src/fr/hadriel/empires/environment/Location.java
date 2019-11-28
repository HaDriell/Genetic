package fr.hadriel.empires.environment;

import fr.hadriel.Util;

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

    private static final Color WATER_COLOR = new Color(128, 128, 225);
    private static final Color DEEP_WATER_COLOR = new Color(64, 64, 164);
    private static final Color ABYSS_WATER_COLOR = new Color(32,32, 64);

    ////////////////////////////////////////////////////////////////////////////////
    // World Settings //////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    public static final float MIN_HEIGHT = 0;
    public static final float MAX_HEIGHT = 100;

    public static final float OCEAN_HEIGHT = 40;
    public static final float OCEAN_DEEP_HEIGHT = OCEAN_HEIGHT * 0.7f;
    public static final float OCEAN_ABYSS_HEIGHT = OCEAN_DEEP_HEIGHT * 0.7f;
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

    public float getFood() {
        return food;
    }

    public float gatherFood(float capacity) {
        float amount = Math.min(capacity, food);
        food -= amount;
        return amount;
    }

    public void dropFood(float amount) {
        food += amount;
    }

    public boolean isWater() {
        return height <= OCEAN_HEIGHT;
    }

    public boolean isSnow() {
        return height >= SNOW_HEIGHT;
    }

    public boolean isDesert() { return aridity > 0.95f; }

    private Color getWaterColor() {
        //Should never happen.
        if (height > OCEAN_HEIGHT)
            return WATER_COLOR;

        if (height > OCEAN_DEEP_HEIGHT) {
            float t = Util.inverseLerp(height, OCEAN_HEIGHT, OCEAN_DEEP_HEIGHT);
            return Util.lerp(t, WATER_COLOR, DEEP_WATER_COLOR);
        }

        if (height > OCEAN_ABYSS_HEIGHT) {
            float t = Util.inverseLerp(height, OCEAN_DEEP_HEIGHT, OCEAN_ABYSS_HEIGHT);
            return Util.lerp(t, DEEP_WATER_COLOR, ABYSS_WATER_COLOR);
        }

        return ABYSS_WATER_COLOR;
    }

    private Color getDesertColor() {
        float t = Util.inverseLerp(height, OCEAN_HEIGHT, SNOW_HEIGHT);
        float altitude = Util.lerp(t, 0.0f, 1.0f);
        return Util.lerp(altitude, DESERT_COLOR, SNOW_COLOR);
    }

    public Color getColor() {
        if (isSnow()) return SNOW_COLOR;
        if (isDesert()) return getDesertColor();
        if (isWater()) return getWaterColor();

        //map t between OCEAN & SNOW
        float t = Util.inverseLerp(height, OCEAN_HEIGHT, SNOW_HEIGHT);
        float altitude = Util.lerp(t, 0.0f, 1.0f);

        //Rock Color (grayscale)
        int rock = (int) Util.lerp(altitude, 32, 200);

        //Food Color (green shade)
        int foodR = (int) Util.lerp(altitude, LOW_GRASS_COLOR.getRed(), HIGH_GRASS_COLOR.getRed());
        int foodG = (int) Util.lerp(altitude, LOW_GRASS_COLOR.getGreen(), HIGH_GRASS_COLOR.getGreen());
        int foodB = (int) Util.lerp(altitude, LOW_GRASS_COLOR.getBlue(), HIGH_GRASS_COLOR.getBlue());

        //Mix Colors
        float tt = Util.inverseLerp(food, 0.0f, FOOD_MAX_CAPACITY);
        if (tt > 1) tt = 1;

        int r = (int) Util.lerp(tt, rock, foodR);
        int g = (int) Util.lerp(tt, rock, foodG);
        int b = (int) Util.lerp(tt, rock, foodB);

        return new Color(r, g, b);
    }

    public void update(float deltaTime) {
        //Nothing to do on Water & Snow
        if(isSnow()) return;
        if (isWater()) return;
        if (isDesert()) return;
        if (food >= FOOD_MAX_CAPACITY) return;

        float growthFactor = 1f / Util.lerp(aridity, ARIDITY_MIN_ATTENUATION, ARIDITY_MAX_ATTENUATION);
        food += FOOD_GROWTH_SPEED * growthFactor * deltaTime;

        //Cap if too much food is present
        if (food >= FOOD_MAX_CAPACITY)
            food = FOOD_MAX_CAPACITY;
    }

    @Override
    public String toString() {
        return String.format("Location %d, %d", x, y);
    }
}