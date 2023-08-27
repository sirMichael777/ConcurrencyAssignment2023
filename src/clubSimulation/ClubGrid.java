// Grid for the club

package clubSimulation;

import java.util.concurrent.Semaphore;

// This class represents the club as a grid of GridBlocks
public class ClubGrid {
    private GridBlock[][] Blocks;
    private Semaphore entranceSemaphore = new Semaphore(1); // Semaphore for entrance door
    private Semaphore exitSemaphore = new Semaphore(1);

    private Semaphore maxInsideSemaphore;
    private final int x;
    private final int y;
    public final int bar_y;

    private GridBlock exit;
    private GridBlock entrance; // Hardcoded entrance
    private final static int minX = 5; // Minimum x dimension
    private final static int minY = 5; // Minimum y dimension

    private PeopleCounter counter;

    // Constructor
    ClubGrid(int x, int y, int[] exitBlocks, PeopleCounter c, int max) throws InterruptedException {
        if (x < minX)
            x = minX; // Ensure minimum x dimension
        if (y < minY)
            y = minY; // Ensure minimum y dimension
        this.maxInsideSemaphore = new Semaphore(max);
        this.x = x;
        this.y = y;
        this.bar_y = y - 3; // Y coordinate for the bar area
        Blocks = new GridBlock[x][y];
        this.initGrid(exitBlocks);
        entrance = Blocks[getMaxX() / 2][0]; // Entrance is in the middle of the top row
        counter = c;
    }

    // Initialize the grid by creating all the GridBlocks
    private void initGrid(int[] exitBlocks) throws InterruptedException {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                boolean exit_block = false;
                boolean bar = false;
                boolean dance_block = false;
                if ((i == exitBlocks[0]) && (j == exitBlocks[1])) {
                    exit_block = true;
                } else if (j >= (y - 3))
                    bar = true; // Blocks in the bar area
                else if ((i > x / 2) && (j > 3) && (j < (y - 5)))
                    dance_block = true; // Blocks where people can dance
                Blocks[i][j] = new GridBlock(i, j, exit_block, bar, dance_block);
                if (exit_block) {
                    this.exit = Blocks[i][j];
                }
            }
        }
    }

    // Get the maximum X dimension of the grid
    public int getMaxX() {
        return x;
    }

    // Get the maximum Y dimension of the grid
    public int getMaxY() {
        return y;
    }

    // Get the entrance GridBlock
    public GridBlock whereEntrance() {
        return entrance;
    }

    // Check if given coordinates are within the grid
    public boolean inGrid(int i, int j) {
        if ((i >= x) || (j >= y) || (i < 0) || (j < 0))
            return false;
        return true;
    }

    // Check if given coordinates are within the patron area
    public boolean inPatronArea(int i, int j) {
        if ((i >= x) || (j > bar_y) || (i < 0) || (j < 0))
            return false;
        return true;
    }

    // Method to handle a patron entering the club
    public GridBlock enterClub(PeopleLocation myLocation) throws InterruptedException {
        counter.personArrived(); // Increment counter of people waiting
        entranceSemaphore.acquire();
        maxInsideSemaphore.acquire();
        entranceSemaphore.release();
        entrance.get(myLocation.getID());
        counter.personEntered(); // Increment counter of people inside
        myLocation.setLocation(entrance);
        myLocation.setInRoom(true);
        return entrance;
    }

    // Method to move a patron to a new GridBlock
    public GridBlock move(GridBlock currentBlock, int step_x, int step_y, PeopleLocation myLocation)
            throws InterruptedException {
        int c_x = currentBlock.getX();
        int c_y = currentBlock.getY();
        int new_x = c_x + step_x; // New x coordinate after movement
        int new_y = c_y + step_y; // New y coordinate after movement

        // Restrict movement within the patron area
        if (!inPatronArea(new_x, new_y)) {
            // Invalid move outside - ignore
            return currentBlock;
        }

        if ((new_x == currentBlock.getX()) && (new_y == currentBlock.getY()))
            return currentBlock; // Not actually moving

        GridBlock newBlock = Blocks[new_x][new_y];

        if (!newBlock.get(myLocation.getID()))
            return currentBlock; // Stay in the current block

        currentBlock.release(); // Release current block
        myLocation.setLocation(newBlock);
        return newBlock;
    }

    // Method to handle a patron leaving the club
    public void leaveClub(GridBlock currentBlock, PeopleLocation myLocation) throws InterruptedException {
        exitSemaphore.acquire();
        currentBlock.release();
        maxInsideSemaphore.release();
        counter.personLeft(); // Decrement counter of people inside
        myLocation.setInRoom(false);
        exitSemaphore.release();
        entrance.notifyAll(); // Notify waiting threads that entrance is available
    }

    // Get the exit GridBlock
    public GridBlock getExit() {
        return exit;
    }

    // Get the corresponding GridBlock for given coordinates
    public GridBlock whichBlock(int xPos, int yPos) {
        if (inGrid(xPos, yPos)) {
            return Blocks[xPos][yPos];
        }
        System.out.println("Block " + xPos + " " + yPos + " not found");
        return null;
    }

    // Set the exit GridBlock
    public void setExit(GridBlock exit) {
        this.exit = exit;
    }

    // Get the Y coordinate for the bar area
    public int getBar_y() {
        return bar_y;
    }
}
