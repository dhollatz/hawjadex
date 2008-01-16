package jadex.examples.hunterprey;

import jadex.util.SUtil;
import jadex.util.SimplePropertyChangeSupport;
import jadex.util.collection.MultiCollection;

import java.beans.PropertyChangeListener;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *  The environment is the container all objects and creatures.
 */
public class Environment implements IEnvironment
{
	//-------- constants --------

	/** The default number of lease ticks. */
	public static int	DEFAULT_LEASE_TICKS	= 50;

	//-------- attributes --------

	/** The singleton instance. */
	protected static Environment instance;

	/** The creatures. */
	protected Map creatures;

	/** The obstacles. */
	protected Set obstacles;

	/** The prey food. */
	protected Set food;

	/** All world objects accessible per location. */
	public MultiCollection world;

	/** The horizontal size. */
	protected int sizex;

	/** The vertictal size. */
	protected int sizey;

	/** The monitor. */
	protected Object monitor;

	/** The list for move and eat requests. */
	protected List tasklist;

	/** The helper object for bean events. */
	protected SimplePropertyChangeSupport pcs;

	/** The radnom number generator. */
	protected Random	rand;
	
	/** The highscore location. */
	protected List highscore;

	/** The last time the highscore was saved. */
	protected long	savetime;

	/** The interval between saves of highscore (-1 for autosave off). */
	protected long	saveinterval;
	
	/** The foodrate determines how often new food pops up. */
	protected int foodrate;
	
	/** The world age. */
	protected int age;
	
	//-------- constructors --------

	/**
	 *  Create a new environment.
	 */
	public Environment()
	{
		this.creatures = new HashMap();
		this.obstacles = new HashSet();
		this.food = new HashSet();
		this.world = new MultiCollection();
		this.monitor = new Object();
		this.tasklist = new ArrayList();
		this.pcs = new SimplePropertyChangeSupport(this);
		this.sizex = 30;
		this.sizey = 30;
		this.rand	= new Random(12345678);
		this.saveinterval	= 5000;
		this.foodrate = 5;

		// Hack! only for testing
		for(int i=0; i<125; i++)
			addObstacle(new Obstacle(getEmptyLocation()));
		for(int i=0; i<10; i++)
			addFood(new Food(getEmptyLocation()));

		// Read highscore list.
		try
		{
			ObjectInputStream is = new ObjectInputStream(SUtil.getResource("highscore.dmp"));
			this.highscore = SUtil.arrayToList(is.readObject());
			is.close();
		}
		catch(Exception e)
		{
			System.out.println(e);
			highscore = new ArrayList();
		}
	}

	/**
	 *  Get the singleton.
	 *  @return The environment.
	 */
	public static Environment getInstance()
	{
		if(instance==null)
			instance = new Environment();
		return instance;
	}

	//-------- interface methods --------

	/**
	 *  Move one field upwards. The method will block
	 *  until the current simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean moveUp(Creature me)
	{
		return move(me, RequestMove.DIRECTION_UP);
	}

	/**
	 *  Move one field downwards. The method will block
	 *  until the current simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean moveDown(Creature me)
	{
		return move(me, RequestMove.DIRECTION_DOWN);
	}

	/**
	 *  Move one field to the left. The method will block
	 *  until the current simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean moveLeft(Creature me)
	{
		return move(me, RequestMove.DIRECTION_LEFT);
	}

	/**
	 *  Move one field to the right. The method will block
	 *  until the current simulation step has finished.
	 *  @return True, when the operation succeeded.
	 */
	public boolean moveRight(Creature me)
	{
		return move(me, RequestMove.DIRECTION_RIGHT);
	}

	/**
	 *  Eat some object. The object has to be at the same location.
	 *  This method does not block, and can be called multiple
	 *  times during each simulation step.
	 *  @param food The object.
	 *  @return True, when the operation succeeded.
	 */
	public boolean eat(Creature me, WorldObject food)
	{
		boolean ret = false;
		int	points	= 0;
		me = getCreature(me);
		me.setLeaseticks(DEFAULT_LEASE_TICKS);
		if(food instanceof Food)
		{
			if(me.getLocation().equals(food.getLocation()))
			{
				ret	= removeFood((Food)food);
				points	= 1;
			}
		}
		else if(me instanceof Hunter && food instanceof Prey)
		{
			// Get actual creature from world model.
			Creature	creat	= getCreature((Creature)food);
			if(me.getLocation().equals(creat.getLocation()))
			{
				ret	= removeCreature(creat);
				points	= 5;
			}
		}

		if(ret)
		{
			me.setPoints(me.getPoints()+points);
		}
		/*else
		{
			System.out.println("Creature tried to cheat: "+me.getName());
		}*/
		//block(); todo: make blocking for local case
		return ret;
	}

	/**
	 *  Add a move or eat action to the queue.
	 */
	public TaskInfo addEatTask(Creature me, WorldObject obj)
	{
		TaskInfo ret = new TaskInfo(new Object[]{"eat", me, obj});
		tasklist.add(ret);
		return ret;
	}

	/**
	 *  Add a move or eat action to the queue.
	 */
	public TaskInfo addMoveTask(Creature me, String dir)
	{
		TaskInfo ret = new TaskInfo(new Object[]{"move", me, dir});
		tasklist.add(ret);
		return ret;
	}

	/**
	 *  Get the current vision (without updating the creatures leaseticks).
	 *  @param me The creature.
	 */
	public Vision internalGetVision(Creature me)
	{
		Vision ret = new Vision();
		me = getCreature(me);
		WorldObject[] wos;
		if(me instanceof Observer)
		{
			wos	= getAllObjects();
		}
		else
		{
			wos = getNearObjects(me.getLocation(), me.getVisionRange());
		}
		ret.setObjects(wos);
		return ret;
	}

	/**
	 *  Get the current vision. This method does not block,
	 *  and can be called multiple times during each simulation step.
	 *  @param me The creature.
	 */
	public Vision getVision(Creature me)
	{
		me = getCreature(me);
		me.setLeaseticks(DEFAULT_LEASE_TICKS);
		return internalGetVision(me);
	}

	/**
	 *  Get the width of the world.
	 */
	public int	getWidth()
	{
		return sizex;
	}

	/**
	 *  Get the height of the world.
	 */
	public int	getHeight()
	{
		return sizey;
	}

	//-------- management methods --------

	/**
	 *  Add a new prey food to the world.
	 *  @param nfood The new food.
	 */
	public void addFood(Food nfood)
	{
		this.food.add(nfood);
		this.world.put(nfood.getLocation(), nfood);
	}

	/**
	 *  remove a prey food to the world.
	 *  @param nfood Thefood.
	 */
	public boolean removeFood(Food nfood)
	{
		this.world.remove(nfood.getLocation(), nfood);
		return this.food.remove(nfood);
	}

	/**
	 *  Add a new obstacle to the world.
	 *  @param obstacle The new obstacle.
	 */
	public void addObstacle(Obstacle obstacle)
	{
		this.obstacles.add(obstacle);
		this.world.put(obstacle.getLocation(), obstacle);
	}

	/**
	 *  Remove a  obstacle to the world.
	 *  @param obstacle The obstacle.
	 */
	public boolean removeObstacle(Obstacle obstacle)
	{
		this.world.remove(obstacle.getLocation(), obstacle);
		return this.obstacles.remove(obstacle);
	}

	/**
	 *  Add a new creature to the world.
	 *  @param creature The creature.
	 */
	public synchronized Creature addCreature(Creature creature)
	{
		Creature copy;
		if(!creatures.containsKey(creature))
		{
			copy = (Creature)creature.clone();
			copy.setLeaseticks(DEFAULT_LEASE_TICKS);
			copy.setWorldWidth(getWidth());
			copy.setWorldHeight(getHeight());
			this.creatures.put(copy, copy);

			if(!(copy instanceof Observer))
			{
				copy.setAge(0);
				copy.setPoints(0);
				copy.setLocation(getEmptyLocation());
//				if(copy instanceof Hunter)
//					copy.setVisionRange(5);
//				else
					copy.setVisionRange(3);					
				this.world.put(copy.getLocation(), copy);
				this.highscore.add(copy);
			}
			//System.out.println("Environment, creature added: "+copy.getName()+" "+copy.getLocation());
		}
		else
		{
			throw new RuntimeException("Creature already exists: "+creature);
		}
		return copy;
	}

	/**
	 *  Remove a creature to the world.
	 *  @param creature The creature.
	 */
	public synchronized boolean removeCreature(Creature creature)
	{
		// Remove tasks of this creature.
		for(int i=0; i<tasklist.size(); i++)
		{
			Object[] params = (Object[])((TaskInfo)tasklist.get(i)).getAction();
			if(creature.equals(params[1]))
			{
				//System.out.println("Removed: "+tasklist.get(i));
				tasklist.remove(i);
			}
		}
		if(this.world.containsKey(creature.getLocation()))
			this.world.remove(creature.getLocation(), creature);
		return this.creatures.remove(creature)!=null;
	}

	/**
	 *  Execute a step.
	 */
	public void executeStep()
	{
		// Creatures that already acted in this step.
		Set	acted	= new HashSet();

		Creature[]	creatures	= getCreatures();
		for(int i=0; i<creatures.length; i++)
		{
			creatures[i].setAge(creatures[i].getAge()+1);
			creatures[i].setLeaseticks(creatures[i].getLeaseticks()-1);
			if(creatures[i].getLeaseticks()<0)
				removeCreature(creatures[i]);
		}

		// Perform eat/move tasks.
		TaskInfo[]	tasks	= (TaskInfo[])tasklist.toArray(new TaskInfo[tasklist.size()]);
		for(int i=0; i<tasks.length; i++)
		{
			Object[] params = (Object[])tasks[i].getAction();
			if(params[0].equals("eat"))
			{
				if(!acted.contains(params[1]))
				{
					tasks[i].setResult(new Boolean(eat((Creature)params[1], (WorldObject)params[2])));
					acted.add(params[1]);
				}
				else
				{
					tasks[i].setResult(new Boolean(false));
				}
			}
		}
		for(int i=0; i<tasks.length; i++)
		{
			Object[] params = (Object[])tasks[i].getAction();
			if(params[0].equals("move"))
			{
				if(!acted.contains(params[1]))
				{
					tasks[i].setResult(new Boolean(move((Creature)params[1], (String)params[2])));
					acted.add(params[1]);
				}
				else
				{
					tasks[i].setResult(new Boolean(false));
				}
			}
		}

		// Place new food.
		if(age%foodrate==0)
		{
			Location	loc	= getEmptyLocation();
			Location	test= getEmptyLocation();
			// Make sure there will be some empty location left.
			if(!loc.equals(test))
			{
				addFood(new Food(loc));
			}
		}

		tasklist.clear();
		this.pcs.firePropertyChange("tasklist", null, tasklist); // Hack! method returns when old==new :-(

		// Save highscore.
		long	time	= System.currentTimeMillis();
		if(saveinterval>=0 && savetime+saveinterval<=time)
		{
			saveHighscore();
			savetime	= time;
		}
		
		age++;
	}

	/**
	 *  Get the world age.
	 *  @return The age of the world.
	 */
	public int getWorldAge()
	{
	    return age;
	}
	
	/**
	 *  Get the foodrate.
	 *  @return The foodrate.
	 */
	public int getFoodrate()
	{
	    return foodrate;
	}
	
	/**
	 *  Set the foodrate. 
	 *  @param foodrate The foodrate.
	 */
	public void setFoodrate(int foodrate)
	{
	    this.foodrate = foodrate;
	}
	
	/**
	 *  Perform a move.
	 *  @param me The creature.
	 *  @param dir The direction.
	 */
	public boolean move(Creature me, String dir)
	{
		boolean ret = true;
		me	= getCreature(me);
		me.setLeaseticks(DEFAULT_LEASE_TICKS);
		Location newloc = createLocation(me.getLocation(), dir);
		
		Collection col = world.getCollection(newloc);
		if(col!=null && col.size()==1 && col.iterator().next() instanceof Obstacle)
		{
			ret = false;
		}
		else
		{
			// Move creature.
			try
			{
				world.remove(me.getLocation(), me);
				me.setLocation(newloc);
				world.put(me.getLocation(), me);
			}
			catch(Exception e)
			{
				//System.out.println("!!! "+me);
				System.out.println(world+" "+me);
				e.printStackTrace();
			}
		}
		//block(); todo: make blocking for local case
		return ret;
	}

	/**
	 *  Get the creatures.
	 *  @return The creatures.
	 */
	public Creature[] getCreatures()
	{
		return (Creature[])creatures.values().toArray(new Creature[creatures.size()]);
	}

	/**
	 *  Get the obstacles.
	 *  @return The obstacles.
	 */
	public Obstacle[] getObstacles()
	{
		return (Obstacle[])obstacles.toArray(new Obstacle[obstacles.size()]);
	}

	/**
	 *  Get the obstacles.
	 *  @return The obstacles.
	 */
	public Food[] getFood()
	{
		return (Food[])food.toArray(new Food[food.size()]);
	}

	/**
	 *  Get all objects in the world (obstacles, food, and creature).
	 */
	public WorldObject[]	getAllObjects()
	{
		// Add obstacles and food to return set.
		ArrayList	ret	= new ArrayList();
		ret.addAll(obstacles);
		ret.addAll(food);

		// Add creatures.
		ret.addAll(creatures.values());

		// Convert to array and return.
		return (WorldObject[])ret.toArray(new WorldObject[ret.size()]);
	}

	/**
	 *  Create a location.
	 *  @param loc The location.
	 *  @param dir The direction.
	 *  @return The new location.
	 */
	protected Location createLocation(Location loc, String dir)
	{
		int x = loc.getX();
		int y = loc.getY();

		if(RequestMove.DIRECTION_UP.equals(dir))
		{
			y = (sizey+y-1)%sizey;
		}
		else if(RequestMove.DIRECTION_DOWN.equals(dir))
		{
			y = (y+1)%sizey;
		}
		else if(RequestMove.DIRECTION_LEFT.equals(dir))
		{
			x = (sizex+x-1)%sizex;
		}
		else if(RequestMove.DIRECTION_RIGHT.equals(dir))
		{
			x = (x+1)%sizex;
		}

		return new Location(x, y);
	}

	/**
	 *  Get an empty location.
	 *  @return The location.
	 */
	protected Location getEmptyLocation()
	{
		Location	ret	= null;
		while(ret==null)
		{
			ret	= new Location(rand.nextInt(sizex), rand.nextInt(sizey));
			if(world.containsKey(ret))
			{
				ret	= null;
			}
		}

		return ret;
	}

	/**
	 *  Return the size of the task list.
	 *  @return The task size.
	 */
	public int getTaskSize()
	{
		return tasklist.size();
	}

	/**
	 *  Block a thread until the monitor is notified.
	 * /
	protected void block()
	{
		try
		{
			synchronized(monitor)
			{
				monitor.wait();
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 *  Get the internal representation of a creature.
	 *  If the creature is unknown it gets added to the environment.
	 *  @param creature The creature.
	 *  @return The creature as known in the environment.
	 */
	protected Creature getCreature(Creature creature)
	{
		Creature ret = (Creature)creatures.get(creature);
		if(ret==null)
		{
			ret = addCreature(creature);
		}
		return ret;
	}

	/**
	 *  Get objects near a position.
	 *  @param loc The location.
	 *  @param range The range.
	 */
	protected WorldObject[] getNearObjects(Location loc, int range)
	{
		Collection ret = new ArrayList();
		int x = loc.getX();
		int y = loc.getY();

		for(int i=x-range; i<=x+range; i++)
		{
			for(int j=y-range; j<=y+range; j++)
			{
				Collection tmp = world.getCollection(new Location((i+sizex)%sizex, (j+sizey)%sizey));
				if(tmp!=null)
					ret.addAll(tmp);
			}
		}

		return (WorldObject[])ret.toArray(new WorldObject[ret.size()]);
	}

	/**
	 *  Get the current highscore.
	 *  @return The 10 best creatures.
	 */
	public synchronized Creature[] getHighscore()
	{
		Collections.sort(highscore, new Comparator()
		{
			public int	compare(Object o1, Object o2)
			{
				return ((Creature)o2).getPoints()
						- ((Creature)o1).getPoints();
			}
		});
		List copy = highscore.subList(0, Math.min(highscore.size(), 10));
		return (Creature[])copy.toArray(new Creature[copy.size()]);
	}

	/**
	 *  Save the highscore to a file.
	 */
	public synchronized void saveHighscore()
	{
		try
		{
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("highscore.dmp"));
			os.writeObject(getHighscore());
			os.close();
		}
		catch(Exception e)
		{
			System.out.println("Error writing hunterprey highscore 'highscore.dmp'.");
		}
	}

	/**
	 *  Set the highscore save interval (-1 for autosave off).
	 */
	public void	setSaveInterval(long saveinterval)
	{
		this.saveinterval	= saveinterval;
	}

	/**
	 *  Get the highscore save interval (-1 for autosave off).
	 */
	public long	getSaveInterval()
	{
		return this.saveinterval;
	}

	//-------- property methods --------

	/**
     *  Add a PropertyChangeListener to the listener list.
     *  The listener is registered for all properties.
     *  @param listener  The PropertyChangeListener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
    }

    /**
     *  Remove a PropertyChangeListener from the listener list.
     *  This removes a PropertyChangeListener that was registered
     *  for all properties.
     *  @param listener  The PropertyChangeListener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
    }
}
