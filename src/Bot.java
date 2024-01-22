/* Authors: Andy Sun & Merrick Lu
   Date: December 19-January 22, 2024
   Project: "Chip Masters": GUI Poker
   Bot class. Makes moves based on the game and a randomized personality
 */

import java.util.ArrayList;
import java.util.Arrays;

public class Bot {

    private final int[][] range; //range table the bot uses
    private int[] botTypes; //what kind of bot is at which position

    public Bot() {
        super();
        botTypes = new int[Game.NUM_PLAYERS];
        PreflopRanges ranges = new PreflopRanges();
        // choose a random range to play
        range = ranges.getRange();
        for (int i = 0; i < botTypes.length; i++) {
            botTypes[i] = (int) (Math.random() * 3);// choose a random bot to play
        }
    }

    public void makeMove(Game g) {
        if (g.isPreFlop) {
            preflop(g);
        } else {
            postFlop(g);
        }
    }

    //how the bot plays preflop
    private void preflop(Game g) {
        // first read the hand
        int x = g.getCurrentPlayer().getHand().get(0).getCardNum();// smaller card
        int y = g.getCurrentPlayer().getHand().get(g.getCurrentPlayer().getHand().size() - 1).getCardNum(); // bigger
        // card
        if (g.getCurrentPlayer().getHand().get(0).getCardSuit() == g.getCurrentPlayer().getHand()
                .get(g.getCurrentPlayer().getHand().size() - 1).getCardSuit()) {// suited hand
            if(y<x) {
                int temp = y;
                y = x;
                x = temp;
            }
        }
        if(x==0 && y==0) {
            x = 13;
            y = 13;
        }
        else {
            if (x == 0) {// if x is an ace (hand is unsuited)
                x = y;
                y = 13;
            }
            if (y == 0) {// if y is an ace (hand is suited)
                y = x;
                x = 13;
            }
        }

        x -= 1;// index conversion
        y -= 1;

        double[] equity = { 0, (Math.random() / 10) + 0.6, (Math.random() / 10) + 0.7, 1.0 };
        // calculates the pot odds
        int betSize = g.toCall - g.bets[g.currentPos];
        if (((double) betSize) / g.getPot() > equity[range[x][y]]) {
            g.fold();
            return;
        }
        if (g.minRaise < g.bb * (range[x][y] + 1)) {
            g.raise((int) ((Math.random() + (range[x][y] + 1)) * g.bb));
        } else {
            g.call();
        }
    }

    private void postFlop(Game g) {
        switch (botTypes[g.currentPos]) {
            case 0:
                checkBot(g);
                break;
            case 1:
                callBot(g);
                break;
            case 2:
                superBot(g);
                break;
        }
    }

    private void checkBot(Game g) {
        if (g.canCheck())
            g.check();
        else {
            g.fold();
        }
    }

    private void callBot(Game g) {
        g.call();
    }

    private void superBot(Game g) {
        // makes decision by cheating lol
        // calculates the pot odds
        double[] equity = getEquities(g);
        int betSize = g.toCall - g.bets[g.currentPos];

        double[] sorted = equity.clone();
        Arrays.sort(sorted);

        if (equity[g.currentPos] == sorted[sorted.length - 1]) {
            if(g.minRaise > g.getCurrentPlayer().getStack()) {
                g.allIn();
                return;
            }
            g.raise(g.minRaise);
            return;
        }

        if ((double) betSize / g.getPot() > equity[g.currentPos]) {
            g.fold();
        } else {
            g.call();
        }
    }

    //calculates all equities
    public static double[] getEquities(Game g) {
        ArrayList<Card> comm = (ArrayList<Card>) g.comm.getHand().clone();
        double[] equities = new double[Game.NUM_PLAYERS];
        ArrayList<Integer> winners = new ArrayList<>();
        int cardsMissing = 5 - comm.size();
        TotalHand[] t = new TotalHand[Game.NUM_PLAYERS];
        // making a new deck with all the blockers
        for (int j = 0; j < 10000; j++) {
            Deck d = new Deck();
            d.shuffle();
            for (int i = 0; i < comm.size(); i++) {
                d.block(comm.get(i));
            }
            // blocking the players cards
            for (int i = 0; i < g.table.length; i++) {
                d.block(g.table[i].getHand().get(0));
                d.block(g.table[i].getHand().get(g.table[i].getHand().size() - 1));
            }
            for (int k = 0; k < cardsMissing; k++) {
                // deal cards
                comm.add(d.deal());
            }
            winners = g.getWinnerIdx(comm);
            for (int c : winners) {
                equities[c] += 1.0 / winners.size();
            }
            for (int k = 0; k < cardsMissing; k++) {
                // deal cards
                comm.remove(comm.size() - 1);
            }
        }
        for (int i = 0; i < equities.length; i++) {
            equities[i] = (int) equities[i] / 100.0;
        }
        g.allHands = new TotalHand[Game.NUM_PLAYERS];
        return equities;

    }

}
