import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * Created by estrozix on 1/31/17.
 */

/**
 * A JPanel which contains a BufferedImage that will be drawn during a paintComponent call.
 * The image can be translated and zoomed in to with mouse control, the data from which is stored in a corresponding AffineTransform object.
 */
public class CrystalView extends JPanel {

    private BufferedImage image;
    private int size;

    AffineTransform af = new AffineTransform();

    int mousePosX = -1;
    int mousePosY = -1;
    double mouseScrollSpeed = 1.1;

    /**
     * Creates the CrystalView and its BufferedImage and AffineTransform objects.
     * @param size, size of the internal BufferedImage that will be drawn during paintComponent.
     */
    public CrystalView(int size) {
        this.size = size;
        this.setPreferredSize(new Dimension(size, size));
        this.setBackground(Color.BLACK);
        this.setVisible(true);

        this.image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePosX = e.getX();
                mousePosY = e.getY();

            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Cursor cursor = new Cursor(Cursor.MOVE_CURSOR);
                setCursor(cursor);

                if (mousePosX == -1 || mousePosY == -1) {
                    mousePosX = e.getX();
                    mousePosY = e.getY();
                    return;
                }

                //Takes the current zoom level into consideration when translating, such that the mouse stays on the same part of the image when dragged
                try {
                    AffineTransform afInverse = af.createInverse();
                    Point2D.Float distanceChange = new Point2D.Float(-(mousePosX - e.getX()), -(mousePosY - e.getY()));
                    afInverse.deltaTransform(distanceChange, distanceChange);
                    af.translate(distanceChange.x, distanceChange.y);
                } catch (NoninvertibleTransformException e1) {
                    e1.printStackTrace();
                }

                mousePosX = e.getX();
                mousePosY = e.getY();

                validate();
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mousePosX = e.getX();
                mousePosY = e.getY();
            }



            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double scaleChange = Math.pow(mouseScrollSpeed, -e.getWheelRotation());
                zoom(scaleChange);
                directionalZoom(scaleChange,e);
                mousePosX = e.getX();
                mousePosY = e.getY();
                validate();
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e){
                Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
                setCursor(cursor);
            }

        };

        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        addMouseWheelListener(mouse);
    }

    /**
     * Scales the BufferedImage with a specified amount, effectively zooming.
     * <p>
     * Note: this is applied to a separate AffineTransform object, the effect survives a {@link CrystalView#resetImage()} and does not effect the data in the image whatsoever (no information will be lost).
     * @param scaleChange
     */
    private void zoom(double scaleChange) {
        af.scale(scaleChange, scaleChange);
    }

    /**
     * Translates the image during a {@link CrystalView#zoom(double)} such the the mouse position will stay on the same part of the image
     * <p>
     * Note: this is applied to a separate AffineTransform object, the effect survives a {@link CrystalView#resetImage()} and does not effect the data in the image whatsoever (no information will be lost).
     * @param scaleChange
     * @param e
     */
    private void directionalZoom(double scaleChange, MouseWheelEvent e) {
        double offsetX = (scaleChange-1)*(e.getX()-af.getTranslateX());
        double offsetY = (scaleChange-1)*(e.getY()-af.getTranslateY());
        af.translate(-offsetX/af.getScaleX(),-offsetY/af.getScaleY());
    }

    private int lastX, lastY = -1;

    /**
     * Clears the BufferedImage of all content, resetting the image.
     */
    public void resetImage() {
        lastX = -1;
        lastY = -1;

        for (int y = 0; y < size; y++) {
            for(int x = 0; x < size; x++) {
                image.setRGB(x, y, Color.BLACK.getRGB());
            }
        }

        //af =  new AffineTransform();
        repaint();
    }

    /**
     * Paints a pixel on the BufferedImage green, and sets the previously painted dot to red.
     * <p>
     * Note: Has no effect on any other pixels.
     * @param x , x-coordinate of the dot
     * @param y , y-coordinate of the dot
     */
    public void updateImage(int x, int y) {
        image.setRGB(y, x, Color.GREEN.getRGB());

        if (lastX != -1 && lastY != -1)
            image.setRGB(lastY, lastX, Color.RED.getRGB());

        lastX = x;
        lastY = y;

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.transform(af);
        g2d.drawImage(image, 0, 0, null);
    }
}
