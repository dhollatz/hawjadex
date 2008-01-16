package jadex.examples.cleanerworld.multi.cleaner;

import jadex.examples.cleanerworld.multi.RequestPickUpWaste;
import jadex.examples.cleanerworld.multi.Waste;

/**
 *  Pick up a piece of waste in the environment.
 */
public class RemotePickUpWasteActionPlan extends RemoteActionPlan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Waste waste = (Waste)getParameter("waste").getValue();
		RequestPickUpWaste rp = new RequestPickUpWaste();
		rp.setWaste(waste);
		requestAction(rp);
	}
}
