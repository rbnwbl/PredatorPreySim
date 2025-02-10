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
        return getLocationsInRange(location, 1);
    }

    public List<Location> getLocationsInRange(Location location, int range)
    {
        // The list of locations to be returned.
        List<Location> locations = new ArrayList<>();
        if(location != null) {
            int row = location.row();
            int col = location.col();
            for(int roffset = -range; roffset <= range; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -range; coffset <= range; coffset++) {
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
     * Print out the number of each organisms in the field.
     */
    public void fieldStats()
    {
        int numLions = 0, numCheetahs = 0, numHyenas = 0, numZebras = 0,  numElephants = 0, numGrass = 0, numFruit = 0;
        for(Organism organism : field.values()) {
            if(organism instanceof Lion lion) {
                if(lion.isAlive()) {
                    numLions++;
                }
            }
            else if(organism instanceof Cheetah cheetah) {
                if(cheetah.isAlive()) {
                    numCheetahs++;
                }
            }
            else if(organism instanceof Hyena hyena) {
                if(hyena.isAlive()) {
                    numHyenas++;
                }
            }
            else if(organism instanceof Zebra zebra) {
                if(zebra.isAlive()) {
                    numZebras++;
                }
            }
            else if(organism instanceof Elephant elephant) {
                if(elephant.isAlive()) {
                    numElephants++;
                }
            }
            else if(organism instanceof Grass grass) {
                if (grass.isAlive()) {
                    numGrass++;
                }
            }
            else if (organism instanceof Fruit fruit) {
                if (fruit.isAlive()) {
                    numFruit++;
                }
            }
        }
        System.out.println("Lions: " + numLions +
                           " Cheetahs: " + numCheetahs +
                           " Hyenas: " + numHyenas +
                           " Zebras: " + numZebras +
                           " Elephant: " + numElephants +
                           " Grass: " + numGrass +
                           " Fruit: " + numFruit);
    }

    /**
     * Empty the field.
     */
    public void clear()
    {
        field.clear();
    }

    /**
     * Return whether there is at least one predator, one prey, and one plant in the field.
     * @return true if there is at least oone predator, one prey, and one plant in the field.
     */
    public boolean isViable()
    {
        boolean preyFound = false;
        boolean predatorFound = false;
        boolean plantFound = false;
        Iterator<Organism> it = organisms.iterator();
        while(it.hasNext() && ! ((preyFound && predatorFound && plantFound))) {
            Organism organism = it.next();
            if(organism instanceof Lion lion) {
                if(lion.isAlive()) {
                    predatorFound = true;
                }
            }
            else if(organism instanceof Cheetah cheetah) {
                if(cheetah.isAlive()) {
                    predatorFound = true;
                }
            }
            else if(organism instanceof Hyena hyena) {
                if(hyena.isAlive()) {
                    predatorFound = true;
                }
            }
            else if(organism instanceof Zebra zebra) {
                if(zebra.isAlive()) {
                    preyFound = true;
                }
            }
            else if(organism instanceof Elephant elephant) {
                if(elephant.isAlive()) {
                    preyFound = true;
                }
            }
            else if (organism instanceof Grass grass) {
                if (grass.isAlive()) {
                    plantFound = true;
                }
            }
            else if (organism instanceof Fruit fruit) {
                if (fruit.isAlive()) {
                    plantFound = true;
                }
            }
        }
        return preyFound && predatorFound && plantFound;
    }
    
    /**
     * Get the list of organisms.
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
