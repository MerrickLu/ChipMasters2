import java.sql.Array;
import java.util.*;

public class Deck {
    private Queue<Card> deck = new ArrayDeque<Card>();

    // This makes a new deck that is completely in order
    public Deck() {
        for (int i = 0; i < 52; i++) {
            deck.add(new Card(i));
        }
    }

    // prints the entire deck
    public void printDeck() {
        while (deck.peek() != null) {
            System.out.println(deck.poll());
        }
    }

    // shuffles
    public void shuffle() {
        List<Card> shuffled = new ArrayList<Card>(deck);
        Collections.shuffle(shuffled);
        deck = new ArrayDeque<Card>(shuffled);
    }

    // deals one card
    public Card deal() {
        return deck.poll();
    }

    public void block(Card blocker) {
        List<Card> d = new ArrayList<Card>(deck);
        for (int i = 0; i < d.size(); i++) {
            if (d.get(i).equals(blocker)) {
                d.remove(i);
                deck = new ArrayDeque<Card>(d);
                return;
            }
        }

    }

}
