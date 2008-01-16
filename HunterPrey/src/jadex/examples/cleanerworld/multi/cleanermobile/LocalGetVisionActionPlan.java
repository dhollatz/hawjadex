package jadex.examples.cleanerworld.multi.cleanermobile;

import jadex.examples.cleanerworld.multi.Cleaner;
import jadex.examples.cleanerworld.multi.IEnvironment;
import jadex.examples.cleanerworld.multi.Location;
import jadex.examples.cleanerworld.multi.Vision;
import jadex.examples.cleanerworld.multi.Waste;
import jadex.runtime.IEvent;
import jadex.runtime.MobilePlan;

/**
 *  Pick up a piece of waste in the environment.
 */
public class LocalGetVisionActionPlan extends	MobilePlan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void action(IEvent event)
	{
		IEnvironment	environment	= (IEnvironment)getBeliefbase().getBelief("environment").getFact();
		Cleaner cl = new Cleaner((Location)getBeliefbase().getBelief("my_location").getFact(),
			getAgentName(),
			(Waste)getBeliefbase().getBelief("carriedwaste").getFact(),
			((Number)getBeliefbase().getBelief("my_vision").getFact()).doubleValue(),
			((Number)getBeliefbase().getBelief("my_chargestate").getFact()).doubleValue());

		Vision	vision	= environment.getVision(cl);

		getParameter("vision").setValue(vision);
	}
}
