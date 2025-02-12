import java.util.Random;
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
    // The plant's nutrition value.
    private final int nutrition;
    
    private static final Random rand = Randomizer.getRandom();
    /**
     * Constructor for objects of the Plant class.
     * @param location The plant's location.
     */
    public Plant(Location location,int nutrition) {
        this.alive = true;
        this.location = location;
        this.age = 0;
        this.nutrition = nutrition;
        this.sex = (rand.nextDouble() < 0.5)?'M':'F';
    }

    /**
     *  @return whether the plant is alive or not.
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

    public char getSex() {
        return sex;
    }

    public int getNutrition() {
        return nutrition;
    }
}