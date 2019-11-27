package fr.hadriel.empires.ai;

import fr.hadriel.empires.environment.Location;

import java.util.Objects;

public class Peon {

    public final Tribe tribe;
    public final boolean colon;

    private Location location;

    public Peon(Tribe tribe, Location location, boolean isColon) {
        this.tribe = Objects.requireNonNull(tribe);
        this.location = Objects.requireNonNull(location);
        this.colon = isColon;
    }

    public Location getLocation() {
        return location;
    }

    public void update(float deltaTime) {
        //TODO : ai of Gatherer + Colon
    }
    public boolean isAlive(){
        //TODO : implement
        return true;
    }
}