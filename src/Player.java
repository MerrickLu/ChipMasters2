public class Player extends Hand {
    private int inPot;
    private int stack;

    public Player(int s) {
        super();
        stack = s;
        inPot = 0;
    }

    public void bet(int n) {
        stack -= n;
        inPot += n;
    }

    public int getStack() {
        return stack;
    }

    public void resetHand() {
        super.clear();// clear hand
        inPot = 0;
    }

    public void addToStack(int i) {
        stack += i;
    }

    public String toString() {
        String str = "";
        for (Card c : super.getHand()) {
            str += (c + ", ");
        }

        return str;

    }
}
