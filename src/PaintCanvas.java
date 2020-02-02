import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PaintCanvas extends Canvas implements MouseInputListener {
    public static final int MODE_DRAW = 0;
    public static final int MODE_LINE = 1;
    public static final int MODE_TRIANGLE = 2;

    private int canvasWidth;
    private int canvasHeight;
    private Color BGColor;

    private BufferedImage bufferedImage;
    private Graphics2D g2d;

    private Color strokeColor;
    private int strokeThickness;

    private Point currentPos = null;
    private Point lastPos = null;
    private int mode;

    public PaintCanvas(int width, int height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
        this.BGColor = Color.WHITE;
        this.bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB); // 描画内容を保持するBufferedImageを生成
        this.g2d = (Graphics2D) bufferedImage.getGraphics();
        this.setStrokeColor("Black");
        this.setStrokeThickness("Regular");
        this.mode = MODE_DRAW;

        this.setBackground(BGColor);
        this.setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        g2d.setColor(BGColor); //BufferedImageの背景も白にする
        g2d.fillRect(0, 0, canvasWidth, canvasHeight);

        repaint(); //描画
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(bufferedImage, 0, 0, null); //描画内容をキャンバスにも反映
    }

    @Override
    public void update(Graphics g){
        paint(g);
    }

    public void drawLine(Point pos1, Point pos2){
        g2d.drawLine((int)pos1.getX(), (int)pos1.getY(), (int)pos2.getX(), (int)pos2.getY());
    }

    public void drawTriangle(Point pos1, Point pos2, Point pos3){
        drawLine(pos1, pos2);
        drawLine(pos2, pos3);
        drawLine(pos3, pos1);
    }

    public void clear() {
        g2d.setColor(BGColor);
        g2d.fillRect(0, 0, canvasWidth, canvasHeight);
        repaint();
    }

    public void clearPos(){
        lastPos = null;
        currentPos = null;
    }

    public Color parseColor(String colorName){
        Color color = Color.BLACK;
        switch(colorName){
            case "Black":
                color = Color.BLACK;
                break;

            case "Red":
                color = Color.RED;
                break;

            case "Blue":
                color = Color.BLUE;
                break;

            case "Yellow":
                color = Color.YELLOW;
                break;

            case "Green":
                color = Color.GREEN;
                break;
        }
        return color;
    }

    public int parseThickness(String thicknessName){
        int thickness = 3;
        switch (thicknessName){
            case "Thin":
                thickness = 1;
                break;

            case "Regular":
                thickness = 3;
                break;

            case "Thick":
                thickness = 5;
                break;
        }
        return thickness;
    }

    public void setStrokeColor(Color color) {
        this.strokeColor = color;
    }

    public void setStrokeColor(String color) {
        setStrokeColor(parseColor(color));
    }

    public void setStrokeThickness(int thickness) {
        this.strokeThickness = thickness;
    }

    public void setStrokeThickness(String thickness) {
        setStrokeThickness(parseThickness(thickness));
    }

    public void setCurrentPos(Point pos) {
        this.lastPos = currentPos;
        this.currentPos = pos;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (mode){
            case MODE_DRAW:
                break;

            case MODE_LINE:
                if(currentPos != null){
                    drawLine(currentPos, e.getPoint());
                    clearPos();
                }else {
                    setCurrentPos(e.getPoint());
                }
                break;

            case MODE_TRIANGLE:
                if(currentPos != null && lastPos != null){
                    drawTriangle(lastPos, currentPos, e.getPoint());
                    clearPos();
                }else {
                    setCurrentPos(e.getPoint());
                }
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(mode == MODE_DRAW){
            clearPos();
            g2d.fillOval(e.getX(), e.getY(), 2, 2);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mode == MODE_DRAW){
            setCurrentPos(e.getPoint());

            if (currentPos != null && lastPos != null) {
                BasicStroke stroke = new BasicStroke(strokeThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                g2d.setStroke(stroke);
                g2d.setColor(strokeColor);
                this.drawLine(lastPos, currentPos);
            }
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
