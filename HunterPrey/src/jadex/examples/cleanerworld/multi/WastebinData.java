/*
 * WastebinData.java
 *
 * Generated by Protege plugin Beanynizer. 
 * Changes will be lost! 
 */
package jadex.examples.cleanerworld.multi;



/**
 *  Java class for concept Wastebin of cleaner_beans ontology.
 */
public abstract class WastebinData	extends LocationObject implements nuggets.INugget
{
	//-------- constants ----------

	//-------- attributes ----------

	/** Attribute for slot wastes. */
	protected  java.util.List  wastes;

	/** Attribute for slot capacity. */
	protected  int  capacity;

	/** Attribute for slot name. */
	protected  String  name;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>Wastebin</code>.
	 */
	public WastebinData()  { //
		this.wastes  = new java.util.ArrayList();
	}

	//-------- accessor methods --------

	/**
	 *  Get the wastes of this Wastebin.
	 * @return wastes
	 */
	public Waste[]  getWastes() {
		return (Waste[])wastes.toArray(new Waste [wastes.size()]);
	}

	/**
	 *  Set the wastes of this Wastebin.
	 * @param wastes the value to be set
	 */
	public void  setWastes(Waste[] wastes) {
		this.wastes.clear();
		for(int i=0; i<wastes.length; i++)
			this.wastes.add(wastes[i]);
	}

	/**
	 *  Get an wastes of this Wastebin.
	 *  @param idx The index.
	 *  @return wastes
	 */
	public Waste  getWaste(int idx) {
		return (Waste)this.wastes.get(idx);
	}

	/**
	 *  Set a waste to this Wastebin.
	 *  @param idx The index.
	 *  @param waste a value to be added
	 */
	public void  setWaste(int idx, Waste waste) {
		this.wastes.set(idx, waste);
	}

	/**
	 *  Add a waste to this Wastebin.
	 *  @param waste a value to be removed
	 */
	public void  addWaste(Waste waste)  {
		this.wastes.add(waste);
	}

	/**
	 *  Remove a waste from this Wastebin.
	 *  @param waste a value to be removed
	 *  @return  True when the wastes have changed.
	 */
	public boolean  removeWaste(Waste waste)  {
		return this.wastes.remove(waste);
	}


	/**
	 *  Get the capacity of this Wastebin.
	 * @return capacity
	 */
	public int  getCapacity() {
		return this.capacity;
	}

	/**
	 *  Set the capacity of this Wastebin.
	 * @param capacity the value to be set
	 */
	public void  setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 *  Get the name of this Wastebin.
	 * @return name
	 */
	public String  getName() {
		return this.name;
	}

	/**
	 *  Set the name of this Wastebin.
	 * @param name the value to be set
	 */
	public void  setName(String name) {
		this.name = name;
	}

	//-------- object methods --------

	/**
	 *  Get a string representation of this Wastebin.
	 *  @return The string representation.
	 */
	public String toString() {
		return "Wastebin("
		+ "id="+getId()
		+ ", location="+getLocation()
		+ ", name="+getName()
           + ")";
	}
	
	//--------- nuggets methods ---------
	
	/**
	 * Persist this Wastebin using the nuggets utility.
	 * @param c 
	 */
	public void _persist(nuggets.ICruncher c) {
		// declare references
		int idLocation = c.declare(location);
		int idWastes = c.declare(wastes);
		// persist the nugget
		c.startConcept(this);
		if (capacity!=0) 
			c.put("Capacity", String.valueOf(capacity));
		if (id!=null) 
			c.put("Id", id);
		if (idLocation>0) 
			c.put("Location", idLocation);
		if (name!=null) 
			c.put("Name", name);
		if (idWastes>0) 
			c.put("Wastes", idWastes);
	}
	
	/**
	 * Restore this Wastebin 
	 * @param a the name of the attribute
	 * @param v the value of the attribute
	 */
	public void _set(String a, Object v) { //
		switch(hash(a)) {
		case 0: id =  (String)v; return;
		case 1: location =  (Location)v; return;
		case 2: wastes =  (java.util.List)v; return;
		case 3: capacity =  Integer.parseInt((String)v); return;
		case 4: name =  (String)v; return;
		}                        
	}
	
private static final int hash(String name) {
   return ((1391742904*name.charAt(0))>>>15)%5;
}

}
