/**
 * Abstract class for plant subclasses.
 * 
 * @author Yiun K and Reuben W
 * @version 7.0
 */
public abstract class Plant implements Organism {
    // Whether the plant is alive or not.
    protected boolean alive;
    // The plant's sex, 'M' for 'Male', 'F' for 'Female'.
    private char sex;
    // The plant's location.
    protected Location location;
    // The plant's age.
    protected int age;
    /**
     * Constructor for objects of the Plant class.
     * @param location The plant's location.
     */
    public Plant(Location location) {
        this.alive = true;
        this.location = location;
        this.age = 0;
    }

    /**
     *  @return whether the animal is alive or not.
     */
    public boolean isAlive() 
    {
        return alive;
    }
    
    protected void setDead() 
    {
        location = null;
        alive = false;
    }
    
    public Location getLocation() 
    {
        return location;
    }
    
    public void incrementAge()
    {
        age++;
    }

}