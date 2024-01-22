/* Authors: Andy Sun & Merrick Lu
   Date: December 19-January 22, 2024
   Project: "Chip Masters": GUI Poker
   Menu Class. GUI for the home menu screen
 */

import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class Menu {
    public static Color crimson = new Color(187, 33, 14); // custom title color
    public static File fontFile; // custom title font
    private final Image BG_IMAGE; // background image
    private final float TITLE_SIZE = GamePanel.GAME_HEIGHT/8; // font size scaled to panel size
    private final float TEXT_SIZE = GamePanel.GAME_HEIGHT/25;
    private String[] options = {"Start Game", "Odds Calculator", "Settings", "Rules"};
    private Rectangle[] optionRects = new Rectangle[4];
    private int selectedOption = 4; // start at 3 to prevent becoming negative


    public Menu() {
        fontFile = new File("images/PokerFont.otf"); // font path
        BG_IMAGE = new ImageIcon("images/MenuBG.jpg").getImage(); // bg image path
        // create rectangles for each text option to detect mouse events later
        for (int i = 0; i < options.length; i++) {
            int textY = (int) (GamePanel.GAME_HEIGHT * (0.43 + i * 0.11));
            optionRects[i] = new Rectangle( // create rectangle
                    0,
                    textY - (int) (TEXT_SIZE * 0.7),
                    GamePanel.GAME_WIDTH,
                    (int) (TEXT_SIZE * 1.4)
            );
        }
    }

    // draws everything to screen
    public void draw(Graphics g) throws IOException, FontFormatException {
        String text;
        g.drawImage(BG_IMAGE, 0, 0, GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT, null); // draw background to fit dimensions of panel
        g.setColor(crimson);
        drawCenteredString(g, "Chip Masters", GamePanel.PANEL_BOUNDS, (int) (GamePanel.GAME_HEIGHT*0.32), Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(TITLE_SIZE));
        g.setColor(Color.white);

        // print each option
        for (double i = 0, yCoord = 0.43; i < options.length; i++, yCoord+=0.11) {
            text = options[(int)i];
            // display currently selected option in different color
            if((int) i == selectedOption-4) {
                g.setColor(crimson);
            } else {
                g.setColor(Color.white);
            }
            // draw the string
            drawCenteredString(g, text, GamePanel.PANEL_BOUNDS, (int) (GamePanel.GAME_HEIGHT*yCoord), Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(TEXT_SIZE));
        }

    }

    // returns true to GamePanel if space is pressed (allowing game to start)
    public String keyPressed(KeyEvent e) {
        // if up key is pressed
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            selectedOption--;
            selectedOption = selectedOption%4+4; // reset number to stay within 3-5
        }

        // if down key is pressed
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            selectedOption++;
            selectedOption = selectedOption%4+4;
        }

        // enter to select option
        else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            return options[selectedOption-4]; // return what was selected
        }
        return "";
    }

    public void mouseMoved(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        for (int i = 0; i < optionRects.length; i++) {
            if (optionRects[i].contains(mouseX, mouseY)) {
                // The mouse is over this option
                selectedOption = i+4;
                break;
            }
            else {

            }
        }
    }

    public String mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        for (int i = 0; i < optionRects.length; i++) {
            if (optionRects[i].contains(mouseX, mouseY)) { // mouse is over this position
                // return the option pressed
                return options[i];
            }
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


