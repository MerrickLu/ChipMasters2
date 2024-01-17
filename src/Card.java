public class Card implements Cloneable {
    // wsg
    public static final String CARDS = "A23456789TJQKA";
    public static final String[] SUITS = { "Diamonds", "Clubs", "Hearts", "Spades" };

    public int cardID;
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
