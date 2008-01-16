package jadex.examples.hunterprey.creature.hunters.ldahunter;

import jadex.examples.hunterprey.Hunter;
import jadex.examples.hunterprey.Location;
import jadex.examples.hunterprey.Prey;
import jadex.examples.hunterprey.Vision;
import jadex.examples.hunterprey.creature.hunters.ldahunter.potentialfield.JointField;
import jadex.examples.hunterprey.creature.hunters.ldahunter.potentialfield.PotentialFrame;
import jadex.runtime.IGoal;
import jadex.runtime.Plan;


/**
 * A plan skeleton. Custom code goes into the body() method.
 */
public class HuntPlan extends Plan {
  Hunter myself;
  Location myLoc;
  JointField jf;
  PotentialFrame pf;
  Vision vis;
  Prey prey;
  
	/**
	 * The plan body. The plan is finished when this method returns.
	 */
	public void body()	{
    // set variables 
		vis = (Vision)(getBeliefbase()).getBelief("vision").getFact();
		while(vis==null) {
			waitFor(500);
			vis = (Vision)getBeliefbase().getBelief("vision").getFact();
		}
		myself = (Hunter)getBeliefbase().getBelief("my_self").getFact();
    myLoc = myself.getLocation();
    
		jf = (JointField)getBeliefbase().getBelief("potential_field").getFact();
    pf = (PotentialFrame)getBeliefbase().getBelief("potential_window").getFact();
    
    prey = (Prey)getBeliefbase().getBelief("next_sheep").getFact();

		jf.update(vis.getObjects(), myself);
    
		if (!eating()) foolAround();
	}
   
  
  /** 
   * @return true if it was eating
   */
  protected boolean eating() {
    if(prey!=null) {
      if(prey.getLocation().equals(myLoc)) { // eat it
        IGoal eat = createGoal("eat");
        eat.getParameter("object").setValue(prey);
        jf.eaten(prey);
        pf.update(jf, myLoc, myLoc.getX(), myLoc.getY());
        dispatchSubgoalAndWait(eat); /// ------------->
        return true;
      }
    }
    return false;
  }
  
  
  /** 
   */
  protected void foolAround() {
    Location to;
    String dir=null;
    
    to = prey!=null?prey.getLocation():jf.getBestLocation(); 
    pf.update(jf, myLoc, to.getX(), to.getY());
    
    while(myself.getDistance(myLoc, to)>1 && jf.getNearerLocation(to))  {/**/}
    
    if(myself.getDistance(myLoc, to)==1) {
      String dirs[] = myself.getDirections(myLoc, to);
      if(dirs.length>0) dir = dirs[0];
    } 
    
    if (dir==null) {
      String posDirs[] = myself.getPossibleDirections(vis.getObjects());
      String lastDir = (String)getBeliefbase().getBelief("last_direction").getFact();
      dir = posDirs[randomInt(posDirs.length)];
      for(int i = 0; i<posDirs.length; i++)    {
        if(lastDir==posDirs[i] && Math.random()>0.2)     {
          dir = lastDir;
          break;
        }
      }
    }
    
    getLogger().info("Moving "+dir+" to "+to);

    IGoal move = createGoal("move");
    move.getParameter("direction").setValue(dir);
    getBeliefbase().getBelief("last_direction").setFact(dir);
    dispatchSubgoalAndWait(move); /// ------------->
  }

	/**
	 *  @param max
	 *  @return integer less than max
	 */
	int randomInt(final int max)	{
		int rnd = (int)Math.floor(Math.random()*max);
		return (rnd<max)? rnd: max-1;
	}

}

