#omaha

http://theaigames.com/competitions/heads-up-omaha

### Rules
#### Structure of the game
The game is a pot-limit Omaha Hold 'em poker variant in which two bots battle each other heads up. Omaha is a poker variant that is a bit less known than Texas Hold 'em, but very similar in structure. However, the strategies in this game are very different!

In Omaha players are dealt 4 hole cards (cards in the hand), where in Texas Hold 'em they get 2. The community cards (cards on the table) are dealt exactly the same: flop, turn and river. At showdown there's a difference again. The players' hand is the best combination of cards of exactly 2 hole cards and exactly 3 community cards. In Texas Hold 'em more than 3 community cards are allowed for the strongest hand.

In each match, both bots start with the same amount of chips. This amount will be provided by the engine to your bot at the start of the game. The usual poker rules for heads-up play apply. This means that the bot that has the dealer button receives the small blind and the other bot the big blind. Before the flop the bot with the small blind has to act first, but in the betting rounds after the flop the other bot has to act first each time. The match continues until one of the bots has won all the chips. So the matches are played like two player tournaments. This game is pot limit, so a bot can never raise more than the size of the current pot.

#### List of poker hands
Below you will find a list of possible hands in poker, ranked from highest to lowest. If two hands are the same, the one with the higher value cards in that hand wins. For example, a pair of aces wins over a pair of fives.
1. Royal flush	
2. Straight flush	
3. Four of a kind	
4. Full house	
5. Flush	
6. Straight	
7. Three of a kind	
8. Two pair	
9. One pair	
10. High card	

#### Blind levels
In each match the blinds are small at the start of the match. The blinds are increased each time after a fixed number of hands has been played. This keeps the game dynamic and gives your bot more strategic possibilities. It also ensures that matches will not go on endlessly, since the blinds eventually will become so high that eventually the bots are forced to go all in by paying the blinds. But of course there are enough hands to play to eliminate the opponent with a good strategy, before the luck factor becomes too high.

#### Winning
In this game type your bot plays against one other bot. Your bot will not know which other bot he's playing against at the start of the game, so your bot cannot use predefined strategies based on opponent's identities. (By the way, this is not allowed at all on this site.) This does not mean your bot can't gather information about the strategies of your opponent during the game! Then use this information against him to win the match.

Your bot wins when he gets all the opponents chips. The number of hands played are not relevant. Easy, right?

#### Technical details
During a match, bots are asked to make moves. Each bot starts a new match with a time bank. The time that your bot takes to return its next action is deducted from the time bank. But the time bank is increased with a small amount for each action your bot has to make. For example, the initial time bank could be 5 seconds and the time added per move half a second. That would mean that your bot should on average react in half a second. If your bot keeps reacting within half a second, then your time bank will stay at the maximum amount, giving your bot the opportunity to perform larger calculations when needed. If your bot then once uses 4 seconds to return its move, then it has only 1 second of its time bank left, which together with the added time brings the time bank for the next move down to 1.5 seconds. So bots should decide on their moves quite fast, but the time bank gives your bot the flexibility to think a little longer now and then. The amount of time per action and the maximal time bank are given to the bot at the start of a match. Currently we start with a time bank of 5 seconds and 0.5 seconds added time per move.

Besides the time bank, there is also two other measures against bad bots, that could keep our game server occupied. Firstly, if your bot returns nothing more than twice in a row, all it's following moves are automatically a check. To avoid this happening to your bot, make sure it returns at least something in time, like a check or a fold. Secondly, the maximum amount of time your bot can run now is 5 minutes. This should be more than enough, as games take about 4-5 seconds on average.

###Pot-Limit Omaha
The popularity of Pot-Limit Omaha has surged recently, to the point that this particular Omaha variation is now the second most played poker variant both online and live. In fact, it's not uncommon for 100% of the night's online high-stakes action to take place over Omaha tables.

1. Betting proceeds clockwise from the button. The player to the left of the button is the small blind and the player on his left is the big blind. The player on his left is under the gun, and acts first.
2. His options are to call the big blind, raise or fold.
3. Your minimum bet is equal to the size of the big blind (this is assuming no players have bet before you on this betting round).
4. To determine the maximum bet, count all the money in the pot and all the bets on the table, including any call you would make before raising. (It sounds more complicated than it really is.) Two examples for you:
 - You're first to act on the flop with a pot of $15. You have the option to check or bet. You can bet anywhere from as little as the amount of the big blind, to the full amount of the pot ($15). Any bet in between is a "legal bet."
 - You're second to act on the flop with a pot of $15. The first player bets $10. You now have the option to fold, call ($10) or raise.
5. Your minimum raise is equal to the amount of the previous bet. In this hand your minimum raise is $10 ($10 + $10 for a total bet of $20).
6. Your maximum raise is the amount of the pot. To figure this out, add up the pot + the bet + your call ($15 + $10 + $10 = $35). You are allowed to bet that total amount in addition to your call, meaning your total bet is $45 ($10 for the call + $35 for the size of the pot).
7. You can raise any amount in between the minimum and the maximum raise amount.
8. The size of the game is determined by the blind size. The buy-in is usually minimum 20 big blinds and maximum 100 big blinds.

sources: 
http://theaigames.com/competitions/heads-up-omaha/rules
http://www.pokerlistings.com/omaha-poker-betting-rules

