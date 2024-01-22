/* Authors: Andy Sun & Merrick Lu
   Date: December 19-January 22, 2024
   Project: "Chip Masters": GUI Poker
   Card class. Represents a basic playing card
 */

public class Card implements Cloneable {
    public static final String CARDS = "A23456789TJQKA";
    public static final String[] SUITS = { "Clubs", "Spades", "Hearts", "Diamonds" };

    public int cardID; // used in GUI
    private int cardNum; // number from 0-12
    private int cardSuit; // 0 - diamonds, 1 - clubs, 2 - hearts, 3 - spades

    public Card(int num) {
        cardNum = num / 4;// AAAA22223333 etc.
        cardSuit = num % 4;// diamonds, clubs, hearts, spades, diamonds, clubs, etc...
        cardID = cardSuit*13+cardNum+1;
    }

    public Card(int num, int s) {
        cardNum = num;
        cardSuit = s;
        cardID = cardSuit*13+cardNum+1;
    }

    public Card() {
        cardID = 0;
    }

    public static Card IDtoNum(int ID) {
        int num = (ID-1)%13;
        int cardSuit = (ID-1)/13;
        return (new Card(num, cardSuit));
    }

    public int getCardID() {

        return cardID;
    }



    public int getCardNum() {
        return cardNum;
    }

    public int getCardSuit() {
        return cardSuit;
    }

    public static String numToString(int num) {
        return String.valueOf(CARDS.charAt(num));
    }

    public String toString() {
        return (numToString(this.getCardNum()) + " of " + SUITS[this.getCardSuit()]);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Card c) {
        return (cardNum == c.getCardNum() && cardSuit == c.getCardSuit());
    }

}
