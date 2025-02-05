import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a hyena.
 * hyenas age, move, eat zebra, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Hyena extends Animal
{
    // Characteristics shared by all hyenas (class variables).
    // The age at which a hyena can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a hyena can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a hyena breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The range hyena can mate in.
    private static final int MATE_RANGE = 3;
    // The food value of a single zebra. In effect, this is the
    // number of steps a hyena can go before it has to eat again.
    private static final int ZEBRA_FOOD_VALUE = 9;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).

    // The hyena's age.
    private int age;
    // The hyena's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create a hyena. A hyena can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the hyena will have random age and hunger level.
     * @param location The location within the field.
     */
    public Hyena(boolean randomAge, Location location)
    {
        super(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        foodLevel = rand.nextInt(ZEBRA_FOOD_VALUE);
    }
    
    /**
     * This is what the hyena does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            if(! freeLocations.isEmpty()) {
                giveBirth(currentField, nextFieldState, freeLocations);
            }
            // Move towards a source of food if found.
            Location nextLocation = findFood(currentField);
            if(nextLocation == null && ! freeLocations.isEmpty()) {
                // No food found - try to move to a free location.
                nextLocation = freeLocations.remove(0);
            }
            // See if it was possible to move.
            if(nextLocation != null) {
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
        return  "Hyena{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }

    /**
     * Increase the age. This could result in the hyena's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this hyena more hungry. This could result in the hyena's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for foods adjacent to the current location.
     * Only the first live zebra is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if(animal instanceof Zebra zebra) {
                if(zebra.isAlive()) {
                    zebra.setDead();
                    foodLevel = ZEBRA_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }
    
    /**
     * Check whether this hyena is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations)
    {
        // New hyenaes are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0 && canMate(currentField)) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Hyena young = new Hyena(false, loc);
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
     * A hyena can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * A hyena can mate if there is a hyena of opposite sex within MATE_RANGE
     */
    private boolean canMate(Field field)
    {
        List<Location> adjacent = field.getLocationsInRange(getLocation(),MATE_RANGE);
        Iterator<Location> it = adjacent.iterator();
        Location mateLocation = null;
        while(mateLocation == null && it.hasNext()) {
            Location loc = it.next();
            Animal animal = field.getAnimalAt(loc);
            if(animal instanceof Hyena hyena) {
                if (hyena.getSex() != getSex()) {
                    return true;
                }
            }
        }
        return false;
    }
}