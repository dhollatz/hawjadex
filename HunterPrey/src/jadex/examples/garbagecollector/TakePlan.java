package jadex.examples.garbagecollector;

import jadex.runtime.IGoal;
import jadex.runtime.Plan;

/**
 *  Take some garbage and bring it to the burner.
 */
public class TakePlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		Environment env = (Environment)getBeliefbase().getBelief("env").getFact();

		// Pickup the garbarge.
		IGoal pickup = createGoal("pick");
		dispatchSubgoalAndWait(pickup);

		// Go to the burner.
		Position oldpos = env.getPosition(getAgentName());
		IGoal go = createGoal("go");
		go.getParameter("pos").setValue(env.getBurnerPosition());
		dispatchSubgoalAndWait(go);

		// Put down the garbarge.
		//System.out.println("Calling drop: "+getAgentName()+" "+getRootGoal());
		env.drop(getAgentName());

		// Go back.
		IGoal goback = createGoal("go");
		goback.getParameter("pos").setValue(oldpos);
		dispatchSubgoalAndWait(goback);
	}

	/*public void aborted()
	{
		System.out.println("aborted: "+getAgentName()+" "+this+" "+isAbortedOnSuccess());
	}

	public void failed()
	{
		System.out.println("failed: "+getAgentName()+" "+this+" "+getException());
	}

	public void passed()
	{
		System.out.println("passed: "+getAgentName()+" "+this);
	}*/
	
}
