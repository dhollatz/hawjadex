package jadex.examples.puzzle.humanplayer;

import jadex.adapter.fipa.AgentIdentifier;
import jadex.adapter.fipa.SFipa;
import jadex.examples.puzzle.IBoard;
import jadex.examples.puzzle.Move;
import jadex.runtime.ICapability;
import jadex.runtime.IExternalAccess;
import jadex.runtime.IMessageEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 */
public class BoardObserver
{
	/**
	 *
	 */
	public BoardObserver(IBoard board, final ICapability capa)
	{
		//IBoard board = (Board)capa.getBeliefbase().getBelief("board").getFact();

		board.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				//System.out.println("evt");
				IExternalAccess ex = capa.getExternalAccess();
				if(IBoard.MOVE.equals(evt.getPropertyName()))
				{
					AgentIdentifier hp = (AgentIdentifier)ex.getBeliefbase().getBelief("humanplayer").getFact();
					Move move = (Move)evt.getNewValue();
					IMessageEvent rmm = ex.createMessageEvent("request_move");
					RequestMove rm = new RequestMove(move.getStart());
					rmm.getParameterSet(SFipa.RECEIVERS).addValue(hp);
					rmm.setContent(rm);
					if(ex.sendMessageAndWait(rmm, 3000).getParameter(SFipa.PERFORMATIVE).equals(SFipa.FAILURE))
						System.out.println("Could not perform move :-(");
				}
				else if(IBoard.TAKEBACK.equals(evt.getPropertyName()))
				{
					AgentIdentifier hp = (AgentIdentifier)ex.getBeliefbase().getBelief("humanplayer").getFact();
					IMessageEvent rmm = ex.createMessageEvent("request_takeback");
					RequestTakeback rt = new RequestTakeback();
					rmm.getParameterSet(SFipa.RECEIVERS).addValue(hp);
					rmm.setContent(rt);
					if(ex.sendMessageAndWait(rmm, 3000).getParameter(SFipa.PERFORMATIVE).equals(SFipa.FAILURE))
						System.out.println("Could not perform takeback :-(");
				}
			}
		});
	}
}
