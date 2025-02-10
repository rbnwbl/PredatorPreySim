import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Grass extends Plant
{
    private static final int BREEDING_AGE = 10;
    private static final int MAX_AGE = 20;
    private static final double BREEDING_PROBABILITY = 0.12;
    private static final int MAX_YIELD = 25;
    private static final int NUTRITION = 1;
    private static final int MATE_RANGE = 2;

    private int age;

    private static final Random rand = Randomizer.getRandom();

    public Grass(boolean randomAge, Location location)
    {
        age = 0;
        if (randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        super(location,NUTRITION);
    }

    public void act(Field currentField,Field nextFieldState, int time)
    {
        incrementAge();
        if (isAlive()) {
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            if(! freeLocations.isEmpty()) {
                giveBirth(currentField, nextFieldState, freeLocations);
            }
        }
    }

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
     * Check whether or not this grass is to reproduce at this step.
     * New plants will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations)
    {
        // New plants are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0 && canMate(currentField)) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Grass young = new Grass(false,loc);
                nextFieldState.placeOrganism(young, loc);
            }
        }
    }
        
    /**
     * Generate a number representing the yield,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_YIELD) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A grass can breed if it has reached the breeding age.
     * @return true if the grass can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * A plant can mate if there is a plant of opposite sex within MATE_RANGE
     */
    private boolean canMate(Field field)
    {
        List<Location> adjacent = field.getLocationsInRange(getLocation(),MATE_RANGE);
        Iterator<Location> it = adjacent.iterator();
        Location mateLocation = null;
        while(mateLocation == null && it.hasNext()) {
            Location loc = it.next();
            Organism organism = field.getOrganismAt(loc);
            if(organism instanceof Grass grass) {
                if(grass.getSex() != getSex()) {
                    return true;
                }
            }
        }
        return false;
    }

}
