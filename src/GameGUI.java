import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.*;

public class GameGUI implements Runnable  {
    private final Image TABLE;
    private final Image raise, raise1, call, call1, fold, fold1, options;
    private static HashMap<Integer, Image> cardMap;
    private int hoveredOption = -1;
    private Rectangle buttonRects[] = new Rectangle[3];
    private Rectangle escapeRect;
    public boolean paused = false;

    Slider slider;

    private boolean isRaising;

    private int numTicks;
    private boolean hasDrawn;

    private int[][] cardLocations;
    private Game game = GamePanel.gameInstance;
    private int width = GamePanel.GAME_WIDTH, height = GamePanel.GAME_HEIGHT;



    public GameGUI() {
        slider = new Slider((int)(width*0.8-Slider.RANGE_WIDTH/2), height*9/10,(int)(width*0.8-Slider.RANGE_WIDTH/2));
        isRaising = false;
        numTicks = 0;
        hasDrawn = false;
        // load images
        TABLE = new ImageIcon("images/pokerTable.jpg").getImage();
        raise = new ImageIcon("images/buttons/raise.png").getImage();
        raise1 = new ImageIcon("images/buttons/raise1.png").getImage();
        call = new ImageIcon("images/buttons/call.png").getImage();
        call1 = new ImageIcon("images/buttons/call1.png").getImage();
        fold = new ImageIcon("images/buttons/fold.png").getImage();
        fold1 = new ImageIcon("images/buttons/fold1.png").getImage();
        options = new ImageIcon("images/options.png").getImage();

        cardLocations = new int[5][2];
        cardLocations[0][0] = 45;
        cardLocations[0][1] = height-350;
        cardLocations[1][0] = 150;
        cardLocations[1][1] = height-480;
        cardLocations[2][0] = 425;
        cardLocations[2][1] = height-490;
        cardLocations[3][0] = 715;
        cardLocations[3][1] = height-450;
        cardLocations[4][0] = 750;
        cardLocations[4][1] = height-250;

        // populate rectangles for raise, call, & fold buttons
        for (int i = 0; i < 3; i++) {
            buttonRects[i] = new Rectangle((int) (width*(0.33+0.12*i)), (int)(height*0.95-width*0.1*0.4), (int)(width*0.1), (int)(width*0.1*0.4));
        }


        // escape button
        escapeRect = new Rectangle((int)(width*0.02), (int)(width*0.02), (int)(width*0.04), (int)(width*0.04));

        // populate cardMap to match numbers from 1-52 with a unique card
        cardMap = new HashMap<Integer, Image>();
        for (int i = 0; i <= 52; i++) {
            cardMap.put(i, new ImageIcon("images/cards/" + i + ".png").getImage());
        }
    }

    // included to make class implement runnable (to run simultaneously with Game)
    public void run() {
    }

    // draw components to screen
    public void draw(Graphics g) {
        int buttonY = (int)(height*0.95-width*0.1*0.4);
        int buttonWidth = (int)(width*0.1);
        int buttonHeight = (int)(buttonWidth*0.4);
        g.drawImage(TABLE, 0, 0, width, height, null); // draw background to fit dimensions of panel
        g.setColor(Color.white);
        g.fillRect(escapeRect.x, escapeRect.y, escapeRect.width, escapeRect.height);
        // draw options button
        g.drawImage(options, buttonHeight / 2, buttonHeight / 2, buttonHeight, buttonHeight, null);

        // draw cards
        g.drawImage(cardMap.get(game.yourHand[0]), 15, height - 105, 72, 96, null);
        g.drawImage(cardMap.get(game.yourHand[1]), 90, height - 105, 72, 96, null);
        // bot cards
        for (int i = 0; i<5;i++) {
            g.drawImage(cardMap.get(0),cardLocations[i][0], cardLocations[i][1], 45, 60, null);
            g.drawImage(cardMap.get(0),cardLocations[i][0]+50, cardLocations[i][1], 45, 60, null);
        }
        // draw flop once it comes
        if (game.isFlop) {
            for(int i=0;i<3;i++) {
                g.drawImage(cardMap.get(game.comm.getHand().get(i).getCardID()), (int)(width*0.28)+75*i, (int)(height*0.4), 72, 96, null);
            }
        }
        // draw turn once it comes
        if (game.isTurn) {
            g.drawImage(cardMap.get(game.comm.getHand().get(3).getCardID()), (int)(width*0.28)+75*3, (int)(height*0.4), 72, 96, null);
        }
        // draw river
        if (game.isRiver) {
            g.drawImage(cardMap.get(game.comm.getHand().get(4).getCardID()), (int)(width*0.28)+75*4, (int)(height*0.4), 72, 96, null);
        }

        //Your Turn
        if(game.isActionOnYou) {
            g.setFont(new Font("Arial", Font.PLAIN, 40));
            g.drawString("Your Turn!", (int)(width*0.4), (int)(height*0.7));

            //draw buttons, with hover-over effect
            if (hoveredOption == 1)
                g.drawImage(raise1, (int) (width * 0.33), buttonY, buttonWidth, buttonHeight, null);
            else g.drawImage(raise, (int) (width * 0.33), buttonY, buttonWidth, buttonHeight, null);
            if (hoveredOption == 2)
                g.drawImage(call1, (int) (width * 0.45), buttonY, buttonWidth, buttonHeight, null);
            else g.drawImage(call, (int) (width * 0.45), buttonY, buttonWidth, buttonHeight, null);
            if (hoveredOption == 3)
                g.drawImage(fold1, (int) (width * 0.57), buttonY, buttonWidth, buttonHeight, null);
            else g.drawImage(fold, (int) (width * 0.57), buttonY, buttonWidth, buttonHeight, null);

            // draw text over buttons
            try {
                g.setFont(Font.createFont(Font.TRUETYPE_FONT, Menu.fontFile).deriveFont(18f));
            } catch (Exception e) {
                e.printStackTrace();
            }
            g.setFont(new Font("Garamond", Font.BOLD, 18));
            g.setColor(Color.white);
            g.drawString("Raise", (int) (width * 0.355), (int) (buttonY * 1.05));
            if(game.canCheck()) g.drawString("Check", (int) (width * 0.475), (int) (buttonY * 1.05));
            else g.drawString("Call", (int) (width * 0.48), (int) (buttonY * 1.05));
            g.drawString("Fold", (int) (width * 0.6), (int) (buttonY * 1.05));
        }

        //stacks and pots
        // yours
        g.setColor(Color.black);
        drawCenteredString(g, "Pot: " + game.getPot(), GamePanel.PANEL_BOUNDS, (int)(height*0.35), new Font("Garamond", Font.PLAIN, 30));
        g.setColor(Settings.golden);
        g.setFont(new Font("Garamond", Font.PLAIN, 18));
        g.drawString("Your Stack: " + game.table[game.yourPos].getStack(), 175, height - 70);
        g.drawString("Your Bet: " + game.bets[game.yourPos], 175, height-90);
        // bots
        for(int i = 0; i<cardLocations.length; i++) {
            g.drawString(i+1 + "'s Stack: " + game.table[i+1].getStack(), cardLocations[i][0], cardLocations[i][1]+75);

            if(!game.isFold[i+1]) {
                g.drawString(i+1 + "'s Bet: " + game.bets[i+1], cardLocations[i][0], cardLocations[i][1]+90);
                if (game.hasGone[i+1]) { // display bot action (call, check, raise)
                    g.drawString(game.actions[i+1], cardLocations[i][0], cardLocations[i][1]+105);
                }
            }
            else {
                g.drawString("FOLD", cardLocations[i][0], cardLocations[i][1]+90);
            }
        }

        // display winners
        if (!game.winners.isEmpty()) {
            g.setFont(new Font("Garamond", Font.PLAIN, 30));
            g.setColor(Settings.golden);
            g.fillRoundRect((int)(width*0.5-200), (int)(height*0.5-50),400, 100 , 75, 75);
            g.setColor(Color.WHITE);
            g.drawString("Winner(s): ",  (int)(width*0.42-game.winners.size()*25), (int)(height*0.5));
            for (int i = 0, winner, ID; i<game.winners.size(); i++) {
                winner = game.winners.get(i);
                g.drawString((i == 0 ? "" : ",") + winner, (int) (width * 0.55 + i * 25), (int) (height * 0.5));
                if (winner != 0) { // display winning bot hands
                    ID = game.table[winner].getHand().get(0).getCardID();
                    g.drawImage(cardMap.get(ID), cardLocations[winner - 1][0], cardLocations[winner - 1][1], 45, 60, null);
                    ID = game.table[winner].getHand().get(1).getCardID();
                    g.drawImage(cardMap.get(ID), cardLocations[winner - 1][0] + 50, cardLocations[winner - 1][1], 45, 60, null);
                }
            }
        }

        //slider
        if(isRaising) {
            slider.draw(g);
        }
    }

    public void drawCenteredString(Graphics g, String text, Rectangle rect, int y, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        g.setColor(Settings.golden);
        g.fillRoundRect(x-20, y-font.getSize(), metrics.stringWidth(text)+40, font.getSize()+10, 30,20);
        // Draw the String
        g.setFont(font);
        g.setColor(Color.black);
        g.drawString(text, x, y);
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


    public String mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        if (escapeRect.contains(mouseX, mouseY)) {
            return "Escape";
        }
        if(game.isActionOnYou) {
            if(isRaising) {
                slider.mousePressed(e);
            }
            // return the button pressed to game
            if (game.yourAction.equals("")) { // only accept button input once
                if (buttonRects[0].contains(mouseX, mouseY)) {
                    if(isRaising) {
                        isRaising = false;
                        game.raise(slider.getSliderNum());
                        game.yourAction = "R";
                    }
                    else {
                        isRaising = true;
                        slider.setRange(Math.min(game.minRaise, game.table[game.yourPos].getStack()), game.table[game.yourPos].getStack());
                    }
//                    game.yourAction = "R";
                } else if (buttonRects[1].contains(mouseX, mouseY)) {
                    isRaising = false;
                    if(game.canCheck()) game.yourAction = "K";
                    else game.yourAction = "C";
                } else if (buttonRects[2].contains(mouseX, mouseY)) {
                    isRaising = false;
                    game.yourAction = "F";
                }
            }

        }
        return "";
    }

    public void mouseDragged(MouseEvent e) {
        if(isRaising) {
            slider.mouseDragged(e);
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