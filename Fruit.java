import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a fruit plant.
 * Fruit plants age, reproduce and die.
 * 
 * @author Yiun Kim and Reuben Weibel
 * @version 7.1
 */
public class Fruit extends Plant
{
    // Characteristics shared by all fruit plants.
    // The minimum age a fruit needs to be to reproduce.
    private static final int BREEDING_AGE = 5;
    // The age to which a fruit can live.
    private static final int MAX_AGE = 10;
    // The likelihood that a fruit plant reproduces.
    private static final double BREEDING_PROBABILITY = 0.14;
    // The max amount of children that a fruit can birth at a time.
    private static final int MAX_YIELD = 6;
    // The nutritional value of a fruit.
    private static final int NUTRITION = 4;
    // The range a fruit can mate in.
    private static final int MATE_RANGE = 2;

    // The fruit's age.
    private int age;
    
    // A shared random number generator.
    private static final Random rand = Randomizer.getRandom();

    /**
     * The constructor for instances of Fruit class.
     * Assigns a random age, up to the max age, to the plant.
     * @param randomAge True if the plant should have a random age set, false if age should = 0.
     * @param location The plant's location.
     */
    public Fruit(boolean randomAge, Location location)
    {
        super(location,NUTRITION);
        age = 0;
        if (randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }

    /**
     * Method that is run at each step of the simulation.
     * The fruit ages and reproduces.
     * 
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     * @param time The current time of the simulation.
     * @param weather The current state of the weather.
     */
    public void act(Field currentField,Field nextFieldState, int time, Weather weather)
    {
        incrementAge();
        if (isAlive()) {
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            if(! freeLocations.isEmpty()) {
                giveBirth(currentField, nextFieldState, freeLocations, weather);
            }

        }   
    }
    /**
     * Increases the fruit's age.
     * Could result in the fruit's death.
     */
    private void incrementAge()
    {
        age++;
        if (age > MAX_AGE) {
            setDead();
        }
    }

    @Override
    public String toString()
    {
        return "Plant{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                "}";
    }

    /**
     * Check whether or not this fruit is to reproduce at this step.
     * New plants will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     * @param weather The current state of the weather.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations, Weather weather)
    {
        // New plants are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed(weather.getRain());
        if(births > 0 && canMate(currentField,weather.getVisibility())) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Fruit young = new Fruit(false,loc);
                nextFieldState.placeOrganism(young, loc);
            }
        }
    }

    /**
     * Generate a number representing the yield,
     * if it can breed.
     * @param isRaining True if it is raining.
     * @return The number of births (may be zero).
     */
    private int breed(boolean isRaining)
    {
        int births;
        double newBreedingProbability = BREEDING_PROBABILITY;
        // More likely to breed if raining.
        if (isRaining) {
            newBreedingProbability = newBreedingProbability * 1.5;
        }

        if(canBreed() && rand.nextDouble() <= newBreedingProbability) {
            births = rand.nextInt(MAX_YIELD) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A fruit can breed if it has reached the breeding age.
     * @return true if the fruit can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * A plant can mate if there is a plant of opposite sex within MATE_RANGE.
     * @param field The field the grass is currently in.
     * @param visibility The current visibility given by the member.
     */
    private boolean canMate(Field field, int visibility)
    {
        int newMateRange = Math.max(1,MATE_RANGE + visibility);
        List<Location> adjacent = field.getLocationsInRange(getLocation(),newMateRange);
        Iterator<Location> it = adjacent.iterator();
        Location mateLocation = null;
        while(mateLocation == null && it.hasNext()) {
            Location loc = it.next();
            Organism organism = field.getOrganismAt(loc);
            if(organism instanceof Fruit fruit) {
                if(fruit.getSex() != getSex()) {
                    return true;
                }
            }
        }
        return false;
    }

}
