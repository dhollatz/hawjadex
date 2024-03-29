/*
 * CurrentVision.java
 *
 * Generated by Protege plugin Beanynizer. 
 * Changes will be lost! 
 */
package jadex.examples.hunterprey;



/**
 *  Java class for concept CurrentVision of hunterprey_beans ontology.
 */
public class CurrentVision implements nuggets.INugget
{
	//-------- constants ----------

	//-------- attributes ----------

	/** The creature. */
	protected  Creature  creature;

	/** The current vision of the creature. */
	protected  Vision  vision;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>CurrentVision</code>.
	 */
	public CurrentVision()  { //
	}

	/**
	 *  Init Constructor. <br>
	 *  Create a new CurrentVision.<br>
	 *  Initializes the object with required attributes.
	 * @param creature
	 * @param vision
	 */
	public CurrentVision(Creature creature, Vision vision)  {
		this();
		setCreature(creature);
		setVision(vision);
	}

	//-------- accessor methods --------

	/**
	 *  Get the creature of this CurrentVision.
	 *  The creature.
	 * @return creature
	 */
	public Creature  getCreature() {
		return this.creature;
	}

	/**
	 *  Set the creature of this CurrentVision.
	 *  The creature.
	 * @param creature the value to be set
	 */
	public void  setCreature(Creature creature) {
		this.creature = creature;
	}

	/**
	 *  Get the vision of this CurrentVision.
	 *  The current vision of the creature.
	 * @return vision
	 */
	public Vision  getVision() {
		return this.vision;
	}

	/**
	 *  Set the vision of this CurrentVision.
	 *  The current vision of the creature.
	 * @param vision the value to be set
	 */
	public void  setVision(Vision vision) {
		this.vision = vision;
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this CurrentVision.
	 *  @return The string representation.
	 */
	public String toString() {
		return "CurrentVision("
		+ "creature="+getCreature()
		+ ", vision="+getVision()
           + ")";
	}
	
	//--------- nuggets methods ---------
	
	/**
	 * Persist this CurrentVision using the nuggets utility.
	 * @param c 
	 */
	public void _persist(nuggets.ICruncher c) {
		// declare references
		int idCreature = c.declare(creature);
		int idVision = c.declare(vision);
		// persist the nugget
		c.startConcept(this);
		if (idCreature>0) 
			c.put("Creature", idCreature);
		if (idVision>0) 
			c.put("Vision", idVision);
	}
	
	/**
	 * Restore this CurrentVision 
	 * @param a the name of the attribute
	 * @param v the value of the attribute
	 */
	public void _set(String a, Object v) { //
		switch(hash(a)) {
		case 0: creature =  (Creature)v; return;
		case 1: vision =  (Vision)v; return;
		}                        
	}
	
private static final int hash(String name) {
   return ((144602107*name.charAt(0))>>>15)%2;
}

}
