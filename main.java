public static void main(String[] args) {
    Simulator simulator1 = new Simulator();
    simulator1.reportStats();
    simulator1.simulateOneStep();
    simulator1.reportStats();
    simulator1.runLongSimulation();
    simulator1.reportStats();
    
}