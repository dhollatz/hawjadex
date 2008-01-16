package jadex.examples.puzzle.humanplayer;

import jadex.adapter.fipa.Done;
import jadex.examples.puzzle.Board;
import jadex.examples.puzzle.IBoard;
import jadex.examples.puzzle.Move;
import jadex.examples.puzzle.Position;
import jadex.runtime.IMessageEvent;
import jadex.runtime.Plan;

import java.util.List;

/**
 *  Make a requested move.
 */
public class MakeMovePlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		IMessageEvent me = (IMessageEvent)getInitialEvent();
		RequestMove rm = (RequestMove)me.getContent();
		//Move move = rm.getMove();
		Position start = rm.getStart();

		IBoard board = (Board)getBeliefbase().getBelief("board").getFact();
		List pmoves = board.getPossibleMoves();
		boolean moved = false;
		for(int i=0; i<pmoves.size() && !moved; i++)
		{
			Move m = (Move)pmoves.get(i);
			if(m.getStart().equals(start))
			{
				board.move(m);
				moved = true;
			}
		}
		if(!moved)
		{
			System.out.println("Cannot make move with piece on: "+start);
			sendMessage(me.createReply("failure"));
		}
		else
		{
			sendMessage(me.createReply("inform_action_done", new Done(rm)));
		}
	}
}
