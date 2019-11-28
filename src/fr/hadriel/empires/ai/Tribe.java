package fr.hadriel.empires.ai;

import fr.hadriel.Util;
import fr.hadriel.empires.World;
import fr.hadriel.empires.environment.Location;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tribe {

    private static int randomColorIndex = 0;
    private static final Color[] CYCLE = {
        Color.red,
        Color.blue,
        Color.green,
        Color.pink,
        Color.orange,
        Color.darkGray,
        Color.white,
        Color.yellow,
        Color.lightGray,
        Color.magenta,
        Color.black,
        Color.cyan
    };

    private static Color nextColorInCycle() {
        return CYCLE[randomColorIndex++ % CYCLE.length];
    }

    public final World world;

    public final List<Village> villages;
    public final List<Unit> units;
    public final Characteristics characteristics;
    public final Color color;

    public Tribe(World world, Characteristics characteristics, boolean randomColor) {
        this.world = world;
        this.characteristics = characteristics;
        this.color = randomColor ? nextColorInCycle() : calculateColor(characteristics);
        this.villages = new ArrayList<>();
        this.units = new ArrayList<>();
    }

    public void update(float deltaTime) {
        //Execute Births & Gathering from Villages first
        for (Village village : villages) {
            village.update(deltaTime);
        }

        //Execute peons
        for (Unit unit : units) {
            unit.update(deltaTime);
        }
    }

    public void removeDeads() {
        List<Unit> deads = new ArrayList<>();
        for (Unit unit : units) {
            if (!unit.isAlive()) {
                deads.add(unit);
                world.removeUnitAtLocation(unit, unit.getLocation());
            }
        }
        units.removeAll(deads);
    }

    public void render(Graphics2D g) {
        g.setColor(color);
        for (Village village : villages) {
            g.drawRect(village.location.x, village.location.y, 1, 1);
        }

        for (Unit unit : units) {
            Location location = unit.getLocation();
            g.fillRect(location.x, location.y, 1, 1);
        }
    }

    private static Color calculateColor(Characteristics characteristics) {
        float r = characteristics.forceNormalized;
        float g = characteristics.agilityNormalized;
        float b = characteristics.visionNormalized;

        //Desaturate Color if low constitution
        float avg = (r + g + b) / 3.0f;
        float saturation = 0.2f + (0.8f * characteristics.constitutionNormalized);

        r = Util.lerp(saturation, avg, r);
        g = Util.lerp(saturation, avg, g);
        b = Util.lerp(saturation, avg, b);

        return new Color(r, g, b);
    }
}