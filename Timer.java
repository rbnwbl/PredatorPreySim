import java.util.Random;

/**
 * This class keeps track of time of day. (in hours)
 *
 * @author Yiun K and Reuben W
 * @version 06.02.2025
 */
public class Timer
{
    // Time is stored in hours.
    private int hour;
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Timer
     */
    public Timer()
    {
        hour = rand.nextInt(24);
    }

    /**
     * Reset timer.
     */
    public void reset()
    {
        hour = rand.nextInt(24);
    }
    
    /**
     * Increment time by 1 hour.
     */
    public void increment()
    {
        hour++;
        if (hour == 24) {
            hour = 0;
        }
    }

    /**
     * Return the time.
     * @return The time.
     */
    public int getTime() {
        return hour;
    }

}