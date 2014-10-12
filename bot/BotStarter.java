/**
 * www.TheAIGames.com 
 * Heads Up Omaha pokerbot
 *
 * Last update: May 07, 2014
 *
 * @author Jim van Eeden, Starapple
 * @version 1.0
 * @License MIT License (http://opensource.org/Licenses/MIT)
 */


package bot;

import poker.Card;
import poker.HandOmaha;
import poker.PokerMove;

import com.stevebrecher.HandEval;

/**
 * This class is the brains of your bot. Make your calculations here and return the best move with GetMove
 */
public class BotStarter implements Bot {

	/**
	 * Implement this method to return the best move you can. Currently it will return a raise the average ordinal value 
	 * of your hand is higher than 9, a call when the average ordinal value is at least 6 and a check otherwise.
	 * As you can see, it will only consider it's current hand, not what's on the table.
	 * @param state : The current state of your bot, with all the (parsed) information given by the engine
	 * @param timeOut : The time you have to return a move
	 * @return PokerMove : The move you will be doing
	 */
	@Override
	public PokerMove getMove(BotState state, Long timeOut) {
		HandOmaha hand = state.getHand();
		String handCategory = getHandCategory(hand, state.getTable()).toString();
		System.err.printf("my hand is %s, opponent action is %s, pot: %d\n", handCategory, state.getOpponentAction(), state.getPot());
		
		// Get the ordinal values of the cards in your hand
		int[] ordinalHand = {
			hand.getCard(0).getHeight().ordinal(),
			hand.getCard(1).getHeight().ordinal(),
			hand.getCard(2).getHeight().ordinal(),
			hand.getCard(3).getHeight().ordinal()
		};
		
		// Get the average ordinal value
		double averageOrdinalValue = 0;
		for(int i=0; i<ordinalHand.length; i++) {
			averageOrdinalValue += ordinalHand[i];
		}
		averageOrdinalValue /= ordinalHand.length;
		
		// Return the appropriate move according to our amazing strategy
		if( averageOrdinalValue >= 9 ) {
			return new PokerMove(state.getMyName(), "raise", 2*state.getBigBlind());
		} else if( averageOrdinalValue >= 5 ) {
			return new PokerMove(state.getMyName(), "call", state.getAmountToCall());
		} else {
			return new PokerMove(state.getMyName(), "check", 0);
		}
	}
	
	/**
	 * Quite a tedious method to check what we have in our hand. With 5 cards on the table we do 60(!) checks: all possible
	 * combinations of 2 out of 4 cards (our hand) times all possible combinations of 3 out of 5 cards (the table).
	 * For less cards on the table we need less calculation. This uses the com.stevebrecher package to get hand strength.
	 * @param hand : cards in hand
	 * @param table : cards on table
	 * @return HandCategory with what the bot has got, given the table and hand
	 */
	public HandEval.HandCategory getHandCategory(HandOmaha hand, Card[] table) {
		int strength = 0;
		
		// Try all possible combinations of 2 out of 4 cards for what we have in our hand (6 possibilities)
		for(int i=0; i<hand.getNumberOfCards()-1; i++) {
			for(int j=i+1; j<hand.getNumberOfCards(); j++) {
				
				if( table == null || table.length == 0 ) { // The table is empty, so we just check what we have in our hand and a pair is the best we can do
					if( hand.getCard(i).getHeight() == hand.getCard(j).getHeight() ) { // If two cards have the same height:
						return HandEval.HandCategory.PAIR; // We found a pair; return that we have a pair
					}
					else if ( i == hand.getNumberOfCards() - 2 && j == hand.getNumberOfCards() - 1 ) { // Last pair of cards
						return HandEval.HandCategory.NO_PAIR; // If we reach this we didn't find a pair, so return NO_PAIR
					}
					
				} else { // There are cards on the table
					long handCode = hand.getCard(i).getNumber() + hand.getCard(j).getNumber();
					
					if ( table.length == 3 ) { // Easy, because we must use all 3 cards on the table for evaluation
						for(int c=0; c<table.length; c++) {
							handCode += table[c].getNumber(); 
						}
						strength = Math.max(strength, HandEval.hand5Eval(handCode));
					}
					else if ( table.length == 4 ) { // We need to evaluate all combinations of 3 out of 4 cards (4 possibilities)
						for(int k=0; k<table.length; k++) {
							handCode = hand.getCard(i).getNumber() + hand.getCard(j).getNumber();
							for(int c=0; c<table.length; c++) 
								if(c != k)
									handCode += table[c].getNumber();
							strength = Math.max(strength, HandEval.hand5Eval(handCode));
						}
					}
					else if ( table.length == 5 ) { // We need to evaluate all combinations of 3 out of 5 cards (10 possibilities)
						for(int k=0; k<table.length-2; k++)
							for(int l=k+1; l<table.length-1; l++)
								for(int m=l+1; m<table.length; m++)
								{
									handCode = hand.getCard(i).getNumber() + hand.getCard(j).getNumber();
									handCode += table[k].getNumber();
									handCode += table[l].getNumber();
									handCode += table[m].getNumber();
									strength = Math.max(strength, HandEval.hand5Eval(handCode));
								}
					}
				}
			}
			
		}
		return rankToCategory(strength);
	}
	
	/**
	 * small method to convert the int 'rank' to a readable enum called HandCategory
	 */
	public HandEval.HandCategory rankToCategory(int rank) {
		return HandEval.HandCategory.values()[rank >> HandEval.VALUE_SHIFT];
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BotParser parser = new BotParser(new BotStarter());
		parser.run();
	}

}
