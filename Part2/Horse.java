package Part2;

import java.util.List;
import java.util.ArrayList;

public class Horse {
    private String name;
    private double confidence;
    private String unicodeHorse;
    private int distance;
    private boolean fallen;
    private List<RaceResult> raceResults = new ArrayList<>();
    private int racesWon;
    private int timesFallen;
    private boolean hasWon;

    public Horse(String unicodeHorse, String horseName, double horseConfidence) {
        this.unicodeHorse = unicodeHorse;
        this.name = horseName;
        this.confidence = horseConfidence;
        this.hasWon = false;
    }
    private static class RaceResult {
        int time;
        int length;
        

        public RaceResult(int time, int length) {
            this.time = time;
            this.length = length;
        }
    }

    public void addRaceTime(int time) {
        raceResults.add(new RaceResult(time, this.distance));
    }

    public double getAverageSpeed() {
        if (raceResults.isEmpty()) return 0;
        double totalDistance = raceResults.stream().mapToDouble(result -> result.length).sum();
        double totalTime = raceResults.stream().mapToInt(result -> result.time).sum() / 1000.0;
        return totalDistance / totalTime;
    }

    public void setHasWon(boolean hasWon) {
        this.hasWon = hasWon;
        if (hasWon) {
            racesWon++;
        }
    }

    public boolean hasWon() {
        return hasWon;
    }

    public double getWinRatio() {
        return racesWon / (double) raceResults.size();
    }

    public void increaseWins() {
        racesWon++;
    }

    public void increaseTimesFallen() {
        timesFallen++;
    }

    public String getUnicodeHorse() {
        return unicodeHorse;
    }

    public void setUnicodeHorse(String unicodeHorse) {
        this.unicodeHorse = unicodeHorse;
    }

    public void fall() {
        this.fallen = true;
        setConfidence(confidence - 0.1);
        increaseTimesFallen();
    }

    public void reset() {
        this.fallen = false;
        this.distance = 0;
        this.hasWon = false;
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

    public int getNumberOfRaces() {
        return this.raceResults.size();
    }

    public void goBackToStart() {
        this.distance = 0;
    }

    public int getRacesWon() {
        return this.racesWon;
    }

    public int getTimesFallen(){
        return timesFallen;
    }

    public boolean hasFallen() {
        return this.fallen;
    }

    public void moveForward() {
        distance++;
    }

    public void setConfidence(double newConfidence) {
        this.confidence = Math.round(newConfidence * 10.0) / 10.0; // Round to one decimal place
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void increaseConfidence() {
        if (this.confidence < 1.0){
            setConfidence(this.confidence + 0.1);
        }
    }

}
