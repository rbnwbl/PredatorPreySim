public class Grass extends Plant 
{
    private static final int BREEDING_AGE = 8;
    private static final int MAX_AGE = 20;
    private static final double BREEDING_PROBABILITY = 0.40;
    private static final int MAX_YIELD = 25;
    private static final int NUTRITION = 1;
    public Grass(Location location, char sex) {
        super(location,sex);
    }
    public void act(Field currentField, Field nextFieldState) {

    }
}