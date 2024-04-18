package Part2;

public class Horse {
    private String name;
    private String unicodeHorse; 
    private int distance;
    private boolean fallen;
    private double confidence;

    public Horse(String unicodeHorse, String horseName, double horseConfidence) {
        this.unicodeHorse = unicodeHorse;
        this.name = horseName;
        this.confidence = horseConfidence;
    }

    public String getUnicodeHorse() {
        return unicodeHorse;
    }

    public void setUnicodeHorse(String unicodeHorse) {
        this.unicodeHorse = unicodeHorse;
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

    public void setName(String newName) {
        this.name = newName;
    }

    public void increaseConfidence() {
        if (this.confidence < 1.0){
            this.confidence += 0.1;
        }
    }

}
