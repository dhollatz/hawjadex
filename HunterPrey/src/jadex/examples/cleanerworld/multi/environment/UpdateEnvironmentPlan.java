package jadex.examples.cleanerworld.multi.environment;

import jadex.adapter.fipa.AgentDescription;
import jadex.adapter.fipa.Done;
import jadex.adapter.fipa.ServiceDescription;
import jadex.examples.cleanerworld.multi.Chargingstation;
import jadex.examples.cleanerworld.multi.Cleaner;
import jadex.examples.cleanerworld.multi.CleanerOntology;
import jadex.examples.cleanerworld.multi.Environment;
import jadex.examples.cleanerworld.multi.RequestCompleteVision;
import jadex.examples.cleanerworld.multi.Vision;
import jadex.examples.cleanerworld.multi.Waste;
import jadex.examples.cleanerworld.multi.Wastebin;
import jadex.runtime.GoalFailureException;
import jadex.runtime.IGoal;
import jadex.runtime.Plan;

/**
 *  Update the environment belief.
 */
public class UpdateEnvironmentPlan extends Plan
{
	//-------- conctructors --------

	/**
	 *  Create a new plan.
	 */
	public UpdateEnvironmentPlan()
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
			// Search and store the environment agent.
			if(getBeliefbase().getBelief("environmentagent").getFact()==null)
				searchEnvironmentAgent();
			if(getBeliefbase().getBelief("environmentagent").getFact()==null)
			{
				// If no environment agent found, wait a while before trying again.
				waitFor(5000);
			}
			else
			{
				RequestCompleteVision rv = new RequestCompleteVision();
				IGoal rg = createGoal("rp_initiate");
				rg.getParameter("receiver").setValue(getBeliefbase().getBelief("environmentagent").getFact());
				rg.getParameter("action").setValue(rv);
				rg.getParameter("ontology").setValue(CleanerOntology.ONTOLOGY_NAME);
				//rg.getParameter("language").setValue(SFipa.FIPA_SL0);

				try
				{
					dispatchSubgoalAndWait(rg);
					Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
					env.clear();
					Vision vision = ((RequestCompleteVision)((Done)rg.getParameter("result").getValue()).getAction()).getVision();
					Cleaner[] cleaners = vision.getCleaners();
					for(int i=0; i<cleaners.length; i++)
						env.addCleaner(cleaners[i]);
					Waste[] wastes = vision.getWastes();
					for(int i=0; i<wastes.length; i++)
						env.addWaste(wastes[i]);
					Wastebin[] wastebins = vision.getWastebins();
					for(int i=0; i<wastebins.length; i++)
						env.addWastebin(wastebins[i]);
					Chargingstation[] stations = vision.getStations();
					for(int i=0; i<stations.length; i++)
						env.addChargingStation(stations[i]);
					env.setDaytime(vision.isDaytime());
				}
				catch(GoalFailureException gfe)
				{
					gfe.printStackTrace();
					getLogger().warning("Request vision failed: "+gfe);
				}
				waitFor(100);
				//System.out.println("Updated environement");
			}
		}
	}

	/**
	 *  Search the environent agent and store its AID in the beliefbase.
	 */
	protected void searchEnvironmentAgent()
	{
		// Create a service description to search for.
		ServiceDescription sd = new ServiceDescription();
		sd.setType("dispatch vision");
		AgentDescription dfadesc = new AgentDescription();
		dfadesc.addService(sd);

		// Use a subgoal to search for a translation agent
		IGoal ft = createGoal("df_search");
		ft.getParameter("description").setValue(dfadesc);
		if(getBeliefbase().getBelief("df").getFact()!=null)
			ft.getParameter("df").setValue(getBeliefbase().getBelief("df").getFact());
		try
		{
			dispatchSubgoalAndWait(ft);
			//Object result = ft.getResult();
			Object result = ft.getParameterSet("result").getValues();

			if(result instanceof AgentDescription[])
			{
				AgentDescription[] tas = (AgentDescription[])result;
	
				if(tas.length!=0)
				{
					getBeliefbase().getBelief("environmentagent").setFact(tas[0].getName());
					if(tas.length>1)
						System.out.println("WARNING: more than environment agent found.");
				}
			}
		}
		catch(GoalFailureException gfe)
		{
			getLogger().warning("DF search failed: "+gfe);
		}
	}
}
