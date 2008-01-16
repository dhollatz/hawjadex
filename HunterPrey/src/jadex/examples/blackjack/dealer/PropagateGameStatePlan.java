package jadex.examples.blackjack.dealer;

import jadex.examples.blackjack.GameState;
import jadex.examples.blackjack.Player;
import jadex.runtime.IMessageEvent;
import jadex.runtime.IParameterSet;
import jadex.runtime.Plan;
import jadex.util.SUtil;

import java.util.List;

/**
 *  Updates the belief "gamestate" and propage the gamestate.
 */
public class PropagateGameStatePlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		//System.out.println("propagating..");

		GameState gs = (GameState)getBeliefbase().getBelief("gamestate").getFact();
		Player me = (Player)getBeliefbase().getBelief("myself").getFact();

		if(gs.getDealer()==null)
			gs.setDealer(me);

		List olds = SUtil.arrayToList(gs.getPlayers());
		Player[] news = (Player[])getBeliefbase().getBeliefSet("players").getFacts();
		for(int i=0; i<news.length; i++)
		{
			olds.remove(news[i]);
			// Don't add myself to the game state.
			if(!news[i].equals(me))
				gs.updateOrAddPlayer(news[i]);
		}
		for(int i=0; i<olds.size(); i++)
		{
			gs.removePlayer((Player)olds.get(i));
		}

		Player[] players = (Player[])getBeliefbase().getBeliefSet("players").getFacts();
		if(players.length>0)
		{
			IMessageEvent	inform	= createMessageEvent("inform_game_state");
			inform.setContent(gs);
			IParameterSet rec = inform.getParameterSet("receivers");
			for(int i=0; i<players.length; i++)
				if(!rec.containsValue(players[i].getAgentID()))
					rec.addValue(players[i].getAgentID());
			sendMessage(inform);
		}
	}
}
