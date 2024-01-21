import java.util.ArrayList;

class EquityCalculator implements Runnable{
    
    public double[] equity;
    GamePanel clone;
    public void run(){
        while(true) {

                try {
                    equity = getEquities(clone.training);
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }

        }


    }
    
    public EquityCalculator(GamePanel gamePanel) throws CloneNotSupportedException {
        clone = gamePanel;
        equity = new double[6]; 

    }

    public double[] getEquities(Training train) throws CloneNotSupportedException {
        int handCounter = 0;
        for(int i = 0; i<train.hands.length; i++) {
            if(train.hands[i][0].getCardID()!=0 && train.hands[i][1].getCardID()!=0 ) {
                handCounter++;
            }
        }
        if(handCounter<2) {
            return new double[6];
        }
        TotalHand[] tHand = new TotalHand[6];
        ArrayList<Card> h = new ArrayList<>();
        Deck d = new Deck();

        double[] equities = new double[6];
        for(int j = 0; j<10000; j++) {
            ArrayList<Integer> maxloc = new ArrayList<>();// locations of the winners

            d = new Deck();
            d.shuffle();
            for(int i = 0; i<train.comm.length; i++) {
                if(train.commCard[i].getCardID()!=0) {
                    h.add((Card)train.commCard[i].clone());
                    d.block((Card)train.commCard[i].clone());
                }
            }
            for(int i = 0; i<train.hands.length; i++) {
                if(train.hands[i][0].getCardID()!=0 && train.hands[i][1].getCardID()!=0 ) {
                    d.block((Card)train.hands[i][0].clone());
                    d.block((Card)train.hands[i][1].clone());
                }
            }
            while(h.size()<5) {
                h.add(d.deal());
            }
            for(int i = 0; i<train.hands.length; i++) {
                if(train.hands[i][0].getCardID()!=0 && train.hands[i][1].getCardID()!=0 ) {
                    h.add((Card)train.hands[i][0].clone());
                    h.add((Card)train.hands[i][1].clone());
                    tHand[i] = new TotalHand(h);

                    h.remove(h.size()-1);
                    h.remove(h.size()-1);
                }
            }
            int firstGood = 0;
            while(tHand[firstGood] == null) firstGood++;
            maxloc.add(firstGood);
            for (int i = firstGood+1; i < tHand.length; i++) {
                if (tHand[i]==null)
                    continue;
                if (tHand[i].compareTo(tHand[maxloc.get(0)]) > 0) {
                    // stronger hand
                    maxloc.clear();
                    maxloc.add(i);
                } else if (tHand[i].compareTo(tHand[maxloc.get(0)]) == 0) {
                    // same hand
                    maxloc.add(i);
                }
            }
            for(int c: maxloc) {
                equities[c]+=1.0/maxloc.size();
            }
            h.clear();
        }
        return equities;
    }


}  