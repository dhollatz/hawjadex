package jadex.examples.puzzle.humanplayer;

import jadex.adapter.fipa.Done;
import jadex.examples.puzzle.Board;
import jadex.examples.puzzle.IBoard;
import jadex.runtime.IMessageEvent;
import jadex.runtime.Plan;

/**
 *  Takeback a requested move.
 */
public class TakebackPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		IMessageEvent me = (IMessageEvent)getInitialEvent();
		RequestTakeback rt = (RequestTakeback)me.getContent();

		IBoard board = (Board)getBeliefbase().getBelief("board").getFact();
		if(board.takeback())
		{
			sendMessage(me.createReply("inform_action_done", new Done(rt)));
		}
		else
		{
			System.out.println("Cannot takeback move.");
			sendMessage(me.createReply("failure"));
		}
	}
}
