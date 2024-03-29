package jadex.examples.hunterprey.environment;

import jadex.examples.hunterprey.Creature;
import jadex.examples.hunterprey.CurrentVision;
import jadex.examples.hunterprey.Environment;
import jadex.examples.hunterprey.Prey;
import jadex.examples.hunterprey.Vision;
import jadex.runtime.AgentEvent;
import jadex.runtime.GoalFailureException;
import jadex.runtime.IAgentListener;
import jadex.runtime.IExternalAccess;
import jadex.runtime.IGoal;
import jadex.util.SGUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


/**
 *  The gui for the cleaner world example.
 *  Shows the world from the viewpoint of the environment agent.
 */
/*  @requires belief environment
 *  @requires belief roundtime
 */
public class EnvironmentGui	extends JFrame
{
	//-------- attributes --------
	
	/** The panel showing the map. */
	protected MapPanel	map;
	
	/** The round counter label. */
	protected JLabel	roundcnt;
	
	/** The panel displaying active creatures. */
	protected CreaturePanel	creatures;

	/** The panel displaying current observers. */
	protected CreaturePanel	observers;

	/** The panel displaying alltime highscores. */
	protected CreaturePanel	highscore;
	
	//-------- constructors --------

	/**
	 *  Create a new gui plan.
	 */
	public EnvironmentGui(final IExternalAccess agent)
	{
		super(agent.getAgentName());
		
		// Map panel.
		this.map	= new MapPanel();
		map.setMinimumSize(new Dimension(300, 300));
		map.setPreferredSize(new Dimension(600, 600));

		JPanel options = createOptionsPanel(agent);

		this.creatures = new CreaturePanel();
		this.observers = new CreaturePanel(true);
		this.highscore = new CreaturePanel();

		JTabbedPane tp = new JTabbedPane();
		//tp.addTab("Control", options);
		tp.addTab("Living creatures", creatures);
		tp.addTab("Observers", observers);
		tp.addTab("Highscore", highscore);
		tp.setMinimumSize(new Dimension(0, 0));
		tp.setPreferredSize(new Dimension(243, 0));

		JPanel	east	= new JPanel(new BorderLayout());
		east.add(BorderLayout.CENTER, tp);
		east.add(BorderLayout.SOUTH, options);

		// Show the gui.
		JSplitPane	split	= new JSplitPane();
		split.setLeftComponent(map);
		split.setRightComponent(east);
		split.setResizeWeight(1);

		getContentPane().add(BorderLayout.CENTER, split);
		pack();
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);

		enableGuiUpdate(agent);
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				// todo: move to end goal
				Environment en = (Environment)agent.getBeliefbase().getBelief("environment").getFact();
				Creature[] creatures = en.getCreatures();
				for(int i=0; i<creatures.length; i++)
				{
					try
					{
//						System.out.println(creatures[i].getAID());
						IGoal kg = agent.createGoal("ams_destroy_agent");
						kg.getParameter("agentidentifier").setValue(creatures[i].getAID());
						agent.dispatchTopLevelGoalAndWait(kg);
					}
					catch(GoalFailureException gfe) {}
				}
				agent.killAgent();
			}
		});
		
		agent.addAgentListener(new IAgentListener()
		{
			public void agentTerminating(AgentEvent ae)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						EnvironmentGui.this.dispose();
					}
				});
			}
		}, false);
	}

	//-------- helper methods --------
	
	/**
	 *  Create a panel for the options area.
	 */
	protected JPanel createOptionsPanel(final IExternalAccess agent)
	{
		final Environment	env	= (Environment)agent.getBeliefbase().getBelief("environment").getFact();

		JPanel	options	= new JPanel(new GridBagLayout());
		options.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Environment Control"));
		this.roundcnt = new JLabel("0");
		final JTextField roundtimetf = new JTextField(""+agent.getBeliefbase().getBelief("roundtime").getFact(), 5);
		roundtimetf.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Long val = new Long(roundtimetf.getText());
				agent.getBeliefbase().getBelief("roundtime").setFact(val);
				//roundtimesl.setValue((int)Math.log(val.intValue()));
			}
		});
		final JTextField saveintervaltf = new JTextField(""+env.getSaveInterval(), 5);
		saveintervaltf.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				env.setSaveInterval(Long.parseLong(saveintervaltf.getText()));
			}
		});
		final JButton hs = new JButton("Save highscore");
		hs.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				//System.out.println("saving highscore: "+SUtil.arrayToString(env.getHighscore()));
				env.saveHighscore();
			}
		});
		final JTextField foodrate = new JTextField(""+env.getFoodrate(), 4);
		foodrate.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent ae)
		    {
		        env.setFoodrate(Integer.parseInt(foodrate.getText()));
		    }
		}); 
		
		Insets insets = new Insets(2, 4, 4, 2);
		options.add(new JLabel("Round number:"), new GridBagConstraints(0, 0, 1, 1, 0, 0,
			GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
		options.add(roundcnt, new GridBagConstraints(1, 0, 3, 1, 1, 0,
			GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
		options.add(new JLabel("Round time [millis]:"), new GridBagConstraints(0, 1, 1, 1, 0, 0,
			GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
		//options.add(roundtimesl, new GridBagConstraints(1, 1, 1, 1, 0, 0,
		//	GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
		options.add(roundtimetf, new GridBagConstraints(1, 1, 1, 1, 0, 0,
			GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
		options.add(new JLabel("Autosave highscore [millis, -1 for off]"), new GridBagConstraints(0, 2, 1, 1, 0, 0,
			GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
		options.add(saveintervaltf, new GridBagConstraints(1, 2, 3, 1, 1, 0,
			GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
		options.add(new JLabel("Food rate [every n ticks]"), new GridBagConstraints(0, 3, 1, 1, 0, 0,
			GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
		options.add(foodrate, new GridBagConstraints(1, 3, 1, 1, 0, 0,
			GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
		options.add(hs, new GridBagConstraints(0, 4, 1, 1, 0, 0,
				GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
		return options;
	}

	/**
	 *  Ensure that the gui is updated on changes in the environment.
	 */
	protected void	enableGuiUpdate(IExternalAccess agent)
	{
		final Environment	env	= (Environment)agent.getBeliefbase().getBelief("environment").getFact();
		env.addPropertyChangeListener(new PropertyChangeListener()
		{
			// Hack!!! Dummy creature required for world size.
			protected Creature	dummy	= new Prey();

			public void propertyChange(PropertyChangeEvent evt)
			{
				roundcnt.setText(""+env.getWorldAge());
	
				dummy.setWorldWidth(env.getWidth());
				dummy.setWorldHeight(env.getHeight());
	
				Vision	vision	= new Vision();
				vision.setObjects(env.getAllObjects());
	
				map.update(new CurrentVision(dummy, vision));
				creatures.update(env.getCreatures());
				observers.update(env.getCreatures());
				highscore.update(env.getHighscore());
			}
		});
	}
}

