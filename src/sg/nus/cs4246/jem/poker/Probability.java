package sg.nus.cs4246.jem.poker;

import java.util.Arrays;
import java.util.Random;

public class Probability {

    public static final int OPPONENTS = 1;
    public static final int ITERATIONS = 1000;

    private Cards[] hand;     // Represents the hand
    private Cards[] table;    // Represents to table
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
        CA, DA, HA, SA;

        public int rank() {
            return (ordinal() >> 2) + 2;
        }

        public int suit() {
            return ordinal() % 2;
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
        Deck deck = new Deck(hand, table);
        int roundsWon = 0;

        Cards[][] opponentHands = new Cards[OPPONENTS][4];
        Cards[] possibleTable = Arrays.copyOf(table, 5);

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
    private static int getBestHandStrength(Cards[] hand, Cards[] table) {
        int strength = 0;

        // Run through all possibilities of picking two cards from the hand
        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 4; j++) {
                Cards[] handCards = new Cards[]{hand[i], hand[j]};

                if (table.length < 3) {
                    // No cards on the table
                    int e = calcHandStrength(handCards, new Cards[0]);
                    if (e > strength) strength = e;
                }
                else {
                    // Run through all possibilities of picking 3 cards from the table
                    for (int x = 0; x < table.length - 2; x++) {
                        for (int y = x + 1; y < table.length - 1; y++) {
                            for (int z = y + 1; z < table.length; z++) {
                                Cards[] tableCards = new Cards[]{table[x], table[y], table[z]};
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
    
    private static int calcHandStrength(Cards[] handCards, Cards[] tableCards) {
        Cards[] cards = Arrays.copyOf(tableCards,  tableCards.length + handCards.length);
        System.arraycopy(handCards, 0, cards, tableCards.length, handCards.length);

        // Reverse sort the array
        Arrays.sort(cards);
        for (int i = 0; i < cards.length / 2; i++) {
            Cards tmp = cards[i];
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
        Hands bestHand = Hands.HIGH_CARD;

        /**
         * Keeps track of whether or not a flush is possible
         */
        boolean flush = true;

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
        Cards[] mostSignificantCards = new Cards[]{null, null};

        // Run through cards and check rank only, i.e. ONE_PAIR, TWO_PAIR,
        // THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND
        int cardsOfThisRank = 1;
        for (int i = 1; i < cards.length; i++) {
            Cards thisCard = cards[i];

            flush = flush && cards[i - 1].suit() != thisCard.suit();

            // This and previous card has same rank
            if (cards[i - 1].rank() == thisCard.rank()) {
                cardsOfThisRank++;

                if (cardsOfThisRank == 2 && bestHand == Hands.HIGH_CARD) {
                    // ONE_PAIR from HIGH_CARD
                    bestHand = Hands.ONE_PAIR;
                    mostSignificantCards[0] = cards[i - 1];
                    freeCards = 3;
                } else if (cardsOfThisRank == 2) {
                    // TWO_PAIR from ONE_PAIR
                    bestHand = Hands.TWO_PAIR;
                    mostSignificantCards[1] = cards[i - 1];
                    freeCards = 1;
                } else if (cardsOfThisRank == 3 && bestHand == Hands.ONE_PAIR) {
                    // THREE_OF_A_KIND from ONE_PAIR
                    bestHand = Hands.THREE_OF_A_KIND;
                    freeCards = 0;
                } else if (cardsOfThisRank == 2 && bestHand == Hands.THREE_OF_A_KIND) {
                    // FULL_HOUSE from THREE_OF_A_KIND (5, 5, 5, 7, _7_)
                    bestHand = Hands.FULL_HOUSE;
                    mostSignificantCards[1] = cards[i - 1];
                    freeCards = 0;
                } else if (cardsOfThisRank == 3 && bestHand == Hands.TWO_PAIR) {
                    // FULL_HOUSE from TWO_PAIRS (5, 5, 7, 7, _7_)
                    bestHand = Hands.FULL_HOUSE;
                    mostSignificantCards[1] = mostSignificantCards[0];
                    mostSignificantCards[0] = cards[i - 1];
                    freeCards = 0;
                } else if (cardsOfThisRank == 4) {
                    // FOUR_OF_A_KIND from WHATEVER
                    // TODO check if bestHand is always THREE_OF_A_KIND
                    bestHand = Hands.FOUR_OF_A_KIND;
                    freeCards = 0;
                    break;
                }
            } else {
                cardsOfThisRank = 1;
            }
        }

        // Check for STRAIGHT, FLUSH, and STRAIGHT_FLUSH
        if (bestHand == Hands.HIGH_CARD && cards.length == 5) {
            boolean straight = cards[0].rank() - cards[4].rank() == 4 || (cards[4].rank() == 5 && cards[0].rank() == 14);

            if (straight || flush) {
                mostSignificantCards[0] = cards[0];
                freeCards      = flush ? 5 : 0;
                bestHand       = (straight && flush) ? Hands.STRAIGHT_FLUSH : (straight ? Hands.STRAIGHT : Hands.FLUSH);
            }
        }

        int handValue = 0;
        int t         = 28561; // Multiplication factor (13^4)

        // Assign a high weight to the cards that affect the hand value, e.g. the rank of a pair
        if (mostSignificantCards[0] != null) {
            handValue = mostSignificantCards[0].rank() * t;
            if (mostSignificantCards[1] != null) {
                t         /= 13;
                handValue += mostSignificantCards[1].rank() * t;
            }
        }

        // Increase the hand value according to the cards not used in the main hand
        int s = 0;
        while (freeCards > 0 && s < cards.length) {
            if (cards[s] != mostSignificantCards[0] && (mostSignificantCards[1] == null || cards[s] != mostSignificantCards[1])) {
                freeCards--;
                t         /= 13;
                handValue += cards[s].rank() * t;
            }
            s++;
        }

        switch (bestHand) {
            case STRAIGHT_FLUSH:  handValue += 8000000; break;
            case FOUR_OF_A_KIND:  handValue += 7000000; break;
            case FULL_HOUSE:      handValue += 6000000; break;
            case FLUSH:           handValue += 5000000; break;
            case STRAIGHT:        handValue += 4000000; break;
            case THREE_OF_A_KIND: handValue += 3000000; break;
            case TWO_PAIR:        handValue += 2000000; break;
            case ONE_PAIR:        handValue += 1000000; break;
        }

        return handValue;
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
        }

        public void shuffle() {
            for (int i = cards.length - 1; i > 0; i--) {
                int index = rnd.nextInt(i + 1);

                Cards a = cards[index];
                cards[index] = cards[i];
                cards[i] = a;
            }
        }

        public Cards getCard(int x) {
            return this.cards[x];
        }
    }

}
