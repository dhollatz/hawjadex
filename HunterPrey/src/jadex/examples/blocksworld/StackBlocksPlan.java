package jadex.examples.blocksworld;

import jadex.runtime.IGoal;
import jadex.runtime.InternalEventFilter;
import jadex.runtime.Plan;

/**
 *  Stack a block on top of another.
 */
public class StackBlocksPlan	extends Plan
{
	//-------- constants --------

	/** The normal execution mode. */
	public static final String	MODE_NORMAL	= "Normal";

	/** The slow execution mode. */
	public static final String	MODE_SLOW	= "Slow";

	/** The step execution mode. */
	public static final String	MODE_STEP	= "Step";

	//-------- attributes --------

	/** The block to be moved. */
	protected Block	block;

	/** The block on to which to put the other block. */
	protected Block	target;

	/** The execution mode. */
	protected String	mode;

	/** The quiet flag (do not printout messages). */
	protected boolean	quiet;

	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public StackBlocksPlan(String mode)
	{
		this(mode, false);
	}

	/**
	 *  Create a new plan.
	 */
	public StackBlocksPlan(String mode, boolean quiet)
	{
		this.block	= (Block)getParameter("block").getValue();
		this.target	= (Block)getParameter("target").getValue();
		this.mode	= mode;
		this.quiet	= quiet;
	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Clear blocks.
		IGoal clear = createGoal("clear");
		clear.getParameter("block").setValue(block);
		dispatchSubgoalAndWait(clear);

		clear = createGoal("clear");
		clear.getParameter("block").setValue(target);
		dispatchSubgoalAndWait(clear);

		// Maybe wait before moving block.
		if(mode.equals(MODE_SLOW))
		{
			waitFor(1000);
		}
		else if(mode.equals(MODE_STEP))
		{
			waitFor(new InternalEventFilter("step"));
		}

		// Now move block.
		if(!quiet)
			System.out.println("Moving '"+block+"' to '"+target+"'");

		// This operation has to be performed atomic,
		// because it fires bean changes on several affected blocks. 
		startAtomic();
		block.stackOn(target);
		endAtomic();
	}
}
