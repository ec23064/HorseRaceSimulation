package Part1;

/**
 * Write a description of class Horse here.
 * 
 * @author Joshua Benisty
 * @version 1
 */
public class Horse {
    private String name;
    private char symbol;
    private int distance;
    private boolean fallen;
    private double confidence;

    // Constructor of class Horse
    /**
     * Constructor for objects of class Horse
     */
    public Horse(char horseSymbol, String horseName, double horseConfidence) {
        this.symbol = horseSymbol;
        this.name = horseName;
        this.confidence = horseConfidence;
    }

    // Other methods of class Horse
    public void fall() {
        this.fallen = true;
    }

    public double getConfidence() {
        return this.confidence;
    }

    public int getDistanceTravelled() {
        return this.distance;
    }

    public String getName() {
        return this.name;
    }

    public char getSymbol() {
        return this.symbol;
    }

    public void goBackToStart() {
        this.distance = 0;
    }

    public boolean hasFallen() {
        return this.fallen;
    }

    public void moveForward() {
        distance++;
    }

    public void setConfidence(double newConfidence) {
        this.confidence = newConfidence;
    }

    public void setSymbol(char newSymbol) {
        this.symbol = newSymbol;
    }
}
