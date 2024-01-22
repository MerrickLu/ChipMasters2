/* Authors: Andy Sun & Merrick Lu
   Date: December 19-January 22, 2024
   Project: "Chip Masters": GUI Poker
   PreflopRanges. Stores the preflopranges of different scenarios, for bots to use while making moves
 */
public class PreflopRanges {

    private int[][] nine = new int[13][13];
    private int[][] fifteen = new int[13][13];

    private int[][] twenty = new int[13][13];

    private int[][] twentyfive = new int[13][13];

    private int[][] thirtyfive = new int[13][13];

    private int[][] fifty = new int[13][13];

    public PreflopRanges() {
        populateNine();
        populateFifteen();
        populateTwenty();
        populateTwentyFive();
        populateThirtyFive();
        populateFifty();
    }

    public int[][][] getRanges() {
        return new int[][][] { nine, fifteen, twenty, twentyfive, thirtyfive, fifty };
    }

    public int[][] getRange() {
        return fifty;
    }

    public void populateNine() {
        // populate nine
        // range is: 66+, AJs+, KQs, AJo+, KQo

        // 66+
        pocketPairs(6, nine);
        // KQo
        nine[12 - 2][13 - 2] = 1;
        // KQs
        nine[13 - 2][12 - 2] = 2;

        plus(14, 11, nine); // base>min is suited
        plus(11, 14, nine);
    }

    public void populateFifteen() {
        // range is 22+, ATs+, KJs+, 65s+, AJo+, KJo+, QJo
        // 65s+
        connectors(6, 5, fifteen);
        // AJo+
        plus(11, 14, fifteen);
        // KJo+
        plus(11, 13, fifteen);
        // QJo
        plus(11, 12, fifteen);

        // ATs+
        plus(14, 10, fifteen);
        // KJs+
        plus(13, 11, fifteen);
        // 22+
        pocketPairs(2, fifteen);
    }

    public void populateTwenty() {
        // range is 22+, ATs+, KTs+, QTs+, J9s+, T8s+, 54 suited connectors +, ATo+,
        // KTo+, QTo+, JTo
        // JTo - //ATo+
        for (int i = 11; i <= 14; i++) {
            plus(10, i, twenty);

        }
        connectors(5, 4, twenty);
        // T8s+-QTs
        for (int i = 8; i <= 10; i++) {
            plus(i + 2, i, twenty);
        }
        // KTs+
        plus(13, 10, twenty);
        // ATs+
        plus(14, 10, twenty);
        // 22+
        pocketPairs(2, twenty);
    }

    public void populateTwentyFive() {
        // T9o
        twentyfive[9 - 2][10 - 2] = 1;

        // KTo+,QTo+,JTo,
        for (int i = 11; i <= 13; i++) {
            plus(10, i, twentyfive);
        }

        // A9o+,
        plus(9, 14, twentyfive);

        // 54s+,
        connectors(5, 4, twentyfive);

        // T8s+,97s+,86s+,75s+,64s+,
        for (int i = 6; i <= 10; i++) {
            connectors(i, i - 2, twentyfive);
        }

        // K9s+,Q9s+,J9s+,
        for (int i = 11; i <= 13; i++) {
            plus(i, 9, twentyfive);
        }
        // A7s+,
        plus(14, 7, twentyfive);
        // range is 22+,
        pocketPairs(2, twentyfive);
    }

    public void populateThirtyFive() {

        // K9o+,Q9o+,J9o+,T9o
        for (int i = 10; i <= 13; i++) {
            plus(9, i, thirtyfive);
        }

        // A5o-A2o,
        for (int i = 2 - 2; i <= 5 - 2; i++) {
            thirtyfive[i][14 - 2] = 1;
        }

        // A8o+,
        plus(8, 14, thirtyfive);
        // 54s+, 43s+
        connectors(4, 3, thirtyfive);

        // 97s+,86s+,75s+,64s+
        connectors(6, 4, thirtyfive);

        // J8s+,T7s+,
        connectors(10, 7, thirtyfive);

        // Q8s+,
        connectors(12, 8, thirtyfive);
        // K8s+,
        connectors(13, 8, thirtyfive);
        // 22+,A2s+,
        plus(14, 2, thirtyfive);
        pocketPairs(2, thirtyfive);

    }

    public void populateFifty() {
        connectors(5, 6, fifty);

        plus(2, 14, fifty);
        plus(5, 13, fifty);
        for (int i = 10; i <= 12; i++) {
            plus(8, i, fifty);
        }
        connectors(5, 6, fifty);

        connectors(4, 3, fifty);
        connectors(5, 3, fifty);
        connectors(9, 6, fifty);

        plus(14, 2, fifty);

        plus(13, 2, fifty);

        plus(12, 7, fifty);

        plus(11, 7, fifty);
        pocketPairs(2, fifty);
    }

    private void pocketPairs(int min, int[][] arr) {
        for (int i = min - 2; i <= 14 - 2; i++) {
            arr[i][i] = i + 2 >= 8 ? 3 : 2;
        }
    }

    private void plus(int base, int min, int[][] arr) {
        if (base > min) {
            for (int i = min - 2; i < base - 2; i++) {
                arr[base - 2][i] = i + 2 >= 10 ? 2 : 1;
            }
        } else {
            for (int i = base - 2; i < min - 2; i++)
                arr[i][min - 2] = 1;
        }

    }

    private void connectors(int base, int min, int[][] arr) {
        int separator = base - min;
        if (base > min) {
            for (int i = base - 2; i <= 14 - 2; i++) {
                arr[i][i - separator] = i + 2 >= 10 ? 2 : 1;
            }
        } else {
            for (int i = min - 2; (i) <= (14 - 2); i++) {
                arr[i + separator][i] = i + 2 >= 11 ? 2 : 1;
            }
        }
    }

}
