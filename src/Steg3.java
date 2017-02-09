import javax.swing.*;
import java.awt.*;

/**
 * The main class for the program. It basically starts the main thread for Swing and creates the window.
 */
public class Steg3 extends JFrame {

    private CrystalControl crystalControl;

    public static int size = 600;

    /**
     * Creates the JFrame and sets some properties, also creates the crystalControl object.
     */
    public Steg3() {
        this.setTitle("Crystal");
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        crystalControl = new CrystalControl(size);

        this.add(crystalControl);

        pack();
    }

    private static void setSize(int size) {
        if(size < 0) {
            throw new IllegalArgumentException("Size cannot be negative!");
        }
        Steg3.size = size;
    }

    public static void main(String[] args) {
        boolean ready = false;
        boolean setNew = false;

        String message = "";

        while(!ready) {
            try {
                if(setNew) {
                    setNew = false;
                    setSize(Integer.parseInt(JOptionPane.showInputDialog(message)));
                } else {
                    setSize(Integer.parseInt(args[0]));
                }

                ready = true;
            } catch (ArrayIndexOutOfBoundsException e) {
                setNew = true;
                message = "No argument for size found! Please enter the size of the crystal:";
            } catch (IllegalArgumentException e) {
                setNew = true;
                message = "Incorrect value for the size! Please enter the size of the crystal:";
            }
        }
        SwingUtilities.invokeLater(Steg3::new);
    }
}
