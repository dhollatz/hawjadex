package jadex.examples.cleanerworld.multi.environment;

import jadex.adapter.fipa.Done;
import jadex.examples.cleanerworld.multi.Cleaner;
import jadex.examples.cleanerworld.multi.Environment;
import jadex.examples.cleanerworld.multi.RequestVision;
import jadex.examples.cleanerworld.multi.Vision;
import jadex.runtime.Plan;

/**
 *  The dispatch vision plan calculates the vision for a
 *  participant and send it back.
 */
public class DispatchVisionPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public DispatchVisionPlan()
	{
		getLogger().info("Created: "+this);
	}

	//------ methods -------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		RequestVision rv = (RequestVision)getParameter("action").getValue();
		Cleaner cl = rv.getCleaner();
		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		Vision v = env.getVision(cl);

		rv.setVision(v);
		Done done = new Done();
		done.setAction(rv);
		getParameter("result").setValue(done);
	}
}
