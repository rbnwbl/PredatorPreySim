interface Organism 
{
    boolean isAlive();
    Location getLocation();

    /**
     * Act.
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     * @param time The current time in the simulation.
     */
    void act(Field currentField,Field nextFieldState, int time);
}