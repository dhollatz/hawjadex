package jadex.examples.helloworld;

import jadex.runtime.Plan;

/**
 *  The plan body.
 */
public class TerminateAgentPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		System.out.println("Waiting for 2 secs. Afterwards agent is terminated.");
		waitFor(2000);
		killAgent();
	}
	
}
