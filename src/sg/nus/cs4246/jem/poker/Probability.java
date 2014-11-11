package sg.nus.cs4246.jem.poker;

import java.util.Arrays;
import java.util.Random;

public class Probability {

    public static final int ITERATIONS = 10000;

    private Cards[] hand;     // Represents the hand
    private Cards[] table;    // Represents to table
    private int handCnt;    // How many cards in on the hand (must be 4)
    private int tableCnt;   // How many cards are on the table

    private enum Hands implements Comparable<Hands> {
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

    public enum Cards {
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
        CA, DA, HA, SA;
        
        public int rank;
        public int suit;
        
        private Cards() {
            rank = (ordinal() >> 2) + 2;
            suit = ordinal() % 2;
        }
    }

    public static void main(String[] args) {
        Cards[] hand  = new Cards[]{
                Cards.C2,
                Cards.C4,
                Cards.H4,
                Cards.HT
        };
        Cards[] board = new Cards[]{
                Cards.CT,
                Cards.ST,
                Cards.DT
        };

        Probability p = new Probability(hand, board);
        p.calculate();
    }

    public Probability(Cards[] hand, Cards[] table) {
        this.hand  = hand;
        this.table = table;

        this.handCnt  = hand.length;
        this.tableCnt = table.length;
    }

    // Start the calculation
    public double calculate() {
        // If the number of cards in the hand or on the table are not correct
        if (!isLegalState()) throw new IllegalStateException("State is not valid");

        // Calculate the probability of winning
        return estimateChanceOfWinning();
    }

    /**
     * Calculates the chance of winning
     * @return probability of winning the game
     */
    private double estimateChanceOfWinning() {
        // Create one array containing both cards on hand and on table
        Deck deck = new Deck(hand, table);
        int roundsWon = 0;

        Cards[] opponentHand = new Cards[4];
        Cards[] possibleTable = Arrays.copyOf(table, 5);

        // Play a number of random games, and count how many games we win
        for (int i = 0; i < ITERATIONS; i++) {
            deck.shuffle();

            // Fill the rest of the table and the opponents hand with random cards
            for (int j = tableCnt; j < 5; j++) possibleTable[j] = deck.drawCard();
            for (int k = 0; k < 4; k++) opponentHand[k] = deck.drawCard();

            int myHandStrength  = getBestHandStrength(hand, possibleTable);
            int oppHandStrength = getBestHandStrength(opponentHand, possibleTable);

            if (myHandStrength >= oppHandStrength) roundsWon++;
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
    private static int getBestHandStrength(Cards[] hand, Cards[] table) {
        int bestStrength = -1;
        for (Cards[] permutation : getHandPermutations(hand, table)) {
            int thisStrength = calcHandStrength(permutation);
            if (thisStrength > bestStrength) bestStrength = thisStrength;
        }
        return bestStrength;
    }

    /**
     * Finds the best hand type from the cards on hand and on table
     * @return the best hand type given hand and table
     */
    private static Hands getBestHandType(Cards[] hand, Cards[] table) {
        Hands bestHand = Hands.HIGH_CARD;
        for (Cards[] permutation : getHandPermutations(hand, table)) {
            Hands type = getHandType(permutation);
            if (type.compareTo(bestHand) > 0) bestHand = type;
        }
        return bestHand;
    }

    private static Cards[][] getHandPermutations(Cards[] hand, Cards[] table) {

        /**
         * Calculate the number of permutations
         */

        int p = 6;
        switch(table.length) {
            case 4: p *= 4;  break;
            case 5: p *= 10; break;
        }
        Cards[][] permutations = new Cards[p][table.length > 2 ? 5 : 2];

        /**
         * Generate all the possible permutations of chosen cards
         */

        int permutationCnt = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 4; j++) {
                Cards[] handCards = new Cards[]{hand[i], hand[j]};

                if (table.length < 3) {
                    // No cards on the table
                    System.arraycopy(handCards, 0, permutations[permutationCnt], 0, 2);
                    permutationCnt++;
                }
                else {
                    // Run through all possibilities of picking 3 cards from the table
                    for (int x = 0; x < table.length - 2; x++) {
                        for (int y = x + 1; y < table.length - 1; y++) {
                            for (int z = y + 1; z < table.length; z++) {
                                Cards[] tableCards = new Cards[]{table[x], table[y], table[z]};

                                System.arraycopy(handCards, 0, permutations[permutationCnt], 0, 2);
                                System.arraycopy(tableCards, 0, permutations[permutationCnt], 2, 3);

                                permutationCnt++;
                            }
                        }
                    }
                }
            }
        }

        for (Cards[] cards : permutations) {
            /**
             * Sort the cards in descending order
             */
            Arrays.sort(cards);
            for (int i = 0; i < cards.length / 2; i++) {
                Cards tmp = cards[i];
                cards[i] = cards[cards.length - 1 - i];
                cards[cards.length - 1 - i] = tmp;
            }
        }

        return permutations;
    }

    private static Hands getHandType(Cards[] cards) {
        if (cards.length == 2) {
            return cards[0].rank == cards[1].rank ? Hands.ONE_PAIR : Hands.HIGH_CARD;
        }

        // We have 5 cards

        boolean ad =             cards[0].rank == cards[3].rank;
        boolean ac = ad ||       cards[0].rank == cards[2].rank;
        boolean ab = ac ||       cards[0].rank == cards[1].rank;
        boolean be =             cards[1].rank == cards[4].rank;
        boolean bd = ad || be || cards[1].rank == cards[3].rank;
        boolean bc = ac || bd || cards[1].rank == cards[2].rank;
        boolean ce = be ||       cards[2].rank == cards[4].rank;
        boolean cd = bd ||       cards[2].rank == cards[3].rank;
        boolean de = ce ||       cards[3].rank == cards[4].rank;

        /**
         * Check for straight and flush
         */
        boolean flush    = true;
        boolean straight = true;
        for (int i = 1; i < cards.length; i++) {
            straight = straight && cards[i - 1].rank == cards[i].rank + 1;
            flush    = flush && cards[i - 1].suit != cards[i].suit;
        }

        if (straight && flush)                return Hands.STRAIGHT_FLUSH;
        if (ad || be)                         return Hands.FOUR_OF_A_KIND;
        if ((ac && de) || (ab && ce))         return Hands.FULL_HOUSE;
        if (flush)                            return Hands.FLUSH;
        if (straight)                         return Hands.STRAIGHT;
        if (ac || bd || ce)                   return Hands.THREE_OF_A_KIND;
        if ((ab && (cd || de)) || (bc && de)) return Hands.TWO_PAIR;
        if (ab || bc || cd || de)             return Hands.ONE_PAIR;
        return Hands.HIGH_CARD;
    }
    
    private static int calcHandStrength(Cards[] cards) {

        /**
         * Find the cards that makes the hand type and the "free cards". There are a
         * maximum of two hand cards (two pair and full house have two hand cards while
         * all other only have one, except for high card that has none)
         */

        Hands handType = getHandType(cards);
        int handValue = 0;
        switch (handType) {
            case STRAIGHT_FLUSH:
                handValue = cards[0].rank;
                break;
            case FOUR_OF_A_KIND:
                handValue = cards[1].rank; // take index 1 as it is always part of the four of a kind
                break;
            case STRAIGHT:
                handValue = cards[0].rank;
                break;
            case FULL_HOUSE:
                int threeRank = cards[2].rank;
                int twoRank = cards[0].rank < threeRank ? cards[0].rank : cards[4].rank;
                handValue = (threeRank << 3) + twoRank;
                break;
            case FLUSH:
                handValue += cards[0].rank << 12;
                handValue += cards[1].rank << 9;
                handValue += cards[2].rank << 6;
                handValue += cards[3].rank << 3;
                handValue += cards[4].rank;
                break;
            case THREE_OF_A_KIND:
                threeRank = cards[2].rank;
                handValue = threeRank << 6;
                int cnt = 1;
                for (Cards c : cards) {
                    if (c.rank != threeRank) {
                        handValue += c.rank << (cnt * 3);
                        cnt--;
                    }
                }
                break;
            case TWO_PAIR:
                int hiPairRank = cards[1].rank;
                int loPairRank = cards[2].rank;
                handValue = (hiPairRank << 6) + (loPairRank << 3);
                if (cards[0].rank != hiPairRank) handValue += cards[0].rank;
                else if (cards[4].rank != loPairRank) handValue += cards[4].rank;
                else handValue += cards[2].rank;
                break;
            case ONE_PAIR:
                int opshift = 2;
                for (int i = 0; i < cards.length; i++) {
                    if (i + 1 < cards.length && cards[i].rank == cards[i + 1].rank) {
                        handValue += cards[i].rank << 9;
                        i++; // Skip next card
                    } else {
                        handValue += cards[i].rank << (3 * opshift);
                        opshift--;
                    }
                }
                break;
            case HIGH_CARD:
                int hcshift = 4;
                for (Cards card : cards) {
                    handValue += card.rank << (3 * hcshift);
                    hcshift--;
                }
                break;
        }

        return (handType.ordinal() * 1000000) + handValue;
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
        Cards[] cards;
        int nextCardPtr;
        Random rnd;

        private Deck(Cards[] hand, Cards[] table) {
            rnd = new Random();

            // Join hand and table into one array of cards
            Cards[] handAndTable = Arrays.copyOf(hand, handCnt + tableCnt);
            System.arraycopy(table, 0, handAndTable, handCnt, tableCnt);

            // Create an array containing all remaining cards, i.e. not in hand or on table
            if (handAndTable.length != 0) {
                Arrays.sort(handAndTable);

                // Initialize variables
                this.cards = new Cards[52 - handAndTable.length];
                int prevOrdinal = -1;
                int destPtr = 0;

                // Copy all cards except for the ones on hand or table
                for (Cards c : handAndTable) {
                    // Skip if the card is right after the previous one (avoid copy of size 0)
                    if (c.ordinal() == prevOrdinal + 1) {
                        prevOrdinal++;
                        continue;
                    }

                    int chunkSize = c.ordinal() - prevOrdinal - 1;
                    System.arraycopy(Cards.values(), prevOrdinal + 1, cards, destPtr, chunkSize);
                    destPtr += chunkSize;
                    prevOrdinal = c.ordinal();
                }

                // Copy the rest of the cards
                System.arraycopy(Cards.values(), prevOrdinal + 1, cards, destPtr, 52 - prevOrdinal - 1);
            } else {
                // c is empty, just put the whole deck in the cards array
                cards = Cards.values();
            }

            nextCardPtr = 0;
        }

        public void shuffle() {
            for (int i = cards.length - 1; i > 0; i--) {
                int index = rnd.nextInt(i + 1);

                Cards a = cards[index];
                cards[index] = cards[i];
                cards[i] = a;
            }

            nextCardPtr = 0;
        }

        public Cards drawCard() {
            return this.cards[nextCardPtr++];
        }
    }

}
