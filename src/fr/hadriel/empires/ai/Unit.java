package fr.hadriel.empires.ai;

import fr.hadriel.Util;
import fr.hadriel.empires.World;
import fr.hadriel.empires.environment.Location;

import java.util.Objects;

public abstract class Unit {

    private static final float MOVEMENT_COOLDOWN_END = 1.0f;

    public final World world;
    public final Tribe tribe;

    private Location location;
    private float health;
    private float movementCooldown;

    protected Unit(Tribe tribe, Location location) {
        this.tribe = Objects.requireNonNull(tribe);
        this.location = Objects.requireNonNull(location);
        this.world = tribe.world;
        this.health = tribe.characteristics.maxHealth;
        world.addUnitAtLocation(this, location);
    }

    public void receiveDamage(float amount) {
        health -= amount;
    }

    public Location getLocation() {
        return location;
    }

    public boolean canMove() {
        return movementCooldown > MOVEMENT_COOLDOWN_END;
    }

    public boolean moveToward(Location destination) {
        if (destination != location && canMove()) {
            movementCooldown -= MOVEMENT_COOLDOWN_END; // spend cooldown

            int dx = Util.clamp(destination.x - location.x, -1, 1);
            int dy = Util.clamp(destination.y - location.y, -1, 1);
            Location target = world.terrain.at(location.x + dx, location.y + dy);

            //Moved successfully
            if (target != null) {
                Location source = location;
                location = target;
                world.moveUnit(this, source, target);
                return true;
            }
        }
        return false;
    }

    public void attack() {
        for (Unit unit : world.getWarUnitsAtLocation(getLocation())) {
            //Ignore allies (and self)
            if (unit.tribe == this.tribe) continue;
            unit.receiveDamage(tribe.characteristics.damage);
        }
    }

    public void suicide() {
        health = 0;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void update(float deltaTime) {
        movementCooldown += tribe.characteristics.movementSpeed * deltaTime;
    }
}