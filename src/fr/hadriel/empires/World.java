package fr.hadriel.empires;


import java.awt.*;
import java.awt.geom.AffineTransform;

public class World {

    public final Terrain terrain;
    public final int width;
    public final int height;
    public final int scale;

    public World(int seed, int scale, int width, int height) {
        this.scale = scale;
        this.width = width;
        this.height = height;
        this.terrain = new Terrain(seed, width / scale, height / scale, 60f, 8, 0.5f, 1.7f);
    }

    public void update(float deltaTime) {
        terrain.update(deltaTime);
    }

    public void render(Graphics2D g) {
        AffineTransform matrix = g.getTransform();
        g.scale(scale, scale);
        terrain.render(g);
        g.setTransform(matrix);
    }
}