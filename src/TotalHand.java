import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class TotalHand {

    private List<List<Card>> nums = new ArrayList<List<Card>>(14);// List of a list of all cards with the same number as
    // the index

    public enum HandStrength {
        NOTHING, HIGH_CARD, PAIR, TWO_PAIR, TRIPS, STRAIGHT, FLUSH, FULL_HOUSE, QUADS, STRAIGHT_FLUSH, ROYAL_FLUSH
    }

    private HandStrength strength;

    public TotalHand(ArrayList<Card> a) {
        if (a != null) {
            ArrayList<Card> c = (ArrayList<Card>) a.clone();// make a clone so it doesn't mess up anything else
            if (c != null) {
                c.sort((o1, o2) -> Integer.compare(o1.getCardNum(), o2.getCardNum()));// sort the hand first
                // Accounting for ace high and low ace high and ace low (Aces added twice cuz of
                // straights)
                ArrayList<Card> lowAces = new ArrayList<Card>();
                ArrayList<Card> highAces = new ArrayList<Card>();
                for (Card card : c) {
                    if (card.getCardNum() == 0) {
                        lowAces.add(card);
                        highAces.add(new Card(13, card.getCardSuit()));
                    }
                }
                nums.add(lowAces);
                for (int i = 1; i < 13; i++) {
                    ArrayList<Card> toAdd = new ArrayList<Card>();
                    for (Card card : c) {
                        if (card.getCardNum() == i) {
                            toAdd.add(card);
                        }
                    }
                    nums.add(toAdd);
                }
                nums.add(highAces);
            }
        }

    }

    // gets all suited cards if there are more than 5 of them in a suit
    public ArrayList<Card> getFlushed() {
        ArrayList<Card> diamonds = new ArrayList<Card>();
        ArrayList<Card> clubs = new ArrayList<Card>();
        ArrayList<Card> hearts = new ArrayList<Card>();
        ArrayList<Card> spades = new ArrayList<Card>();

        for (List<Card> innerList : nums) {
            for (Card c : innerList) {
                switch (c.getCardSuit()) {
                    case 0:
                        diamonds.add(c);
                        break;
                    case 1:
                        clubs.add(c);
                        break;
                    case 2:
                        hearts.add(c);
                        break;
                    case 3:
                        spades.add(c);
                        break;
                }
            }
        }
        if (diamonds.size() >= 5)
            return diamonds;
        else if (clubs.size() >= 5)
            return clubs;
        else if (hearts.size() >= 5)
            return hearts;
        else if (spades.size() >= 5)
            return spades;
        else
            return null;
    }

    // returns the biggest flush
    public ArrayList<Card> getBiggestFlush() {
        ArrayList<Card> flush = getFlushed();
        if (flush == null)
            return null;
        while (flush.size() > 5) {// remove the smallest element until there are only 5 left
            flush.remove(0);
        }
        if (flush.get(0).getCardNum() == 0)
            return null;
        else {
            Collections.reverse(flush);
            strength = HandStrength.FLUSH;
            return flush;
        }
    }

    // returns the biggest straight
    public ArrayList<Card> getStraight() {
        int count = 0;
        int minLoc = 0;
        int i = nums.size() - 1;
        ArrayList<Card> straight = new ArrayList<>();
        done: while (i >= 0) {// go through all indexes from biggest to smallest
            while (i >= 0 && nums.get(i).isEmpty()) {// if there is no card with the number 'i', reset the count.
                i--;
                count = 0;
            }
            while (i >= 0 && !nums.get(i).isEmpty()) {// if there IS a card with the number 'i', count it until 5
                count++;
                if (count == 5) {// found the biggest straight
                    minLoc = i;
                    break done;
                }
                i--;
            }
        }

        if (count < 5)
            return null;

        for (int j = minLoc; j < minLoc + 5; j++) {
            straight.add(nums.get(j).get(0));
        }
        strength = HandStrength.STRAIGHT;
        return straight;
    }

    // gets suited straight
    public ArrayList<Card> getStraightFlush() {
        TotalHand flush = new TotalHand(getFlushed()); // make a new hand with all suited cards
        ArrayList<Card> straightFlush = flush.getStraight();// gets a straight from all the suited cards
        if (straightFlush == null)
            return null;

        if (straightFlush.get(0).getCardNum() == 9) {
            // smallest card is a 10 -> royal flush
            strength = HandStrength.ROYAL_FLUSH;
        } else
            strength = HandStrength.STRAIGHT_FLUSH;
        return straightFlush;
    }

    // gets literally anything else
    public ArrayList<Card> getElse() {
        // check in ascending order for the number with the most frequency
        // this is guaranteed to be added
        HashSet<Integer> invalidIdx = new HashSet<Integer>();
        int maxLoc = 0;
        int spotsLeft = 5;
        ArrayList<Card> bestHand = new ArrayList<>();
        String str = ""; // this represents the frequency of cards added. (Ex: Full house would be "32")

        while (spotsLeft > 0) {
            maxLoc = 0;
            for (int i = 0; i < nums.size(); i++) {
                if (!invalidIdx.contains(i) && !nums.get(i).isEmpty()) {
                    maxLoc = i;
                    break;
                }
            }
            for (int i = maxLoc+1; i < nums.size(); i++) {
                if (!invalidIdx.contains(i) // checking if i is valid
                        && nums.get(i).size() >= nums.get(maxLoc).size() // checking if freq is bigger
                        && nums.get(i).size() <= spotsLeft // checking if hand can fit
                        && !nums.get(i).isEmpty()
                ) {
                    maxLoc = i;
                }
            }
            bestHand.addAll(nums.get(maxLoc));
            spotsLeft -= nums.get(maxLoc).size();
            str += nums.get(maxLoc).size();
            // if it was aces, invalidate high ace and low ace
            if (maxLoc == nums.size() - 1) {
                invalidIdx.add(0);
            }
            if (maxLoc == 0) {
                invalidIdx.add(nums.size()-1);
            }
            if(invalidIdx.contains(maxLoc)) {
                strength = HandStrength.NOTHING;
                return null;
            }
            invalidIdx.add(maxLoc);
        }

        switch (str) {
            case "41":
                strength = HandStrength.QUADS;
                break;
            case "32":
                strength = HandStrength.FULL_HOUSE;
                break;
            case "311":
                strength = HandStrength.TRIPS;
                break;
            case "221":
                strength = HandStrength.TWO_PAIR;
                break;
            case "2111":
                strength = HandStrength.PAIR;
                break;
            default:
                strength = HandStrength.HIGH_CARD;
                break;
        }
        return bestHand;
    }

    public ArrayList<Card> getBestHand() {
        // check hands in ascending order
        ArrayList<Card> best = new ArrayList<>();

        best = getElse();
        switch (strength) {
            case QUADS, FULL_HOUSE:// only straight flush and royal flush are bigger than these. Don't check
                // anything else
                best = getStraightFlush() == null ? best : getStraightFlush();
                break;
            default:
                best = getStraight() == null ? best : getStraight();
                best = getBiggestFlush() == null ? best : getBiggestFlush();
                best = getStraightFlush() == null ? best : getStraightFlush();
                break;
        }
        return best;
    }

    public HandStrength getStrength() {
        getBestHand();
        return strength;
    }

    public String getStringStrength() {
        switch (getStrength()) {
            case ROYAL_FLUSH:
                return "Royal Flush";
            case STRAIGHT_FLUSH:
                return "Straight Flush";
            case QUADS:
                return "Quads";
            case FULL_HOUSE:
                return "Full House";
            case TRIPS:
                return "Trips";
            case TWO_PAIR:
                return "Two Pair";
            case PAIR:
                return "Pair";
            case HIGH_CARD:
                return "High Card";
            case NOTHING:
                return "Nothing";
        }
        return "Something went wrong";
    }

    public String toString() {
        String str = "";
        for (int i = 0; i < nums.size(); i++) {
            for (int j = 0; j < nums.get(i).size(); j++) {
                str += nums.get(i).get(j) + "\n";
            }
        }
        return str;
    }

    public int compareTo(TotalHand h) {
        ArrayList<Card> best1 = getBestHand();
        ArrayList<Card> best2 = h.getBestHand();
        if (getStrength().compareTo(h.getStrength()) != 0)
            return getStrength().compareTo(h.getStrength());
        else {
            // just check each index until one of them is greater
            for (int i = 0; i < Math.min(best1.size(), best2.size()); i++) {
                if (best1.get(i).getCardNum() > best2.get(i).getCardNum()) {
                    return 1;
                } else if (best1.get(i).getCardNum() < best2.get(i).getCardNum()) {
                    return -1;
                }
            }
            return 0; // exact same hand
        }
    }
}
