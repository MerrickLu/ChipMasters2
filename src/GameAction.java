public class GameAction {

    String type; //"W" for win, "You" for action on player, "R" for raise, "C" for call, "F" for fold, "K" for check, "A" for all in, "Flop" for 3 cards, "Turn" for turn, "River" for last card, "Collect" for collect bets
    int amount;
    int position; //0-5

    public GameAction() {

    }

    public GameAction(int p, String s, int a) {
        position = p; //position doing the action
        type = s; //type of action they are doing
        amount = a; //amount of action
    }
    public GameAction(String s) {
        type = s;
    }

}
