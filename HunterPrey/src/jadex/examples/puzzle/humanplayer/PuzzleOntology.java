package jadex.examples.puzzle.humanplayer;

/**
 *  Generated Java class for ontology puzzle.
 */
public class PuzzleOntology
{
	//-------- constants --------

	/** The name of the ontology. */
	public static final String	ONTOLOGY_NAME	= "puzzle";

	/** The allowed java classes. */
	public static java.util.HashSet java_classes = new java.util.HashSet();

	//-------- static part --------

	static
	{


		String[] sp = java.beans.Introspector.getBeanInfoSearchPath();
		String[] nsp = new String[sp.length+1];
		System.arraycopy(sp, 0, nsp, 0, sp.length);
		nsp[nsp.length-1] = "jadex.examples.puzzle.humanplayer";
		// Use try/catch for applets / webstart, etc.
		try
		{
			java.beans.Introspector.setBeanInfoSearchPath(nsp);
		}
		catch(SecurityException e)
		{
			System.out.println("Warning: Cannot set BeanInfo search path 'jadex.examples.puzzle.humanplayer'.");
		}
	}


}

