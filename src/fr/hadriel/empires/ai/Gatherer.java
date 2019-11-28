package fr.hadriel.empires.ai;

import fr.hadriel.empires.environment.Location;

public class Gatherer extends Unit {

    private boolean isFounder;
    private float food;
    private Village selectedVillage;

    public Gatherer(Tribe tribe, Location location, boolean isFounder) {
        super(tribe, location);
        this.isFounder = isFounder;
    }

    public void update(float deltaTime) {
        super.update(deltaTime);

        if (!isFull()) {
            gatherFood(deltaTime);
            attackEnemies();
        }

        if (canMakeVillage()) {
            makeVillage();
        }

        //Up to 3 moves per update
        for (int i = 0; i < 3; i++) {
            if (!canMove()) break;

            if (isFull()) {
                if (!moveToNearestVillage()) {
                    break;
                }
            }
            else
            {
                if (!moveToMostInterestingLocation()) {
                    break;
                }
            }
        }

        if(isFull() && canGiveFoodToSelectedVillage()) {
            giveFoodToSelectedVillage();
        }

        //Drop food on death
        if (!isAlive()) {
            getLocation().dropFood(food);
            food = 0.0f;
        }
    }

    private void attackEnemies() {
        for (Unit unit : world.getWarUnitsAtLocation(getLocation())) {
            //Ignore allies (and self)
            if (unit.tribe == this.tribe) continue;
            unit.receiveDamage(tribe.characteristics.damage);
        }
    }

    private void gatherFood(float deltaTime) {
        float desiredAmount = tribe.characteristics.gatheringCapacity - food;
        float maximumAmount = tribe.characteristics.gatheringCapacity * deltaTime;
        food += getLocation().gatherFood(Math.min(desiredAmount, maximumAmount));
    }

    private boolean canGiveFoodToSelectedVillage() {
        return selectedVillage != null && selectedVillage.location == getLocation();
    }

    private void giveFoodToSelectedVillage() {
        if (selectedVillage == null) return;

        selectedVillage.giveFood(food);
        food = 0.0f;
        selectedVillage = null;
    }

    private boolean moveToMostInterestingLocation() {
        Location target = getLocation();
        int los = tribe.characteristics.lineOfSight;
        float fcost = Float.MIN_VALUE;
        float dcost = Float.MAX_VALUE;

        for (int dx = -los; dx <= los; dx++) {
            for (int dy = -los; dy <= los; dy++) {
                int x = dx + getLocation().x;
                int y = dy + getLocation().y;

                Location location = world.terrain.at(x, y);
                if (location == null) continue;
                if (location.isWater()) continue;
                if (location.isSnow()) continue;
                if (location.isDesert()) continue;

                //Filter on food amount
                float fc = location.getFood();
                if (fc < fcost) continue;

                //Filter on distance
                float dc = (dx * dx + dy * dy);
                if (dc > dcost) continue;

                //Found a better location. Save Target & costs
                fcost = fc;
                dcost = dc;
                target = location;
            }
        }
        return moveToward(target);
    }

    private boolean moveToNearestVillage() {

        if (selectedVillage == null) {
            Village nearestVillage = null;
            int distance = Integer.MAX_VALUE;
            for (Village village : tribe.villages) {
                int dx = village.location.x - getLocation().x;
                int dy = village.location.y - getLocation().y;
                int d = dx * dx + dy * dy;

                if (distance > d) {
                    distance = d;
                    nearestVillage = village;
                }
            }
            selectedVillage = nearestVillage;
        }
        return selectedVillage != null && moveToward(selectedVillage.location);
    }

    private boolean isFull() {
        return food >= tribe.characteristics.gatheringCapacity;
    }

    private boolean canMakeVillage() {
        if (!isFounder) return false;
        if (getLocation().isWater()) return false;
        if (getLocation().isDesert()) return false;
        if (getLocation().isSnow()) return false;

        for(Village village : tribe.villages) {
            int dx = village.location.x - getLocation().x;
            int dy = village.location.y - getLocation().y;
            float d = dx * dx + dy * dy;
            if (d < Village.MIN_VILLAGE_DISTANCE * Village.MIN_VILLAGE_DISTANCE) {
                return false;
            }
        }
        return true;
    }

    private void makeVillage() {
        if (isFounder) {
            tribe.villages.add(new Village(tribe, getLocation()));
            isFounder = false;
        }
    }
}