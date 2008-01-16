package jadex.examples.cleanerworld.multi.cleaner;

import jadex.adapter.fipa.AgentAction;
import jadex.adapter.fipa.AgentDescription;
import jadex.adapter.fipa.AgentIdentifier;
import jadex.adapter.fipa.ServiceDescription;
import jadex.examples.cleanerworld.multi.CleanerOntology;
import jadex.runtime.GoalFailureException;
import jadex.runtime.IGoal;
import jadex.runtime.Plan;
import jadex.runtime.PlanFailureException;


/**
 *  Offers a methods for interacting with the cleanerworld environment.
 */
public abstract class RemoteActionPlan extends Plan
{
	/**
	 *  Request an action from the environment. 
	 *  @param action The action.
	 *  @return The finished goal.
	 *  @throws GoalFailureException	when the request goal fails.
	 */
	public IGoal requestAction(AgentAction action)
		throws GoalFailureException
	{
		// Search and store the environment agent.
		AgentIdentifier	env	= searchEnvironmentAgent();
		IGoal rg = createGoal("rp_initiate");
		rg.getParameter("receiver").setValue(env);
		rg.getParameter("action").setValue(action);
		rg.getParameter("ontology").setValue(CleanerOntology.ONTOLOGY_NAME);
		//rg.getParameter("language").setValue(SFipa.NUGGETS_XML);

		dispatchSubgoalAndWait(rg);
		
		return rg;
	}

	/**
	 *  When the plan has failed, assume that environment is down.
	 *  Remove fact to enable new search for environment.
	 */
	public void failed()
	{
		// Received a timeout. Probably the environment agent has died.
		getBeliefbase().getBelief("environmentagent").setFact(null);
	}
	
	//--------- helper methods --------
	
	/**
	 *  Search the environent agent and store its AID in the beliefbase.
	 */
	protected AgentIdentifier	searchEnvironmentAgent()
	{
		AgentIdentifier	res	= (AgentIdentifier)getBeliefbase().getBelief("environmentagent").getFact();

		if(res==null)
		{
			// Create a service description to search for.
			ServiceDescription sd = new ServiceDescription();
			sd.setType("dispatch vision");
			AgentDescription dfadesc = new AgentDescription();
			dfadesc.addService(sd);
	
			// Use a subgoal to search for a translation agent
			IGoal ft = createGoal("df_search");
			ft.getParameter("description").setValue(dfadesc);
			if(getBeliefbase().containsBelief("df"))
				ft.getParameter("df").setValue(getBeliefbase().getBelief("df").getFact());
			dispatchSubgoalAndWait(ft);
			//Object result = ft.getResult();
			AgentDescription[] tas = (AgentDescription[])ft.getParameterSet("result").getValues();

			if(tas.length!=0)
			{
				// Found.
				res	= tas[0].getName();
				getBeliefbase().getBelief("environmentagent").setFact(res);
				if(tas.length>1)
					getLogger().warning("More than environment agent found.");
			}
			else
			{
				// Not found.
				throw new PlanFailureException();
			}
		}

		return res;
	}

}
