import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

public class CrystalControl extends JPanel implements Runnable {

    private CrystalModel crystalModel;
    private CrystalView crystalView;

    private JPanel buttonPanel;
    private JButton[] buttons;

    private Thread thread = null;
    private boolean threadOn = false;
    private boolean simulate = false;
    private long sleepTime = 0;

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
                crystalModel.crystallizeOneIon();
                try {
                    Thread.sleep(getSleepTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

    private synchronized long getSleepTime() {
        return this.sleepTime;
    }

    private synchronized void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    private void addButtons() {
        buttons = new JButton[3];
        buttons[0] = new JButton("Start/Pause");
        buttons[2] = new JButton("Reset");

        buttons[0].addActionListener(actionEvent -> toggleSimulation());

        /*
        buttons[1].addActionListener(actionEvent -> {
            String response = JOptionPane.showInputDialog("Set sleep time between iteration (milliseconds): ", null);
            setSleepTime(Long.parseLong(response));
        });
        */
        buttons[2].addActionListener(actionEvent -> {
            crystalModel.reset();
            crystalView.resetImage();
        });
        JSlider speedPicker = new JSlider(0,30,0);
        speedPicker.addChangeListener((ChangeEvent e) -> setSleepTime((long)Math.pow(10,((double)((JSlider)(e.getSource())).getValue())/10)-1)); //Kan vara lite overkill med exponentialfunktionen, om så önskas kan detta bytas mot endast getvalue
        this.buttonPanel.add(buttons[0]);
        this.buttonPanel.add(buttons[2]);
        buttonPanel.add(speedPicker);
    }
}
