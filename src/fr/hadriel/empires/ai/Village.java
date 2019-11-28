package fr.hadriel.empires.ai;

import fr.hadriel.empires.environment.Location;

import java.util.Objects;

public class Village {

    public static final int MIN_VILLAGE_DISTANCE = 10;

    public final Location location;
    public final Tribe tribe;

    private float expansionism;
    private float food;

    public Village(Tribe tribe, Location location) {
        this.location = Objects.requireNonNull(location);
        this.tribe = Objects.requireNonNull(tribe);
        this.food = 0.0f;
        this.expansionism = 0.0f;
    }

    public boolean isDestroyed() {
        return false;
    }

    public void giveFood(float amount) {
        food += amount;
    }

    public void update(float deltaTime) {
        //Gather food inplace for the Village
        food += location.gatherFood(tribe.characteristics.gatheringCapacity * deltaTime);

        //Process Births as long as there is food
        while (food >= tribe.characteristics.spawningFoodCost) {
            food -= tribe.characteristics.spawningFoodCost;

            //Check to spawn Colons or Peons
            boolean founder = false;
            expansionism += tribe.characteristics.expansionismNormalized;
            if (expansionism > 1.0f) {
                expansionism -= 1.0f;
                founder = true;
            }

            tribe.units.add(new Gatherer(tribe, location, founder));
        }
    }
}