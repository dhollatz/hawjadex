package jadex.examples.hunterprey.environment;

import jadex.adapter.fipa.Done;
import jadex.examples.hunterprey.Environment;
import jadex.examples.hunterprey.RequestEat;
import jadex.examples.hunterprey.TaskInfo;
import jadex.runtime.Plan;

/**
 *  Handle eat requests by the environment.
 */
public class  EatPlan extends Plan
{
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public  EatPlan()
	{
		getLogger().info("Created: "+this);
	}

	//------ methods -------

	/**
	 *  The plan body.
	 */
	public void body()
	{
//		System.out.println("a) eat: "+getName());

		RequestEat re = (RequestEat)getParameter("action").getValue();

		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		TaskInfo ti = env.addEatTask(re.getCreature(), re.getObject());

		// Wait until all tasks are processed by the environment.
		//waitForCondition(getCondition("emptylist"));
		waitForCondition("$beliefbase.environment.getTaskSize()==0");

//		System.out.println("b) eat: "+getName());

		// Result can null when creature died and action was not executed.
		if(ti.getResult()!=null && ((Boolean)ti.getResult()).booleanValue())
		{
			Done done = new Done();
			done.setAction(re);
			getParameter("result").setValue(done);
		}
		else
		{
			fail();
		}
	}

	/**
	 *  The plan body.
	 * /
	public void body()
	{
//		System.out.println("a) eat: "+getName());

		IMessageEvent req = (IMessageEvent)getInitialEvent();
		RequestEat re = (RequestEat)req.getContent();

		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		TaskInfo ti = env.addEatTask(re.getCreature(), re.getObject());

		// Wait until all tasks are processed by the environment.
		//waitForCondition(getCondition("emptylist"));
		waitForCondition("$beliefbase.environment.getTaskSize()==0");

//		System.out.println("b) eat: "+getName());

		// Result can null when creature died and action was not executed.
		if(ti.getResult()!=null && ((Boolean)ti.getResult()).booleanValue())
		{
			Done done = new Done();
			done.setAction(re);
			sendMessage(req.createReply("inform_done", done));
		}
		else
		{
			//sendMessage(req.createReply("failure", "Eat action failed."));
			IMessageEvent rep = req.createReply("failure", "Eat action failed.");
			//rep.getParameter("language").setValue("plain-text");
			rep.getParameter("ontology").setValue(null);
			sendMessage(rep);
		}
	}*/
}
