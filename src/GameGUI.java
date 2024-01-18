import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.ImageIcon;

public class GameGUI {
    private final Image TABLE;
    private final Image raise, raise1, call, call1, fold, fold1, options;
    private static HashMap<Integer, Image> cardMap;
    private int hoveredOption = -1;
    private Rectangle buttonRects[] = new Rectangle[3];
    private Rectangle escapeRect;
    private boolean paused = false;



    public GameGUI() {
        // load images
        TABLE = new ImageIcon("images/pokerTable.jpg").getImage();
        raise = new ImageIcon("images/buttons/raise.png").getImage();
        raise1 = new ImageIcon("images/buttons/raise1.png").getImage();
        call = new ImageIcon("images/buttons/call.png").getImage();
        call1 = new ImageIcon("images/buttons/call1.png").getImage();
        fold = new ImageIcon("images/buttons/fold.png").getImage();
        fold1 = new ImageIcon("images/buttons/fold1.png").getImage();
        options = new ImageIcon("images/options.png").getImage();

        // populate rectangles for raise, call, & fold buttons
        for (int i = 0; i < 3; i++) {
            buttonRects[i] = new Rectangle((int) (GamePanel.GAME_WIDTH*(0.33+0.12*i)), (int)(GamePanel.GAME_HEIGHT*0.95-GamePanel.GAME_WIDTH*0.1*0.4), (int)(GamePanel.GAME_WIDTH*0.1), (int)(GamePanel.GAME_WIDTH*0.1*0.4));
        }

        escapeRect = new Rectangle((int)(GamePanel.GAME_WIDTH*0.02), (int)(GamePanel.GAME_WIDTH*0.02), (int)(GamePanel.GAME_WIDTH*0.04), (int)(GamePanel.GAME_WIDTH*0.04));

        // populate cardMap to match numbers from 1-52 with a unique card
        cardMap = new HashMap<Integer, Image>();
        for (int i = 0; i <= 52; i++) {
            cardMap.put(i, new ImageIcon("images/cards/" + i + ".png").getImage());
        }
    }


    // draw components to screen
    public void draw(Graphics g) {
        int buttonY = (int)(GamePanel.GAME_HEIGHT*0.95-GamePanel.GAME_WIDTH*0.1*0.4);
        int buttonWidth = (int)(GamePanel.GAME_WIDTH*0.1);
        int buttonHeight = (int)(buttonWidth*0.4);
        g.drawImage(TABLE, 0, 0, GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT, null); // draw background to fit dimensions of panel
        g.setColor(Color.white);
        g.fillRect(escapeRect.x, escapeRect.y, escapeRect.width, escapeRect.height);
        // draw options button
        g.drawImage(options, buttonHeight / 2, buttonHeight / 2, buttonHeight, buttonHeight, null);
        //draw buttons, with hover-over effect
        if (hoveredOption == 1)
            g.drawImage(raise1, (int) (GamePanel.GAME_WIDTH * 0.33), buttonY, buttonWidth, buttonHeight, null);
        else g.drawImage(raise, (int) (GamePanel.GAME_WIDTH * 0.33), buttonY, buttonWidth, buttonHeight, null);
        if (hoveredOption == 2)
            g.drawImage(call1, (int) (GamePanel.GAME_WIDTH * 0.45), buttonY, buttonWidth, buttonHeight, null);
        else g.drawImage(call, (int) (GamePanel.GAME_WIDTH * 0.45), buttonY, buttonWidth, buttonHeight, null);
        if (hoveredOption == 3)
            g.drawImage(fold1, (int) (GamePanel.GAME_WIDTH * 0.57), buttonY, buttonWidth, buttonHeight, null);
        else g.drawImage(fold, (int) (GamePanel.GAME_WIDTH * 0.57), buttonY, buttonWidth, buttonHeight, null);

        // draw text over buttons
        try {
            g.setFont(Font.createFont(Font.TRUETYPE_FONT, Menu.fontFile).deriveFont(18f));
        } catch (Exception e) {
            e.printStackTrace();
        }
        g.setFont(new Font("Garamond", Font.BOLD, 18));
        g.setColor(Color.white);
        g.drawString("Raise", (int) (GamePanel.GAME_WIDTH * 0.355), (int) (buttonY * 1.05));
        g.drawString("Call", (int) (GamePanel.GAME_WIDTH * 0.48), (int) (buttonY * 1.05));
        g.drawString("Fold", (int) (GamePanel.GAME_WIDTH * 0.6), (int) (buttonY * 1.05));
        Card c = new Card(4,3);
        Card c2 = new Card(0, 2);
        drawCards(g, 0, 15, GamePanel.GAME_HEIGHT - 105);
        drawCards(g, c2.getCardID(), 95, GamePanel.GAME_HEIGHT - 105);
    }


public void drawCards(Graphics g, int cardNum, int x, int y) {
    g.drawImage(cardMap.get(cardNum), x, y, 72, 96, null);

}


// mouse moved, called from GamePanel
public void mouseMoved(MouseEvent e) {
    int mouseX = e.getX();
    int mouseY = e.getY();

    // detect which option mouse is hovering over
    if (escapeRect.contains(mouseX, mouseY)) {
        hoveredOption = 0;
        return;
    }
    for (int i = 0; i < buttonRects.length; i++) {
        if (buttonRects[i].contains(mouseX, mouseY)) {
            // The mouse is over this option
            hoveredOption = i+1;
            break;
        }
        else {
            hoveredOption = -1;
        }
    }


}


public void mousePressed(MouseEvent e) {
    int mouseX = e.getX();
    int mouseY = e.getY();
    if (escapeRect.contains(mouseX, mouseY)) {
        paused = true;
    }
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
