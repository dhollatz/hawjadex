package jadex.examples.blackjack.player;

import jadex.adapter.fipa.AgentDescription;
import jadex.adapter.fipa.AgentIdentifier;
import jadex.adapter.fipa.SearchConstraints;
import jadex.adapter.fipa.ServiceDescription;
import jadex.runtime.IGoal;
import jadex.runtime.Plan;

import java.util.Random;

/**
 *
 */
public class PlayerSearchDealerPlan extends Plan
{
	//-------- methods --------

	/**
	 *  First the player searches a dealer, then sends a join-request to this
	 *  dealer.
	 */
	public void body()
	{
		//System.out.println("Searching dealer...");
		// Create a service description to search for.
		ServiceDescription sd = new ServiceDescription();
		sd.setType("blackjack");
		AgentDescription dfadesc = new AgentDescription();
		dfadesc.addService(sd);
		SearchConstraints	sc	= new SearchConstraints();
		sc.setMaxResults(-1);

		// Use a subgoal to search for a dealer-agent
		IGoal ft = createGoal("df_search");
		ft.getParameter("description").setValue(dfadesc);
		ft.getParameter("constraints").setValue(sc);
		dispatchSubgoalAndWait(ft);
		//AgentDescription[]	result	= (AgentDescription[])ft.getResult();
		AgentDescription[]	result	= (AgentDescription[])ft.getParameterSet("result").getValues();

		if(result==null || result.length==0)
		{
			getLogger().warning("No blackjack-dealer found.");
			fail();
		}
		else
		{
			// at least one matching AgentDescription found,
			getLogger().info(result.length + " blackjack-dealer found");

			// choose one dealer randomly out of all the dealer-agents
			AgentIdentifier dealer = result[new Random().nextInt(result.length)].getName();
			getBeliefbase().getBelief("dealer").setFact(dealer);
		}

	}

	/**
	 *  Called when something went wrong (e.g. timeout).
	 */
	public void	failed()
	{
		// Remove dealer fact.
		getBeliefbase().getBelief("dealer").setFact(null);
	}
}
