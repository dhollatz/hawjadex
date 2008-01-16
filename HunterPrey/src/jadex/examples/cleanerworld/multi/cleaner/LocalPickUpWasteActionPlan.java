package jadex.examples.cleanerworld.multi.cleaner;

import jadex.examples.cleanerworld.multi.IEnvironment;
import jadex.examples.cleanerworld.multi.Waste;
import jadex.runtime.Plan;
import jadex.runtime.PlanFailureException;

/**
 *  Pick up a piece of waste in the environment.
 */
public class LocalPickUpWasteActionPlan extends	Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		IEnvironment	environment	= (IEnvironment)getBeliefbase().getBelief("environment").getFact();
		Waste waste = (Waste)getParameter("waste").getValue();

		boolean	success	= environment.pickUpWaste(waste);

		if(!success)
			throw new PlanFailureException();
	}
}
