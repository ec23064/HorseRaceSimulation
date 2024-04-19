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

    public gui() {
        createHomeScreen();
    }

    private void createHomeScreen() {
        frame = new JFrame("Horse Race Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);

        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BorderLayout());
        homePanel.setBackground(Color.ORANGE);

        JLabel titleLabel = new JLabel("Horse Race Simulation", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        homePanel.add(titleLabel, BorderLayout.NORTH);

        ImageIcon horseRacingImage = new ImageIcon("/Users/jbenisty/Documents/HorseRaceSimulation/Part2/images/racing.jpeg");
        Image scaledImage = horseRacingImage.getImage().getScaledInstance(frame.getWidth() - 100, frame.getHeight() - 100, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel horseRacingLabel = new JLabel(scaledIcon);
        homePanel.add(horseRacingLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addHorseButton = new JButton("Add Horse");
        addHorseButton.addActionListener(e -> addHorse());
        buttonPanel.add(addHorseButton);

        startRaceButton = new JButton("Start Race");
        startRaceButton.setEnabled(false);
        startRaceButton.addActionListener(e -> startRaceSelection());
        buttonPanel.add(startRaceButton);

        viewStatsButton = new JButton("View Statistics");
        viewStatsButton.addActionListener(e -> showStatistics());
        viewStatsButton.setEnabled(false);
        buttonPanel.add(viewStatsButton);

        homePanel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(homePanel);
        frame.setVisible(true);
    }

    public void startRaceSelection() {
        if (horses.size() < 2) {
            JOptionPane.showMessageDialog(frame, "At least two horses are required to start the race.", "Insufficient Horses", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new GridLayout(0, 1));  // Arrange checkboxes in a single column
    
        JCheckBox[] checkboxes = new JCheckBox[horses.size()];
        for (int i = 0; i < horses.size(); i++) {
            checkboxes[i] = new JCheckBox(horses.get(i).getName());
            checkboxes[i].setBackground(new Color(245, 245, 245)); // Light gray background for checkboxes
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
                // After confirming the selection, prompt for betting
                startRace();
            }
        }
    }
    

    private void showBettingPanel() {
        JFrame bettingFrame = new JFrame("Place Your Bets");
        bettingFrame.setSize(400, 300);
        JPanel bettingPanel = new JPanel(new GridLayout(0, 1));
    
        JLabel balanceLabel = new JLabel("Current Balance: $" + String.format("%.2f", betManager.getTotalMoney()));
        bettingPanel.add(balanceLabel);
    
        JComboBox<String> horseCombo = new JComboBox<>();
        JTextField betAmountField = new JTextField(10);
        for (Horse horse : horses) {
            double odds = betManager.calculateOdds(horse, raceLength);
            horseCombo.addItem(horse.getName() + " (Odds: " + String.format("%.2f", odds) + ")");
        }
    
        JButton submitBetButton = new JButton("Submit Bet and Start Race");
        submitBetButton.addActionListener(e -> {
            Horse selectedHorse = horses.get(horseCombo.getSelectedIndex());
            double betAmount = Double.parseDouble(betAmountField.getText());
            double odds = betManager.calculateOdds(selectedHorse, raceLength);
            betManager.placeBet(selectedHorse, betAmount, odds);
            bettingFrame.dispose();
            runRace();
        });
    
        JButton skipBetButton = new JButton("Skip Betting and Start Race");
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

    private void updateUI() {
        startRaceButton.setEnabled(horses.size() >= 2);
        viewStatsButton.setEnabled(!horses.isEmpty());
    }

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

    private void initialiseRaceGUI() {
        panel = new JPanel(null);
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(trackWidth, 300));

        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(finalWidth, 300));
        frame.add(scrollPane);

        updateRaceGUI();
    }

    private void runRace() {
        int raceStartTime = (int) System.currentTimeMillis();
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
    
    private void recordRaceForAll(int duration) {
        selectedHorses.forEach(horse -> {
            horse.addRaceTime(duration);
        });
    }

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

private void showWinner(Horse winner) {
    JLabel winnerLabel = new JLabel("Race finished. " + (winner != null ? "Winner is " + winner.getName() : "No winner"), JLabel.CENTER);
    winnerLabel.setBounds(10, panel.getPreferredSize().height, this.finalWidth, 20);
    panel.add(winnerLabel);

    // Check and display betting results
    if (betManager.hasBets()) {
        betManager.calculatePayouts();
    }

    panel.revalidate();
    panel.repaint();
}

private void showStatistics() {
    JFrame statsFrame = new JFrame("Horse Race Statistics");
    statsFrame.setSize(600, 400);
    statsFrame.setLocationRelativeTo(frame); 
    statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    // Styling the table to make it look more appealing
    String[] columns = {"Name", "Races", "Wins", "Times Fallen", "Average Speed", "Win Ratio"};
    DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    JTable statsTable = new JTable(model);
    statsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    statsTable.setRowHeight(25);
    statsTable.setBackground(new Color(245, 245, 245)); 
    statsTable.setForeground(Color.DARK_GRAY);
    statsTable.setGridColor(new Color(234, 234, 234)); 
    statsTable.getTableHeader().setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
    statsTable.getTableHeader().setBackground(Color.BLACK);
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


    protected boolean raceWonBy(Horse theHorse) {
        return theHorse.getDistanceTravelled() >= raceLength;
    }

    protected void moveHorse(Horse theHorse) {
        if (!theHorse.hasFallen()) {
            if (Math.random() < theHorse.getConfidence()) {
                theHorse.moveForward();
            }
            if (Math.random() < (0.1 * theHorse.getConfidence() * theHorse.getConfidence())) {
                theHorse.fall();
            }
        }
    }

    public void printRace() {
        panel.removeAll();
    
        int yPosition = 30;
        for (Horse horse : selectedHorses) {
            printLane(horse, yPosition);
            yPosition += 60; 
        }
    
        panel.setPreferredSize(new Dimension(trackWidth, yPosition));
        panel.revalidate();
        panel.repaint();
    }
    

    protected void printLane(Horse theHorse, int yPos) {
        int horsePosition = (int) ((double) theHorse.getDistanceTravelled() / raceLength * trackWidth);

        // Create and add the lane label
        JLabel laneLabel = new JLabel();
        laneLabel.setBounds(10, yPos, trackWidth, 50);
        laneLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        laneLabel.setBackground(Color.LIGHT_GRAY);
        laneLabel.setOpaque(true);
        panel.add(laneLabel);

        // Create and add the name label
        JLabel nameLabel = new JLabel(theHorse.getName() + " (Confidence: " + theHorse.getConfidence() + ")");
        nameLabel.setBounds(trackWidth + 15, yPos, 200, 50);
        panel.add(nameLabel);

        // Determine the symbol to display
        String symbolDisplay;
        if (theHorse.hasFallen()) {
            symbolDisplay = "<html><font color='red'>X</font></html>";
        } else {
            symbolDisplay = String.valueOf(theHorse.getUnicodeHorse());
        }

        // Create and add the horse symbol label
        JLabel horseLabel = new JLabel(symbolDisplay);
        horseLabel.setBounds(10 + horsePosition, yPos + 5, 40, 40);
        horseLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(horseLabel);

        // Ensure horse label is on top
        panel.setComponentZOrder(horseLabel, 0);
    }

    private void updateRaceGUI() {
        int laneHeight = 60;
        int baseHeight = 100;
        int width = finalWidth;

        int totalHeight = selectedHorses.size() * laneHeight + baseHeight;

        // Update the panel and scroll pane sizes
        panel.setPreferredSize(new Dimension(width - 50, selectedHorses.size() * laneHeight));
        panel.revalidate();
        panel.repaint();

        frame.setSize(width, Math.max(300, totalHeight));
        frame.revalidate();
    }

    public static void main(String[] args) {
        new gui();
    }
}
