import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class Paused {
    private int hoveredOption = -1;
    private Image BG_IMAGE;
    private Rectangle[] optionRects = new Rectangle[3];
    private int width = GamePanel.GAME_WIDTH, height = GamePanel.GAME_HEIGHT;
    private final int TEXT_SIZE = 40;
    public Paused() {
        BG_IMAGE = new ImageIcon("images/settingsBG.jpg").getImage();

        // X button
        optionRects[0] = new Rectangle((int)(width*0.87), (int) (height*0.05), (int)(TEXT_SIZE*1.2), (int)(TEXT_SIZE*1.2));
        optionRects[1] = new Rectangle(0, (int)(height*0.5-TEXT_SIZE*0.7), width, (int)(TEXT_SIZE*1.4));
        optionRects[2] = new Rectangle(0, (int)(height*0.7-TEXT_SIZE*0.7), width, (int)(TEXT_SIZE*1.4));
    }

    public void draw(Graphics g) {
        g.drawImage(BG_IMAGE, 0, 0, width, height, null);

        g.setColor(Color.white);
        drawCenteredString(g, "Paused", optionRects[1], (int)(height*0.2), new Font("Arial", Font.PLAIN, (int)(TEXT_SIZE*2)));

        if(hoveredOption == 0) g.setColor(Color.red);
        else g.setColor(Color.white);
        drawCenteredString(g, "X", optionRects[0], (int)(height*0.13), new Font("Arial", Font.PLAIN, TEXT_SIZE));
        g.drawRect(optionRects[0].x,optionRects[0].y,optionRects[0].width,optionRects[0].height);

        if(hoveredOption == 1) g.setColor(Color.red);
        else g.setColor(Color.white);
        drawCenteredString(g, "Exit to Main Menu", optionRects[1], (int)(height*0.53), new Font("Arial", Font.PLAIN, TEXT_SIZE));

        if(hoveredOption == 2) g.setColor(Color.red);
        else g.setColor(Color.white);
        drawCenteredString(g, "Settings", optionRects[1], (int)(height*0.73), new Font("Arial", Font.PLAIN, TEXT_SIZE));
    }

    public void drawCenteredString(Graphics g, String text, Rectangle rect, int y, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Draw the String
        g.setFont(font);
        g.drawString(text, x, y);
    }

    public void mouseMoved(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        // detect which option mouse is hovering over
        for (int i =0; i < optionRects.length; i++) {
            if (optionRects[i].contains(mouseX, mouseY)) {
                hoveredOption = i;
                break;
            }
            else {
                hoveredOption = -1;
            }
        }
    }


    public String mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (optionRects[0].contains(mouseX, mouseY)) {
            return "X";
        } else if (optionRects[1].contains(mouseX, mouseY)) {
            return "Menu";
        } else if (optionRects[2].contains(mouseX, mouseY)) {
            return "Settings";
        }
        return "";
    }

    // returns the cursor type
    public Cursor getCursor() {
        if (hoveredOption != -1) {
            return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); // pointer
        } else {
            return Cursor.getDefaultCursor(); // normal cursor
        }
    }
}