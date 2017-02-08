import java.util.Random;

/**
 * CrystalModel är en klass som representerar ett elektrolytbad som omges med en
 * cirkulär anod av zink, och i vars mitt en kolkatod är placerad. Därefter läggs
 * en svag spänning över systemet.
 */

public class CrystalModel {

    private ModelUpdateListener modelUpdateListener;

    // variabler
    private int escapeCircleRadius; // radius of escape circle
    private int startCircleRadius; // radius of start circle

    private int currentRadius = 5;
    // the bath

    private boolean[][] modelRep; // (model Representation) is the bolean matxix

    // position of walking ion in our coordinate system, the bath,
    // where 0,0 is in the middle
    private int x = 0;  // xBath
    private int y = 0;  // yBath

    private int size;

    /**
     * Skapar en modell av kristallbadet (elektrolytbadet).
     * @param size Kristallbadets bredd

     */
    public CrystalModel(int size) {
        this.size = size;
        escapeCircleRadius = size / 2 - 4; //(-4 to awoid indexOutOfBounds)
        startCircleRadius = escapeCircleRadius - (int)(0.1 * escapeCircleRadius);
        reset();
    }

    /**
     * Getter for the x-coordinate (in bath-coordinates).
     * @return An int value representing the x-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Getter for the y-coordinate (in bath-coordinates).
     * @return An int value representing the y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Getter for the escape circle radius.
     * @return An int value representing the escape circle radius.
     */
    public int getEscapeCircleRadius() {
        return this.escapeCircleRadius;
    }

    /**
     * Sets the modelUpdateListener for this specific object
     * @param modelUpdateListener A ModelUpdateListener
     */
    public void setUpdateListener(ModelUpdateListener modelUpdateListener) {
        this.modelUpdateListener = modelUpdateListener;
    }

    /**
     * Kontrollera om det finns en kristalliserad jon på position x,y.
     * @param x koordinaten
     * @param y koordinaten
     * @return "true" om det finns en kristalliserad jon på position x,y.
     */
    public boolean getModelValue(int x, int y) {
        return modelRep[yBathToModelRep(y)][xBathToModelRep(x)];
    }

    /**
     * Släpper en ny jon och flyttar jonen ett steg åt gången tills den
     * kristalliseras. Kommer den utanför flyktcirkeln så släpps en ny jon.
     * @return "false" när kristallen är klar (dvs när sista jonen kristalliseras
     * på startcirkeln) och "true" om vi kan kristallisera fler joner
     */
    public boolean crystallizeOneIon() {
        dropNewIon();

        Random random = new Random();

        while (!getModelValue(x, y)) {
            /*try {
                Thread.sleep(500);

            } catch(InterruptedException e) {}*/

            if (anyNeighbours(x, y)) {
                //System.out.println("Neighbour found, I'm at " + x + " " + y);
                modelRep[yBathToModelRep(y)][xBathToModelRep(x)] = true;

                double rad = Math.sqrt(x * x + y * y);
                int margin = 3;

                if (rad + margin > currentRadius)
                    currentRadius = (int)rad + margin;

                //System.out.println(currentRadius + " " + startCircleRadius);

                if (this.modelUpdateListener != null) modelUpdateListener.update();

                if (outsideCicle(startCircleRadius, x, y)) {
                    return false;
                }
                //System.out.println(this);
                return true;
            }

            if (outsideCicle(escapeCircleRadius, x, y)) {
                return true;
            }

            switch (random.nextInt(4)) {
                case 0:
                    x++;
                    break;
                case 1:
                    x--;
                    break;
                case 2:
                    y++;
                    break;
                case 3:
                    y--;
                    break;
                default:
                    break;
            }

            //System.out.println("Current Position: " + x + " " + y);
        }
        return false;
    }

    /**
     * A function running the method crystallizeOneIon() "steps" times.
     * @param steps Int value of how many times you want to run the function.
     * @return Returns true when all steps have been made.
     */
    public boolean runSomeSteps(int steps) {
        int i= 0;
        boolean goOn = false;

        do {
            goOn = crystallizeOneIon();
            i++;
        } while (i < steps && goOn);

        return goOn; // we are done
    }

    /**
     * Initierar modellen (dvs matrisen) och lägger en första kristalliserad jon mitt i "badet".
     */
    public void reset() {
        x = 0;
        y = 0;

        modelRep = new boolean[size][size];
        modelRep[yBathToModelRep(0)][xBathToModelRep(0)] = true;

        if(modelUpdateListener != null) modelUpdateListener.update();

        currentRadius = 5;
    }

    /**
     * Kollar om position x,y är utanför (eller på) cirkeln med radie r.
     * Använder pytagoras sats.
     * @param r Cirkelns radie.
     * @param x koordinaten
     * @param y koordinaten
     * @return "true" om positionen är utanför cirkeln
     */
    public static boolean outsideCicle(int r, int x, int y) {
        double distance = Math.sqrt(x * x + y * y);
        return distance >= r;
    }

    /**
     * Returns the crystals state i.e. a string according to figure 3 i labPM.
     * x and y is the position of the ion in the bath
     * @return A string that draws the crystal.
     */
    public String toString() {
        int x = getX(); // the ions position in the bath
        int y = getY();

        int size = getEscapeCircleRadius();
        StringBuffer s = new StringBuffer(1000);

        for(int i = -size - 1; i < size + 1; i++) {
            s.append("-");
        }

        s.append("\n");
        for(int i= -size; i < size; i++) {
            s.append("|");
            for(int j= -size; j < size; j++) {
                if (getModelValue(i, j)) {
                    if (i == x && j == y) {
                        s.append("#");
                    } else {
                        s.append("*");
                    }
                } else {
                    s.append(" ");
                }
            }
            s.append("|");
            s.append("\n");
        }
        for(int i = -size-1; i < size+1; i++) {
            s.append("-");
        }
        s.append("\n");
        return s.toString();
    }


    /**
     * Släpper en jon på startcirkeln (dvs slumpar fram en ny punkt x,y på startcirkeln).
     */
    private void dropNewIon() {
        Random random = new Random();

        int angle = random.nextInt(360);

        x = (int) (currentRadius * Math.cos(Math.toRadians(angle)));
        y = (int) (currentRadius * Math.sin(Math.toRadians(angle)));
        //System.out.println("Dropping new at " + x + " " + y);
    }

    /**
     * Omvandlar en "bad"-kordinat till ett matris värde.
     * All access till matrisen måste transformeras i.e. 0,0 -> size/2, size/2
     * @param x "bad"-koordinat som ska omvandlas
     * @return motsvarande x-koordinat iför matrisen
     */
    private int xBathToModelRep(int x) {
        return x + escapeCircleRadius + 4;
    }

    /**
     * Omvandlar en "bad"-kordinat till ett matris värde.
     * All access till matrisen måste transformeras i.e. 0,0 -> size/2, size/2
     * @param y "bad"-koordinat som ska omvandlas
     * @return motsvarande y-koordinat iför matrisen
     */
    private int yBathToModelRep(int y) {
        return escapeCircleRadius -y + 4;
    }

    /**
     * Kollar om jonen på position x,y har några grannar som kristalliserats.
     * @param x koordinaten
     * @param y koordinaten
     * @return "true" om jonen har några grannar som kristalliserats
     */
    private boolean anyNeighbours(int x, int y) {
        if(getModelValue(x, y + 1)) return true;
        if(getModelValue(x, y - 1)) return true;
        if(getModelValue(x + 1, y)) return true;
        if(getModelValue(x - 1, y)) return true;

        return false;
    }

}