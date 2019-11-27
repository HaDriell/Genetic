package fr.hadriel.empires.ai;

import fr.hadriel.empires.environment.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Village {

    public final Location location;
    public final Tribe tribe;
    public final List<Peon> spawns;

    private float expansionism;
    private float food;

    public Village(Tribe tribe, Location location) {
        this.location = Objects.requireNonNull(location);
        this.tribe = Objects.requireNonNull(tribe);
        this.food = 0.0f;
        this.expansionism = 0.0f;
        this.spawns = new ArrayList<>();
    }

    public void update(float deltaTime) {
        //Gather food inplace for the Village
        food += location.gatherFood(tribe.characteristics.gatheringCapacity);

        //Process Births as long as there is food
        while (food >= tribe.characteristics.spawningFoodCost) {
            food -= tribe.characteristics.spawningFoodCost;

            //Check to spawn Colons or Peons
            boolean spawnColon = false;
            if (expansionism > 1.0f) {
                expansionism -= 1.0f;
                spawnColon = true;
            } else {
                expansionism += tribe.characteristics.expansionismNormalized;
            }

            spawns.add(new Peon(tribe, location, spawnColon));
        }
    }
}