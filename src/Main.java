import javax.swing.*;
import java.awt.*;

/**
 * The main class for the program. It basically starts the main thread for Swing and creates the window.
 */
public class Main extends JFrame {

    private CrystalControl crystalControl;

    private int size = 600;

    /**
     * Creates the JFrame and sets some properties, also creates the crystalControl object.
     */
    public Main() {
        this.setTitle("Crystal");
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        crystalControl = new CrystalControl(size);

        this.add(crystalControl);

        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
