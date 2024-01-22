/* Authors: Andy Sun & Merrick Lu
   Date: December 19-January 22, 2024
   Project: "Chip Masters": GUI Poker
   Slider GUI, drawn in GameGUI when raise is pressed
 */
import java.awt.*;
import java.awt.event.*;

public class Slider extends Rectangle {

    public Rectangle range;//range over which the knob can slide

    public Rectangle errorMargin;
    public static final int KNOB_DIAMETER = 20;
    public static final int RANGE_WIDTH = 200;

    public int low, high;

    public double sliderPercent;

    //constructor creates ball at given location with given dimensions
    public Slider(int x, int y, int rangex){
        super(x, y, KNOB_DIAMETER, KNOB_DIAMETER);
        range = new Rectangle(rangex, y+KNOB_DIAMETER/5, RANGE_WIDTH, 10);
        errorMargin = new Rectangle(rangex-15, y+KNOB_DIAMETER/5-15, RANGE_WIDTH+30, 40);
        sliderPercent = 0;
        low = 0;
        high = 0;
    }

    public void setRange(int lowNum, int highNum) {
        low = lowNum;
        high = highNum;
    }


    //called from GamePanel whenever a mouse click is detected
    //changes the current location of the ball to be wherever the mouse is located on the screen
    public void mousePressed(MouseEvent e){
        if(errorMargin.contains(e.getX(), e.getY())) {
            x = keepInBounds(e.getX());
        }
    }


    public void mouseDragged(MouseEvent e) {
        if(errorMargin.contains(e.getX(), e.getY())) {
            x = keepInBounds(e.getX());
        }

    }

    public int keepInBounds(int i) {
        if(i<range.x){
            return range.x;
        }
        if(i>range.x+RANGE_WIDTH-KNOB_DIAMETER) {
            return range.x+RANGE_WIDTH-KNOB_DIAMETER;
        }
        return i;
    }

    //called frequently from the GamePanel class
    //draws the current location of the ball to the screen
    public void draw(Graphics g){
        g.setColor(Color.white);
        g.drawString(""+getSliderNum(), x, range.y-30);
        g.fillRoundRect(range.x, range.y, range.width, range.height, 10, 10);
        g.setColor(Color.RED);
        g.fillOval(x, y, KNOB_DIAMETER, KNOB_DIAMETER);
    }

    public double getSliderPercent() {
        sliderPercent = (double) (x - range.x) /(RANGE_WIDTH-KNOB_DIAMETER);
        return sliderPercent;
    }

    public int getSliderNum() {
        return (int)(low+(high-low)*(getSliderPercent()));
    }

}