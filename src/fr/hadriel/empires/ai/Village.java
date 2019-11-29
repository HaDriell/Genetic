package fr.hadriel.empires.ai;

import fr.hadriel.empires.environment.Location;

import java.util.Objects;

public class Village {

    public static final int MIN_VILLAGE_DISTANCE = 10;

    public final Location location;
    public final Tribe tribe;

    private float warriorTraining;
    private float founderTraining;
    private float food;

    public Village(Tribe tribe, Location location) {
        this.location = Objects.requireNonNull(location);
        this.tribe = Objects.requireNonNull(tribe);
        this.food = 0.0f;
        this.founderTraining = 0.0f;
        this.warriorTraining = 0.0f;
    }

    public boolean isDestroyed() {
        //TODO : implement Village Health System
        return false;
    }

    private void gatherFood(float deltaTime) {
        //Gather food inplace for the Village
        food += location.gatherFood(tribe.characteristics.gatheringCapacity * deltaTime);
    }

    public void giveFood(float amount) {
        food += amount;
    }

    public void update(float deltaTime) {

        //Gather food inplace for the Village
        gatherFood(deltaTime);

        //Process Births as long as there is food
        while (canSpawn()) {
            //Advance Trainings
            warriorTraining += deltaTime;
            founderTraining += deltaTime;

            if (canSpawnWarrior()) {
                tribe.units.add(new Warrior(tribe, location));
            } else if (canSpawnFounder()) {
                tribe.units.add(new Gatherer(tribe, location, true));
            } else {
                tribe.units.add(new Gatherer(tribe, location, false));
            }
        }
    }

    private boolean canSpawn() {
        if (food > tribe.characteristics.spawningFoodCost) {
            food -= tribe.characteristics.spawningFoodCost;
            return true;
        }
        return false;
    }

    private boolean canSpawnWarrior() {
        if (warriorTraining > tribe.characteristics.warriorSpawningFrequency) {
            warriorTraining -= tribe.characteristics.warriorSpawningFrequency;
            return true;
        }
        return false;
    }

    private boolean canSpawnFounder() {
        if (founderTraining > tribe.characteristics.founderSpawningFrequency) {
            founderTraining -= tribe.characteristics.founderSpawningFrequency;
            return true;
        }
        return false;
    }

}