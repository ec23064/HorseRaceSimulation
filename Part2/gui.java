package Part2;

import javax.swing.*;
import java.awt.*;
import Part1.Horse;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

public class gui{
    private JFrame frame;
    private JPanel panel;
    private int trackWidth;
    private int finalWidth;
    private int raceLength;
    private ArrayList<Horse> horses = new ArrayList<>();

    public gui(int distance) {
        this.raceLength = distance;
        this.trackWidth = Math.max(100, 10 * distance);  
        this.finalWidth = Math.max(100, this.trackWidth + 200);
        initializeGUI();
    }
    
    public void addHorse(Horse theHorse, int laneNumber) {
        horses.add(laneNumber - 1, theHorse);
    }

        /**
     * Start the race
     * The horse are brought to the start and
     * then repeatedly moved forward until the 
     * race is finished
     */
    public void startRace() {
        new Thread(() -> {
            boolean finished = false;
    
            for (Horse horse : horses) {
                horse.goBackToStart();
            }
                          
            while (!finished) {
                for (Horse horse : horses) {
                    moveHorse(horse);
                }
    
                SwingUtilities.invokeLater(this::printRace); 
    
                for (Horse horse : horses) {
                    if (raceWonBy(horse)) {
                        finished = true;
                        SwingUtilities.invokeLater(() -> showWinner());
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
        JLabel winnerLabel = null;
        for (Horse horse : horses) {
            if (raceWonBy(horse)) {
                winnerLabel = new JLabel("And the winner is " + horse.getName());
                horse.increaseConfidence();
            }
        }
    
        if (winnerLabel != null) {
            winnerLabel.setBounds(10, panel.getPreferredSize().height, this.finalWidth, 20);
            panel.add(winnerLabel);
            panel.setPreferredSize(new Dimension(this.trackWidth, panel.getPreferredSize().height + 60));
            panel.revalidate();
            panel.repaint();
        }
    }
    

    /**
     * Reset the fallen status of the horses
     * 
     * @param horse1 the first horse
     * @param horse2 the second horse
     * @param horse3 the third horse
     */
    protected void resetFallen(Horse horse1, Horse horse2, Horse horse3){
        horse1.reset();
        horse2.reset();
        horse3.reset();
    }
    
    /**
     * Randomly make a horse move forward or fall depending
     * on its confidence rating
     * A fallen horse cannot move
     * 
     * @param theHorse the horse to be moved
     */
    protected void moveHorse(Horse theHorse)
    {
        //if the horse has fallen it cannot move, 
        //so only run if it has not fallen
        
        if  (!theHorse.hasFallen())
        {
            //the probability that the horse will move forward depends on the confidence;
            if (Math.random() < theHorse.getConfidence())
            {
               theHorse.moveForward();
            }
            
            //the probability that the horse will fall is very small (max is 0.1)
            //but will also will depends exponentially on confidence 
            //so if you double the confidence, the probability that it will fall is *2
            if (Math.random() < (0.1*theHorse.getConfidence()*theHorse.getConfidence()))
            {
                theHorse.fall();
            }
        }
    }
        
    /** 
     * Determines if a horse has won the race
     *
     * @param theHorse The horse we are testing
     * @return true if the horse has won, false otherwise.
     */
    protected boolean raceWonBy(Horse theHorse)
    {
        if (theHorse.getDistanceTravelled() == raceLength)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void initializeGUI() {
        frame = new JFrame("Horse Race Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(this.finalWidth, 300);  
        frame.setLocationRelativeTo(null);

        panel = new JPanel(null);  
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(this.trackWidth, 300));  

        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(this.finalWidth, 300));
        frame.add(scrollPane);

        frame.setVisible(true);
    }

    public void printRace() {
        panel.removeAll(); 

        int yPosition = 30;

        for (int i = 0; i < horses.size(); i++) {
            if (horses.get(i) != null) {
                printLane(horses.get(i), yPosition);
                yPosition += 60;  // Space between lanes
            }
            if (i < horses.size() - 1) {
                JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
                separator.setBounds(10, yPosition, this.trackWidth, 2);
                panel.add(separator);
                yPosition += 2;  // Adjust for the separator
            }
        }

        panel.setPreferredSize(new Dimension(this.trackWidth, yPosition + 30)); 
        panel.revalidate();
        panel.repaint();
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










