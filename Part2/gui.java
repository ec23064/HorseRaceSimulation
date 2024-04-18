package Part2;

import javax.swing.*;
import java.awt.*;
import Part1.Horse;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.net.URL;

public class gui {
    private JFrame frame;
    private JPanel panel; 
    private JButton startRaceButton;
    private ArrayList<Horse> horses = new ArrayList<>();
    private int raceLength;
    private int trackWidth;
    private int finalWidth;

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
    
        JLabel titleLabel = new JLabel("Horse Race Simulation", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        homePanel.add(titleLabel, BorderLayout.NORTH);

        ImageIcon horseRacingImage = new ImageIcon("/Users/jbenisty/Documents/HorseRaceSimulation/Part2/images/racing.jpeg");
        Image scaledImage = horseRacingImage.getImage().getScaledInstance(frame.getWidth() - 100, frame.getHeight() - 100, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel horseRacingLabel = new JLabel(scaledIcon);
        homePanel.add(horseRacingLabel, BorderLayout.CENTER);

        // Sub-panel for buttons
        JPanel buttonPanel = new JPanel();
        JButton addHorseButton = new JButton("Add Horse");
        addHorseButton.addActionListener(e -> addHorse());
        buttonPanel.add(addHorseButton);
    
        startRaceButton = new JButton("Start Race");
        startRaceButton.setEnabled(false); 
        startRaceButton.addActionListener(e -> startRace());
        buttonPanel.add(startRaceButton);
    
        JButton editHorsesButton = new JButton("Edit Horses");
        editHorsesButton.addActionListener(e -> editHorses());
        buttonPanel.add(editHorsesButton);
    
        homePanel.add(buttonPanel, BorderLayout.SOUTH); 
    
        frame.add(homePanel);
        frame.setVisible(true);
    }
    

    private void editHorses() {
        if (horses.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No horses in the race. Add horses first.");
            return;
        }
        String[] horseNames = new String[horses.size()];
        for (int i = 0; i < horses.size(); i++) {
            horseNames[i] = horses.get(i).getName();
        }
        String selectedHorse = (String) JOptionPane.showInputDialog(frame, "Select a horse to edit:", "Edit Horses", JOptionPane.QUESTION_MESSAGE, null, horseNames, horseNames[0]);
        if (selectedHorse != null) {
            for (Horse horse : horses) {
                if (horse.getName().equals(selectedHorse)) {
                    editHorse(horse);
                    break;
                }
            }
        }
    }



    private void editHorse(Horse horse) {
        String symbol = JOptionPane.showInputDialog(frame, "Enter the new symbol for the horse:", horse.getSymbol());
        if (symbol == null || symbol.isEmpty() || symbol.length() > 1) {
            JOptionPane.showMessageDialog(frame, "Invalid input for symbol.");
            return;
        }
        String name = JOptionPane.showInputDialog(frame, "Enter the new name of the horse:", horse.getName());
        if (name == null || name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Invalid input for name. Please enter a non-empty value.");
            return;
        }
        double confidence;
        try {
            confidence = Double.parseDouble(JOptionPane.showInputDialog(frame, "Enter the new confidence rating of the horse (0.0 - 1.0):", horse.getConfidence()));
            if (confidence < 0.1 || confidence > 0.9) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid input for confidence. Please enter a valid number between 0.1 and 0.9.");
            return;
        }
        horse.setSymbol(symbol.charAt(0));
        horse.setName(name);
        horse.setConfidence(confidence);
        updateUI();
    }

    private void addHorse() {
        String symbol = JOptionPane.showInputDialog(frame, "Enter the symbol for the horse:");
        if (symbol == null || symbol.isEmpty() || symbol.length() > 1) {
            JOptionPane.showMessageDialog(frame, "Invalid input for symbol.");
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

        Horse horse = new Horse(symbol.charAt(0), name, confidence);
        horses.add(horse);
        updateUI();  
    }

    private void updateUI() {
        startRaceButton.setEnabled(horses.size() >= 2);
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
        runRace();
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
        new Thread(() -> {
            boolean finished = false;

            while (!finished) {
                for (Horse horse : horses) {
                    moveHorse(horse);
                }

                SwingUtilities.invokeLater(this::printRace);

                for (Horse horse : horses) {
                    if (raceWonBy(horse)) {
                        finished = true;
                        SwingUtilities.invokeLater(this::showWinner);
                    }
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private void showWinner() {
        for (Horse horse : horses) {
            if (raceWonBy(horse)) {
                JLabel winnerLabel = new JLabel("And the winner is " + horse.getName(), JLabel.CENTER);
                winnerLabel.setBounds(10, panel.getPreferredSize().height, this.finalWidth, 20);
                panel.add(winnerLabel);
                horse.increaseConfidence();
                panel.setPreferredSize(new Dimension(trackWidth, panel.getPreferredSize().height + 60));
                panel.revalidate();
                panel.repaint();
                break;
            }
        }
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

        for (Horse horse : horses) {
            printLane(horse, yPosition);
            yPosition += 60;  // Space between lanes
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
            symbolDisplay = String.valueOf(theHorse.getSymbol());
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

    int totalHeight = horses.size() * laneHeight + baseHeight;

    // Update the panel and scroll pane sizes
    panel.setPreferredSize(new Dimension(width - 50, horses.size() * laneHeight));
    panel.revalidate();
    panel.repaint();

    frame.setSize(width, Math.max(300, totalHeight));
    frame.revalidate();
}

    

    public static void main(String[] args) {
        new gui();  
    }
}
