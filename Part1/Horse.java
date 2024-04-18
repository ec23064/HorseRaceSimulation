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
        checkConfidenceValue(horseConfidence);
        this.symbol = horseSymbol;
        this.name = horseName;
        this.confidence = horseConfidence;
    }

    public void checkConfidenceValue(double confidence) {
        if (confidence < 0 || confidence > 1) {
            throw new IllegalArgumentException("Confidence value must be between 0 and 1");
        }
    }

    // Other methods of class Horse
    public void fall() {
        this.fallen = true;
        if (this.confidence > 0.0){
            this.confidence -= 0.1;
            this.confidence = Math.round(this.confidence * 100.0) / 100.0;
        }
    }

    public void reset() {
        this.fallen = false;
        this.distance = 0;
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

    public void setName(String newName) {
        this.name = newName;
    }

    public void increaseConfidence() {
        if (this.confidence < 1.0){
            this.confidence += 0.1;
        }
    }

}
