package jadex.examples.cleanerworld.multi.cleanermobile;

import jadex.examples.cleanerworld.multi.Location;
import jadex.examples.cleanerworld.multi.MapPoint;
import jadex.runtime.IEvent;
import jadex.runtime.IGoal;
import jadex.runtime.IGoalEvent;
import jadex.runtime.MobilePlan;

import java.util.List;


/**
 *  Plan to explore the map by going to the seldom visited positions.
 *  Uses the absolute quantity to go to positions that are not yet
 *  explored.
 */
public class ExploreMapPlan extends MobilePlan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public ExploreMapPlan()
	{
		getLogger().info("Created: "+this+" for goal "+getRootGoal());
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void action(IEvent event)
	{
		if(event instanceof IGoalEvent && !((IGoalEvent)event).isInfo())
		{
			// Select randomly one of the seldom visited locations.
			List	mps = (List)getExpression("query_min_quantity").execute();
			MapPoint mp = (MapPoint)mps.get(0);
			int cnt	= 1;
			for( ; cnt<mps.size(); cnt++)
			{
				MapPoint mp2 = (MapPoint)mps.get(cnt);
				if(mp.getSeen()!=mp2.getSeen())
					break;
			}
			mp	= (MapPoint)mps.get((int)(Math.random()*cnt));
			Location dest = mp.getLocation();
	
			IGoal moveto = createGoal("achievemoveto");
			moveto.getParameter("location").setValue(dest);
			dispatchSubgoalAndWait(moveto);
		}
	}
}
