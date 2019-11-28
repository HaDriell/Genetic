package fr.hadriel.empires;


import fr.hadriel.empires.ai.*;
import fr.hadriel.empires.environment.Location;
import fr.hadriel.empires.environment.Terrain;

import java.util.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

public class World {
    public static final int TRIBE_STARTING_GATHERER_COUNT   = 10;
    public static final float TRIBE_MINIMUM_DISTANCE_FACTOR = 0.1f;

    private final Map<Location, List<Unit>> warUnits;

    public final List<Tribe> tribes;
    public final Terrain terrain;

    public final int width;
    public final int height;
    public final int scale;

    public World(int seed, int scale, int width, int height) {
        this.tribes = new ArrayList<>();
        this.warUnits = new HashMap<>();
        this.scale = scale;
        this.width = width;
        this.height = height;
        this.terrain = new Terrain(seed, width / scale, height / scale, 60f, 8, 0.5f, 1.7f);
    }

    public void spawnTribe(Characteristics characteristics, boolean randomColor) {
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
                    if (Math.sqrt(dx * dx + dy * dy) <= terrain.width * TRIBE_MINIMUM_DISTANCE_FACTOR)
                        continue spawnLocationFinding;
                }
            }
            //LocationFinding finished
            break;
        }
        Location location = terrain.at(x, y);
        Tribe tribe = new Tribe(this, characteristics, randomColor);

        tribe.villages.add(new Village(tribe, location));
        for (int i = 0; i < TRIBE_STARTING_GATHERER_COUNT; i++) {
            tribe.units.add(new Gatherer(tribe, location, false)); // start with 10 peons and no colons
        }

        tribes.add(tribe);
    }

    public void addUnitAtLocation(Unit unit, Location location) {
        List<Unit> locals = warUnits.computeIfAbsent(location, key -> new ArrayList<>());
        locals.add(unit);
    }

    public void moveUnit(Unit unit, Location location, Location target) {
        removeUnitAtLocation(unit, location);
        addUnitAtLocation(unit, target);
    }

    public void removeUnitAtLocation(Unit unit, Location location) {
        List<Unit> locals = warUnits.get(location);
        if (locals != null) { // should never skip but we're never too sure
            locals.remove(unit);

            //Clean-up Memory when nom ore locals.
            if (locals.isEmpty())
                warUnits.remove(location);
        }
    }

    public List<Unit> getWarUnitsAtLocation(Location location) {
        List<Unit> locals = warUnits.get(location);
        return locals == null ? Collections.emptyList() : locals;
    }

    public void update(float deltaTime) {
        terrain.update(deltaTime);

        for (Tribe tribe : tribes) {
            tribe.update(deltaTime);
        }

        for (Tribe tribe : tribes) {
            tribe.removeDeads();
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