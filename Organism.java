interface Organism 
{
    boolean isAlive();
    Location getLocation();
    void act(Field currentField,Field nextFieldState);
}