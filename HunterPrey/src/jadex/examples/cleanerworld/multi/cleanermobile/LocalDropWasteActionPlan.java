package jadex.examples.cleanerworld.multi.cleanermobile;

import jadex.examples.cleanerworld.multi.IEnvironment;
import jadex.examples.cleanerworld.multi.Waste;
import jadex.examples.cleanerworld.multi.Wastebin;
import jadex.runtime.IEvent;
import jadex.runtime.MobilePlan;
import jadex.runtime.PlanFailureException;

/**
 *  Pick up a piece of waste in the environment.
 */
public class LocalDropWasteActionPlan extends	MobilePlan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void action(IEvent event)
	{
		IEnvironment	environment	= (IEnvironment)getBeliefbase().getBelief("environment").getFact();
		Waste waste = (Waste)getParameter("waste").getValue();
		Wastebin wastebin = (Wastebin)getParameter("wastebin").getValue();

		boolean	success	= environment.dropWasteInWastebin(waste, wastebin);

		if(!success)
			throw new PlanFailureException();
	}
}
