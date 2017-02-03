import javax.swing.*;
import java.awt.*;

/**
 * Created by estrozix on 1/30/17.
 */
public class Main extends JFrame {

    private CrystalView crystalView;
    private CrystalModel crystalModel;

    private int size = 600;

    public Main() {
        setTitle("Crystal");
        setSize(new Dimension(size, size));
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        crystalModel = new CrystalModel(size);

        crystalView = new CrystalView(size);
        crystalView.setSize(new Dimension(size, size));

        add(crystalView);

        addListeners();
        run();
    }

    private void addListeners() {
        crystalModel.setUpdateListener(() -> {
            int escRad = crystalModel.getEscapeCircleRadius();

            crystalView.updateImage(crystalModel.getX() + escRad, crystalModel.getY() + escRad);
        });
    }

    private void run() {
        while(crystalModel.crystallizeOneIon());
    }

    public static void main(String[] args) {
        new Main();
    }
}
