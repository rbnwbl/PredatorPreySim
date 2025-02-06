import java.util.Random;

/**
 * Common elements of foxes and rabbits.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public abstract class Animal implements Organism
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's position.
    private Location location;
    // The animal's stamina.
    private int stamina;
    // The animal's sex. 'M' for 'Male', 'F' for 'Female'.
    private char sex;
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
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    abstract public void act(Field currentField, Field nextFieldState);
    
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

    public char getSex() {
        return sex;
    }
}
