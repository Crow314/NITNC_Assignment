import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PaintCanvas extends Canvas implements MouseInputListener {
    public static final int MODE_PEN = 0;
    public static final int MODE_LINE = 1;
    public static final int MODE_TRIANGLE = 2;
    public static final int MODE_CHARACTER = 3;
    public static final int MODE_ERASER = 4;
    public static final int MODE_MAX = MODE_ERASER;

    private int canvasWidth;
    private int canvasHeight;
    private Color BGColor;

    private BufferedImage bufferedImage;
    private Graphics2D g2d;
    private CharacterInputFrame characterInputFrame;
    private ColorPickerFrame colorPickerFrame;

    private Color strokeColor;
    private int strokeThickness;
    private boolean rainbowColor;

    private Point currentPos = null;
    private Point lastPos = null;
    private int mode;

    public PaintCanvas(int width, int height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
        this.BGColor = Color.WHITE;
        this.bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB); // 描画内容を保持するBufferedImageを生成
        this.g2d = (Graphics2D) bufferedImage.getGraphics();
        this.characterInputFrame = new CharacterInputFrame(this);
        this.colorPickerFrame = new ColorPickerFrame(this);
        this.setStrokeColor("Black");
        this.setStrokeThickness("Regular");
        this.rainbowColor = false;
        this.mode = MODE_PEN;

        this.setBackground(BGColor);
        this.setPreferredSize(new Dimension(canvasWidth, canvasHeight));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        g2d.setFont(new Font("Yu Gothic", Font.PLAIN, 80));
        clear();

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
        drawLine(pos1, pos2, strokeColor);
    }

    public void drawLine(Point pos1, Point pos2, Color color){
        BasicStroke stroke = new BasicStroke(strokeThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2d.setStroke(stroke);
        g2d.setColor(color);
        g2d.drawLine((int)pos1.getX(), (int)pos1.getY(), (int)pos2.getX(), (int)pos2.getY());
        repaint();
    }

    public void drawTriangle(Point pos1, Point pos2, Point pos3){
        drawLine(pos1, pos2);
        drawLine(pos2, pos3);
        drawLine(pos3, pos1);
    }

    public void drawText(Point pos, String text){
        g2d.drawString(text, (int)pos.getX(), (int)pos.getY());
        repaint();
    }

    public void showColorPicker(int mode){
        colorPickerFrame.setMode(mode);
        colorPickerFrame.setVisible(true);
    }

    public void clear() {
        g2d.setColor(BGColor);
        g2d.fillRect(0, 0, canvasWidth, canvasHeight);
        g2d.setColor(strokeColor);
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

    public int parseMode(String modeName){
        int mode = 0;
        switch (modeName){
            case "Pen":
                mode = MODE_PEN;
                break;

            case "Line":
                mode = MODE_LINE;
                break;

            case "Triangle":
                mode = MODE_TRIANGLE;
                break;

            case "Character":
                mode = MODE_CHARACTER;
                break;

            case "Eraser":
                mode = MODE_ERASER;
                break;

        }
        return mode;
    }

    public void setStrokeColor(Color color) {
        this.strokeColor = color;
    }

    public void setStrokeColor(String color) {
        switch (color){
            case "Rainbow":
                rainbowColor = true;
                setStrokeColor(Color.getHSBColor(0.0F, 1.0F, 1.0F));
                break;

            case "Select":
                showColorPicker(ColorPickerFrame.MODE_STROKE_COLOR);
                break;

            default:
                rainbowColor = false;
                setStrokeColor(parseColor(color));
        }
    }

    public void setStrokeThickness(int thickness) {
        if(thickness > 0){
            this.strokeThickness = thickness;
        }
    }

    public void setStrokeThickness(String thickness) {
        setStrokeThickness(parseThickness(thickness));
    }

    public void setMode(int mode){
        if(mode >= 0 && mode <= MODE_MAX){
            this.mode = mode;
        }
        clearPos();
    }

    public void setMode(String mode){
        setMode(parseMode(mode));
    }

    public void setCurrentPos(Point pos) {
        this.lastPos = currentPos;
        this.currentPos = (Point) pos.clone();
    }

    public void setBGColor(Color BGColor) {
        this.BGColor = BGColor;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch (mode) {
            case MODE_PEN:
            case MODE_ERASER:
                clearPos();
                g2d.fillOval(e.getX(), e.getY(), 2, 2);
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (mode){

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

            case MODE_CHARACTER:
                characterInputFrame.setPosition(e.getPoint());
                characterInputFrame.setVisible(true);
                break;
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        switch (mode){
            case MODE_PEN:
                setCurrentPos(e.getPoint());

                if (currentPos != null && lastPos != null) {
                    drawLine(lastPos, currentPos);
                }

                if (rainbowColor){
                    float[] hsb = Color.RGBtoHSB(strokeColor.getRed(), strokeColor.getGreen(), strokeColor.getBlue(), null);
                    setStrokeColor(Color.getHSBColor(hsb[0] + 0.01F, hsb[1], hsb[2]));
                }
                break;

            case MODE_ERASER:
                setCurrentPos(e.getPoint());

                if (currentPos != null && lastPos != null) {
                    drawLine(lastPos, currentPos, BGColor);
                }
                break;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
