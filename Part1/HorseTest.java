package Part1;

public class HorseTest {
    public static void main(String[] args) {
        testAddingHorseToFakeLane();

        testAddingMoreHorsesThanLanes();
    }

    private static void testAddingHorseToFakeLane() {
        Race race = new Race(20);
        Horse horse = new Horse('Z', "", 0.9);
        race.addHorse(horse, 4);
    }

    private static void testAddingMoreHorsesThanLanes() {
        Race race = new Race(20);
        Horse horse1 = new Horse('A', "Storm", 0.8);
        Horse horse2 = new Horse('B', "Blaze", 0.7);
        Horse horse3 = new Horse('C', "Thunder", 0.6);
        Horse horse4 = new Horse('D', "Lightning", 0.5);
        race.addHorse(horse1, 1);
        race.addHorse(horse2, 2);
        race.addHorse(horse3, 3);
        race.addHorse(horse4, 3); 

        race.startRace();
    }
}
