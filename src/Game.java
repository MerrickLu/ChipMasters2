import java.sql.Array;
import java.util.*;

public class Game implements Runnable {

    public static final int NUM_PLAYERS = 6;
    public static Scanner in = new Scanner(System.in);

    public int sb;
    public int bb;
    public int sbPos;
    public int currentPos;
    public int yourPos;
    public int toCall;
    public int minRaise;

    public boolean[] isFold;
    public boolean[] hasGone;
    public boolean[] isAllIn;
    public boolean isFlop, isTurn, isRiver;
    public boolean isPreFlop;
    public boolean isActionOnYou = false;

    public int[] bets;
    public int[] pot;
    public int[] yourHand; // to be returned to GameGUI

    public Player[] table;
    public TotalHand[] allHands;
    public Hand comm;

    Deck d;
    Bot bot = new Bot();
    public Thread gameThread;
    public boolean running = true;
    public String[] actions = new String[NUM_PLAYERS];
    public String yourAction = "";
    ArrayList<Integer> winners = new ArrayList<Integer>(); // for gui
    ArrayList<ArrayList<Card>> winnerHands = new ArrayList<ArrayList<Card>>();

    public Game(int s, int b, int st) {
        table = new Player[NUM_PLAYERS];
        isFold = new boolean[NUM_PLAYERS];
        hasGone = new boolean[NUM_PLAYERS];
        isAllIn = new boolean[NUM_PLAYERS];
        sbPos = 0;
        sb = s;
        bb = b;
        bets = new int[NUM_PLAYERS];
        pot = new int[NUM_PLAYERS];
        yourHand = new int[2];
        allHands = new TotalHand[NUM_PLAYERS];

        toCall = 0;
        minRaise = 0;
        comm = new Hand();
        d = new Deck();
        d.shuffle();

        for (int i = 0; i < NUM_PLAYERS; i++) {
            table[i] = new Player(st);
            isFold[i] = false;
            isAllIn[i] = false;
            hasGone[i] = false;
        }
        bot = new Bot();
    }


    public void run() {
        startGame();
    }
    public void startGame() {
//        System.out.println("Position: ");
//        yourPos = in.nextInt();
        while(true) {
            d = new Deck();
            d.shuffle();
            preflop();
            processWin();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            winners.clear();
            winnerHands.clear();
            sbPos = (sbPos+1)%NUM_PLAYERS;
        }
    }

    public void preflop() {
        isPreFlop = true;
        dealHands();
        yourHand[0] = table[yourPos].getHand().get(0).cardID;
        yourHand[1] = table[yourPos].getHand().get(1).cardID;
        System.out.println("Your hand is: " + table[yourPos].getHand() + "\n");
        int idx = sbPos;
        while(table[idx].getStack() == 0) {
            idx=(idx+1)%NUM_PLAYERS;
        }
        int min = Math.min(table[idx].getStack(), sb);
        bets[idx] = min;

        if(min != sb) {
            System.out.println(sbPos + " is all in ");
            isAllIn[sbPos] = true;
        }
        else {
            System.out.println(sbPos + " bets " + Math.min(table[sbPos].getStack(), sb));
        }

        idx = (idx+1)%NUM_PLAYERS;
        while(table[idx].getStack() == 0) {
            idx=(idx+1)%NUM_PLAYERS;
        }
        min = Math.min(table[idx].getStack(), bb);
        bets[idx] = min;
        if(min!=bb) {
            System.out.println(sbPos + " is all in ");
            isAllIn[(sbPos+1)%NUM_PLAYERS] = true;
        }
        else {
            System.out.println((sbPos + 1) % NUM_PLAYERS + " bets " + bb);
        }
        toCall = bb;
        minRaise = sb + bb;
        currentPos = (sbPos + 2) % NUM_PLAYERS;

        bettingRound();
        if(checkNumPlayers()>1) {
            comm.addToHand(d.deal());
            comm.addToHand(d.deal());
            comm.addToHand(d.deal());
            System.out.println("\nFlop comes " + comm);
            isFlop = true;
            System.out.println("Done");
            turn();
        }
    }

    public void turn() {
        isPreFlop = false;
        bettingRound();
        if(checkNumPlayers()>1) {
            comm.addToHand(d.deal());
            System.out.println("\nTurn comes " + comm);
            isTurn = true;
            river();
        }
    }

    public void river() {

        bettingRound();
        if(checkNumPlayers()>1) {
            comm.addToHand(d.deal());
            System.out.println("\nRiver comes " + comm);
            isRiver = true;
            bettingRound();//last betting round
        }
    }



    public void bettingRound() {
        while (!canCont()) {// while you cannot continue
            if (isFold[currentPos] || isAllIn[currentPos]) {
                nextPos();
                continue;
            }
            if (currentPos == yourPos) {
                isActionOnYou = true;
                actionOnYou();
            } else {
                actionOnBot();
                isActionOnYou = false;
            }
        }

        collect();

    }

    public void actionOnYou() {
        // print msg depending on if you can check
        if (canCheck())
            System.out.println("""
					[R] - Raise
					[K] - Check
					[F] - Fold""");

        else
            System.out.println("""
					[R] - Raise
					[C] - Call
					[F] - Fold""");

        while (true) {
            // do nothing until user does something
            System.out.print(yourAction); // DO NOT ERASE THIS LINE FOR SOME REASON IT BUGS OUT
            if (!yourAction.equals("")) {
                System.out.println("you chose " + yourAction);
                break;
            }
        }
        switch (yourAction) {
            case "R":
                break;
            case "C":
                call();
                break;
            case "K":
                if (canCheck()) {
                    check();
                    yourAction = "";

                } else
                    System.out.println("Can't do that");
                return;
            case "F":
                fold();
                break;
            default:
                System.out.println("Not an option");
        }
        yourAction = ""; // reset
    }

    public void actionOnBot() {
        bot.makeMove(this);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void processWin() {
        winners = getWinnerIdx(comm.getHand());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("The winners are: " + winners);
        // store winner hands to arraylist
        for (int i = 0; i<winners.size();i++) {
            System.out.println(table[winners.get(i)].getHand());
            winnerHands.add(table[winners.get(i)].getHand());
            System.out.println(winnerHands);
        }
        if(checkNumPlayers()>1) System.out.println(allHands[winners.get(0)].getStringStrength());
        for (int i = 0; i<NUM_PLAYERS; i++) {
            System.out.println(i + "'s hand was " + table[i].getHand());
        }
        for (int i = 0; i < winners.size(); i++) {
            System.out.println(winners.get(i) + " wins " + getPot() / winners.size());
            table[winners.get(i)].addToStack(getPot() / winners.size());
        }
        for(int i = 0; i<NUM_PLAYERS; i++) {
            table[i].resetHand();
            if(table[i].getStack()==0) {
                isFold[i] = true;
            }
            else {
                isFold[i] = false;
            }
            hasGone[i] = false;
            isAllIn[i] = false;
            System.out.println(i + "'s stack is now " + table[i].getStack());
            pot[i] = 0;
        }
        isFlop = false;
        isTurn = false;
        isRiver = false;
        yourAction = "";
        comm.clear();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

    }

    public void check() {
        actions[currentPos] = "CHECK";
        System.out.println(currentPos + " checks.");
        nextPos();
    }

    public void call() {
        if (toCall >= table[currentPos].getStack()) {
            // this means the player is all in
            allIn();
            return;
        }
        if (canCheck()) {
            check();
            return;
        }
        actions[currentPos] = "CALL";
        System.out.println(currentPos + " calls " + toCall);
        bets[currentPos] = toCall;
        nextPos();
    }

    public void allIn() {
        isAllIn[currentPos] = true;
        raise(table[currentPos].getStack());
    }

    public void fold() {
        actions[currentPos] = "FOLD";
        System.out.println(currentPos + " folds.");
        isFold[currentPos] = true;
        nextPos();
    }

    public void raise(int r) {
        if (r > table[currentPos].getStack()) {
            allIn();
            return;
        }

        if(r==table[currentPos].getStack()) {
            System.out.println(currentPos + " goes all in for " + getCurrentPlayer().getStack() + ".");
            // figure out how much the new minraise is now;
            minRaise = Math.max(toCall + (r - toCall) * 2, minRaise);
            bets[currentPos] = r;
            toCall = Math.max(toCall, r);
            System.out.println("Bet to call is " + toCall);
            nextPos();
            return;
        }

        else if (r < minRaise) {
            System.out.println("raise higher");
            return;
        }




        // figure out how much the new minraise is now;
        minRaise = Math.max(toCall + (r - toCall) * 2, minRaise);
        bets[currentPos] = r;
        toCall = Math.max(toCall, r);
        actions[currentPos] = "RAISE";
        System.out.println(currentPos + " raises to " + toCall);
        nextPos();
    }

    public boolean canCont() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            // if folded or all in skip checking
            if (isFold[i] || isAllIn[i])
                continue;
            // if player has not gone yet, or has not bet the appropriate amount, cannot
            // continue
            if (!hasGone[i] || bets[i] != toCall)
                return false;
        }
        return true;
    }

    public void nextPos() {
        hasGone[currentPos] = true;
        currentPos = (currentPos + 1) % NUM_PLAYERS;
    }

    public int getPot() {
        int count = 0;
        for (int i = 0; i < pot.length; i++) {
            count += bets[i];
            count += pot[i];
        }
        return count;
    }

    public boolean canCheck() {
        if (bets[currentPos] == toCall)
            return true;
        else
            return false;
    }

    public Player getCurrentPlayer() {
        return table[currentPos];
    }

    public ArrayList<Integer> getWinnerIdx(ArrayList<Card> community) {
        ArrayList<Integer> maxloc = new ArrayList<>();// locations of the winners
        if(checkNumPlayers()==1) {
            for(int i = 0; i<NUM_PLAYERS; i++) {
                if(!isFold[i]){
                    maxloc.add(i);
                    return maxloc;
                }
            }
        }
        int notfold = 0;
        int maxStrength = 0;
        while (notfold < NUM_PLAYERS && isFold[notfold])
            notfold++;
        maxloc.add(notfold);

        for (int i = 0; i < NUM_PLAYERS; i++) {
            ArrayList<Card> hand = (ArrayList<Card>) community.clone();
            hand.addAll((Collection<? extends Card>) table[i].getHand().clone());
            allHands[i] = new TotalHand(hand);
        }
        if (notfold == isFold.length - 1) {
            return maxloc;
        }
        for (int i = notfold + 1; i < NUM_PLAYERS; i++) {
            if (isFold[i])
                continue;
            if (allHands[i].compareTo(allHands[maxloc.get(0)]) > 0) {
                // stronger hand
                maxloc.clear();
                maxloc.add(i);
            } else if (allHands[i].compareTo(allHands[maxloc.get(0)]) == 0) {
                // same hand
                maxloc.add(i);
            }
        }
        return maxloc;
    }

    public void displayWinnerHands() {
        ArrayList<Integer> maxloc = getWinnerIdx(comm.getHand());
        for (int c : maxloc) {
            System.out.println(c + " has " + allHands[c].getStrength());
            for (Card card : allHands[c].getBestHand()) {
                System.out.print(card + ", ");
            }
            System.out.println();
        }
    }

    public void dealHands() {
        // deal each player 2 cards;
        for (int i = 0; i < 2 * NUM_PLAYERS; i++) {
            table[i % NUM_PLAYERS].addToHand(d.deal());
        }
    }

    public String toString() {
        String str = "";
        str += "Pot is " + getPot() + "\n\n";
        for (int i = 0; i < NUM_PLAYERS; i++) {
            str += table[i] + "\nBet: " + bets[i] + "\n" + "Stack: " + table[i].getStack() + "\n\n";
        }

        System.out.println(Arrays.toString(isFold));
        System.out.println(Arrays.toString(isAllIn));

        str += ("Community cards are \n");
        for (Card c : comm.getHand()) {
            str += (c + "\n");
        }
        return str;
    }

    public void collect() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            pot[i] += bets[i];
            table[i].bet(bets[i]);
            bets[i] = 0;
            hasGone[i] = false;
        }
        toCall = 0;
        minRaise = bb;
        currentPos = sbPos;
    }

    public int checkNumPlayers() {
        int count = 0;
        for(int i = 0; i<NUM_PLAYERS; i++) {
            if(!isFold[i]) count++;
        }
        return count;
    }

}