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
    private static final double HYENA_CREATION_PROBABILITY = 0.04;
    // The probability that a zebra will be created in any given position.
    private static final double ZEBRA_CREATION_PROBABILITY = 0.14;
    // The probability that a fruit plant will be created in any given position.
    private static final double FRUIT_CREATION_PROBABILITY = 0.20;
    // The probability that a grass plant will be created in any given position.
    private static final double GRASS_CREATION_PROBABILITY = 0.36;
    // The probability that an animal will be infected by a disease.
    private static final double DISEASE_PROBABILITY = 0.1;
    

    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // The timer to keep track of time of the simulation
    private Timer timer;
    // The weather.
    private Weather weather;
    // A graphical view of the simulation.
    private final SimulatorView view;
    private Random rand = Randomizer.getRandom();

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
        timer = new Timer();
        weather = new Weather();

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
        timer.increment();
        weather.change();
        // Use a separate Field to store the starting state of
        // the next step.
        Field nextFieldState = new Field(field.getDepth(), field.getWidth());

        // Calculates the new disease probability: higher temp, less disease.
        double newDiseaseProb = DISEASE_PROBABILITY - ((weather.getTemp() - 20)/100);

        List<Organism> organisms = field.getOrganisms();
        for (Organism anOrganism : organisms) {
            anOrganism.act(field, nextFieldState, timer.getTime(), weather);

            // If the organism is an animal, randomly infect it.
            if (anOrganism instanceof Animal animal) {
                if (! animal.isInfected() && rand.nextDouble() <= newDiseaseProb) {
                    animal.setInfected();
                }
            }
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
        timer.reset();
        
    }
    /**
     * Randomly populate the field with hyenaes and zebras.
     */
    private void populate()
    {
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                double roll = rand.nextDouble();
                // The sex of the organism being created.
                if(roll <= HYENA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Hyena hyena = new Hyena(true, location);
                    field.placeOrganism(hyena, location);
                }
                else if(roll <= ZEBRA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Zebra zebra = new Zebra(true, location);
                    field.placeOrganism(zebra, location);
                }
                else if (roll <= FRUIT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fruit fruit = new Fruit(true,location);
                    field.placeOrganism(fruit, location);
                }
                else if (roll <= GRASS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Grass grass = new Grass(true,location);
                    field.placeOrganism(grass,location);
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
