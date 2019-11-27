package fr.hadriel.empires.ai;

import fr.hadriel.Util;
import fr.hadriel.empires.World;
import fr.hadriel.empires.environment.Location;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Tribe {

    public final World world;

    public final List<Village> villages;
    public final List<Peon> peons;
    public final Characteristics characteristics;
    public final Color color;

    public Tribe(World world, Characteristics characteristics) {
        this.world = world;
        this.characteristics = characteristics;
        this.color = calculateColor(characteristics);
        this.villages = new ArrayList<>();
        this.peons = new ArrayList<>();
    }

    public void update(float deltaTime) {
        //Execute Births & Gathering from Villages first
        for (Village village : villages) {
            village.update(deltaTime);
        }

        //Execute peons
        for (Peon peon : peons) {
            peon.update(deltaTime);
        }
    }

    public void removeDeads() {
        peons.removeIf(peon -> !peon.isAlive());
    }

    public void render(Graphics2D g) {
        g.setColor(color);
        for (Village village : villages) {
            g.drawRect(village.location.x, village.location.y, 1, 1);
        }

        for (Peon peon : peons) {
            Location location = peon.getLocation();
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