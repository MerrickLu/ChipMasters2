/* Authors: Andy Sun & Merrick Lu
   Date: December 19-January 22, 2024
   Project: "Chip Masters": GUI Poker
   Game Class to handle the main logic of each game
*/

import java.sql.Array;
import java.util.*;

public class Game implements Runnable {

    public static final int NUM_PLAYERS = 6;
    private SoundPlayer shuffle = new SoundPlayer("sounds/shuffle.wav");
    private SoundPlayer deal = new SoundPlayer("sounds/deal.wav");
    private SoundPlayer jackpot = new SoundPlayer("sounds/jackpot.wav");
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
    public boolean isPreFlop;

    public int[] bets;
    public int[] pot;

    public Player[] table;
    public TotalHand[] allHands;
    public Hand comm;
    Deck d;
    Bot bot = new Bot();

    // variables to be returned to GUI
    public Thread gameThread;
    public boolean youLost;
    public boolean isFlop, isTurn, isRiver;
    public boolean isActionOnYou = false;
    public boolean onWinners = false;
    public boolean startSequence;
    public int sequenceNum;
    public int[] yourHand;
    public String[] actions = new String[NUM_PLAYERS];
    public String yourAction = "";
    ArrayList<Integer> winners = new ArrayList<Integer>();
    ArrayList<ArrayList<Card>> winnerHands = new ArrayList<ArrayList<Card>>();

    public boolean inGame[];


    public Game(int s, int b, int st) {
        inGame = new boolean [NUM_PLAYERS];
        youLost = false;
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
            inGame[i] = true;
            table[i] = new Player(st);
            isFold[i] = false;
            isAllIn[i] = false;
            hasGone[i] = false;
        }
        bot = new Bot();
    }

    // runs simultaneously with GameGUI
    public void run() {
        startGame();
    }

    public void startGame() {
//        System.out.println("Position: ");
//        yourPos = in.nextInt();
        while(!youLost) {
            d = new Deck();
            d.shuffle();
            dealHands();
            startSequence = true;
            sequenceNum = 0;
            while(startSequence) { // wait for GUI dealing sequence to finish
                sequenceNum++;
                try {
                    if (sequenceNum == 1) Thread.sleep(500);
                    else if(sequenceNum == 2) {
                        shuffle.setVolume(Settings.getEffectsVolume());
                        shuffle.play();
                        Thread.sleep(1500);
                    } else {
                        deal.setVolume(Settings.getEffectsVolume());
                        deal.play();
                        Thread.sleep(300);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            preflop();
            processWin();
            onWinners = true;
            jackpot.setVolume(Settings.getEffectsVolume());
            jackpot.play();
            while(onWinners) { // wait for user key press
                System.out.print("");
            }
            reset();
            winnerHands.clear();
            sbPos = (sbPos+1)%NUM_PLAYERS;
            if(table[yourPos].getStack() == 0) {
                youLost = true;
            }
        }
    }

    public void preflop() {
        int idx = sbPos;
        isPreFlop = true;
        System.out.println("Your hand is: " + table[yourPos].getHand() + "\n");
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
                isActionOnYou = false;
                actionOnBot();
            }
        }
        collect();

    }

    public void actionOnYou() {

        while (true) {
            // do nothing until user does something
            System.out.print(yourAction); // DO NOT ERASE THIS LINE FOR SOME REASON IT BUGS OUT
            if (!yourAction.equals("")) {
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
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        bot.makeMove(this);
    }

    public void processWin() {
        winners = getWinnerIdx(comm.getHand());
        displayAllHandStrengths();
        System.out.println("The winners are: " + winners);
//        if(checkNumPlayers()>1) System.out.println(allHands[winners.get(0)].getStringStrength());
        for (int i = 0; i<NUM_PLAYERS; i++) {
            if(table[i].getStack() == 0){
                inGame[i] = false;
            }

            System.out.println(i + "'s hand was " + table[i].getHand());
        }
        for (int i = 0; i < winners.size(); i++) {
            System.out.println(winners.get(i) + " wins " + getPot() / winners.size());
            table[winners.get(i)].addToStack(getPot() / winners.size());
        }

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

    }

    private void reset() {
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
        winners.clear();
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
            actions[currentPos] = "ALL IN";
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

    public void updateAllHand(ArrayList<Card> community) {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            ArrayList<Card> hand = (ArrayList<Card>) community.clone();
            hand.addAll((Collection<? extends Card>) table[i].getHand().clone());
            allHands[i] = new TotalHand(hand);
        }
    }

    public ArrayList<Integer> getWinnerIdx(ArrayList<Card> community) {
        ArrayList<Integer> maxloc = new ArrayList<>();// locations of the winners
        updateAllHand(community);

        int notfold = 0;
        int maxStrength = 0;
        while (notfold < NUM_PLAYERS && isFold[notfold])
            notfold++;
        maxloc.add(notfold);


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

    public void displayAllHandStrengths() {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" +
                "\nHAND STRENGHTS: ");
        for (int c = 0; c<allHands.length; c++) {
            System.out.println(c + " has " + allHands[c].getStringStrength());
            if(allHands[c].getBestHand() != null) {
                for (int i = 0; i < allHands[c].getBestHand().size(); i++) {
                    System.out.print(allHands[c].getBestHand().get(i) + ", ");
                }
                System.out.println();
            }
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public void dealHands() {
        // deal each player 2 cards;
        for (int i = 0; i < 2 * NUM_PLAYERS; i++) {
            table[i % NUM_PLAYERS].addToHand(d.deal());
        }
        // feedback to GUI
        yourHand[0] = table[yourPos].getHand().get(0).cardID;
        yourHand[1] = table[yourPos].getHand().get(1).cardID;
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