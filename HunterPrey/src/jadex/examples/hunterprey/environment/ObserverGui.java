package jadex.examples.hunterprey.environment;

import jadex.examples.hunterprey.Creature;
import jadex.examples.hunterprey.CurrentVision;
import jadex.examples.hunterprey.Vision;
import jadex.runtime.AgentEvent;
import jadex.runtime.IBeliefListener;
import jadex.runtime.IExternalAccess;
import jadex.util.SUtil;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


/**
 *  Gui for external observers.
 */
public class ObserverGui	extends EnvironmentGui
{
	//-------- attributes --------

	/** The last time the highscore was refreshed. */
	protected long	refreshtime;

	/** The interval between refreshes of highscore (-1 for autorefresh off). */
	protected long	refreshinterval;

	//-------- constructors --------

	/**
	 *  Create a new gui plan.
	 */
	public ObserverGui(IExternalAccess agent)
	{
		super(agent);
	}

	//-------- helper methods --------

	/**
	 *  Create the options panel.
	 */
	protected JPanel	createOptionsPanel(final IExternalAccess agent)
	{
		JPanel	options	= new JPanel(new GridBagLayout());
		options.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Observer Control"));
		final JTextField refreshintervaltf = new JTextField(""+refreshinterval, 5);
		refreshintervaltf.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refreshinterval	= Math.max(5000L,
					Long.parseLong(refreshintervaltf.getText()));
				refreshintervaltf.setText(""+refreshinterval);
			}
		});
		JButton refresh	= new JButton("Refresh highscore");
		refresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				refreshHighscore(agent);
			}
		});

		Insets insets = new Insets(2, 4, 4, 2);
		options.add(new JLabel("Autorefresh highscore [millis, -1 for off]"), new GridBagConstraints(0, 0, 1, 1, 0, 0,
			GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
		options.add(refreshintervaltf, new GridBagConstraints(1, 0, 3, 1, 1, 0,
			GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));
		options.add(refresh, new GridBagConstraints(0, 1, 1, 1, 0, 0,
			GridBagConstraints.WEST,  GridBagConstraints.NONE, insets, 0 , 0));		

		return options;
	}

	/**
	 *  Refresh the highscore.
	 */
	protected void	refreshHighscore(IExternalAccess agent)
	{
		// Read highscore list from resource.
		try
		{
			ObjectInputStream is = new ObjectInputStream(
				SUtil.getResource((String)agent.getBeliefbase().getBelief("highscore").getFact()));
			Creature[]	hscreatures	= (Creature[])is.readObject();
			is.close();
			highscore.update(hscreatures);
		}
		catch(Exception e)
		{
			System.out.print("Error loading highscore: ");
			e.printStackTrace();
		}
	}

	/**
	 *  Ensure that the gui is updated on changes in the environment.
	 */
	protected void	enableGuiUpdate(final IExternalAccess agent)
	{
		agent.getBeliefbase().getBelief("vision").addBeliefListener(new IBeliefListener()
		{
			public void beliefChanged(AgentEvent ae)
			{
				Vision	vision	= (Vision)ae.getValue(); 
				Creature	me	= (Creature)agent.getBeliefbase().getBelief("my_self").getFact();
				if(vision!=null)
				{
					// Update map and creature list from vision.
					map.update(new CurrentVision(me, vision));
					creatures.update(vision.getCreatures());
					observers.update(vision.getCreatures());
				}

				// Refresh highscore.
				long	time	= System.currentTimeMillis();
				if(refreshinterval>=0 && refreshtime+refreshinterval<=time)
				{
					refreshHighscore(agent);
					refreshtime	= time;
				}
			}
		}, false);
	}
}

