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
    public static final String PROPERTY_BELLICISM       = "bellicism";

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
     * Influences Damage
     * Influences the Warriors spawning frequency
     */
    public final int bellicism;
    public final float bellicismNormalized;

    /**
     * Influences the Warrior spawning frequency
     * Influences the Founders spawning frequency
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
    public final int lineOfSight;
    public final float warriorSpawningFrequency;
    public final float founderSpawningFrequency;

    public Characteristics(int force, int constitution, int vision, int agility, int expansionism, int bellicism) {
        //Assign characteristics
        this.force          = force;
        this.constitution   = constitution;
        this.vision         = vision;
        this.agility        = agility;
        this.expansionism   = expansionism;
        this.bellicism      = bellicism;
        //Compute normalized values
        this.forceNormalized        = Util.inverseLerp(force, MINIMUM_VALUE, MAXIMUM_VALUE);
        this.constitutionNormalized = Util.inverseLerp(constitution, MINIMUM_VALUE, MAXIMUM_VALUE);
        this.visionNormalized       = Util.inverseLerp(vision, MINIMUM_VALUE, MAXIMUM_VALUE);
        this.agilityNormalized      = Util.inverseLerp(agility, MINIMUM_VALUE, MAXIMUM_VALUE);
        this.expansionismNormalized = Util.inverseLerp(expansionism, MINIMUM_VALUE, MAXIMUM_VALUE);
        this.bellicismNormalized    = Util.inverseLerp(bellicism, MINIMUM_VALUE, MAXIMUM_VALUE);
        //Compute perks
        this.spawningFoodCost = calculateSpawningFoodCost();
        this.movementSpeed = calculateMovementSpeed();
        this.damage = calculateDamage();
        this.maxHealth = calculateMaxHealth();
        this.gatheringCapacity = calculateGatheringCapacity();
        this.lineOfSight = calculateLineOfSight();
        this.warriorSpawningFrequency = calculateWarriorSpawningFrequency();
        this.founderSpawningFrequency = calculateFounderSpawningFrequency();
    }

    public Characteristics(Random random) {
        this(
                Util.lerp(random.nextFloat(), MINIMUM_VALUE, MAXIMUM_VALUE),
                Util.lerp(random.nextFloat(), MINIMUM_VALUE, MAXIMUM_VALUE),
                Util.lerp(random.nextFloat(), MINIMUM_VALUE, MAXIMUM_VALUE),
                Util.lerp(random.nextFloat(), MINIMUM_VALUE, MAXIMUM_VALUE),
                Util.lerp(random.nextFloat(), MINIMUM_VALUE, MAXIMUM_VALUE),
                Util.lerp(random.nextFloat(), MINIMUM_VALUE, MAXIMUM_VALUE)
        );
    }

    public static Characteristics CreateChild(Random random, Characteristics a, Characteristics b) {
        int force, constitution, vision, agility, expansionism, bellicism;
        force           = random.nextBoolean() ? a.force : b.force;
        constitution    = random.nextBoolean() ? a.constitution : b.constitution;
        vision          = random.nextBoolean() ? a.vision : b.vision;
        agility         = random.nextBoolean() ? a.agility : b.agility;
        expansionism    = random.nextBoolean() ? a.expansionism : b.expansionism;
        bellicism       = random.nextBoolean() ? a.bellicism : b.bellicism;

        //Mutation happens rarely
        if (random.nextFloat() < 0.001f) {
            //New value
            int value = Util.lerp(random.nextFloat(), Characteristics.MINIMUM_VALUE, Characteristics.MAXIMUM_VALUE);

            //Select the gene to mutate
            switch (random.nextInt(6)) {
                case 0: force           = value; break;
                case 1: constitution    = value; break;
                case 2: vision          = value; break;
                case 3: agility         = value; break;
                case 4: expansionism    = value; break;
                case 5: bellicism       = value; break;
            }
        }
        return new Characteristics(force, constitution, vision, agility, expansionism, bellicism);
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
        float f = force         * 1.0f;
        float c = constitution  * 4.0f;
        return f + c;
    }

    private float calculateDamage() {
        float f = force     * 0.7f;
        float a = agility   * 0.3f;
        float bFactor = Util.lerp(bellicismNormalized, 1.0f, 2.0f);
        return bFactor * (f + a);
    }

    private float calculateGatheringCapacity() {
        float c = constitution * 1.5f;
        return c;
    }

    private int calculateLineOfSight() {
        float v = visionNormalized * visionNormalized; // Square curve-like
        int los = Util.lerp(v, 0, 10);
        return 1 + los;
    }

    private float calculateWarriorSpawningFrequency() {
        float f = Util.lerp(forceNormalized, 0.1f, 10.0f);
        float c = Util.lerp(constitutionNormalized, 0.1f, 10.0f);
        float a = Util.lerp(agilityNormalized, 0.1f, 10.0f);
        float bFactor = Util.lerp(bellicismNormalized, 1.0f, 0.6f);
        return bFactor * (f + c + a);
    }

    private float calculateFounderSpawningFrequency() {
        float e = Util.lerp(expansionismNormalized, 5.0f, 1.0f);
        return e;
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

    public static Characteristics Load(InputStream stream) throws IOException {
        Properties properties = new Properties();
        int force, constitution, vision, agility, expansionism, bellicism;

        //Load file
        properties.load(stream);

        //Parse properties
        force           = Integer.parseInt(properties.getProperty(PROPERTY_FORCE));
        constitution    = Integer.parseInt(properties.getProperty(PROPERTY_CONSTITUTION));
        vision          = Integer.parseInt(properties.getProperty(PROPERTY_VISION));
        agility         = Integer.parseInt(properties.getProperty(PROPERTY_AGILITY));
        expansionism    = Integer.parseInt(properties.getProperty(PROPERTY_EXPANSIONISM));
        bellicism       = Integer.parseInt(properties.getProperty(PROPERTY_BELLICISM));

        return new Characteristics(force, constitution, vision, agility, expansionism, bellicism);
    }

    public void save(String path) {
        try (FileOutputStream stream = new FileOutputStream(path)) {
            save(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(OutputStream stream) throws IOException {
        Properties properties = new Properties();

        //Serialize fields
        properties.put(PROPERTY_FORCE,          Integer.toString(force));
        properties.put(PROPERTY_CONSTITUTION,   Integer.toString(constitution));
        properties.put(PROPERTY_VISION,         Integer.toString(vision));
        properties.put(PROPERTY_AGILITY,        Integer.toString(agility));
        properties.put(PROPERTY_EXPANSIONISM,   Integer.toString(expansionism));
        properties.put(PROPERTY_BELLICISM,      Integer.toString(bellicism));

        //Store file
        properties.store(stream, null);
    }

    public static void main(String... args) {
        for (int i = 0; i < 10; i++)
            System.out.println(new Characteristics(new Random()).toString());
    }
}