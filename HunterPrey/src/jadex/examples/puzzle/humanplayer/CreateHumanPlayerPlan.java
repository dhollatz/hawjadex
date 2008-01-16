package jadex.examples.puzzle.humanplayer;

import jadex.adapter.fipa.AgentIdentifier;
import jadex.runtime.IGoal;
import jadex.runtime.Plan;

/**
 *  Plan for playing the puzzle by hand.
 */
public class CreateHumanPlayerPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		IGoal create = createGoal("ams_create_agent");
		create.getParameter("type").setValue("jadex.examples.puzzle.humanplayer.HumanPlayer");
		dispatchSubgoalAndWait(create);
		AgentIdentifier hp = (AgentIdentifier)create.getParameter("agentidentifier").getValue();
		getBeliefbase().getBelief("humanplayer").setFact(hp);
	}
}
