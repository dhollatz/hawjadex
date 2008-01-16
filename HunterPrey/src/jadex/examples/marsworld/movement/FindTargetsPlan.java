package jadex.examples.marsworld.movement;

import jadex.examples.marsworld.Environment;
import jadex.examples.marsworld.Location;
import jadex.examples.marsworld.Target;
import jadex.runtime.Plan;

/**
 *  Look if a target is near to my position.
 */
public class FindTargetsPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public FindTargetsPlan()
	{
		getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		while(true)
		{
			Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
			Location myloc = (Location)getBeliefbase().getBelief("my_location").getFact();
			Double myvis = (Double)getBeliefbase().getBelief("my_vision").getFact();

			Target[] ts = env.getTargetsNear(myloc, myvis.doubleValue());
			//if(ts.length>0)
			//	System.out.println("Sees: "+SUtil.arrayToString(ts));
			for(int i=0; i<ts.length; i++)
			{
				if(!ts[i].isMarked() && !getBeliefbase().getBeliefSet("my_targets").containsFact(ts[i]))
				{
					//System.out.println("Found a new target: "+ts[i]);
					getBeliefbase().getBeliefSet("my_targets").addFact(ts[i]);
				}
			}

			waitFor(20);
		}
	}
}
