/* Home menu */

import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Settings {
    public static Color golden = new Color(234, 198, 114); // custom font color
    private final Image BG_IMAGE; // background image
    private final float TITLE_SIZE = GamePanel.GAME_HEIGHT / 8; // font size scaled to panel size
    private final int TEXT_SIZE = GamePanel.GAME_HEIGHT / 15;
    private boolean mouseHeld = false;

    private static String[][] options = { { "Music Volume", "100" }, { "Effects Volume", "100" }, { "Starting Cash", "500" } };
    private String[] controls;
    private Rectangle[] controlRects = new Rectangle[6]; // rectangles for each control to detect mouse
    private Rectangle backRect;

    private int selectedOption = -1; // currently selected option, -1 for nothing

    public Settings() {
        BG_IMAGE = new ImageIcon("images/settingsBG.jpg").getImage(); // bg image path
        controls = new String[options.length*2]; // array of - + controls
        for(int i = 0; i < options.length; i++) { // populate
            controls[2*i] = "-";
            controls[2*i + 1] = "+";
        }

        // create rectangles for each '-' sign
        for (int i = 0; i < controls.length; i+=2) {

            controlRects[i] = new Rectangle( // create rectangle
                    (int) (GamePanel.GAME_WIDTH*0.48),
                    (int) (GamePanel.GAME_HEIGHT * (0.35 + i/2 * 0.15)) - (int) (TEXT_SIZE * 0.7), // y coordinate + padding
                    35, // width of "-" symbol
                    (int) (TEXT_SIZE * 1.4)
            );

        }

        // rectangles for each '+' sign
        for (int i = 1; i < controls.length; i+=2) {
            controlRects[i] = new Rectangle( // create rectangle
                    (int) (GamePanel.GAME_WIDTH*0.68),
                    (int) (GamePanel.GAME_HEIGHT * (0.35 + (i-1)/2 * 0.15)) - (int) (TEXT_SIZE * 0.7), // y coordinate + padding
                    40, // width of "+" symbol
                    (int) (TEXT_SIZE * 1.4)
            );
        }

        backRect = new Rectangle (0, 0, 120, (int)(TEXT_SIZE*1.5)); // rectangle for back button
    }
    public int getMusicVolume() {
        return Integer.parseInt(options[0][1]);
    }

    public int getEffectsVolume() {
        return Integer.parseInt(options[1][1]);
    }

    public int getStartingCash() {
        return Integer.parseInt(options[2][1]);
    }
    // draws everything to screen
    public void draw(Graphics g) throws IOException, FontFormatException {
        g.drawImage(BG_IMAGE, 0, 0, GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT, null); // draw background to fit dimensions of panel
        if (selectedOption == -2) g.setColor(Menu.crimson); // mouse hover indicator
        else g.setColor(Color.white);
        g.setFont(Font.createFont(Font.TRUETYPE_FONT, Menu.fontFile).deriveFont(TITLE_SIZE/2));
        g.drawString("Back", (int) (GamePanel.GAME_WIDTH * 0.02), (int) (GamePanel.GAME_HEIGHT * 0.07)); // draw back option
        g.setColor(golden); // draw title
        drawCenteredString(g, "Settings", GamePanel.PANEL_BOUNDS, (int) (GamePanel.GAME_HEIGHT * 0.15), Font.createFont(Font.TRUETYPE_FONT, Menu.fontFile).deriveFont(TITLE_SIZE));

        g.setFont(new Font("Garamond", Font.PLAIN, TEXT_SIZE));
        // draw options
        for (int i = 0; i < options.length; i++) {
            // write the option
            g.drawString(options[i][0], (int) (GamePanel.GAME_WIDTH * 0.15), (int) (GamePanel.GAME_HEIGHT * (0.35 + 0.15 * i)));

            // write the corresponding "-" control for each option
            if (selectedOption == 2 * i) g.setColor(Menu.crimson); // highlight if mouse hovering
            g.drawString(controls[2 * i], (int) (GamePanel.GAME_WIDTH * 0.5), (int) (GamePanel.GAME_HEIGHT * (0.35 + 0.15 * i)));

            g.setColor(golden); // reset color

            // add a '$' in front of value
            if (options[i][0].equals("Starting Cash")) g.drawString("$" + options[i][1], (int) (GamePanel.GAME_WIDTH * 0.55), (int) (GamePanel.GAME_HEIGHT * (0.35 + 0.15 * i)));
                // add a '%' behind value
            else g.drawString(options[i][1] + "%", (int) (GamePanel.GAME_WIDTH * 0.55), (int) (GamePanel.GAME_HEIGHT * (0.35 + 0.15 * i)));

            // write the corresponding "+" control
            if (selectedOption == 2 * i + 1) g.setColor(Menu.crimson); // highlight if selected
            g.drawString(controls[2 * i + 1], (int) (GamePanel.GAME_WIDTH * 0.7), (int) (GamePanel.GAME_HEIGHT * (0.35 + 0.15 * i)));
            g.setColor(golden);

        }
    }

    public void mouseMoved(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (backRect.contains(mouseX, mouseY)) {
            selectedOption = -2;
        }
        else {
            for (int i = 0; i < controlRects.length; i++) {
                if (controlRects[i].contains(mouseX, mouseY)) {
                    // The mouse is over this option
                    selectedOption = i;
                    break;
                } else {
                    selectedOption = -1;

                }
            }
        }
    }

    public String mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (backRect.contains(mouseX, mouseY)) {
            return "Back";
        }
        else{
            for (int i = 0; i < controlRects.length; i++) {
                if (controlRects[i].contains(mouseX, mouseY)) { // mouse is over this position
                    mouseHeld = true;
                    startUpdateThread(i);
                }
            }
        }
        return "";
    }

    public void mouseReleased(MouseEvent e) {
        mouseHeld = false; // Mouse is released
    }

    private void startUpdateThread(int index) {
        new Thread(() -> {
            int n = 0; // keeps track of how many "presses"
            while (mouseHeld) {
                // Update the option based on the index
                changeOption(index, n);

                try {
                    Thread.sleep(250); // delay slightly longer first press
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                n++;
            }
        }).start();
    }

    /*
     * Updates variables according to which control pressed
     */
    private void changeOption(int i, int n) {
        int temp;
        switch (i) {
            case 0: // decrease music volume by 10%
                temp = Integer.parseInt(options[0][1]);
                if (temp >= 10) { // can't decrease below 0
                    options[0][1] = Integer.toString(temp-10);
                }
                break;

            case 1: // increase music volume by 10%
                temp = Integer.parseInt(options[0][1]);
                if (temp <= 90) { // can't increase past 100
                    options[0][1] = Integer.toString(temp+10);
                }
                break;
            case 2: // decrease effects volume by 10%
                temp = Integer.parseInt(options[1][1]);
                if (temp >= 10) {
                    options[1][1] = Integer.toString(temp-10);
                }
                break;
            case 3: // increase effects volume by 10%
                temp = Integer.parseInt(options[1][1]);
                if (temp <= 90) {
                    options[1][1] = Integer.toString(temp+10);
                }
                break;
            case 4: // decrease starting balance by $50
                temp = Integer.parseInt(options[2][1]);
                if (temp >= 100) { // balance can't be 0
                    // speed up if held down
                    if (n<=5) options[2][1] = Integer.toString(temp-50);
                    else if (n <= 10) options[2][1] = Integer.toString(temp-100);
                    else if (n <= 15) options[2][1] = Integer.toString(temp-1000);
                    else options[2][1] = Integer.toString(temp-10000);
                }
                break;
            case 5: // increase starting balance by $50
                temp = Integer.parseInt(options[2][1]);
                if (temp <= 999900) { // balance has to 6 digits or less
                    // speed up if held down
                    if (n<=5) options[2][1] = Integer.toString(temp+50);
                    else if (n <= 10) options[2][1] = Integer.toString(temp+100);
                    else if (n <= 15) options[2][1] = Integer.toString(temp+1000);
                    else options[2][1] = Integer.toString(temp+10000);
                }
                break;
        }
    }


    /*
     * returns the cursor type; pointer if hovering over an option, default cursor otherwise
     */
    public Cursor getCursor() {
        if (selectedOption != -1) {
            return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        } else {
            return Cursor.getDefaultCursor();
        }
    }

    /**
     * Draw a String centered in the horizontal middle of the screen, using a
     * rectangle
     *
     * @param g    The Graphics instance.
     * @param text The String to draw.
     * @param rect The Rectangle to center the text in.
     * @param y    The y location to draw the string in.
     * @param font The font of the string
     */
    public void drawCenteredString(Graphics g, String text, Rectangle rect, int y, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Draw the String
        g.setFont(font);
        g.drawString(text, x, y);
    }
}
