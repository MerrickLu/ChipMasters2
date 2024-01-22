/* Authors: Andy Sun & Merrick Lu
   Date: December 19-January 22, 2024
   Project: "Chip Masters": GUI Poker
   Hand class. Represents a player's hand of 2 cards
 */

import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> hand = new ArrayList<>();

    public Hand() {

    }

    public Hand(ArrayList<Card> h) {
        hand = (ArrayList<Card>) h.clone();
    }

    public void addToHand(Card c) {
        hand.add(c);
    }


    public ArrayList<Card> getHand() {
        return hand;
    }

    public void clear() {
        hand.clear();
    }

    public String toString() {
        String str = "";
        for (Card c : hand) {
            str += c + "\n";
        }
        return str;
    }

}
