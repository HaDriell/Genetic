package fr.hadriel.empires.ai;

import fr.hadriel.Util;
import fr.hadriel.empires.environment.Location;

import java.util.List;
import java.util.stream.Collectors;

public class Warrior extends Unit {

    private Village enemyVillage;
    private Unit enemyUnit;

    public Warrior(Tribe tribe, Location location) {
        super(tribe, location);
    }

    public void update(float deltaTime) {
        super.update(deltaTime);

        //Check for any enemies in sight
        if (!checkForEnemyUnit()) {
            enemyUnit = selectNearestEnemyUnit();
        }

        if (!checkForEnemyVillage()) {
            enemyVillage = selectRandomEnemyVillage();
        }



        //Chase enemy in sight if any
        if (enemyUnit != null) {

        }
        //If at Enemy Village, damage it.
    }

    private boolean checkForEnemyUnit() {
        if (enemyUnit == null)
            return false;

        //Make sure it's not too far
        if (!enemyUnit.isAlive()) {
            int los = tribe.characteristics.lineOfSight;
            float distanceSq = Util.distanceSquared(getLocation(), enemyUnit.getLocation());
            if (distanceSq <= los * los) {
                return true;
            }
        }

        //Lost track of the Enemy. (or dead enemy)
        enemyUnit = null;
        return false;
    }

    private boolean checkForEnemyVillage() {
        if (enemyVillage == null)
            return false;

        if (enemyVillage.isDestroyed()) {
            enemyVillage = null;
            return false;
        }
        return true;
    }

    private Unit selectNearestEnemyUnit() {
        int los = tribe.characteristics.lineOfSight;
        for (int dx = -los; dx <= los; dx++) {
            for (int dy = -los; dy <= los; dy++) {
                int x = getLocation().x + dx;
                int y = getLocation().y + dy;
                Location location = world.terrain.at(x, y);
                if (location == null) continue;
                List<Unit> units = world.getWarUnitsAtLocation(location);

                //There are ennemies here !
                if (units.stream().anyMatch(u -> u.tribe != tribe)) {

                }
            }
        }
        return null;
    }

    private Village selectRandomEnemyVillage() {
        //Select all Ennemy Villages
        List<Village> targets = world.tribes.stream()
                .filter(t -> t == tribe) // don't target own homeland
                .flatMap(t -> t.villages.stream())
                .collect(Collectors.toList());

        return targets.get((int) (targets.size() * Math.random()));
    }
}