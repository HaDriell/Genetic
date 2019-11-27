package fr.hadriel.empires;


import fr.hadriel.empires.ai.Characteristics;
import fr.hadriel.empires.ai.Peon;
import fr.hadriel.empires.ai.Tribe;
import fr.hadriel.empires.ai.Village;
import fr.hadriel.empires.environment.Location;
import fr.hadriel.empires.environment.Terrain;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

public class World {

    public final List<Tribe> tribes;
    public final Terrain terrain;

    public final int width;
    public final int height;
    public final int scale;

    public World(int seed, int scale, int width, int height) {
        this.tribes = new ArrayList<>();
        this.scale = scale;
        this.width = width;
        this.height = height;
        this.terrain = new Terrain(seed, width / scale, height / scale, 60f, 8, 0.5f, 1.7f);
    }

    public void spawnTribe(Characteristics characteristics) {
        Random random = new Random();

        int x, y;

        spawnLocationFinding:
        while (true) {
            x = (int) (random.nextFloat() * terrain.width);
            y = (int) (random.nextFloat() * terrain.height);

            Location location = terrain.at(x, y);
            //Reject when Desert, Snow or Water
            if (location.isDesert()) continue;
            if (location.isSnow()) continue;
            if (location.isWater()) continue;

            for (Tribe tribe : tribes) {
                for (Village village : tribe.villages) {
                    int dx = location.x - village.location.x;
                    int dy = location.y - village.location.y;

                    //Must respect a minimum distance between 2 Villages
                    if (Math.sqrt(dx * dx + dy * dy) <= 75)
                        continue spawnLocationFinding;
                }
            }
            //LocationFinding finished
            break;
        }
        Location location = terrain.at(x, y);
        Tribe tribe = new Tribe(this, characteristics);

        tribe.villages.add(new Village(tribe, location));
        for (int i = 0; i < 10; i++) {
            tribe.peons.add(new Peon(tribe, location, false)); // start with 10 peons and no colons
        }

        tribes.add(tribe);
    }

    public void update(float deltaTime) {
        terrain.update(deltaTime);

        for (Tribe tribe : tribes) {
            tribe.update(deltaTime);
        }
    }

    public void render(Graphics2D g) {
        AffineTransform matrix = g.getTransform();
        g.scale(scale, scale);

        terrain.render(g);

        for (Tribe tribe : tribes) {
            tribe.render(g);
        }

        g.setTransform(matrix);
    }
}