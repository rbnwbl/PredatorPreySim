import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a zebra.
 * Rabbits age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Zebra extends Animal
{
    // Characteristics shared by all zebras (class variables).
    // The age at which a zebra can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a zebra can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a zebra breeding.
    private static final double BREEDING_PROBABILITY = 0.12;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The range hyena can mate in.
    private static final int MATE_RANGE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    //
    private static final int BASE_STAMINA = 15;
    
    // Individual characteristics (instance fields).
    
    // The zebra's age.
    private int age;

    /**
     * Create a new zebra. A zebra may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the zebra will have a random age.
     * @param location The location within the field.
     */
    public Zebra(boolean randomAge, Location location)
    {
        super(location, BASE_STAMINA);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }
    
    /**
     * This is what the zebra does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState)
    {
        incrementAge();
        if(isAlive()) {
            List<Location> freeLocations = 
                nextFieldState.getFreeAdjacentLocations(getLocation());
            if(!freeLocations.isEmpty()) {
                giveBirth(currentField, nextFieldState, freeLocations);
            }
            // Try to move into a free location.
            if(! freeLocations.isEmpty()) {
                Location nextLocation = freeLocations.get(0);
                setLocation(nextLocation);
                nextFieldState.placeOrganism(this, nextLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    @Override
    public String toString() {
        return "Zebra{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Increase the age.
     * This could result in the zebra's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this zebra is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations)
    {
        // New zebras are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0 && canMate(currentField)) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Zebra young = new Zebra(false, loc);
                nextFieldState.placeOrganism(young, loc);
            }
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A zebra can breed if it has reached the breeding age.
     * @return true if the zebra can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * A zebra can mate if there is a hyena of opposite sex within MATE_RANGE
     */
    private boolean canMate(Field field)
    {
        List<Location> adjacent = field.getLocationsInRange(getLocation(),MATE_RANGE);
        Iterator<Location> it = adjacent.iterator();
        Location mateLocation = null;
        while(mateLocation == null && it.hasNext()) {
            Location loc = it.next();
            Organism organism = field.getOrganismAt(loc);
            if(organism instanceof Zebra zebra) {
                if(zebra.getSex() != getSex()) {
                    return true;
                }
            }
        }
        return false;
    }
}
