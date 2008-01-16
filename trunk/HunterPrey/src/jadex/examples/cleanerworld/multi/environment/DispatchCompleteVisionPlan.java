package jadex.examples.cleanerworld.multi.environment;

import jadex.adapter.fipa.Done;
import jadex.examples.cleanerworld.multi.Environment;
import jadex.examples.cleanerworld.multi.RequestCompleteVision;
import jadex.examples.cleanerworld.multi.Vision;
import jadex.runtime.Plan;

/**
 *  The dispatch vision plan calculates the vision for a
 *  participant and send it back.
 */
public class DispatchCompleteVisionPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public DispatchCompleteVisionPlan()
	{
		getLogger().info("Created: "+this);
	}

	//------ methods -------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		Vision v = env.getCompleteVision();

		RequestCompleteVision rcv = (RequestCompleteVision)getParameter("action").getValue();
		rcv.setVision(v);
		Done done = new Done();
		done.setAction(rcv);
		getParameter("result").setValue(done);
	}

}
