/* Authors: Andy Sun & Merrick Lu
   Date: December 19-January 22, 2024
   Project: "Chip Masters": GUI Poker
   Training GUI. Calculates equities of different hands in different situations
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;

public class Training {
    GamePanel clone; //Placeholder for GamePanel object to check conditions

    private final int TEXT_SIZE = GamePanel.GAME_HEIGHT / 15; //This is the text size inside the game
    private final float TITLE_SIZE = GamePanel.GAME_HEIGHT / 8; // font size scaled to panel size

    public static DecimalFormat percent = new DecimalFormat("#.00"); //for Rounding equities
    private final Image TABLE; //Background image
    private static HashMap<Integer, Image> cardMap; //Cards

    private Rectangle[][] cardOptions; //The 52 cards on the table.
    private Rectangle[][] handOptions; //The 6 squares for the 6 player's hands

    public Rectangle[] comm; //community cards
    public Card[] commCard; //cards in the community cards
    private int width = GamePanel.GAME_WIDTH, height = GamePanel.GAME_HEIGHT;
    private int cardWidth = 39; //width of cards on display
    private int cardHeight = 52; //height of cards on display
    private boolean hovering = false; //hovering on the back rectangle
    private Rectangle escapeRect;
    private int[][][] handLocations; //where to draw the hands
    private int[][][] cardLocations; //where to draw the cards

    private int[] choice; //this is the current selected card slot
    Card[][] hands; //cards in the hands
    HashSet<Integer> cardsLeft; //cards left of the 52 on the table

    public boolean hasStarted = false;
    double[] equity; //equity percentages
    Thread t1;
    EquityCalculator calculator; //Calculates equity
    Rectangle backRect;


    public Training(GamePanel gamePanel) throws CloneNotSupportedException {
        backRect = new Rectangle (0, 0, 120, (int)(TEXT_SIZE*1.5)); // rectangle for back button
        clone = gamePanel;
        equity = new double[6];
        choice = new int[]{0,0};
        TABLE = new ImageIcon("images/pokerTable.jpg").getImage();

        handLocations = new int[6][2][2];
        handOptions = new Rectangle[6][2];
        cardLocations = new int[4][13][2];
        hands = new Card[6][2];

        //locations of the player hands as well as the actual hands
        for(int i = 0; i<6; i++) {
            handLocations[i][0][0] = width*75/100;
            handLocations[i][0][1] = 20+i*75;
            handLocations[i][1][0] = width*75/100 + cardWidth + 4;
            handLocations[i][1][1] = 20+i*75;
            handOptions[i][0] = new Rectangle(handLocations[i][0][0], handLocations[i][0][1], cardWidth, cardHeight);
            handOptions[i][1] = new Rectangle(handLocations[i][1][0], handLocations[i][1][1], cardWidth, cardHeight);

            hands[i][0] = new Card();
            hands[i][1] = new Card();
        }

        //these are the options left
        cardsLeft = new HashSet<>();

        //options
        cardOptions = new Rectangle[4][13];
        for(int i = 0; i<52; i++) {
            cardsLeft.add(i+1);
            cardLocations[i/13][i%13][0] = 75 + (i%13)*(cardWidth+2);
            cardLocations[i/13][i%13][1] = 140 + (i/13)*(cardHeight+2);
            cardOptions[i/13][i%13] = new Rectangle(cardLocations[i/13][i%13][0], cardLocations[i/13][i%13][1], cardWidth, cardHeight);
        }


        // escape button
        escapeRect = new Rectangle((int) (width * 0.02), (int) (width * 0.02), (int) (width * 0.04), (int) (width * 0.04));

        // populate cardMap to match numbers from 1-52 with a unique card
        cardMap = new HashMap<Integer, Image>();
        for (int i = 0; i <= 52; i++) {
            cardMap.put(i, new ImageIcon("images/cards/" + i + ".png").getImage());
        }

        //comm cards
        comm = new Rectangle[5];
        commCard = new Card[5];
        for(int i = 0; i<comm.length; i++) {
            comm[i] = new Rectangle(200+i*(cardWidth+10), 25, cardWidth+4, cardHeight+4);
            commCard[i] = new Card();
        }

    }

    public void draw(Graphics g) throws IOException, FontFormatException, CloneNotSupportedException {
        if(!hasStarted) {
            hasStarted = true;
            calculator = new EquityCalculator(clone);
            t1 = new Thread(calculator);
            t1.start();
        }

        g.drawImage(TABLE, -width/8-100, -height/8, width*5/4, height*5/4, null); // draw background to fit dimensions of panel
        drawCardOptions(g);
        drawChoice(g);
        drawComm(g);
        drawHands(g);
        drawEquities(g);
        g.setFont(Font.createFont(Font.TRUETYPE_FONT, Menu.fontFile).deriveFont(TITLE_SIZE/2));
        if (hovering) g.setColor(Menu.crimson);
        else g.setColor(Color.white);
        g.drawString("Back", (int) (GamePanel.GAME_WIDTH * 0.02), (int) (GamePanel.GAME_HEIGHT * 0.07)); // draw back option

    }

    //Draw the 52 options for cards on the board
    public void drawCardOptions(Graphics g) {
        for(int i = 0; i<52; i++) {
            if(cardsLeft.contains(i+1)) g.drawImage(cardMap.get(i+1), cardLocations[i/13][i%13][0], cardLocations[i/13][i%13][1], cardWidth, cardHeight, null);
        }
    }

    //draw the player's hands
    public void drawHands(Graphics g) {
        for(int i = 0; i<handLocations.length; i++) {
            g.drawImage(cardMap.get(hands[i][0].getCardID()), handLocations[i][0][0], handLocations[i][0][1], cardWidth, cardHeight, null);
            g.drawImage(cardMap.get(hands[i][1].getCardID()), handLocations[i][1][0], handLocations[i][1][1], cardWidth, cardHeight, null);

        }
    }

    //draw the 5 community cards
    public void drawComm(Graphics g) {
        for(int i = 0; i<comm.length; i++) {
            g.drawImage(cardMap.get(commCard[i].getCardID()), comm[i].x+2, comm[i].y+2, cardWidth, cardHeight, null);
        }
    }

    //highlight the selected square
    public void drawChoice(Graphics g) {
        for(int i = 0; i<handLocations.length; i++) {
            for(int j = 0; j<handLocations[0].length; j++) {
                if(i == choice[0] && j == choice[1]) {
                    g.setColor(Color.green);
                }
                else g.setColor(Menu.crimson);
                g.fillRect(handLocations[i][j][0]-2, handLocations[i][j][1]-2, cardWidth+4, cardHeight+4);
            }
        }

        for(int i = 0; i<comm.length; i++) {
            if(choice[0] == -1 && i == choice[1]) {
                g.setColor(Color.green);
            }
            else {
                g.setColor(Menu.crimson);
            }
            g.fillRect(comm[i].x, comm[i].y, comm[i].width, comm[i].height);
        }
    }

    public void drawEquities(Graphics g) throws CloneNotSupportedException {
        g.setColor(Color.white);
        for(int i = 0; i<handLocations.length; i++) {
            g.drawString("Equity: " + percent.format((Math.round((int)calculator.equity[i]/10))*0.01) + "%", handLocations[i][1][0] + 50,handLocations[i][1][1]+50);
        }
    }

    public void mouseMoved(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if(backRect.contains(mouseX, mouseY)) {
            hovering = true;
        } else {
            hovering = false;
        }
    }

    public void mouseClicked(MouseEvent e) throws CloneNotSupportedException {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if(backRect.contains(mouseX, mouseY)) {
            if(clone.beforeTraining.equals("Menu")) clone.onMenu = true;
            else if(clone.beforeTraining.equals("Paused")) clone.onPaused = true;
            clone.onTraining = false;
            t1.interrupt();
        }

        for (int i = 0; i < cardOptions.length; i++) {
            for(int j = 0; j<cardOptions[0].length; j++) {
                if(cardOptions[i][j].contains(mouseX, mouseY)) {
                    if(choice[0] == -1) {
                        swapIntoComm(i, j);
                    }
                    else swapIntoHand(i, j);
                }
            }
        }
        for (int i = 0; i < handOptions.length; i++) {
            for(int j = 0; j<handOptions[0].length; j++) {
                if(handOptions[i][j].contains(mouseX, mouseY)) {
                    if(choice[0] == i && choice[1] == j) {
                        swapOutOfHand(i, j);

                    }
                    else {
                        choice[0] = i;
                        choice[1] = j;
                    }
                }
            }
        }
        for(int i = 0; i<comm.length; i++) {
            if(comm[i].contains(mouseX, mouseY)) {
                if(choice[0] == -1 && choice[1] == i) {
                    swapOutOfComm(i);
                }
                else {
                    choice[0] = -1;
                    choice[1] = i;
                }
            }
        }

    }

    //takes a card from the card options and puts it into a player's hand
    public void swapIntoHand(int i, int j) {
        if(!cardsLeft.contains(i*13 + j + 1)) {
            return;
        }
        Card temp = hands[choice[0]][choice[1]];
        hands[choice[0]][choice[1]] = new Card(j, i);
        cardsLeft.remove((i*13 + j + 1));
        cardsLeft.add(temp.cardID);
    }

    //takes a card from a player's hand and puts it back into the card options
    public void swapOutOfHand(int i, int j) {
        cardsLeft.add(hands[i][j].getCardID());
        hands[i][j] = new Card();
    }

    //takes a card from the options and puts it into the community cards
    public void swapIntoComm(int i, int j) {
        if(!cardsLeft.contains(i*13 + j + 1)) {
            return;
        }
        Card temp = commCard[choice[1]];
        commCard[choice[1]] = new Card(j, i);
        cardsLeft.remove((i*13 + j + 1));
        cardsLeft.add(temp.cardID);
    }

    //takes a card from the community and places back into the card options
    public void swapOutOfComm(int i) {
        cardsLeft.add(commCard[i].getCardID());
        commCard[i] = new Card();
    }



}