import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private CrystalControl crystalControl;

    private int size = 600;

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
