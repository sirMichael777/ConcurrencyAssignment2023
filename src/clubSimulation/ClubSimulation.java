// Main class for the club simulation

package clubSimulation;

// Import necessary libraries
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.*;

// Import the club package
import static clubSimulation.Clubgoer.club;

// Main class that starts the simulation
public class ClubSimulation {
    // Simulation parameters
    static int noClubgoers = 10;
    static int frameX = 400;
    static int frameY = 500;
    static int yLimit = 400;
    static int gridX = 15; // Number of x grids in the club - default value if not provided on command line
    static int gridY = 15; // Number of y grids in the club - default value if not provided on command line
    static int max = 5; // Max number of customers - default value if not provided on command line

    // Arrays and objects for the simulation
    static Clubgoer[] patrons; // Array for customer threads
    static PeopleLocation[] peopleLocations; // Array to keep track of where customers are
    static PeopleCounter tallys; // Counters for the number of people inside and outside the club
    static Barman barman;
    static ClubView clubView; // Threaded panel to display terrain
    static ClubGrid clubGrid; // Club grid
    static CounterDisplay counterDisplay; // Threaded display of counters

    private static int maxWait = 1200; // For the slowest customer
    private static int minWait = 500; // For the fastest customer

    // Method to set up the GUI
    public static void setupGUI(int frameX, int frameY, int[] exits) {
        // Initialize the GUI frame
        JFrame frame = new JFrame("Club Animation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(frameX, frameY);

        // Create a panel for GUI components
        JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS));
        g.setSize(frameX, frameY);

        // Initialize the club view and add it to the panel
        clubView = new ClubView(peopleLocations, clubGrid, exits);
        clubView.setSize(frameX, frameY);
        g.add(clubView);

        // Create a panel for displaying counters
        JPanel txt = new JPanel();
        txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS));
        JLabel maxAllowed = new JLabel("Max: " + tallys.getMax() + "    ");
        JLabel caught = new JLabel("Inside: " + tallys.getInside() + "    ");
        JLabel missed = new JLabel("Waiting:" + tallys.getWaiting() + "    ");
        JLabel scr = new JLabel("Left club:" + tallys.getLeft() + "    ");
        txt.add(maxAllowed);
        txt.add(caught);
        txt.add(missed);
        txt.add(scr);
        g.add(txt);
        counterDisplay = new CounterDisplay(caught, missed, scr, tallys); // Thread to update score

        // Create buttons for controlling the simulation
        JPanel b = new JPanel();
        b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));
        JButton startB = new JButton("Start");

        // ActionListener for the "Start" button
        startB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Set simulationStarted to true for all patrons
                for (Clubgoer patron : patrons) {
                    patron.simulationStarted = true;
                }
            }
        });

        final JButton pauseB = new JButton("Pause ");

        // ActionListener for the "Pause" button
        pauseB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Toggle the pause status of the Clubgoer threads
                Clubgoer.togglePause();

                // Signal threads to pause or resume
                synchronized (Clubgoer.pauseMonitor) {
                    Clubgoer.pauseMonitor.notifyAll(); // Notify waiting threads to resume
                }
            }
        });

        JButton endB = new JButton("Quit");

        // ActionListener for the "Quit" button
        endB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Exit the program
            }
        });

        b.add(startB);
        b.add(pauseB);
        b.add(endB);

        g.add(b);

        frame.setLocationRelativeTo(null); // Center the window on the screen
        frame.add(g); // Add contents to the window
        frame.setContentPane(g);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException {
        // Deal with command line arguments if provided
        if (args.length == 4) {
            noClubgoers = Integer.parseInt(args[0]);
            gridX = Integer.parseInt(args[1]);
            gridY = Integer.parseInt(args[2]);
            max = Integer.parseInt(args[3]);
        }

        // Hardcoded exit doors
        int[] exit = { 0, (int) gridY / 2 - 1 };

        // Initialize counters and grid
        tallys = new PeopleCounter(max);
        clubGrid = new ClubGrid(gridX, gridY, exit, tallys, max);
        club = clubGrid;

        // Initialize arrays and objects
        peopleLocations = new PeopleLocation[noClubgoers];
        patrons = new Clubgoer[noClubgoers];

        Random rand = new Random();

        // Create Clubgoer threads
        for (int i = 0; i < noClubgoers; i++) {
            peopleLocations[i] = new PeopleLocation(i);
            int movingSpeed = (int) (Math.random() * (maxWait - minWait) + minWait);
            patrons[i] = new Clubgoer(i, peopleLocations[i], movingSpeed);
        }

        int movingSpeed = (int) (Math.random() * (maxWait - minWait) + minWait);
        barman = new Barman(-1, new PeopleLocation(-1), movingSpeed);

        // Setup GUI and start threads
        setupGUI(frameX, frameY, exit);
        Thread t = new Thread(clubView);
        t.start();
        Thread s = new Thread(counterDisplay);
        s.start();
        Thread b = new Thread(barman);
        b.start();
        for (int i = 0; i < noClubgoers; i++) {
            patrons[i].start();
        }
    }
}

