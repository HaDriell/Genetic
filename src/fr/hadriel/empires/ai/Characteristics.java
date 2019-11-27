package fr.hadriel.empires.ai;

import fr.hadriel.Util;

import java.io.*;
import java.util.Properties;
import java.util.Random;

public class Characteristics {

    ////////////////////////////////////////////////////////////////////////////////
    // Constants ///////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    public static final int MINIMUM_VALUE = 1;
    public static final int MAXIMUM_VALUE = 100;

    public static final String PROPERTY_FORCE           = "force";
    public static final String PROPERTY_CONSTITUTION    = "constitution";
    public static final String PROPERTY_VISION          = "vision";
    public static final String PROPERTY_AGILITY         = "agility";
    public static final String PROPERTY_EXPANSIONISM    = "expansionism";

    ////////////////////////////////////////////////////////////////////////////////
    // Characteristics Definition //////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Influences Damage
     * Influences Health
     */
    public final int force;
    public final float forceNormalized;

    /**
     * Influences Health
     * Influences Speed
     * Influences Gathering Capacity
     */
    public final int constitution;
    public final float constitutionNormalized;

    /**
     * Influences Scanning Distances
     */
    public final int vision;
    public final float visionNormalized;

    /**
     * Influences Damage
     * Influences Speed
     */
    public final int agility;
    public final float agilityNormalized;

    /**
     * Influences the probability of Colons spawning
     */
    public final int expansionism;
    public final float expansionismNormalized;

    ////////////////////////////////////////////////////////////////////////////////
    // Perks ///////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    public final float spawningFoodCost;
    public final float movementSpeed;
    public final float damage;
    public final float maxHealth;
    public final float gatheringCapacity;

    public Characteristics(int force, int constitution, int vision, int agility, int expansionism) {
        //Assign characteristics
        this.force          = force;
        this.constitution   = constitution;
        this.vision         = vision;
        this.agility        = agility;
        this.expansionism   = expansionism;
        //Compute normalized values
        this.forceNormalized        = Util.inverseLerp(force, MINIMUM_VALUE, MAXIMUM_VALUE);
        this.constitutionNormalized = Util.inverseLerp(constitution, MINIMUM_VALUE, MAXIMUM_VALUE);
        this.visionNormalized       = Util.inverseLerp(vision, MINIMUM_VALUE, MAXIMUM_VALUE);
        this.agilityNormalized      = Util.inverseLerp(agility, MINIMUM_VALUE, MAXIMUM_VALUE);
        this.expansionismNormalized = Util.inverseLerp(expansionism, MINIMUM_VALUE, MAXIMUM_VALUE);
        //Compute perks
        this.spawningFoodCost = calculateSpawningFoodCost();
        this.movementSpeed = calculateMovementSpeed();
        this.damage = calculateDamage();
        this.maxHealth = calculateMaxHealth();
        this.gatheringCapacity = calculateGatheringCapacity();
    }

    public Characteristics(Random random) {
        this(
                Util.lerp(random.nextFloat(), MINIMUM_VALUE, MAXIMUM_VALUE),
                Util.lerp(random.nextFloat(), MINIMUM_VALUE, MAXIMUM_VALUE),
                Util.lerp(random.nextFloat(), MINIMUM_VALUE, MAXIMUM_VALUE),
                Util.lerp(random.nextFloat(), MINIMUM_VALUE, MAXIMUM_VALUE),
                Util.lerp(random.nextFloat(), MINIMUM_VALUE, MAXIMUM_VALUE)
        );
    }

    public Characteristics(Random random, Characteristics a, Characteristics b) {
        //TODO : mutations
        this(
                random.nextBoolean() ? a.force : b.force,
                random.nextBoolean() ? a.constitution : b.constitution,
                random.nextBoolean() ? a.vision : b.vision,
                random.nextBoolean() ? a.agility : b.agility,
                random.nextBoolean() ? a.expansionism : b.expansionism
        );
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Perks Calculation Functions /////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    private float calculateMovementSpeed() {
        float inverseConstitutionNormalized = 1.0f - constitutionNormalized;
        float t = inverseConstitutionNormalized * agilityNormalized;
        float speed = Util.lerp(t, 0.0f, (float) MAXIMUM_VALUE);
        return (float) Math.sqrt(speed); // [0; 10]
    }

    private float calculateSpawningFoodCost() {
        float f = force         * 1.2f;
        float c = constitution  * 0.8f;
        float v = vision        * 2.0f;
        float a = agility       * 1.5f;
        return f + c + v + a;
    }

    private float calculateMaxHealth() {
        float f = force * 1.0f;
        float c = constitution * 4.0f;
        return f + c;
    }

    private float calculateDamage() {
        float f = force * 1.0f;
        float aFactor = Util.lerp(agilityNormalized, 1.0f, 2.0f);
        return aFactor * f;
    }

    private float calculateGatheringCapacity() {
        float c = constitution * 1.5f;
        return c;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Persistence Utilities ///////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    public static Characteristics Load(String path) {
        try (FileInputStream stream = new FileInputStream(path)) {
            return Load(stream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Characteristics Load(InputStream stream) {
        Properties properties = new Properties();
        int force, constitution, vision, agility, expansionism;
        try {
            //Load file
            properties.load(stream);

            //Parse properties
            force           = Integer.parseInt(properties.getProperty(PROPERTY_FORCE));
            constitution    = Integer.parseInt(properties.getProperty(PROPERTY_CONSTITUTION));
            vision          = Integer.parseInt(properties.getProperty(PROPERTY_VISION));
            agility         = Integer.parseInt(properties.getProperty(PROPERTY_AGILITY));
            expansionism    = Integer.parseInt(properties.getProperty(PROPERTY_EXPANSIONISM));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new Characteristics(force, constitution, vision, agility, expansionism);
    }

    public void save(String path) {
        try (FileOutputStream stream = new FileOutputStream(path)) {
            Properties properties = new Properties();

            //Serialize fields
            properties.put(PROPERTY_FORCE,          force);
            properties.put(PROPERTY_CONSTITUTION,   constitution);
            properties.put(PROPERTY_VISION,         vision);
            properties.put(PROPERTY_AGILITY,        agility);
            properties.put(PROPERTY_EXPANSIONISM,   expansionism);

            //Store file
            properties.store(stream, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        StringBuilder ss = new StringBuilder();

        ss.append("Characteristics[");
        {
            ss.append(String.format("Force : %3d", force));
            ss.append(" ");
            ss.append(String.format("Constitution : %3d", constitution));
            ss.append(" ");
            ss.append(String.format("Vision : %3d", vision));
            ss.append(" ");
            ss.append(String.format("Agility : %3d", agility));
            ss.append(" ");
            ss.append("Perks[");
            {
                ss.append(String.format("Cost : %04.2f", spawningFoodCost));
                ss.append(" ");
                ss.append(String.format("Speed : %04.02f", movementSpeed));
                ss.append(" ");
                ss.append(String.format("Damage : %04.2f", damage));
                ss.append(" ");
                ss.append(String.format("Health : %04.2f", maxHealth));
                ss.append(" ");
                ss.append(String.format("Capacity : %04.2f", gatheringCapacity));
                ss.append(" ");
            }
            ss.append("]");
        }
        ss.append("]");


        return ss.toString();
    }
}