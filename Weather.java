import java.util.Random;

/**
 * This is a class that represents the weather in the simulation.
 * @author Yiun Kim and Reuben Weibel
 * @version 7.1
 */
public class Weather {
    // Characteristics of the weather.
    // The current temperature.
    private int temp;
    // The current visibility.
    private int visibility;
    // Whether it is currently raining.
    private boolean rain;

    // Bounds for the temperature attribute.
    private static final int MIN_TEMP = 20;
    private static final int MAX_TEMP = 30;

    // Bounds for the visibility attribute.
    private static final int MIN_VISIBILITY = -1;
    private static final int MAX_VISIBILITY = 1;
    
    // Random number generator.
    private final static Random rand = Randomizer.getRandom();
    
    /**
     * Constructor for instances of the Weather class.
     * Assigns a random temperature and visibility.
     * Decides if it is raining or not (0.2 chance of rain).
     */
    public Weather() {
        temp = rand.nextInt(MIN_TEMP,MAX_TEMP);
        visibility = rand.nextInt(MIN_VISIBILITY, MAX_VISIBILITY);
        // 20% chance to start with rain.
        rain = (rand.nextDouble() < 0.2)?true:false;
    }

    /**
     * Method that updates the weather.
     */
    public void change() {
        // Change temp by 1 degree (positively or negatively)
        do {
            temp = temp + rand.nextInt(-1,2);
        } while (temp < MIN_TEMP || temp > MAX_TEMP);

        // New value for visibility. MAX_VISIBILITY + 1 so possible values are -1,0,1.
        visibility = rand.nextInt(MIN_VISIBILITY,MAX_VISIBILITY +1);
        
        // Update whether it is raining or not (0.2 chance of rain).
        rain = (rand.nextDouble() < 0.2)?true:false;
    }

    /**
     * Getter method for temperature
     * @return The current temperature.
     */
    public int getTemp()
    {
        return temp;
    }
    
    /**
     * Getter method for visibility.
     * @return The current visibility.
     */
    public int getVisibility()
    {
        return visibility;
    }

    /**
     * Getter method for rain.
     * @return True if it is currently raining.
     */
    public boolean getRain() 
    {
        return rain;
    }
}
