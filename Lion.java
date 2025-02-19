import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a lion.
 * lions age, move, eat zebras, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Lion extends Animal
{
    // Characteristics shared by all lions (class variables).
    // The age at which a lion can start to breed.
    private static final int BREEDING_AGE = 25;
    // The age to which a lion can live.
    private static final int MAX_AGE = 200;
    // The likelihood of a lion breeding.
    private static final double BREEDING_PROBABILITY = 0.05;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The range lion can mate in.
    private static final int MATE_RANGE = 6;
    // The start & end time of the period lions are active in a day.
    private static final int ACTIVE_TIME_START = 22;
    private static final int ACTIVE_TIME_END = 8;
    // The range lions can move in when they are active.
    private static final int ACTIVE_RANGE = 3;
    // The start & end time of the period lions sleep in a day.
    private static final int SLEEP_TIME_START = 10;
    private static final int SLEEP_TIME_END = 20;
    // The food value of a single lion.
    private static final int NUTRITION = 10;
    // The maximum stamina of a lion.
    private static final int MAX_STAMINA = 15;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).

    // The lion's stamina, which is increased by eating food.
    private int stamina;
    // The lion's age.
    private int age;

    /**
     * Create a lion. A lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Location location)
    {
        super(location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
        stamina = rand.nextInt(MAX_STAMINA);
    }
    
    /**
     * This is what the lion does most of the time: it hunts for
     * food. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     * @param time The current time of the simulation.
     * @param weather The current state of the weather.
     */
    public void act(Field currentField, Field nextFieldState, int time, Weather weather)
    {
        incrementAge();
        decrementStamina();
        if(isAlive()) {
            if (isInfected()) {
                // stamina/MAX_STAMINA = stamina percentage
                disinfect(weather.getTemp(),stamina/MAX_STAMINA);
                infect(currentField, weather.getTemp());
                decrementInfectionSteps();
            }
            if (! isAsleep(time)) {
                List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
                if(! freeLocations.isEmpty()) {
                    giveBirth(currentField, nextFieldState, freeLocations,weather);
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
        return "Lion{" +
                "age=" + age +
                ", alive=" + isAlive() +
                ", location=" + getLocation() +
                ", stamina=" + stamina +
                '}';
    }

    /**
     * Increase the age. This could result in the lion's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Decrement stamina of this lion. This could result in the lion's death.
     */
    private void decrementStamina()
    {
        stamina--;
        if(stamina <= 0) {
            setDead();
        }
    }
    
    /**
     * Return the nutrition value of a lion. This is the amount of stamina increase when another species eat a lion.
     * @return The nutrition constant of a lion.
     */
    public static int getNutrition() {
        return NUTRITION;
    }

    /**
     * Check if it is the lion's active time.
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
     * Check if it is the lion's sleep time.
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
     * @param range The range in which the animal can find food.
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
            if(organism instanceof Elephant elephant) {
                if(elephant.isAlive()) {
                    elephant.setDead();
                    stamina += Elephant.getNutrition();
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
     * Check whether this lion is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     * @param weather The current state of the weather.
     */
    private void giveBirth(Field currentField, Field nextFieldState, List<Location> freeLocations,Weather weather)
    {
        // New lions are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0 && canMate(currentField,weather.getVisibility())) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Lion young = new Lion(false, loc);
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
     * A lion can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * A lion can mate if there is a lion of opposite sex within MATE_RANGE.
     * @param field The current field.
     * @param visibility The current visibility given by Weather.
     */
    private boolean canMate(Field field,int visibility)
    {
        int newMateRange = Math.max(1,MATE_RANGE + visibility);
        List<Location> adjacent = field.getLocationsInRange(getLocation(),newMateRange);
        Iterator<Location> it = adjacent.iterator();
        Location mateLocation = null;
        while(mateLocation == null && it.hasNext()) {
            Location loc = it.next();
            Organism organism = field.getOrganismAt(loc);
            if(organism instanceof Lion lion) {
                if(lion.getSex() != getSex()) {
                    return true;
                }
            }
        }
        return false;
    }
    
}
