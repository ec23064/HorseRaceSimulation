package Part2;

import javax.swing.*;
import java.awt.*;
import Part1.Horse;
import Part1.Race;

public class gui extends Race {
    private JFrame frame;
    private JPanel panel;
    private int trackWidth;  // Track width as a class member

    public gui(int distance) {
        super(distance);
        this.trackWidth = Math.max(100, 10 * distance);  // Calculate track width based on distance
        initializeGUI();
    }

    private void initializeGUI() {
        int finalWidth = Math.max(100, this.trackWidth + 200);  // Ensure frame is at least 800 or wider based on trackWidth

        frame = new JFrame("Horse Race Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(finalWidth, 300);  // Use finalWidth to size the frame
        frame.setLocationRelativeTo(null);

        panel = new JPanel(null);  // Use null layout for absolute positioning
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(this.trackWidth, 300));  // Set panel width to trackWidth

        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(finalWidth, 300));
        frame.add(scrollPane);

        frame.setVisible(true);
    }

    @Override
    public void printRace() {
        SwingUtilities.invokeLater(() -> {
            panel.removeAll(); 

            Horse[] horses = {lane1Horse, lane2Horse, lane3Horse};
            int yPosition = 30;

            for (int i = 0; i < horses.length; i++) {
                if (horses[i] != null) {
                    printLane(horses[i], yPosition);
                    yPosition += 60;  // Space between lanes
                }
                if (i < horses.length - 1) {
                    JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
                    separator.setBounds(10, yPosition, this.trackWidth, 2);
                    panel.add(separator);
                    yPosition += 2;  // Adjust for the separator
                }
            }

            panel.setPreferredSize(new Dimension(this.trackWidth, yPosition + 30)); 
            panel.revalidate();
            panel.repaint();
        });
    }

    protected void printLane(Horse theHorse, int yPos) {
        int horsePosition = (int) ((double) theHorse.getDistanceTravelled() / raceLength * this.trackWidth);

        JLabel laneLabel = new JLabel();
        laneLabel.setBounds(10, yPos, this.trackWidth, 50);
        laneLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        laneLabel.setOpaque(true);
        laneLabel.setBackground(Color.LIGHT_GRAY);

        JLabel nameLabel = new JLabel(theHorse.getName() + " (Confidence: " + String.format("%.2f", theHorse.getConfidence()) + ")");
        nameLabel.setBounds(10 + this.trackWidth + 10, yPos, 200, 50);

        JLabel horseLabel = new JLabel();
        horseLabel.setBounds(10 + horsePosition, yPos, 50, 50);
        String symbol = theHorse.hasFallen() ? "<html><font color='red'>X</font></html>" : Character.toString(theHorse.getSymbol());
        horseLabel.setText(symbol);

        panel.add(nameLabel);
        panel.add(horseLabel);
        panel.add(laneLabel);
    }

    public static void main(String[] args) {
        gui raceGUI = new gui(50);
        Horse horse1 = new Horse('&', "Horse 1", 0.8);
        Horse horse2 = new Horse('#', "Horse 2", 0.1);
        Horse horse3 = new Horse('$', "Horse 3", 0.5);

        raceGUI.addHorse(horse1, 1);
        raceGUI.addHorse(horse2, 2);
        raceGUI.addHorse(horse3, 3);
        
        raceGUI.startRace();
        
    }
}










