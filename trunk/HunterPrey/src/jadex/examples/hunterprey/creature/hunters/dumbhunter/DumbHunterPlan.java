package jadex.examples.hunterprey.creature.hunters.dumbhunter;

import jadex.examples.hunterprey.Creature;
import jadex.examples.hunterprey.Hunter;
import jadex.examples.hunterprey.Prey;
import jadex.examples.hunterprey.Vision;
import jadex.examples.hunterprey.WorldObject;
import jadex.runtime.GoalFailureException;
import jadex.runtime.IGoal;
import jadex.runtime.Plan;
import jadex.util.SUtil;

import java.util.Random;


/**
 *  Plan to move around in the environment and try to catch prey.
 */
public class DumbHunterPlan	extends Plan
{
	//-------- attributes --------

	/** Random number generator. */
	protected Random	rand;
	private Creature me;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public DumbHunterPlan()
	{
		System.out.println("What the?!");
		this.rand	= new Random(hashCode());		
	}

	public void print(String msg) {
		if (me != null) {
			System.out.print(me.getName() + ": ");
		}
		System.out.println(msg);
	}
	
	//-------- methods --------

	
	/**
	 *  The plan body.
	 */
	public void body()
	{
		print("DumbHunterPlan.body()");
		waitForBeliefChange("vision");
		print("DumbHunterPlan.body() vision changed");
	    //waitForCondition(getCondition("has_vision"));
		//int	dir	= 0;

		
		while(true){
			me = ((Creature)getBeliefbase().getBelief("my_self").getFact());
			Vision vision = ((Vision)getBeliefbase().getBelief("vision").getFact());
			WorldObject[] objects	= vision.getObjects();
			String[] nonBlockedDirections = me.getPossibleDirections(objects);
			me.sortByDistance(objects);
			
			Hunter hunter = null;
			Prey prey = null;
			for (int i = 0; i < objects.length; i++) {
				if (objects[i] instanceof Prey && me != objects[i]) {
					prey = (Prey) objects[i];
					break;
				} else if (objects[i] instanceof Hunter && me != objects[i]) {
					hunter = (Hunter) objects[i];
					break;
				}				
			}
	    	
			if (prey != null) {
				print("Found a prey: " + prey.getName());
				//int	distanceToHunter = me.getDistance(hunter);
				String[] directionsToPrey	= me.getDirections(prey);
	        	String[] possibleDirectionsToPrey = (String[])SUtil.cutArrays(directionsToPrey, nonBlockedDirections);
	        	
	        	// Wenn Prey erreichbar, hin da, Junge, echt jetzt!
	        	if (me.getDistance(prey) == 0) {
	        		eat(prey);
	        	} else if (possibleDirectionsToPrey.length > 0) {
	        		print("There is a prey: " + prey.getName());
	        		moveRandomTo(possibleDirectionsToPrey);
	        	} else {
	        		print("No way to " + prey.getName());
	        		moveRandomTo(nonBlockedDirections);
	        	}
			} else if (hunter != null) {
				print("Found a hunter: " + hunter.getName());
				//int	distanceToHunter = me.getDistance(hunter);
				String[] directionsToHunter	= me.getDirections(hunter);
	        	String[] possibleDirectionsToHunter = (String[])SUtil.cutArrays(directionsToHunter, nonBlockedDirections);
	        	String[] directionsAway = (String[]) SUtil.substractArrays(nonBlockedDirections, directionsToHunter);
	        	
	        	// Wenn Hunter erreichbar, hin da, Junge, echt jetzt!
	        	if (directionsAway.length > 0) {
	        		print("Move away from: " + hunter.getName());
	        		moveRandomTo(directionsAway);
	        	} else {
	        		print("Can't move away from: " + hunter.getName());
	        		moveRandomTo(nonBlockedDirections);
	        	}
//	        	if (possibleDirectionsToHunter.length > 0) {
//	        		print("There is someone: " + hunter.getName());
//	        		moveRandomTo(possibleDirectionsToHunter);
//	        	} else {
//	        		print("No way to " + hunter.getName());
//	        		moveRandomTo(nonBlockedDirections);
//	        	}
			} else {
				moveRandomTo(nonBlockedDirections);
			}
		}
				
		
		// alter Bockmist
//		while(true)
//		{
//			// Look whats around.
//			Creature me = ((Creature)getBeliefbase().getBelief("my_self").getFact());
//			Vision vision = ((Vision)getBeliefbase().getBelief("vision").getFact());
//	
//			WorldObject[]	objects	= vision.getObjects();
//			me.sortByDistance(objects);
//	    	String[] posdirs = me.getPossibleDirections(objects);
//
//			// Find nearest interesting objects.
//			int	distance	= Integer.MAX_VALUE;
//			ArrayList	interesting	= new ArrayList();
//			for(int i=0; i<objects.length; i++)
//			{
//				if(objects[i] instanceof Prey)
//				{
//					int	dist	= me.getDistance(objects[i]);
//					if(dist>distance)
//						break;
//					interesting.add(objects[i]);
//					distance	= dist;
//				}
//			}
//
//			// Take appropriate action (move or eat).
//			if(interesting.size()>0)
//			{
//				WorldObject	obj	= (WorldObject)interesting.get(rand.nextInt(interesting.size()));
//				// Move towards nearest object.
//				String[] dirs	= me.getDirections(obj);
//	        	String[] posmoves = (String[])SUtil.cutArrays(dirs, posdirs);
//
//				if(me.getDistance(obj)==0)
//				{
//					eat(obj);
//				}
//				else if(posmoves.length>0)
//				{
//					// Move towards object.
//					move(posmoves[rand.nextInt(posmoves.length)]);
//				}
//				else
//				{
//					// Move randomly.
//					move(posdirs[rand.nextInt(posdirs.length)]);
//				}
//			}
//			else
//			{
//				// Move randomly.
//				move(posdirs[rand.nextInt(posdirs.length)]);
//			}
//		}
	}
	
	private void moveRandomTo(String[] directions) {
		move(directions[rand.nextInt(directions.length)]);
	}
	

	/**
	 *  Move towards a direction.
	 */
	protected void move(String direction)
	{
		try
		{
			//System.out.println(getAgentName()+" wants to move: "+direction);
			IGoal move = createGoal("move");
			move.getParameter("direction").setValue(direction);
			dispatchSubgoalAndWait(move);
		}
		catch(GoalFailureException e)
		{
			getLogger().warning("Move goal failed");
		}
	}

	/**
	 *  Eat an object.
	 */
	protected void eat(WorldObject object)
	{
		try
		{
			IGoal eat = createGoal("eat");
			eat.getParameter("object").setValue(object);
			dispatchSubgoalAndWait(eat);
		}
		catch(GoalFailureException e)
		{
			getLogger().warning("Move goal failed");
		}
	}

}


