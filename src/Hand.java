import java.lang.reflect.Array;
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

    public void sortHand() {
        hand.sort((o1, o2) -> Integer.compare(o1.getCardNum(), o2.getCardNum()));// ascending order
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
