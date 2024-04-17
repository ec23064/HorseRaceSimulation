package Part2;

import javax.swing.*;
import java.awt.*;
import Part1.Horse;
import Part1.Race;

public class gui extends Race {
    private JFrame frame;
    private JPanel panel;

    public gui(int distance) {
        super(distance);
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Horse Race Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        panel = new JPanel(null); 
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(750, 300));

        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        frame.add(scrollPane);

        frame.setVisible(true);
    }

    @Override
    public void printRace() {
        panel.removeAll(); 

        Horse[] horses = {lane1Horse, lane2Horse, lane3Horse}; 
        int yPosition = 30;

        for (int i = 0; i < horses.length; i++) {
            if (horses[i] != null) {
                printLane(horses[i], yPosition);
                yPosition += 60; 
            }
            // Add a separator between lanes
            if (i < horses.length - 1) {
                JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
                separator.setBounds(10, yPosition, 700, 2);
                panel.add(separator);
                yPosition += 2; 
            }
        }

        panel.setPreferredSize(new Dimension(750, yPosition + 30)); 
        panel.revalidate();
        panel.repaint();
    }

    protected void printLane(Horse theHorse, int yPos) {
        int trackWidth = 700; // Width of the race track
        int horsePosition = (int) ((double) theHorse.getDistanceTravelled() / raceLength * trackWidth);

        JLabel laneLabel = new JLabel();
        laneLabel.setBounds(10, yPos, trackWidth + 40, 50);
        laneLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
        laneLabel.setOpaque(true);
        laneLabel.setBackground(Color.LIGHT_GRAY);
        laneLabel.setForeground(Color.BLACK);
        
        JLabel horseLabel = new JLabel();
        horseLabel.setBounds(10 + horsePosition, yPos, 50, 50);
        String symbol = theHorse.hasFallen() ? "<html><font color='red'>X</font></html>" : Character.toString(theHorse.getSymbol());
        horseLabel.setText(symbol);

        panel.add(horseLabel);
        panel.add(laneLabel);
    }

    public static void main(String[] args) {
        gui raceGUI = new gui(20);
        Horse horse1 = new Horse('&', "Horse 1", 0.8);
        Horse horse2 = new Horse('#', "Horse 2", 0.1);
        Horse horse3 = new Horse('$', "Horse 3", 0.5);

        raceGUI.addHorse(horse1, 1);
        raceGUI.addHorse(horse2, 2);
        raceGUI.addHorse(horse3, 3);
        
        raceGUI.startRace();
        
    }
}
