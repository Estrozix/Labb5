import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by estrozix on 1/31/17.
 */
public class CrystalView extends JPanel {

    private BufferedImage image;
    private int size;

    public CrystalView(int size) {
        this.size = size;
        this.setPreferredSize(new Dimension(size, size));
        this.setBackground(Color.BLACK);
        this.setVisible(true);

        this.image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
    }

    private int lastX, lastY = -1;

    public void resetImage() {
        lastX = -1;
        lastY = -1;

        image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        repaint();
    }

    public void updateImage(int x, int y) {
        image.setRGB(x, y, Color.GREEN.getRGB());

        if (lastX != -1 && lastY != -1)
            image.setRGB(lastX, lastY, Color.RED.getRGB());

        lastX = x;
        lastY = y;

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }
}
