package sg.nus.cs4246.jem.poker;

import java.util.Random;

//Here we assume we are playing first, we then have to decide how much we are going to bet
// (folding is nerver a good option here as we can always bet 0 instead)

public class BetStrategy {

    public enum Round {PRE_FLOP, FLOP, TURN, RIVER}

    /**
     * It is assumed that we have payed the small blind and the
     * opponent has payed the small blind. This is only for the
     * first round.
     *
     * @param probabilityOfWin probability to win given our hand and the table
     * @param round the current round
     * @param smallBlind size of the small blind
     * @param chips the amount of chips we have
     * @param potAmount: the amount in the pot in the center of the table.
     * @return the amount of chips to bet (-1 means fold)
     */
    public int getBetAmount(double probabilityOfWin, Round round, int smallBlind, int chips,int potAmount) {
//        double foldPayoff   = -1 * smallBlind; //TODO when we are little blind, we begin twice, then the other player will begin every other rounds
//        double[] betPayoff  = getBetPayoff(probabilityOfWin, smallBlind, round);
//
//        double intersection = (foldPayoff - betPayoff[1]) / betPayoff[0];
//        int maxBetAmount    = selectBetAmount(chips);
//
//        // We will only bet if our selected bet amount yields a higher expected reward than folding
//        if (maxBetAmount < intersection) return -1;

        double foldPayoff = -potAmount;
//        return maxBetAmount;
        return 0;
    }

    /**
     * We select our bet amount according to a logarithmic function because we
     * are afraid of loosing too much, so we are more reluctant to bet higher
     * amounts //TODO be careful, the logarithm function must depend on the total amount of money the player still have AND the probability to win
     * @param chips the amount of chips we have
     * @return the highest suggested bet amount
     */
    private int selectBetAmount(int chips) {
        // TODO decide from a bell curve as proposed by JB instead of this linear function
        int maxBet = maxBetGivenChips(chips);
        Random r = new Random();
        double rnd = r.nextDouble();
        return (int) (rnd * maxBet);
    }

    /**
     * Calculate a linear relation between bet size and payoff
     * @param p probability to win round
     * @param b size of the small blind
     * @param r the current round
     * @return linear function defining payoff given bet size
     */
    private double[] getBetPayoff(double p, int b, Round r) {
        double y0 = getPayoff(p, b, 0, r);
        double y1 = getPayoff(p, b, 100, r);
        double slope = (y1 - y0) / 100;

        return new double[]{slope, y0};
    }

    /**
     * Calculates the payoff we expect to get from a bet of x chips
     * @param winProb probability to win given our hand and the table
     * @param b size of the small blind
     * @param x bet size
     * @param round the current round
     * @return the expected payoff
     */
    private double getPayoff(double winProb, int b, int x, Round round) {
        double p = probOpponentFolds(round);
        double t = payoutIfOpponentFoldsInRound(round, b, x);

        double ifBetPayoff = 0.0;

        switch (round) {
            case PRE_FLOP: ifBetPayoff = getPayoff(winProb, b, x, Round.FLOP); break;
            case FLOP:     ifBetPayoff = getPayoff(winProb, b, x, Round.TURN); break;
            case TURN:     ifBetPayoff = getPayoff(winProb, b, x, Round.RIVER); break;
            case RIVER:
                double potSizeAfterAllBetRounds = b + x + betIncrease(1, x) + betIncrease(2, x) + betIncrease(3, x);
                ifBetPayoff = (-0.5 + winProb) * potSizeAfterAllBetRounds;
                break;
        }

        return 2 * p * t + (1-p) * ifBetPayoff;
    }

    /**
     * Calculates the chance that the opponent will fold given a specific round
     * @param round the round of interest
     * @return the probability that the opponent folds in the given round
     */
    private double probOpponentFolds(Round round) {
        // TODO this should be more clever
        switch (round) {
            case PRE_FLOP: return 0.10;
            case FLOP:     return 0.05;
            case TURN:     return 0.02;
            case RIVER:    return 0.02;
            default:       throw new IllegalStateException("The round variable is not valid");
        }
    }

    /**
     * Calculates the payoff we expect to get if the opponent folds in the given round
     * @param foldRound the round that the opponent folds
     * @param b size of the small blind
     * @param x the amount we bet in this round
     * @return the payoff
     */
    private double payoutIfOpponentFoldsInRound(Round foldRound, int b, int x) {
        switch (foldRound) {
            case PRE_FLOP: return b;
            case FLOP:     return b + x;
            case TURN:     return b + x + betIncrease(1, x);
            case RIVER:    return b + x + betIncrease(1, x) + betIncrease(2, x);
            default:       throw new IllegalStateException("The round variable is not valid");
        }
    }

    /**
     * Calculates how much we can maximally bet this round. We can't bet all our chips as
     * that leaves us too few chips to bet in future rounds.
     * @param chips the amount of chips we have
     * @return the maximum amount of chips we can bet this round
     */
    private int maxBetGivenChips(int chips) {
        double y1 = 100 + betIncrease(1, 100) + betIncrease(2, 100) + betIncrease(3, 100);
        double slope = y1 / 100;
        return (int) (chips / slope);
    }

    /**
     * Calculates how much we will have to bet in the next round assuming the probability
     * don't change
     * @param times how many rounds to look ahead
     * @param x the amount we bet now
     * @return the amount we will bet times rounds from now
     */
    private double betIncrease(int times, int x) {
        if (times == 0) return x;
        return  1.1 * betIncrease(times - 1, x);
    }


    public static void main(String[] args) {
//        System.out.println(new BetStrategy().getBetAmount(0.45, Round.PRE_FLOP, 15, 1000));
//        System.out.println(new BetImpl().getBetAmount(0.4415, 15, 1000));
    }
}
