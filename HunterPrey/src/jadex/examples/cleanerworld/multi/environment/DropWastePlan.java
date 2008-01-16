package jadex.examples.cleanerworld.multi.environment;

import jadex.adapter.fipa.Done;
import jadex.examples.cleanerworld.multi.Environment;
import jadex.examples.cleanerworld.multi.RequestDropWaste;
import jadex.examples.cleanerworld.multi.Waste;
import jadex.examples.cleanerworld.multi.Wastebin;
import jadex.runtime.Plan;

/**
 *  A cleaner requests to drop waste into a waste-bin.
 *  This can fail, when the wastebin is already full.
 */
public class  DropWastePlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public  DropWastePlan()
	{
		getLogger().info("Created: "+this);
	}

	//------ methods -------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		RequestDropWaste op = (RequestDropWaste)getParameter("action").getValue();
		Waste waste = op.getWaste();
		String wastebinname = op.getWastebinname();

		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		Wastebin wb = env.getWastebin(wastebinname);
		boolean success = env.dropWasteInWastebin(waste, wb);

		if(!success)
			fail();

		Done done = new Done();
		done.setAction(op);
		getParameter("result").setValue(op);
	}

}
