package Part2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class gui {
    private JFrame frame;
    private JPanel panel;
    private JButton startRaceButton, viewStatsButton;
    private ArrayList<Horse> horses = new ArrayList<>();
    private ArrayList<Horse> selectedHorses = new ArrayList<>();
    private int raceLength;
    private int trackWidth;
    private int finalWidth;
    private BetManager betManager = new BetManager();
    private String weather;

    // Constructor to initialize the GUI application.
    public gui() {
        createHomeScreen();
    }
    
    // Creates and displays the initial screen of the application.
    private void createHomeScreen() {
        frame = new JFrame("Horse Race Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);

        Font titleFont = new Font("Segoe UI Semibold", Font.BOLD, 24);
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 14);

        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BorderLayout());
        homePanel.setBackground(Color.ORANGE);

        JLabel titleLabel = new JLabel("Horse Race Simulation", JLabel.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(0, 102, 204));
        homePanel.add(titleLabel, BorderLayout.NORTH);

        ImageIcon horseRacingImage = new ImageIcon("Part2/images/racing.jpeg");
        Image scaledImage = horseRacingImage.getImage().getScaledInstance(frame.getWidth() - 100, frame.getHeight() - 100, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel horseRacingLabel = new JLabel(scaledIcon);
        homePanel.add(horseRacingLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addHorseButton = new JButton("Add Horse");
        addHorseButton.setFont(buttonFont);
        addHorseButton.addActionListener(e -> addHorse());
        buttonPanel.add(addHorseButton);

        startRaceButton = new JButton("Start Race");
        startRaceButton.setFont(buttonFont);
        startRaceButton.setEnabled(false);
        startRaceButton.addActionListener(e -> startRaceSelection());
        buttonPanel.add(startRaceButton);

        viewStatsButton = new JButton("View Statistics");
        viewStatsButton.setFont(buttonFont);
        viewStatsButton.addActionListener(e -> showStatistics());
        viewStatsButton.setEnabled(false);
        buttonPanel.add(viewStatsButton);

        homePanel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(homePanel);
        frame.setVisible(true);
    }


    // Displays the horse selection panel for initiating a race.
    public void startRaceSelection() {
        if (horses.size() < 2) {
            JOptionPane.showMessageDialog(frame, "At least two horses are required to start the race.", "Insufficient Horses", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new GridLayout(0, 1));  

        JCheckBox[] checkboxes = new JCheckBox[horses.size()];
        for (int i = 0; i < horses.size(); i++) {
            checkboxes[i] = new JCheckBox(horses.get(i).getName());
            checkboxes[i].setBackground(new Color(245, 245, 245)); 
            checkboxes[i].setFont(new Font("SansSerif", Font.BOLD, 12));
            checkboxPanel.add(checkboxes[i]);
        }

        // Scroll pane in case of many horses
        JScrollPane scrollPane = new JScrollPane(checkboxPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scrollPane.setPreferredSize(new Dimension(250, 150));

        int option = JOptionPane.showOptionDialog(frame, scrollPane, "Select horses to race",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                new Object[]{"Confirm Selection", "Cancel"}, "Cancel");

        if (option == 0) {
            selectedHorses.clear();
            for (JCheckBox checkbox : checkboxes) {
                if (checkbox.isSelected()) {
                    selectedHorses.add(horses.stream().filter(h -> h.getName().equals(checkbox.getText())).findFirst().orElse(null));
                }
            }

            if (selectedHorses.size() < 2) {
                JOptionPane.showMessageDialog(frame, "Please select at least two horses to start the race.", "Selection Error", JOptionPane.ERROR_MESSAGE);
            } else {
                startRace();
            }
        }
    }

    // Displays the betting panel where users can place their bets.
    private void showBettingPanel() {
        JFrame bettingFrame = new JFrame("Place Your Bets");
        bettingFrame.setSize(400, 300);
        JPanel bettingPanel = new JPanel(new GridLayout(0, 1));
        bettingPanel.setBackground(new Color(230, 230, 250));  
    
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, 14);
    
        JLabel balanceLabel = new JLabel("Current Balance: $" + String.format("%.2f", betManager.getTotalMoney()));
        balanceLabel.setFont(labelFont);
        bettingPanel.add(balanceLabel);
    
        JComboBox<String> horseCombo = new JComboBox<>();
        JTextField betAmountField = new JTextField(10);
        for (Horse horse : horses) {
            double odds = betManager.calculateOdds(horse, raceLength);
            horseCombo.addItem(horse.getName() + " (Odds: " + String.format("%.2f", odds) + ")");
        }
    
        JButton submitBetButton = new JButton("Submit Bet and Start Race");
        submitBetButton.setFont(buttonFont);
        submitBetButton.addActionListener(e -> {
            Horse selectedHorse = horses.get(horseCombo.getSelectedIndex());
            double betAmount = Double.parseDouble(betAmountField.getText());
            double odds = betManager.calculateOdds(selectedHorse, raceLength);
            betManager.placeBet(selectedHorse, betAmount, odds);
            bettingFrame.dispose();
            runRace();
        });
    
        JButton skipBetButton = new JButton("Skip Betting and Start Race");
        skipBetButton.setFont(buttonFont);
        skipBetButton.addActionListener(e -> {
            bettingFrame.dispose();
            runRace();
        });
    
        bettingPanel.add(new JLabel("Select Horse and View Odds:"));
        bettingPanel.add(horseCombo);
        bettingPanel.add(new JLabel("Bet Amount:"));
        bettingPanel.add(betAmountField);
        bettingPanel.add(submitBetButton);
        bettingPanel.add(skipBetButton);
    
        bettingFrame.add(bettingPanel);
        bettingFrame.setVisible(true);
    }
    

    // Allows users to add a new horse to the race.
    private void addHorse() {
        String[] symbols = {"\uD83D\uDC0E", "\uD83C\uDFA0", "\uD83C\uDFC7", "\uD83D\uDC34"};
        String selectedSymbol = (String) JOptionPane.showInputDialog(frame, "Select a symbol for the horse:", "Add Horse", JOptionPane.QUESTION_MESSAGE, null, symbols, symbols[0]);
        if (selectedSymbol == null) {
            return;
        }
        String name = JOptionPane.showInputDialog(frame, "Enter the name of the horse:");
        if (name == null || name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Invalid input for name. Please enter a non-empty value.");
            return;
        }
        double confidence;
        try {
            confidence = Double.parseDouble(JOptionPane.showInputDialog(frame, "Enter the confidence rating of the horse (0.0 - 1.0):"));
            if (confidence < 0.1 || confidence > 0.9) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid input for confidence. Please enter a valid number between 0.1 and 0.9.");
            return;
        }

        Horse horse = new Horse(selectedSymbol, name, confidence);
        horses.add(horse);
        updateUI();
    }

    // Updates the UI based on the current state, enabling or disabling buttons as needed.
    private void updateUI() {
        startRaceButton.setEnabled(horses.size() >= 2);
        viewStatsButton.setEnabled(!horses.isEmpty());
    }

    // Initializes and begins the race.
    private void startRace() {
        String distanceInput = JOptionPane.showInputDialog(frame, "Enter the race distance:");
        if (distanceInput == null || distanceInput.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Invalid input for race distance. Please enter a non-empty value.");
            return;
        }
        int distance;
        try {
            distance = Integer.parseInt(distanceInput);
            if (distance <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid input for race distance. Please enter a valid positive integer.");
            return;
        }

        this.raceLength = distance;
        this.trackWidth = Math.max(100, 10 * distance);
        this.finalWidth = Math.max(100, this.trackWidth + 200);

        frame.getContentPane().removeAll();
        initialiseRaceGUI();
        frame.revalidate();
        frame.repaint();
        showBettingPanel();
    }

    // Sets up the race GUI, preparing the visual components for the race, including weather display.
    private void initialiseRaceGUI() {
        panel = new JPanel(null);
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(trackWidth, 300));

        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(finalWidth, 300));
        frame.add(scrollPane);

        updateRaceGUI();
    }

    // Generates a string representation of the current weather with an emoji.
    private String getWeatherDisplay() {
        String weatherEmoji;
        switch (weather) {
            case "Rainy":
                weatherEmoji = "🌧️ Rainy";
                break;
            case "Foggy":
                weatherEmoji = "🌫️ Foggy";
                break;
            case "Sunny":
                weatherEmoji = "☀️ Sunny";
                break;
            default:
                weatherEmoji = "❓ Unknown";
                break;
        }
        return "Weather: " + weatherEmoji;
    }


    // Manages the actual running of the race, updating horse positions and handling race completion.
    private void runRace() {
        int raceStartTime = (int) System.currentTimeMillis();
        selectWeather();
        // Adding a weather display at the top of the panel

        new Thread(() -> {
            boolean raceFinished = false;

            while (!raceFinished) {
                for (Horse horse : selectedHorses) {
                    if (!horse.hasFallen() && horse.getDistanceTravelled() < raceLength) {
                        moveHorse(horse);
                        SwingUtilities.invokeLater(this::printRace);
                        if (horse.getDistanceTravelled() >= raceLength) {
                            int raceEndTime = (int) System.currentTimeMillis();
                            recordRaceForAll(raceEndTime - raceStartTime);
                            horse.hasWon();
                            horse.increaseWins();
                            horse.increaseConfidence();
                            horse.hasWon();
                            betManager.calculatePayouts();
                            raceFinished = true;
                            SwingUtilities.invokeLater(() -> {
                                showWinner(horse);
                                addBackButton();
                                viewStatsButton.setEnabled(true);
                            });
                            break;
                        }
                    }
                }

                // Check if all horses have fallen and no one can finish the race
                if (!raceFinished && selectedHorses.stream().allMatch(Horse::hasFallen)) {
                    int raceEndTime = (int) System.currentTimeMillis();
                    raceFinished = true;
                    recordRaceForAll(raceEndTime - raceStartTime);
                    SwingUtilities.invokeLater(this::addBackButton);
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    // Records the duration of the race for all participating horses.
    private void recordRaceForAll(int duration) {
        selectedHorses.forEach(horse -> {
            horse.addRaceTime(duration);
        });
    }

    // Adds a "Back to Home" button to the GUI, allowing users to return to the main screen.
    private void addBackButton() {
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            for (Horse horse : selectedHorses) {
                horse.reset();
            }
            createHomeScreen();
            updateUI();
            frame.revalidate();
            frame.repaint();
        });

        JPanel backPanel = new JPanel();
        backPanel.add(backButton);

        frame.getContentPane().add(backPanel, BorderLayout.SOUTH);

        frame.revalidate();
        frame.repaint();
    }

    // Displays the winner of the race and any betting results.
    private void showWinner(Horse winner) {
        JLabel winnerLabel = new JLabel("Race finished. " + (winner != null ? "Winner is " + winner.getName() : "No winner"), JLabel.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        winnerLabel.setForeground(new Color(34, 139, 34)); // Forest green color for the winner text
        winnerLabel.setBounds(10, panel.getPreferredSize().height, this.finalWidth, 20);
        panel.add(winnerLabel);

        // Check and display betting results
        if (betManager.hasBets()) {
            betManager.calculatePayouts();
        }

        panel.revalidate();
        panel.repaint();
    }

    // Displays statistics about all horses in a new window.
    private void showStatistics() {
        JFrame statsFrame = new JFrame("Horse Race Statistics");
        statsFrame.setSize(600, 400);
        statsFrame.setLocationRelativeTo(frame); 
        statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
        Font headerFont = new Font("Segoe UI Semibold", Font.BOLD, 16);
        Font dataFont = new Font("Segoe UI", Font.PLAIN, 14);
    
        String[] columns = {"Name", "Races", "Wins", "Times Fallen", "Average Speed", "Win Ratio"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    
        JTable statsTable = new JTable(model);
        statsTable.setFont(dataFont);
        statsTable.setRowHeight(25);
        statsTable.setBackground(new Color(245, 245, 245));
        statsTable.setForeground(Color.DARK_GRAY);
        statsTable.setGridColor(new Color(234, 234, 234));
        statsTable.getTableHeader().setFont(headerFont);
        statsTable.getTableHeader().setBackground(Color.GRAY);
        statsTable.getTableHeader().setForeground(Color.WHITE);
    
        for (Horse horse : horses) {
            Object[] row = {
                horse.getName(),
                horse.getNumberOfRaces(),
                horse.getRacesWon(),
                horse.getTimesFallen(),
                String.format("%.2f", horse.getAverageSpeed()),
                String.format("%.2f%%", horse.getWinRatio() * 100)
            };
            model.addRow(row);
        }
        JScrollPane scrollPane = new JScrollPane(statsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        statsFrame.add(scrollPane, BorderLayout.CENTER);
    
        statsFrame.setVisible(true);
    }
    

    // Determines if a horse has won the race based on its distance traveled.
    protected boolean raceWonBy(Horse theHorse) {
        return theHorse.getDistanceTravelled() >= raceLength;
    }

    // Moves the horse forward in the race, simulating progress based on its confidence level and weather conditions.
    protected void moveHorse(Horse theHorse) {
        if (!theHorse.hasFallen()) {
            double weatherModifier = getWeatherModifier(); 
            if (Math.random() < theHorse.getConfidence() * weatherModifier) { 
                theHorse.moveForward();
            }
            if (Math.random() < (0.1 * theHorse.getConfidence() * theHorse.getConfidence() * weatherModifier)) {
                theHorse.fall();
            }
        }
    }

    // Calculate the modifier for horse movement based on the current weather.
    private double getWeatherModifier() {
        switch (weather) {
            case "Rainy":
                return 1.2;
            case "Foggy":
                return 0.8;
            default:
                return 1.0; 
        }
    }

    // Updates the display of the race progress visually on the GUI.
    private void printRace() {
        panel.removeAll();

    // Adding a weather display at the top of the panel
    JLabel weatherLabel = new JLabel(getWeatherDisplay(), JLabel.CENTER);
    weatherLabel.setFont(new Font("Arial", Font.BOLD, 16));
    weatherLabel.setOpaque(true); 
    weatherLabel.setBackground(Color.BLACK); 
    weatherLabel.setForeground(Color.WHITE); 
    weatherLabel.setBounds(10, 5, finalWidth - 20, 30); 
    panel.add(weatherLabel);

        
        int yPosition = 50;
        for (Horse horse : selectedHorses) {
            printLane(horse, yPosition);
            yPosition += 70;
        }
    
        panel.setPreferredSize(new Dimension(trackWidth, yPosition));
        panel.revalidate();
        panel.repaint();
    }

    // Prints the visual representation of each horse's lane during the race.
    protected void printLane(Horse theHorse, int yPos) {
        int horsePosition = (int) ((double) theHorse.getDistanceTravelled() / raceLength * trackWidth);
        horsePosition = Math.min(horsePosition, trackWidth - 30);
    
        // Create and add the lane label
        JLabel laneLabel = new JLabel();
        laneLabel.setBounds(10, yPos, trackWidth, 60); 
        laneLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        laneLabel.setBackground(new Color(245, 245, 220)); 
        laneLabel.setOpaque(true);
        panel.add(laneLabel);
    
        // Create and add the name and confidence label
        JLabel nameLabel = new JLabel(theHorse.getName() + " (Confidence: " + String.format("%.1f", theHorse.getConfidence()) + ")");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.DARK_GRAY);
        nameLabel.setBounds(trackWidth + 20, yPos + 10, 200, 40); 
        panel.add(nameLabel);
    
        // Determine the symbol to display
        String symbolDisplay = theHorse.hasFallen() ? "<html><font color='red'>🚑</font></html>" : String.valueOf(theHorse.getUnicodeHorse());
    
        // Create and add the horse symbol label
        JLabel horseLabel = new JLabel(symbolDisplay);
        horseLabel.setBounds(10 + horsePosition, yPos + 10, 40, 40);
        horseLabel.setFont(new Font("Arial", Font.BOLD, 30)); 
        panel.add(horseLabel);
    
        // Ensure horse label is on top
        panel.setComponentZOrder(horseLabel, 0);
    }
    

    // Updates the GUI dimensions based on the race conditions.
    private void updateRaceGUI() {
        int laneHeight = 70;
        int baseHeight = 200; 
    
        int totalHeight = selectedHorses.size() * laneHeight + baseHeight;
        panel.setPreferredSize(new Dimension(trackWidth, totalHeight));
    
        panel.revalidate();
        panel.repaint();
    
        frame.setSize(Math.max(500, finalWidth), Math.max(300, totalHeight));
        frame.revalidate();
    }
    

    // Randomly selects the weather from rainy, foggy, and sunny
    private void selectWeather() {
        String[] weatherOptions = {"Rainy", "Foggy", "Sunny"};
        int randomIndex = (int) (Math.random() * weatherOptions.length);
        this.weather = weatherOptions[randomIndex];
    }

    public static void main(String[] args) {
        new gui();
    }
}
