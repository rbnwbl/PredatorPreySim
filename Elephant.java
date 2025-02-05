import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a elephant
 .
 * elephants age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Elephant extends Animal
{
    // Characteristics shared by all elephants (class variables).
    // The age at which a elephant can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a elephant can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a elephant breeding.
    private static final double BREEDING_PROBABILITY = 0.12;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The range elephant can mate in.
    private static final int MATE_RANGE = 3;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The elephant's age.
    private int age;

    /**
     * Create a new elephant
     . A elephant
      may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the elephant
      will have a random age.
     * @param location The location within the field.
     */
    public Elephant(boolean randomAge, Location location)
    {
        super(location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }
    
    /**
     * This is what the elephant
      does most of the time - it runs 
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
                giveBirth(nextFieldState, freeLocations);
            }
            // Try to move into a free location.
            if(! freeLocations.isEmpty()) {
                Location nextLocation = freeLocations.get(0);
                setLocation(nextLocation);
                nextFieldState.placeAnimal(this, nextLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    @Override
    public String toString() {
        return  "elephant{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Increase the age.
     * This could result in the elephant
     's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this elephant
      is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New elephants are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Elephant young = new Elephant(false, loc);
                nextFieldState.placeAnimal(young, loc);
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
     * A elephant
      can breed if it has reached the breeding age.
     * @return true if the elephant
      can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * A elephant can mate if there is a hyena of opposite sex within MATE_RANGE
     */
    private boolean canMate(Field field)
    {
        List<Location> adjacent = field.getLocationsInRange(getLocation(),MATE_RANGE);
        Iterator<Location> it = adjacent.iterator();
        Location mateLocation = null;
        while(mateLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if(animal instanceof Elephant elephant) {
                if (elephant.getSex() != getSex()) {
                    return true;
                }
            }
        }
        return false;
    }
}