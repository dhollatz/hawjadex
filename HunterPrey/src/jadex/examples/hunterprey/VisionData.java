/*
 * VisionData.java
 *
 * Generated by Protege plugin Beanynizer. 
 * Changes will be lost! 
 */
package jadex.examples.hunterprey;



/**
 *  Java class for concept Vision of hunterprey_beans ontology.
 */
public abstract class VisionData implements nuggets.INugget
{
	//-------- constants ----------

	//-------- attributes ----------

	/** The visible objects with locations relative to the creature. */
	protected  java.util.List  objects;

	//-------- constructors --------

	/**
	 *  Default Constructor. <br>
	 *  Create a new <code>Vision</code>.
	 */
	public VisionData()  { //
		this.objects  = new java.util.ArrayList();
	}

	//-------- accessor methods --------

	/**
	 *  Get the objects of this Vision.
	 *   The visible objects with locations relative to the creature.
	 * @return objects
	 */
	public WorldObject[]  getObjects() {
		return (WorldObject[])objects.toArray(new WorldObject [objects.size()]);
	}

	/**
	 *  Set the objects of this Vision.
	 *  The visible objects with locations relative to the creature.
	 * @param objects the value to be set
	 */
	public void  setObjects(WorldObject[] objects) {
		this.objects.clear();
		for(int i=0; i<objects.length; i++)
			this.objects.add(objects[i]);
	}

	/**
	 *  Get an objects of this Vision.
	 *  The visible objects with locations relative to the creature.
	 *  @param idx The index.
	 *  @return objects
	 */
	public WorldObject  getObject(int idx) {
		return (WorldObject)this.objects.get(idx);
	}

	/**
	 *  Set a object to this Vision.
	 *  The visible objects with locations relative to the creature.
	 *  @param idx The index.
	 *  @param object a value to be added
	 */
	public void  setObject(int idx, WorldObject object) {
		this.objects.set(idx, object);
	}

	/**
	 *  Add a object to this Vision.
	 *  The visible objects with locations relative to the creature.
	 *  @param object a value to be removed
	 */
	public void  addObject(WorldObject object)  {
		this.objects.add(object);
	}

	/**
	 *  Remove a object from this Vision.
	 *  The visible objects with locations relative to the creature.
	 *  @param object a value to be removed
	 *  @return  True when the objects have changed.
	 */
	public boolean  removeObject(WorldObject object)  {
		return this.objects.remove(object);
	}


	//-------- object methods --------

	/**
	 *  Get a string representation of this Vision.
	 *  @return The string representation.
	 */
	public String toString() {
		return "Vision("
           + ")";
	}
	
	//--------- nuggets methods ---------
	
	/**
	 * Persist this Vision using the nuggets utility.
	 * @param c 
	 */
	public void _persist(nuggets.ICruncher c) {
		// declare references
		int idObjects = c.declare(objects);
		// persist the nugget
		c.startConcept(this);
		if (idObjects>0) 
			c.put("Objects", idObjects);
	}
	
	/**
	 * Restore this Vision 
	 * @param a the name of the attribute
	 * @param v the value of the attribute
	 */
	public void _set(String a, Object v) { //
		objects =  (java.util.List)v;
	}
}