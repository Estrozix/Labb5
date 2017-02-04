import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CrystalControl extends JPanel implements Runnable {

    private CrystalModel crystalModel;
    private CrystalView crystalView;

    private JPanel buttonPanel;
    private JButton[] buttons;

    private Thread thread = null;
    private boolean threadOn = false;

    private boolean simulate = false;

    public CrystalControl(int size) {
        this.setSize(new Dimension(size, size));

        crystalModel = new CrystalModel(size);
        crystalView = new CrystalView(size);
        buttonPanel = new JPanel();

        this.setLayout(new BorderLayout());

        add(crystalView, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        addListeners();
        addButtons();

        thread = new Thread(this);
        thread.start();

        threadOn = true;
    }

    public void run() {
        while(threadOn) {
            if (shouldSimulate()) {
                threadOn = crystalModel.crystallizeOneIon();
            }
        }
    }

    private void addListeners() {
        crystalModel.setUpdateListener(() -> {
            int escRad = crystalModel.getEscapeCircleRadius();

            crystalView.updateImage(crystalModel.getX() + escRad, crystalModel.getY() + escRad);
        });
    }

    private synchronized void toggleSimulation() {
        simulate = !simulate;
    }

    private synchronized boolean shouldSimulate() {
        return this.simulate;
    }

    private void addButtons() {
        buttons = new JButton[3];
        buttons[0] = new JButton("Start");
        buttons[1] = new JButton("Change Speed");
        buttons[2] = new JButton("Reset");

        buttons[0].addActionListener(actionEvent -> {
            toggleSimulation();
        });

        buttons[2].addActionListener(actionEvent -> {
            crystalModel.reset();
            crystalView.resetImage();
        });

        this.buttonPanel.add(buttons[0]);
        this.buttonPanel.add(buttons[1]);
        this.buttonPanel.add(buttons[2]);
    }
}
