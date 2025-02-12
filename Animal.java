import java.util.List;
import java.util.Random;

/**
 * Common elements of foxes and rabbits.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public abstract class Animal implements Organism
{
    // The number of steps infected
    private static final int DISEASE_STEPS = 3;
    private static final double INFECTION_PROBABILITY = 0.20;

    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's position.
    private Location location;
    // Whether the animal is infected by a disease or not.
    // Animals that are infected die after given number of steps.
    private boolean infected;
    // Number of steps the animal has before dying when infected.
    private int infectedSteps;
    // The animal's stamina.
    protected int stamina;
    // The animal's sex. 'M' for 'Male', 'F' for 'Female'.
    private final char sex;
    // Randomiser to introduce variation into the population.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Animal.
     * @param location The animal's location.
     */
    public Animal(Location location,int stamina)
    {
        this.alive = true;
        this.location = location;
        this.infected = false;
        // Randomise the stamina so population stamina varies.
        this.stamina = (int) (stamina *  rand.nextDouble(0.6, 1.3));
        // Randomise sex of the animal.
        if (rand.nextDouble() < 0.5) {
            sex = 'M';
        }
        else {
            sex = 'F';
        }

    }
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     */
    protected void setDead()
    {
        alive = false;
        location = null;
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    public Location getLocation()
    {
        return location;
    }
    
    /**
     * Set the animal's location.
     * @param location The new location.
     */
    protected void setLocation(Location location)
    {
        this.location = location;
    }

    /**
     * Return the animal's sex.
     * @return The animal's sex.
     */
    public char getSex() {
        return sex;
    }

    /**
     * Check whether the animal is infected or not.
     * @return true if the animal is infected.
     */
    public boolean isInfected() {
        return infected;
    }

    /**
     * Indicate that the animal is infected by disease & initialise steps before death.
     */
    public void setInfected()
    {
        infected = true;
        infectedSteps = DISEASE_STEPS;
    }

    public void disinfect(int temp, double staminaLevel)
    {
        if (temp > 28 && staminaLevel > 0.8) {
            infected = false;
        }
    }

    protected void decrementInfectionSteps()
    {
        infectedSteps--;
        if (infectedSteps == 0) {
            setDead();
        }
    }

    /**
     * Try infecting adjacent animals.
     */
    protected void infect(Field field, int temp)
    {
        List<Location> locations = field.getAdjacentLocations(getLocation());

        double newInfectionProb = INFECTION_PROBABILITY - ((temp - 20)/100);
        for (Location loc : locations) {
            Organism organism = field.getOrganismAt(loc);
            if(organism instanceof Animal animal) {
                if(animal.isAlive() && rand.nextDouble() <= newInfectionProb) {
                    animal.setInfected();
                }
            }
        }
    }

}
