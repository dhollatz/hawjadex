package jadex.examples.puzzle.mobile;

import jadex.examples.puzzle.IBoard;
import jadex.examples.puzzle.Move;
import jadex.runtime.IEvent;
import jadex.runtime.IGoal;
import jadex.runtime.IGoalEvent;
import jadex.runtime.IInternalEvent;
import jadex.runtime.MobilePlan;

/**
 *  Make a move and dispatch a subgoal for the next.
 */
public class MovePlan extends MobilePlan
{
	//-------- attributes --------

	/** The move to try. */
	protected Move move;

	/** The recusion depth. */
	protected int depth;

	/** The wait delay. */
	protected long delay;

	//-------- constrcutors --------

	/**
	 *  Create a new move plan.
	 */
	public MovePlan()
	{
		this.move = (Move)getParameter("move").getValue();
		this.depth = ((Integer)getParameter("depth").getValue()).intValue();
		this.delay	= ((Long)getBeliefbase().getBelief("move_delay").getFact()).longValue();
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void action(IEvent event)
	{
		IBoard board = (IBoard)getBeliefbase().getBelief("board").getFact();

		// Process move goal.
		if(event instanceof IGoalEvent && !((IGoalEvent)event).isInfo())
		{
			int triescnt = ((Integer)getBeliefbase().getBelief("triescnt").getFact()).intValue()+1;
			getBeliefbase().getBelief("triescnt").setFact(new Integer(triescnt));
			print("Trying "+move+" ("+triescnt+") ", depth);
			board.move(move);
			waitFor(delay);
		}
		
		// Wait for returned (event=timeout).
		else if(event instanceof IInternalEvent)
		{
			if(!board.isSolution()) // This works, but does not use the target condition :-(
			{
				IGoal mm = createGoal("makemove");
				mm.getParameter("depth").setValue(new Integer(depth+1));
				dispatchSubgoalAndWait(mm);
			}
		}
		
		// else dispatched subgoal succeeded (nothing to do)
	}

	/**
	 *  The plan failure code.
	 */
	public void failed(IEvent event)
	{
		print("Failed "+move, depth);
		IBoard board = (IBoard)getBeliefbase().getBelief("board").getFact();
		assert board.getLastMove().equals(move): "Tries to takeback wrong move.";
		board.takeback();
	}

	/**
	 *  The plan passed code.
	 */
	public void passed(IEvent event)
	{
		print("Succeeded "+move, depth);
	}

	/**
	 *  The plan aborted code.
	 */
	public void aborted(IEvent event)
	{
		print("Aborted "+(isAbortedOnSuccess()?
			"on success: ": "on failure: ")+move, depth);
	}

	/**
	 *  Print out an indented string.
	 *  @param text The text.
	 *  @param indent The number of cols to indent.
	 */
	protected void print(String text, int indent)
    {
        for(int x=0; x<indent; x++)
            System.out.print(" ");
        System.out.println(text);
    }
}
