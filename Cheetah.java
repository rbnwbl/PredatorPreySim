import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a cheetah.
 * cheetahs age, move, eat zebras, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Cheetah extends Animal
{
    // Characteristics shared by all cheetahs (class variables).
    // The age at which a cheetah can start to breed.
    private static final int BREEDING_AGE = 20;
    // The age to which a cheetah can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a cheetah breeding.
    private static final double BREEDING_PROBABILITY = 0.06;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The range cheetah can mate in.
    private static final int MATE_RANGE = 6;
    // The start & end time of the periods cheetahs are active in a day.
    private static final int[] ACTIVE_TIME_START = {8, 16};
    private static final int[] ACTIVE_TIME_END = {10, 18};
    // The range cheetahs can move in when they are active.
    private static final int ACTIVE_RANGE = 4;
    // The start & end time of the period cheetahs sleep in a day.
    private static final int SLEEP_TIME_START = 10;
    private static final int SLEEP_TIME_END = 18;
    // The food value of a single cheetah.
    private static final int NUTRITION = 7;
    // The maximum stamina of an cheetah.
    private static final int MAX_STAMINA = 15;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).

    // The cheetah's stamina.
    private int stamina;
    // The cheetah's age.
    private int age;

    /**
     * Create a cheetah. A cheetah can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the cheetah will have random age and hunger level.
     * @param location The location within the field.
     */
    public Cheetah(boolean randomAge, Location location)
    {
        super(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        stamina = rand.nextInt(MAX_STAMINA);;
    }
    
    /**
     * This is what the cheetah does most of the time: it hunts for
     * zebras. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     * @param time The current time of the simulation.
     */
    public void act(Field currentField, Field nextFieldState, int time, Weather weather)
    {
        incrementAge();
        decrementStamina();
        if(isAlive()) {
            if (isInfected()) {
                disinfect(weather.getTemp(),stamina/MAX_STAMINA);
                infect(currentField, weather.getTemp());
                decrementInfectionSteps();
            }
            if (! isAsleep(time)) {
                List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
                if(! freeLocations.isEmpty()) {
                    giveBirth(currentField, nextFieldState, freeLocations, weather);
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
        return "Cheetah{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", stamina=" + stamina +
                '}';
    }

    /**
     * Increase the age. This could result in the cheetah's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Decrement stamina of this cheetah. This could result in the cheetah's death.
     */
    private void decrementStamina()
    {
        stamina--;
        if(stamina <= 0) {
            setDead();
        }
    }
    
    public static int getNutrition() {
        return NUTRITION;
    }

    /**
     * Check if it is the cheetah's active time.
     * @param time The time.
     * @return Whether current time is in the active time range.
     */
    private boolean isActive(int time)
    {
        int i = 0;
        boolean inRange = false;
        while (i < ACTIVE_TIME_START.length && ! inRange) {
            if (ACTIVE_TIME_START[i] < ACTIVE_TIME_END[i]) {
                inRange = (time >= ACTIVE_TIME_START[i]) && (time <= ACTIVE_TIME_END[i]);
            }
            else {
                inRange = (time >= ACTIVE_TIME_START[i]) || (time <= ACTIVE_TIME_END[i]);
            }
            i++;
        }
        return inRange;
    }

    /**
     * Check if it is the cheetah's sleep time.
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
     * Look for zebras adjacent to the current location.
     * Only the first live zebra is eaten.
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
            if(organism instanceof Hyena hyena) {
                if(hyena.isAlive()) {
                    hyena.setDead();
                    stamina += Hyena.getNutrition();
                    foodLocation = loc;
                }
            }
            if(organism instanceof Zebra zebra) {
                if(zebra.isAlive()) {
                    zebra.setDead();
                    stamina += Zebra.getNutrition();
                    foodLocation = loc;
                }
            }
        }
        if (stamina > MAX_STAMINA) {
            stamina = MAX_STAMINA;
        }
        return foodLocation;
    }
    
    /**
     * Check whether this cheetah is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations,Weather weather)
    {
        // New cheetahes are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0 && canMate(currentField, weather.getVisibility())) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Cheetah young = new Cheetah(false, loc);
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
     * A cheetah can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * A cheetah can mate if there is a cheetah of opposite sex within MATE_RANGE.
     * @param field
     * @return Whether the 
     */
    private boolean canMate(Field field, int visibility)
    {
        int newMateRange = Math.max(1,MATE_RANGE + visibility);
        List<Location> adjacent = field.getLocationsInRange(getLocation(), newMateRange);
        Iterator<Location> it = adjacent.iterator();
        Location mateLocation = null;
        while(mateLocation == null && it.hasNext()) {
            Location loc = it.next();
            Organism organism = field.getOrganismAt(loc);
            if(organism instanceof Cheetah cheetah) {
                if(cheetah.getSex() != getSex()) {
                    return true;
                }
            }
        }
        return false;
    }

}