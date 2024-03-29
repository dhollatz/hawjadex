/*
 * RequestProduction.java
 *
 * Generated by Protege plugin Beanynizer. 
 * Changes will be lost! 
 */
package jadex.examples.marsworld;

import jadex.adapter.fipa.AgentAction;


/**
 *  Java class for concept RequestProduction of mars_beans ontology.
 */
public class RequestProduction	extends AgentAction implements nuggets.INugget
{
	//-------- constants ----------

	//-------- attributes ----------

	/** Attribute for slot target. */
	protected  Target  target;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>RequestProduction</code>.
	 */
	public RequestProduction()  { //
	}

	//-------- accessor methods --------

	/**
	 *  Get the target of this RequestProduction.
	 * @return target
	 */
	public Target  getTarget() {
		return this.target;
	}

	/**
	 *  Set the target of this RequestProduction.
	 * @param target the value to be set
	 */
	public void  setTarget(Target target) {
		this.target = target;
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this RequestProduction.
	 *  @return The string representation.
	 */
	public String toString() {
		return "RequestProduction("
           + ")";
	}
	
	//--------- nuggets methods ---------
	
	/**
	 * Persist this RequestProduction using the nuggets utility.
	 * @param c 
	 */
	public void _persist(nuggets.ICruncher c) {
		// declare references
		int idTarget = c.declare(target);
		// persist the nugget
		c.startConcept(this);
		if (idTarget>0) 
			c.put("Target", idTarget);
	}
	
	/**
	 * Restore this RequestProduction 
	 * @param a the name of the attribute
	 * @param v the value of the attribute
	 */
	public void _set(String a, Object v) { //
		target =  (Target)v; 
	}
}
