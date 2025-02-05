import java.util.*;

/**
 * A simple predator-prey simulator, based on a rectangular field containing 
 * zebras and hyenaes.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a hyena will be created in any given grid position.
    private static final double HYENA_CREATION_PROBABILITY = 0.02;
    // The probability that a zebra will be created in any given position.
    private static final double ZEBRA_CREATION_PROBABILITY = 0.08;    
    // The probability that a grass plant will be created in any given position.
    private static final double GRASS_CREATION_PROBABILITY = 0.04;
    // The probability that a fruit plant will be created in any given position.
    private static final double FRUIT_CREATION_PROBABILITY = 0.03;
    

    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private final SimulatorView view;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be >= zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        field = new Field(depth, width);
        view = new SimulatorView(depth, width);

        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long 
     * period (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(700);
    }
    
    /**
     * Run the simulation for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        reportStats();
        for(int n = 1; n <= numSteps && field.isViable(); n++) {
            simulateOneStep();
            delay(50);         // adjust this to change execution speed
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each hyena and zebra.
     */
    public void simulateOneStep()
    {
        step++;
        // Use a separate Field to store the starting state of
        // the next step.
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());

        List<Organism> organisms = field.getOrganisms();
        for (Organism anOrganism : organisms) {
            anOrganism.act(field, nextFieldState);
        }
        
        // Replace the old state with the new one.
        field = nextFieldState;

        reportStats();
        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        populate();
        view.showStatus(step, field);
    }
    /**
     * Randomly populate the field with hyenaes and zebras.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                // The sex of the organism being created.
                char sex = (rand.nextDouble() < 0.5) ? 'M':'F';
                if(rand.nextDouble() <= HYENA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Hyena hyena = new Hyena(sex, location,true);
                    field.placeOrganism(hyena, location);
                }
                else if (rand.nextDouble() <= FRUIT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fruit fruit = new Fruit(location,sex);
                    field.placeOrganism(fruit, location);
                }
                else if (rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                    Location location = new Location(row,col);
                    Grass grass = new Grass(location,sex);
                    field.placeOrganism(grass,location);
                }
                else if(rand.nextDouble() <= ZEBRA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Zebra zebra = new Zebra(sex, location, true);
                    field.placeOrganism(zebra, location);
                }
            }
        }
    }

    /**
     * Report on the number of each type of organism in the field.
     */
    public void reportStats()
    {
        //System.out.print("Step: " + step + " ");
        field.fieldStats();
    }
    
    /**
     * Pause for a given time.
     * @param milliseconds The time to pause for, in milliseconds
     */
    private void delay(int milliseconds)
    {
        try {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException e) {
            // ignore
        }
    }
}
