/*
 * CleanerData.java
 *
 * Generated by Protege plugin Beanynizer. 
 * Changes will be lost! 
 */
package jadex.examples.cleanerworld.multi;



/**
 *  Java class for concept Cleaner of cleaner_beans ontology.
 */
public abstract class CleanerData	extends LocationObject implements nuggets.INugget
{
	//-------- constants ----------

	//-------- attributes ----------

	/** Attribute for slot chargestate. */
	protected  double  chargestate;

	/** Attribute for slot carried-waste. */
	protected  Waste  carriedwaste;

	/** Attribute for slot vision-range. */
	protected  double  visionrange;

	/** Attribute for slot name. */
	protected  String  name;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>Cleaner</code>.
	 */
	public CleanerData()  { //
	}

	//-------- accessor methods --------

	/**
	 *  Get the chargestate of this Cleaner.
	 * @return chargestate
	 */
	public double  getChargestate() {
		return this.chargestate;
	}

	/**
	 *  Set the chargestate of this Cleaner.
	 * @param chargestate the value to be set
	 */
	public void  setChargestate(double chargestate) {
		this.chargestate = chargestate;
	}

	/**
	 *  Get the carried-waste of this Cleaner.
	 * @return carried-waste
	 */
	public Waste  getCarriedWaste() {
		return this.carriedwaste;
	}

	/**
	 *  Set the carried-waste of this Cleaner.
	 * @param carriedwaste the value to be set
	 */
	public void  setCarriedWaste(Waste carriedwaste) {
		this.carriedwaste = carriedwaste;
	}

	/**
	 *  Get the vision-range of this Cleaner.
	 * @return vision-range
	 */
	public double  getVisionRange() {
		return this.visionrange;
	}

	/**
	 *  Set the vision-range of this Cleaner.
	 * @param visionrange the value to be set
	 */
	public void  setVisionRange(double visionrange) {
		this.visionrange = visionrange;
	}

	/**
	 *  Get the name of this Cleaner.
	 * @return name
	 */
	public String  getName() {
		return this.name;
	}

	/**
	 *  Set the name of this Cleaner.
	 * @param name the value to be set
	 */
	public void  setName(String name) {
		this.name = name;
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this Cleaner.
	 *  @return The string representation.
	 */
	public String toString() {
		return "Cleaner("
		+ "id="+getId()
		+ ", location="+getLocation()
		+ ", name="+getName()
           + ")";
	}
	
	//--------- nuggets methods ---------
	
	/**
	 * Persist this Cleaner using the nuggets utility.
	 * @param c 
	 */
	public void _persist(nuggets.ICruncher c) {
		// declare references
		int idCarriedWaste = c.declare(carriedwaste);
		int idLocation = c.declare(location);
		// persist the nugget
		c.startConcept(this);
		if (idCarriedWaste>0) 
			c.put("CarriedWaste", idCarriedWaste);
		if (chargestate!=0) 
			c.put("Chargestate", String.valueOf(chargestate));
		if (id!=null) 
			c.put("Id", id);
		if (idLocation>0) 
			c.put("Location", idLocation);
		if (name!=null) 
			c.put("Name", name);
		if (visionrange!=0) 
			c.put("VisionRange", String.valueOf(visionrange));
	}
	
	/**
	 * Restore this Cleaner 
	 * @param a the name of the attribute
	 * @param v the value of the attribute
	 */
	public void _set(String a, Object v) { //
		switch(hash(a)) {
		case 0: id =  (String)v; return;
		case 1: location =  (Location)v; return;
		case 2: name =  (String)v; return;
		case 3: carriedwaste =  (Waste)v; return;
		case 4: visionrange =  Double.parseDouble((String)v); return;
		case 5: chargestate =  Double.parseDouble((String)v); return;
		}                        
	}
	
private static final int hash(String name) {
  int h=0;
  int c=name.length();
   if (c<=2) return 0;
   h += 31106403*name.charAt(2);
   return (h>>>15)%6;
}

}
