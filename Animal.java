
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
    // The animal's sex.
    private char sex;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    

    /**
     * Constructor for objects of class Animal.
     * @param location The animal's location.
     */
    public Animal(Location location)
    {
        this.alive = true;
        this.location = location;
        if (rand.nextDouble() < 0.5) {
            sex = 'm';
        }
        else {
            sex = 'f';
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
