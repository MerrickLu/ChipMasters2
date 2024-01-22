/* Home menu */

import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class Rules {
    private final Image one, two, three, four;
    private final float TITLE_SIZE = GamePanel.GAME_HEIGHT/8; // font size scaled to panel size
    private Rectangle backRect;
    private Rectangle nextRect;
    private Rectangle previousRect;
    private int hoveredOption; // whether back is hovered
    private int current = 1; // multiple "pages" for explanations


    public Rules() {
        backRect = new Rectangle (0, 0, 120, (int)(TITLE_SIZE*0.65)); // rectangle for back button
        nextRect = new Rectangle((int)(GamePanel.GAME_WIDTH*0.45), (int)(GamePanel.GAME_HEIGHT*0.86), 120, (int)(TITLE_SIZE*0.65));
        previousRect = new Rectangle((int)(GamePanel.GAME_WIDTH*0.4), 0, 200, (int)(TITLE_SIZE*0.65));
        one = new ImageIcon("images/rules/1.png").getImage();
        two = new ImageIcon("images/rules/2.png").getImage();
        three = new ImageIcon("images/rules/3.png").getImage();
        four = new ImageIcon("images/rules/4.png").getImage();
    }

    // draws everything to screen
    public void draw(Graphics g) throws IOException, FontFormatException {
        if (current == 1) {
            g.drawImage(one, 0, 0, GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT, null);
        } else if (current == 2) {
            g.drawImage(two, 0, 0, GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT, null);
        } else if (current == 3) {
            g.drawImage(three, 0, 0, GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT, null);
        } else if (current == 4) {
            g.drawImage(four, 0, 0, GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT, null);
        }
        // back button
        if (hoveredOption == 0) g.setColor(Menu.crimson); // mouse hover indicator
        else g.setColor(Color.white);
        g.setFont(Font.createFont(Font.TRUETYPE_FONT, Menu.fontFile).deriveFont(TITLE_SIZE/2));
        g.drawString("Back", (int) (GamePanel.GAME_WIDTH * 0.02), (int) (GamePanel.GAME_HEIGHT * 0.07)); // draw back option

        if (hoveredOption == 1) g.setColor(Menu.crimson); // next button
        else g.setColor(Color.white);
        if (current < 4) g.drawString("Next", (int) (GamePanel.GAME_WIDTH * 0.46), (int) (GamePanel.GAME_HEIGHT * 0.94)); // draw back option

        if (hoveredOption == 2) g.setColor(Menu.crimson); // previous button
        else g.setColor(Color.white);
        if (current >1) g.drawString("Previous", (int) (GamePanel.GAME_WIDTH * 0.4), (int) (GamePanel.GAME_HEIGHT * 0.07)); // draw back option


    }

    public void mouseMoved(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (backRect.contains(mouseX, mouseY)) {
            hoveredOption = 0;
        } else if (current < 4 && nextRect.contains(mouseX, mouseY)){
            hoveredOption = 1;
        } else if (current > 1 && previousRect.contains(mouseX, mouseY)) {
            hoveredOption = 2;
        } else {
            hoveredOption = -1;
        }
    }

    public String mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (backRect.contains(mouseX, mouseY)) {
            return "Back";
        } else if (current < 4 && nextRect.contains(mouseX, mouseY)) {
            current++;
        } else if (current > 1 && previousRect.contains(mouseX, mouseY)) {
            current--;
        }
        return "";
    }
    /**
     * Draw a String centered in the horizontal middle of the screen, using a rectangle
     * @param g The Graphics instance.
     * @param text The String to draw.
     * @param rect The Rectangle to center the text in.
     * @param y The y location to draw the string in.
     * @param font The font of the string
     */
    private void drawCenteredString(Graphics g, String text, Rectangle rect, int y, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Draw the String
        g.setFont(font);
        g.drawString(text, x, y);
    }
}


