package clubSimulation;


import static clubSimulation.ClubSimulation.tallys;
import static clubSimulation.Clubgoer.club;

public class Barman extends Thread {
    private PeopleLocation BarmanLoc;

    private boolean isMovingRight= false;

    protected GridBlock currentGridBlock;
    private int ID;
    private int speed;

    private boolean movingRight = true; // Initial direction

    Barman(int Id, PeopleLocation Loc, int S) {
        this.ID = Id;
        this.BarmanLoc = new PeopleLocation(Id);
        this.speed = S;
    }
    public   int getX() { return currentGridBlock.getX();}

    //getter
    public   int getY() {	return currentGridBlock.getY();	}

    @Override
    public void run() {

        try {
            while (tallys.getInside()>0) {
                moveBarman();
            }

    } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void moveBarman() throws InterruptedException {
        if (movingRight) {
            for (int x = 0; x < club.getMaxX(); x++) {
                club.move(currentGridBlock,x, club.bar_y,BarmanLoc);
                Thread.sleep(speed/2); // Adjust the delay as needed
            }
        } else {
            for (int x = club.getMaxX(); x >= 0; x--) {
                club.move(currentGridBlock,x, club.bar_y,BarmanLoc);
                Thread.sleep(speed/2); // Adjust the delay as needed
            }
        }
        movingRight = !movingRight; // Reverse direction
    }


    public static void serve(Clubgoer groovist){
        groovist.thirsty=false;
    }
}