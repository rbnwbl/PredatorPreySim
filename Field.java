import java.util.*;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal/object.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.0
 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The dimensions of the field.
    private final int depth, width;
    // Animals mapped by location.
    private final Map<Location, Organism> field = new HashMap<>();
    // The animals.
    private final List<Organism> organisms = new ArrayList<>();

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
    }

    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param organism The animal to be placed.
     * @param location Where to place the animal.
     */
    public void placeOrganism(Organism organism, Location location)
    {
        assert location != null;
        Object other = field.get(location);
        if(other != null) {
            organisms.remove(other);
        }
        field.put(location, organism);
        organisms.add(organism);
    }
    
    /**
     * Return the organism at the given location, if any.
     * @param location Where in the field.
     * @return The organism at the given location, or null if there is none.
     */
    public Organism getOrganismAt(Location location)
    {
        return field.get(location);
    }

    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = getAdjacentLocations(location);
        for(Location next : adjacent) {
            Organism organism = field.get(next);
            if(organism == null) {
                free.add(next);
            }
            else if(!organism.isAlive()) {
                free.add(next);
            }
        }
        return free;
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> getAdjacentLocations(Location location)
    {
        // The list of locations to be returned.
        List<Location> locations = new ArrayList<>();
        if(location != null) {
            int row = location.row();
            int col = location.col();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }

    /**
     * Print out the number of foxes and zebras in the field.
     */
    public void fieldStats()
    {
        int numHyenas = 0, numZebras=0,  numGrass = 0;
        for(Organism organism : field.values()) {
            if(organism instanceof Hyena hyena) {
                if(hyena.isAlive()) {
                    numHyenas++;
                }
            }
            else if(organism instanceof Zebra zebra) {
                if(zebra.isAlive()) {
                    numZebras++;
                }
            }
            else if(organism instanceof Grass grass) {
                if (grass.isAlive()) {
                    numGrass++;
                }
            }
        }
        System.out.println("Zebras: " + numZebras +
                           " Foxes: " + numHyenas +
                           " Grass: " + numGrass);
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        field.clear();
    }

    /**
     * Return whether there is at least one zebra and one fox in the field.
     * @return true if there is at least one zebra and one fox in the field.
     */
    public boolean isViable()
    {
        boolean zebraFound = false;
        boolean hyenaFound = false;
        Iterator<Organism> it = organisms.iterator();
        while(it.hasNext() && ! (zebraFound && hyenaFound)) {
            Organism organism = it.next();
            if(organism instanceof Zebra zebra) {
                if(zebra.isAlive()) {
                    zebraFound = true;
                }
            }
            else if(organism instanceof Hyena hyena) {
                if(hyena.isAlive()) {
                    hyenaFound = true;
                }
            }
        }
        return zebraFound && hyenaFound;
    }
    
    /**
     * Get the list of rganisms.
     */
    public List<Organism> getOrganisms()
    {
        return organisms;
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
