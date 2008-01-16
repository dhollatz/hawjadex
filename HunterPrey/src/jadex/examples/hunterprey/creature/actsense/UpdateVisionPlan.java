package jadex.examples.hunterprey.creature.actsense;

import jadex.examples.hunterprey.CurrentVision;
import jadex.examples.hunterprey.Food;
import jadex.examples.hunterprey.Hunter;
import jadex.examples.hunterprey.Obstacle;
import jadex.examples.hunterprey.Prey;
import jadex.examples.hunterprey.WorldObject;
import jadex.runtime.IBeliefSet;
import jadex.runtime.IMessageEvent;
import jadex.runtime.Plan;
import jadex.util.SUtil;

import java.util.List;

/**
 *  Update the agent's belief according to the new vision.
 */
/*  @requires belief my_self.
 *  @requires beliefset preys
 *  @requires beliefset hunters
 *  @requires beliefset obstacles
 *  @requires beliefset food
 */
public class UpdateVisionPlan extends Plan
{
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		IMessageEvent req = (IMessageEvent)getInitialEvent();
		CurrentVision cv = (CurrentVision)req.getContent();
		startAtomic();
			getBeliefbase().getBelief("my_self").setFact(null);
			getBeliefbase().getBelief("my_self").setFact(cv.getCreature());
		endAtomic();
		getBeliefbase().getBelief("vision").setFact(cv.getVision());
		WorldObject[] seenobs = cv.getVision().getObjects();

		// When an object is not seen any longer (not
		// in actualvision, but in (near) beliefs), remove it.
		List known = (List)getExpression("query_in_vision_objects").execute();
		List seen = SUtil.arrayToList(seenobs);
		for(int i=0; i<known.size(); i++)
		{
			Object object = known.get(i);
			if(!seen.contains(object))
			{
				if(object instanceof Prey)
					getBeliefbase().getBeliefSet("preys").removeFact(object);
				else if(object instanceof Hunter)
					getBeliefbase().getBeliefSet("hunters").removeFact(object);
				else if(object instanceof Obstacle)
					getBeliefbase().getBeliefSet("obstacles").removeFact(object);
				else if(object instanceof Food)
					getBeliefbase().getBeliefSet("food").removeFact(object);
			}
		}

		// Add (new) seen objects to the beliefs.
		for(int i=0; i<seenobs.length; i++)
		{
			if(seenobs[i] instanceof Prey)
			{
				IBeliefSet bs = getBeliefbase().getBeliefSet("preys");
				if(bs.containsFact(seenobs[i]))
					bs.updateFact(seenobs[i]);
				else
					bs.addFact(seenobs[i]);
				//getBeliefbase().getBeliefSet("preys").updateOrAddFact(seenobs[i]);
			}
			else if(seenobs[i] instanceof Hunter)
			{
				IBeliefSet bs = getBeliefbase().getBeliefSet("hunters");
				if(bs.containsFact(seenobs[i]))
					bs.updateFact(seenobs[i]);
				else
					bs.addFact(seenobs[i]);
				//getBeliefbase().getBeliefSet("hunters").updateOrAddFact(seenobs[i]);
			}
			else if(seenobs[i] instanceof Obstacle)
			{
				if(!getBeliefbase().getBeliefSet("obstacles").containsFact(seenobs[i]))
					getBeliefbase().getBeliefSet("obstacles").addFact(seenobs[i]);
			}
			else if(seenobs[i] instanceof Food)
			{
				if(!getBeliefbase().getBeliefSet("food").containsFact(seenobs[i]))
					getBeliefbase().getBeliefSet("food").addFact(seenobs[i]);
			}
		}
	}
}
