package jadex.examples.hunterprey.creature.preys.basicbehaviour;

import jadex.examples.hunterprey.Creature;
import jadex.examples.hunterprey.Location;
import jadex.examples.hunterprey.Obstacle;
import jadex.examples.hunterprey.Vision;
import jadex.examples.hunterprey.WorldObject;
import jadex.runtime.IGoal;
import jadex.runtime.Plan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *  Plan to wander around in the environment and avoid obstacles.
 */
public class WanderAroundPlan	extends Plan
{
	//-------- attributes --------

	/** Random number generator. */
	protected Random	rand;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public WanderAroundPlan()
	{
		this.rand	= new Random(hashCode());
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		waitForBeliefChange("vision");
	    //waitForCondition(getCondition("has_vision"));
		int	dir	= rand.nextInt(4);
		
		while(true)
		{
			// Look whats around.
			Creature me = ((Creature)getBeliefbase().getBelief("my_self").getFact());
			Vision vision = ((Vision)getBeliefbase().getBelief("vision").getFact());
			
			WorldObject[]	objects	= vision.getObjects();
			Location target = me.createLocation(me.getLocation(), Creature.alldirs[dir]);
			
			List pod = new ArrayList();
			pod.add(new Integer(0));	
			pod.add(new Integer(1));			
			pod.add(new Integer(2));			
			pod.add(new Integer(3));
			for(int i=1; i<4 && me.getObject(target, objects) instanceof Obstacle; i++)
			{
			    //System.out.println("prob at: "+me.getLocation()+" "+Creature.alldirs[dir]);
				pod.remove(new Integer(dir));
				dir = ((Integer)pod.get(rand.nextInt(4-i))).intValue();
				target = me.createLocation(me.getLocation(), Creature.alldirs[dir]);
			}
			if(me.getObject(target, objects) instanceof Obstacle)
			{
				System.out.println("Surrounded by walls :-( "+me.getName()+" "+me.getLocation());
			}
			else
			{
				IGoal move = createGoal("move");
				move.getParameter("direction").setValue(Creature.alldirs[dir]);
				dispatchSubgoalAndWait(move);
			}
		}
	}
}


