import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

/**
 * A controller that connects a CrystalModel and CrystalView. Note that it runs the crystallizing and
 * drawing on a separate thread to avoid interruptions from user input. It also includes control
 * for the user input.
 */

public class CrystalControl extends JPanel implements Runnable {

    private CrystalModel crystalModel;
    private CrystalView crystalView;

    private JPanel buttonPanel;
    private JButton[] buttons;

    private Thread thread = null;
    private boolean threadOn = false;
    private boolean simulate = false;
    private long sleepTime = 0;

    /**
     * Constructor which takes only one value, the size of the JPanel.
     * It will create all the related objects and set the layout and size.
     * Furthermore it will also create the separate thread which will run the
     * crystallization.
     * @param size The size of the JPanel.
     */
    public CrystalControl(int size) {
        this.setSize(new Dimension(size, size));

        crystalModel = new CrystalModel(size);
        crystalView = new CrystalView(size);
        buttonPanel = new JPanel();

        this.setLayout(new BorderLayout());

        add(crystalView, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addListeners();
        addButtons();
        thread = new Thread(this);
        thread.start();

        threadOn = true;
    }

    /**
     * Running the separate thread for the crystallization with a short sleep between each
     * iteration according to the set sleep-time (note, this is set by the user using
     * the slider in the UI)
     */
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

    /**
     * Adding the customized listeners for the crystalModel. This is set so that the CrystalModel can notify
     * the Controller without a direct reference to the controller. This helps reduce clustering in the code
     * and makes the communication between the objects more clear.
     */
    private void addListeners() {
        crystalModel.setUpdateListener(() -> {
            int escRad = crystalModel.getEscapeCircleRadius();

            crystalView.updateImage(crystalModel.getX() + escRad + 4, escRad - crystalModel.getY() + 4);
        });
    }

    /**
     * Toggles the simulation on or off. Note, this method is set to synchronized for good practice since multiple
     * threads are implemented, however this might be unnecessary since only one of the threads in the program
     * calls this method in its current state.
     */
    private synchronized void toggleSimulation() {
        simulate = !simulate;
    }

    /**
     * Getter for the simulate boolean. This is to check whether the other thread should simulate or not.
     * This is added to prevent the crystallizing-thread from locking the simulate-variable.
     * @return A boolean value for the variable simulate.
     */
    private synchronized boolean shouldSimulate() {
        return this.simulate;
    }

    /**
     * Returns the amount of milliseconds to sleep between each iteration. This is set to synchronized
     * for the same reason as toggleSimulation().
     * @return A long value of the sleepTime (milliseconds).
     */
    private synchronized long getSleepTime() {
        return this.sleepTime;
    }

    /**
     * Sets the sleepTime in milliseconds.
     * @param sleepTime The sleeptime in milliseconds.
     */
    private synchronized void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     * Creates and adds the buttons and sliders to the user interface, also adds the corresponding listeners.
     */
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
        this.buttonPanel.add(buttons[0]);
        this.buttonPanel.add(buttons[2]);

        JSlider speedPicker = new JSlider(0,30,0);
        speedPicker.addChangeListener((ChangeEvent e) -> setSleepTime((long)Math.pow(10,((double)((JSlider)(e.getSource())).getValue())/10)-1)); //Kan vara lite overkill med exponentialfunktionen, om så önskas kan detta bytas mot endast getvalue
        buttonPanel.add(speedPicker);
    }
}
