import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a elephant.
 * Rabbits age, move, breed, and die.
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
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The range hyena can mate in.
    private static final int MATE_RANGE = 3;
    // The start & end time of the period hyenas are active in a day.
    private static final int ACTIVE_TIME_START = 22;
    private static final int ACTIVE_TIME_END = 4;
    // The range hyenas can move in when they are active.
    private static final int ACTIVE_RANGE = 2;
    // The start & end time of the period hyenas sleep in a day.
    private static final int SLEEP_TIME_START = 10;
    private static final int SLEEP_TIME_END = 14;
    // The food value of a single elephant. In effect, this is the
    // number of steps a hyena can go before it has to eat again.
    // The food value of a single grass.
    private static final int GRASS_FOOD_VALUE = 3;
    // The food value of a single grass.
    private static final int FRUIT_FOOD_VALUE = 5;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    //
    private static final int BASE_STAMINA = 15;
    
    // Individual characteristics (instance fields).
    
    // The elephant's age.
    private int age;
    // The elephant's food level, which is increased by eating grass & fruits.
    private int foodLevel;

    /**
     * Create a new elephant. A elephant may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the elephant will have a random age.
     * @param location The location within the field.
     */
    public Elephant(boolean randomAge, Location location)
    {
        super(location, BASE_STAMINA);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        foodLevel = rand.nextInt(GRASS_FOOD_VALUE);
    }
    
    /**
     * This is what the elephant does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     * @param time The current time of the simulation.
     */
    public void act(Field currentField, Field nextFieldState, int time)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if (isInfected()) {
                infect(currentField);
                decrementInfectionSteps();
            }
            if (! isAsleep(time)) {
                List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
                if(! freeLocations.isEmpty()) {
                    giveBirth(currentField, nextFieldState, freeLocations);
                }
                Location nextLocation;
                if (isActive(time)) {
                    nextLocation = findFood(currentField, ACTIVE_RANGE);
                }
                else {
                    nextLocation = findFood(currentField, 1);
                }
                // Move towards a source of food if found.
                if(nextLocation == null && ! freeLocations.isEmpty()) {
                    // No food found - try to move to a free location.
                    nextLocation = freeLocations.remove(0);
                }
                // See if it was possible to move.
                if(nextLocation != null) {
                    setLocation(nextLocation);
                    nextFieldState.placeOrganism(this, nextLocation);
                }
                else {
                    // Overcrowding.
                    setDead();
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Elephant{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                '}';
    }

    /**
     * Increase the age.
     * This could result in the elephant's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this elephant more hungry. This could result in the elephant's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Check if it is the elephant's active time.
     * @param time The time.
     * @return Whether current time is in the active time range.
     */
    private boolean isActive(int time)
    {
        if (ACTIVE_TIME_START < ACTIVE_TIME_END) {
            return (time >= ACTIVE_TIME_START) && (time <= ACTIVE_TIME_END);
        }
        else {
            return (time >= ACTIVE_TIME_START) || (time <= ACTIVE_TIME_END);
        }
    }

    /**
     * Check if it is the elephant's sleep time.
     * @param time The time.
     * @return Whether current time is in the sleep time range.
     */
    private boolean isAsleep(int time)
    {
        if (SLEEP_TIME_START < SLEEP_TIME_END) {
            return (time >= SLEEP_TIME_START) && (time <= SLEEP_TIME_END);
        }
        else {
            return (time >= SLEEP_TIME_START) || (time <= SLEEP_TIME_END);
        }
    }

    /**
     * Look for elephants adjacent to the current location.
     * Only the first live elephant is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field, int range)
    {
        List<Location> locations = field.getLocationsInRange(getLocation(), range);
        Iterator<Location> it = locations.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Organism organism = field.getOrganismAt(loc);
            if(organism instanceof Grass grass) {
                if(grass.isAlive()) {
                    grass.setDead();
                    foodLevel = GRASS_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
            if(organism instanceof Fruit fruit) {
                if(fruit.isAlive()) {
                    fruit.setDead();
                    foodLevel = FRUIT_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }

    /**
     * Check whether or not this elephant is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations)
    {
        // New elephants are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0 && canMate(currentField)) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Elephant young = new Elephant(false, loc);
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
     * A elephant can breed if it has reached the breeding age.
     * @return true if the elephant can breed, false otherwise.
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
            Organism organism = field.getOrganismAt(loc);
            if(organism instanceof Elephant elephant) {
                if(elephant.getSex() != getSex()) {
                    return true;
                }
            }
        }
        return false;
    }
}
