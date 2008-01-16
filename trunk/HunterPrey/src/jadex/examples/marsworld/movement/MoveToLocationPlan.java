package jadex.examples.marsworld.movement;

import jadex.examples.marsworld.AgentInfo;
import jadex.examples.marsworld.Environment;
import jadex.examples.marsworld.Location;
import jadex.runtime.Plan;

/**
 *  The move to a location plan.
 */
public class MoveToLocationPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public MoveToLocationPlan()
	{
		//getLogger().info("Created: "+this);
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();

		// Retrieve the target destination.
		Location myloc = (Location)getBeliefbase().getBelief("my_location").getFact();
		Location dest = (Location)getParameter("destination").getValue();
		//long time	= System.currentTimeMillis();

		//System.out.println("move: "+myloc+" "+dest);
		while(!myloc.isNear(dest))
		{
			// Calculate the new position offset.
			//long	newtime	= System.currentTimeMillis();
			double speed = ((Double)getBeliefbase().getBelief("my_speed").getFact()).doubleValue();
			double d = myloc.getDistance(dest);
			double r = speed*0.00001*100;//(newtime-time);
			double dx = dest.getX()-myloc.getX();
			double dy = dest.getY()-myloc.getY();
			//time	= newtime;

			// When radius greater than distance, just move a step.
			double rx = r<d? r*dx/d: dx;
			double ry = r<d? r*dy/d: dy;
			myloc = new Location(myloc.getX()+rx, myloc.getY()+ry);
			getBeliefbase().getBelief("my_location").setFact(myloc);

			env.setAgentInfo(new AgentInfo(getAgentName(),
				(String)getBeliefbase().getBelief("my_type").getFact(), myloc,
				((Double)getBeliefbase().getBelief("my_vision").getFact()).doubleValue()));

			//System.out.println("now at: "+myloc);
			waitFor(100); // wait for 0.01 seconds
		}

		//System.out.println("Agent received: "+myloc+" dest was: "+dest);
	}
}