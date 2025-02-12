import java.util.Random;

public class Weather {

    private int temp;
    private int visibility;
    private boolean rain;

    private static final int MIN_TEMP = 20;
    private static final int MAX_TEMP = 30;

    private static final int MIN_VISIBILITY = -1;
    private static final int MAX_VISIBILITY = 2;
    
    private final static Random rand = Randomizer.getRandom();
    
    public Weather() {
        temp = rand.nextInt(MIN_TEMP,MAX_TEMP);
        visibility = rand.nextInt(MIN_VISIBILITY, MAX_VISIBILITY);
        // 20% chance to start with rain.
        rain = (rand.nextDouble() < 0.2)?true:false;
    }

    public void change() {
        // Change temp by 1 degree (positively or negatively)
        do {
            temp = temp + rand.nextInt(-1,2);
        } while (temp < MIN_TEMP || temp > MAX_TEMP);

        visibility = rand.nextInt(MIN_VISIBILITY,MAX_VISIBILITY);
        

        rain = (rand.nextDouble() < 0.2)?true:false;
    }

    public int getTemp()
    {
        return temp;
    }
    
    public int getVisibility()
    {
        return visibility;
    }

    public boolean getRain() 
    {
        return rain;
    }
}
