# HorseRaceSimulation

This Java application simulates a horse race where users can add horses, place bets, and view the race's progress. It's designed to showcase object-oriented programming principles and GUI interaction using Swing.

### Prerequisites

You need to have Java Development Kit (JDK) installed on your machine. The application was developed using JDK 11, but it should be compatible with other versions that support Swing. Download JDK from [Oracle's Official Site](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).

### Installing

1. Clone the repository or download the ZIP file.
2. Compile the files in an IDE of your choice.


### Running the Application

## Part 1

To run Part 1 do the following in a seperate class:
1. In a method create a new object Race and the desired distance in the paramaters
2. Create 3 objects of Horse which contain in the parameters the symbol, name and confidence rating from 0-1.
3. Add the horse objects to the race with the following method call race.addHorse() with the name of the horse object and desired lane from 1-3 in the parameters
4. Do this for all 3 horses and fill up teh 3 lanes.
5. Finally call race.startRace() to run a race

## Part 2

To run part 2 all you have to do is execute the gui class.
This will open the GUI where you can interact with the application.

## Usage

- **Add Horse**: This button allows you to add a new horse to the race. You will need to provide a name, confidence level, and select an emoji for your horse.
- **View Statistics**: View detailed statistics and outcomes of previous races, including wins, losses, and betting history.
- **Start Race**: Once two or more horses are added, you can start the race. Before the race begins, you can place bets on horses.
- **Distance**: Once you click start race you will be asked to enter the distance of the race.
- **Betting**: After that you will be redirected to a betting screen where you may place a bet with your balance that you can see at the top of the screen based on the odds.
- **Race**: Finally the race will run and when finished click back to go back to the home screen.

### Author
Joshua Isaac Benisty Belilty
