package sg.nus.cs4246.jem.poker;

import java.util.Arrays;
import java.util.Random;

public class Probability {

    public static final int OPPONENTS = 1;
    public static final int ITERATIONS = 1000;

    private int[] hand;     // Represents the hand
    private int[] table;    // Represents to table
    private int handCnt;    // How many cards in on the hand (must be 4)
    private int tableCnt;   // How many cards are on the table
    private String[] hands;

    private enum Hands {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        STRAIGHT,
        FLUSH,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        STRAIGHT_FLUSH
    }

    private enum Cards {
        C2, D2, H2, S2,
        C3, D3, H3, S3,
        C4, D4, H4, S4,
        C5, D5, H5, S5,
        C6, D6, H6, S6,
        C7, D7, H7, S7,
        C8, D8, H8, S8,
        C9, D9, H9, S9,
        CT, DT, HT, ST,
        CJ, DJ, HJ, SJ,
        CQ, DQ, HQ, SQ,
        CK, DK, HK, SK,
        CA, DA, HA, SA
    }

    public static int[] reverse(int[] a) {
        for (int i = 0; i < a.length / 2; i++) {
            int tmp = a[i];
            a[i] = a[a.length - 1 - i];
            a[a.length - 1 - i] = tmp;
        }
        return a;
    }


    public static void main(String[] args) {
        int[] hand  = new int[]{
                Cards.C2.ordinal(),
                Cards.C4.ordinal(),
                Cards.HT.ordinal(),
                Cards.H4.ordinal()
        };
        int[] board = new int[]{
                Cards.CT.ordinal(),
                Cards.ST.ordinal(),
                Cards.DT.ordinal(),
                -1,
                -1
        };

        Probability p = new Probability(hand, board);
        p.calculate();
    }

    public Probability(int[] hand, int[] table) {
        this.hand  = hand;
        this.table = table;

        this.handCnt  = 4;
        this.tableCnt = 3;

        this.hands = new String[]{
                "HIGH_CARD",
                "ONE_PAIR",
                "TWO_PAIR",
                "THREE_OF_A_KIND",
                "STRAIGHT",
                "FLUSH",
                "FULL_HOUSE",
                "FOUR_OF_A_KIND",
                "STRAIGHT_FLUSH"
        };
    }

    // Start the calculation
    private void calculate() {
        // If the number of cards in the hand or on the table are not correct
        if (!isLegalState()) return;

        // Calculate the probability of winning
        double chanceOfWinning = estimateChanceOfWinning();
        System.out.println("Chance of winning: " + chanceOfWinning + "%");

        // Get the hand strength (two pairs, full house, etc.)
        String handStrength = hands[getBestHandStrength(hand, Arrays.copyOf(table, tableCnt)) / 1000000];
        System.out.println("You have: " + handStrength);
    }

    /**
     * Calculates the chance of winning
     * @return probability of winning the game
     */
    private double estimateChanceOfWinning() {
        // Create one array containing both cards on hand and on table
        int[] handAndTable = Arrays.copyOf(hand, handCnt + tableCnt);
        System.arraycopy(table, 0, handAndTable, handCnt, tableCnt);

        Deck deck = new Deck(handAndTable);
        int roundsWon = 0;

        int[][] opponentHands = new int[OPPONENTS][4];
        int[] possibleTable = Arrays.copyOf(table, 5);

        // Play a number of random games, and count how many games we win
        for (int i = 0; i < ITERATIONS; i++) {
            deck.shuffle();

            int m = 0;

            // Fill the rest of the table with random cards
            for (int j = tableCnt; j < 5; j++) {
                possibleTable[j] = deck.getCard(m++);
            }

            // Fill the opponents hand with random cards
            for (int j = 0; j < OPPONENTS; j++) {
                for (int k = 0; k < 4; k++) {
                    opponentHands[j][k] = deck.getCard(m++);
                }
            }

            boolean win = true;
            int myHandStrength = getBestHandStrength(hand, possibleTable);

            int opponent = 0;
            while (win && opponent < OPPONENTS) {
                win = (myHandStrength >= getBestHandStrength(opponentHands[opponent++], possibleTable));
            }

            if (win) roundsWon++;
        }

        return Math.floor(10000 * (((double) roundsWon) / ITERATIONS)) / 100;
    }

    /**
     * Calculates the strength of a given hand and table. All possibilities are
     * explored. All possibilities of picking two cards from the hand combined
     * with all possibilities of picking three cards from the table.
     * @param hand int array representing the hand
     * @param table int array representing the table
     * @return int representing the strength of the hand
     */
    private static int getBestHandStrength(int[] hand, int[] table) {
        int strength = 0;

        // Run through all possibilities of picking two cards from the hand
        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 4; j++) {
                int[] handCards = new int[]{hand[i], hand[j]};

                if (table.length < 3) {
                    // No cards on the table
                    int e = calcHandStrength(handCards, new int[0]);
                    if (e > strength) strength = e;
                }
                else {
                    // Run through all possibilities of picking 3 cards from the table
                    for (int x = 0; x < table.length - 2; x++) {
                        for (int y = x + 1; y < table.length - 1; y++) {
                            for (int z = y + 1; z < table.length; z++) {
                                int[] tableCards = new int[]{table[x], table[y], table[z]};
                                int e = calcHandStrength(handCards, tableCards);
                                if (e > strength) strength = e;
                            }
                        }
                    }
                }
            }
        }
        return strength;
    }
    
    private static int calcHandStrength(int[] handCards, int[] tableCards) {
        int[] cards = Arrays.copyOf(tableCards,  tableCards.length + handCards.length);
        System.arraycopy(handCards, 0, cards, tableCards.length, handCards.length);

        // Reverse sort the array
        Arrays.sort(cards);
        for (int i = 0; i < cards.length / 2; i++) {
            int tmp = cards[i];
            cards[i] = cards[cards.length - 1 - i];
            cards[cards.length - 1 - i] = tmp;
        }

        /**
         * The number of free cards that should be used in the calculation of
         * the hand value. For ONE_PAIR this is 3 as there are three cards that
         * that should be evaluated in case of a tie.
         */
        int freeCards = 5;

        /**
         * The number of the best hand seen so far.
         *
         * (0) ONE_PAIR
         * (1) TWO_PAIR
         * (2) THREE_OF_A_KIND
         * (3) STRAIGHT
         * (4) FLUSH
         * (5) FULL_HOUSE
         * (6) FOUR_OF_A_KIND
         * (7) STRAIGHT_FLUSH
         *
         * Corresponds to the ranksOfHand array
         */
        int bestHand = -1;

        /**
         * Each entry represents a type of hand.
         * (0) ONE_PAIR (1) TWO_PAIR (2) THREE_OF_A_KIND (3) STRAIGHT
         * (4) FLUSH (5) FULL_HOUSE (6) FOUR_OF_A_KIND (7) STRAIGHT_FLUSH
         */
        boolean[] handTypes = new boolean[]{false, false, false, false, false, false, false, false};
        int[] flushCounter = new int[]{0, 0, 0, 0};

        /**
         * This holds the ranks of the hand we have. If we have one pair it will
         * hold the rank of the pair as well as the kicker. If we have two pairs
         * it will hold the rank of the lowest pair and the highest pair.
         *
         * (0) ONE_PAIR        [pair rank      , -1]
         * (1) TWO_PAIR        [high pair rank , low pair rank]
         * (2) THREE_OF_A_KIND [rank of 3 kind , -1]
         * (3) STRAIGHT        [highest card   , -1]
         * (4) FLUSH           [highest card   , -1]
         * (5) FULL_HOUSE      [rank of 3 kind , rank of pair]
         * (6) FOUR_OF_A_KIND  [rank of 4 kind , -1]
         * (7) STRAIGHT_FLUSH  [highest card   , -1]
         *
         * Generalized [most significant, other which is part of the hand]
         */
        int[] ranksOfHand = new int[]{-1, -1};

        // Process first card
        int thisCard = cards[0];
        int suit = thisCard % 4;
        flushCounter[suit]++;

        // Run through cards and check rank only, i.e. ONE_PAIR, TWO_PAIR,
        // THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND
        int cardsOfThisRank = 1;
        for (int i = 1; i < cards.length; i++) {
            thisCard = cards[i];
            suit = thisCard % 4;

            flushCounter[suit]++;

            // This and previous card has same rank
            if (getRank(cards[i - 1]) == getRank(thisCard)) {
                cardsOfThisRank++;

                if (cardsOfThisRank == 2 && bestHand < 0) {
                    // ONE_PAIR from HIGH_CARD
                    handTypes[0] = true;
                    bestHand = 0;
                    ranksOfHand[0] = cards[i - 1];
                    freeCards = 3;
                } else if (cardsOfThisRank == 2) {
                    // TWO_PAIR from ONE_PAIR
                    handTypes[1] = true;
                    handTypes[0] = false;
                    bestHand = 1;
                    ranksOfHand[1] = cards[i - 1];
                    freeCards = 1;
                } else if (cardsOfThisRank == 3 && bestHand == 0) {
                    // THREE_OF_A_KIND from ONE_PAIR
                    handTypes[2] = true;
                    handTypes[0] = false;
                    bestHand = 2;
                    freeCards = 0;
                } else if (cardsOfThisRank == 2 && bestHand == 2) {
                    // FULL_HOUSE from THREE_OF_A_KIND (5, 5, 5, 7, _7_)
                    handTypes[2] = false;
                    handTypes[5] = true;
                    bestHand = 5;
                    ranksOfHand[1] = cards[i - 1];
                    freeCards = 0;
                } else if (cardsOfThisRank == 3 && bestHand == 1) {
                    // FULL_HOUSE from TWO_PAIRS (5, 5, 7, 7, _7_)
                    handTypes[1] = false;
                    handTypes[5] = true;
                    bestHand = 5;
                    ranksOfHand[1] = ranksOfHand[0];
                    ranksOfHand[0] = cards[i - 1];
                    freeCards = 0;
                } else if (cardsOfThisRank == 4) {
                    // FOUR_OF_A_KIND from WHATEVER
                    // TODO check if bestHand is always THREE_OF_A_KIND
                    handTypes[6] = true;
                    handTypes[bestHand] = false;
                    bestHand = 6;
                    freeCards = 0;
                    break;
                }
            } else {
                cardsOfThisRank = 1;
            }
        }

        // Check for STRAIGHT, FLUSH, and STRAIGHT_FLUSH
        if (bestHand < 0 && cards.length == 5) {
            // Check for STRAIGHT
            handTypes[3] = (((cards[0] >> 2) - (cards[4] >> 2) == 4) || (cards[4] >> 2 == 3 && cards[0] > 47));

            // Check for STRAIGHT_FLUSH
            handTypes[7] = handTypes[3] && flushCounter[cards[0] % 4] == 5;

            if (handTypes[3] || handTypes[7]) {
                ranksOfHand[0] = cards[0];
                freeCards = 0;
            }

            // Check for FLUSH
            if (flushCounter[cards[0] % 4] == 5) {
                ranksOfHand[0] = cards[0];
                handTypes[4] = true;
                freeCards = 5;
            }
        }

        int handValue = 0;
        int t = 28561; // Multiplication factor

        // Assign a high weight to the cards that affect the hand value, e.g. the rank of a pair
        if (ranksOfHand[0] != -1) {
            handValue = (ranksOfHand[0] >> 2) * t;
            if (ranksOfHand[1] != -1) {
                t = t / 13;
                handValue += (ranksOfHand[1] >> 2) * t;
            }
        }

        // Increase the hand value according to the cards not used in the main hand
        int s = 0;
        while (freeCards > 0 && s < cards.length) {
            if (cards[s] != ranksOfHand[0] && (ranksOfHand[1] == -1 || cards[s] != ranksOfHand[1])) {
                freeCards--;
                t /= 13;
                handValue += getRank(cards[s]) * t;
            }
            s++;
        }

        if (handTypes[7]) return 8000000 + handValue;
        else if (handTypes[6]) return 7000000 + handValue;
        else if (handTypes[5]) return 6000000 + handValue;
        else if (handTypes[4]) return 5000000 + handValue;
        else if (handTypes[3]) return 4000000 + handValue;
        else if (handTypes[2]) return 3000000 + handValue;
        else if (handTypes[1]) return 2000000 + handValue;
        else if (handTypes[0]) return 1000000 + handValue;
        else return handValue;
    }

    private static int getRank(int card) {
        return card >> 2;
    }

    /**
     * To calculate probabilities we need to have a full hand and more than
     * two cards on the table.
     * @return whether we have a full hand and more than two cards on the table
     */
    private boolean isLegalState() {
        return this.handCnt == 4 && (this.tableCnt > 2 || this.tableCnt == 0);
    }

    /**
     * This class represents the rest of the deck, i.e. the cards that is not in
     * the players hand or on the table.
     */
    private class Deck {
        int[] cards;
        Random rnd;

        private Deck(int[] c) {
            rnd = new Random();

            if (c.length != 0) {
                Arrays.sort(c);

                // Create array with the cards still in the deck
                int d = 52 - c.length;
                this.cards = new int[d];
                int e = 0;
                int f = 0;
                for (int i = 0; i < 52; i++) {
                    if (e >= c.length || i < c[e]) {
                        this.cards[f++] = i;
                    }
                    else if (i == c[e]) {
                        e++;
                    }
                    else {
                        e++;
                        i--;
                    }
                }
            } else {
                // c is empty, just put the whole deck in the cards array
                this.cards = new int[52];
                for (int i = 0; i < 52; i++) this.cards[i] = i;
            }
        }

        public void shuffle() {
            for (int i = cards.length - 1; i > 0; i--) {
                int index = rnd.nextInt(i + 1);

                int a = cards[index];
                cards[index] = cards[i];
                cards[i] = a;
            }
        }

        public int getCard(int x) {
            return this.cards[x];
        }
    }

}
