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


package sg.nus.cs4246.jem.bot;

import sg.nus.cs4246.jem.poker.PokerMove;

public interface Bot {

	public PokerMove getMove(BotState state, Long timeOut);

}
