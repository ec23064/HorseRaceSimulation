package Part2;

import javax.swing.*;
import java.awt.*;
import Part1.Horse;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class gui {
    private JFrame frame;
    private JPanel panel; 
    private JButton startRaceButton;
    private ArrayList<Horse> horses = new ArrayList<>();
    private int raceLength;
    private int trackWidth;
    private int finalWidth;

    public gui(int distance) {
        this.raceLength = distance;
        this.trackWidth = Math.max(100, 10 * distance);
        this.finalWidth = Math.max(100, this.trackWidth + 200);
        createHomeScreen();
    }

    private void createHomeScreen() {
        frame = new JFrame("Horse Race Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);

        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Horse Race Simulation", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        homePanel.add(titleLabel, BorderLayout.NORTH);

        JButton addHorseButton = new JButton("Add Horse");
        addHorseButton.addActionListener(e -> addHorse());
        homePanel.add(addHorseButton, BorderLayout.CENTER);

        startRaceButton = new JButton("Start Race");
        startRaceButton.setEnabled(false); 
        startRaceButton.addActionListener(e -> startRace());
        homePanel.add(startRaceButton, BorderLayout.SOUTH);

        frame.add(homePanel);
        frame.setVisible(true);
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
        frame.getContentPane().removeAll();
        initializeRaceGUI();
        frame.revalidate();
        frame.repaint();
        runRace();
    }

    private void initializeRaceGUI() {
        panel = new JPanel(null);
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(trackWidth, 300));

        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(finalWidth, 300));
        frame.add(scrollPane);
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
        JLabel nameLabel = new JLabel(theHorse.getName() + " (Confidence: " + String.format("%.2f", theHorse.getConfidence()) + ")");
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
    
    

    public static void main(String[] args) {
        new gui(20);  
    }
}
