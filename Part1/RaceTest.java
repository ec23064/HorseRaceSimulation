package Part1;

public class RaceTest {
    public static void main(String[] args) {
        testRace();
    }

    public static void testRace() {
        Race race = new Race(5);

        Horse horse1 = new Horse('&', "Horse 1", 0.9);
        Horse horse2 = new Horse('#', "Horse 2", 0.1);
        Horse horse3 = new Horse('$', "Horse 3", 0.5);

        race.addHorse(horse1, 1);
        race.addHorse(horse2, 2);
        race.addHorse(horse3, 3);

        race.startRace();
        race.startRace();
    }
}


