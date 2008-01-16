package jadex.examples.blackjack.manager;

import jadex.adapter.fipa.AgentIdentifier;
import jadex.examples.blackjack.Player;
import jadex.examples.blackjack.gui.GUIImageLoader;
import jadex.examples.blackjack.player.strategies.AbstractStrategy;
import jadex.runtime.AgentEvent;
import jadex.runtime.BasicAgentIdentifier;
import jadex.runtime.IAgentListener;
import jadex.runtime.IExternalAccess;
import jadex.runtime.IGoal;
import jadex.tools.common.AgentSelectorDialog;
import jadex.util.SGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.table.TableModel;

/**
 *  The manager frame.
 */
public class ManagerFrame extends JFrame implements ActionListener, WindowListener
{
	//-------- constants --------

	/** The dealer default adf. */
	protected static AgentIdentifier LOCAL_DEALER = new AgentIdentifier("BlackjackDealer", true);

	//-------- attributes --------

	protected JPanel playerpan;

	protected JPanel dealerpan;
	protected JTextField dealertf;
	protected AgentIdentifier dealeraid;

	protected JButton exitButton;

	protected JLabel localDealerLabel;
	protected JButton localDealerButton;
	protected String localDealerNameString = "";
	protected String localDealerMaxPlayerString = "";
	protected String localDealerPlayerPlayingString = "";

	protected Timer enableTimer;

	protected JTable dealertable;
	protected TableModel dealermodel;

	protected IExternalAccess agent;
	
	//-------- constructors --------

	/**
	 * Create a new plan.
	 */
	public ManagerFrame(final IExternalAccess access)
	{
		super("Blackjack Manager");

		// set the icon to be displayed for the frame
		ImageIcon icon = GUIImageLoader.getImage("heart_small_m");
		this.setIconImage(icon.getImage());

		this.agent = access;
		this.addWindowListener(this);

		// let this class completly handle the window-closing (see exit()-method)
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		enableTimer = new Timer(2000, new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				localDealerButton.setEnabled(true);
				enableTimer.stop();
			}
		});

		Container cp = this.getContentPane();
		cp.setBackground(Color.WHITE);
		cp.setLayout(new BorderLayout());

		// init player panel
		playerpan = new JPanel();
		playerpan.setBorder(BorderFactory.createTitledBorder(" Player "));
		playerpan.setBackground(Color.WHITE);

		// init dealer panel
		dealerpan = new JPanel();
		dealerpan.setBorder(BorderFactory.createTitledBorder(" Dealer "));
		dealerpan.setBackground(Color.WHITE);
		dealeraid = LOCAL_DEALER;
		dealertf = new JTextField(20);
		dealertf.setEditable(false);
		dealertf.setText(""+dealeraid);
		JButton dealerbut = new JButton("...");
		dealerbut.setMargin(new Insets(0,0,0,0));
		dealerbut.setToolTipText("Set dealer agent identifier");
		dealerbut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				AgentIdentifier tst = new AgentSelectorDialog(ManagerFrame.this, agent).selectAgent(LOCAL_DEALER);
				if(tst!=null)
				{
					dealeraid = tst;
					dealertf.setText(""+dealeraid);
				}
			}
		});
		dealerpan.add(dealertf);
		dealerpan.add(dealerbut);

		JPanel centerpan = new JPanel(new BorderLayout());
		centerpan.add(playerpan, BorderLayout.CENTER);
		centerpan.add(dealerpan, BorderLayout.SOUTH);

		JPanel buttonpan = new JPanel();
		buttonpan.setBackground(Color.WHITE);
		localDealerButton = new JButton("Start local Dealer");
		localDealerButton.addActionListener(this);
		exitButton = new JButton("Exit Blackjack");
		exitButton.addActionListener(this);
		buttonpan.add(localDealerButton);
		buttonpan.add(exitButton);

		agent.addAgentListener(new IAgentListener()
		{
			public void agentTerminating(AgentEvent ae)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						ManagerFrame.this.dispose();
					}
				});
			}
		}, false);

		//cp.add(new JLabel(loadLogo()), BorderLayout.NORTH);
		cp.add(new JLabel(GUIImageLoader.getImage("logo")), BorderLayout.NORTH);
		cp.add(centerpan, BorderLayout.CENTER);
		cp.add(buttonpan, BorderLayout.SOUTH);
		this.setSize(480, 570);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dim = toolkit.getScreenSize();
		this.setLocation((int)(dim.getWidth()/2-this.getWidth()/2),
				(int)(dim.getHeight()/2-this.getHeight()/2));

		this.setLocation(SGUI.calculateMiddlePosition(this));
		this.setVisible(true);
		EventQueue.invokeLater(new Runnable()
		{
			/**
			 * creates the Panel, where the player-information is shown.
			 * This method handles some special cases, i.e. a player - once created - should
			 * be shown as long as it is stopped, even if the dealer is killed,
			 * so don't take a look too close at all these for-loops, most of them
			 * are really just for gui-convenience purposes ;-)
			 */
			public void run()
			{
				// create new Player Panels with the properties as specified in the Manager.xml
				Player[] players = (Player[])access.getBeliefbase().getBeliefSet("players").getFacts();

				// get the 'old' master-panel on which player-panels are shown
				JPanel playerDealerPanel = (JPanel)getContentPane().getComponent(1);
				JPanel playerPanel = (JPanel)playerDealerPanel.getComponent(0);
				playerPanel.setLayout(new GridLayout(players.length, 1, 0, 0));
				playerPanel.setBackground(Color.WHITE);

				for(int i = 0; i<players.length; i++)
				{
					playerPanel.add(new ManagerPlayerPanel(i+1, players[i]));
				}

				getContentPane().add(playerDealerPanel, 1);
				getContentPane().validate();
			}
		});
	}

	/**
	 * @param playerPlaying
	 */
	public void setPlayerPlaying(String playerPlaying)
	{
		localDealerPlayerPlayingString = playerPlaying;
		//setDealerLabels(localDealerNameString, null);
	}

	/**
	 * @param startMode
	 */
	public void setLocalDealerButtonMode(boolean startMode)
	{
		if(startMode)
		{
			localDealerButton.setForeground(Color.BLACK);
			localDealerButton.setText("Start local Dealer");
		}
		else
		{
			localDealerButton.setForeground(Color.RED);
			localDealerButton.setText("Stop local Dealer");
		}
	}

	/**
	 * @param e
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==localDealerButton)
		{
			if(localDealerButton.getText().startsWith("Start"))
			{
				setLocalDealerButtonMode(false);
				localDealerButton.setEnabled(false);
				enableTimer.start();
				startLocalDealer();
			}
			else
			{
				setLocalDealerButtonMode(true);
				stopLocalDealer();
			}
		}
		else if(e.getSource()==exitButton)
		{
			this.exit();
		}
	}

	/**
	 * 
	 */
	protected void exit()
	{
		Object[] options = {"Yes", "No", "Cancel"};
		final int n = JOptionPane.showOptionDialog(this, "Kill all local BlackJack-Agents (Player and Dealer) ?",
				"Close Agents on exit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);
		if(n!=JOptionPane.CANCEL_OPTION)
		{
			if(n==JOptionPane.YES_OPTION)
			{
				stopAllAgents();
			}
			else
			{
				agent.killAgent();
			}

			this.setVisible(false);
			this.dispose();
		}
	}
	
	// ---------- windowListener-methods --------------
	
	public void windowClosing(WindowEvent e)
	{
		this.exit();
	}

	public void windowClosed(WindowEvent e)
	{
		// nothing to do
	}

	public void windowOpened(WindowEvent e)
	{
		// nothing to do
	}

	public void windowIconified(WindowEvent e)
	{
		// nothing to do
	}

	public void windowDeiconified(WindowEvent e)
	{
		// nothing to do
	}

	public void windowActivated(WindowEvent e)
	{
		// nothing to do
	}

	public void windowDeactivated(WindowEvent e)
	{
		// nothing to do
	}

	//-------- helper methods --------

	/**
	 *  Search for a dealer.
	 * /
	 protected AgentDescription	searchLocalDealer()
	 {
	 // Create a service description to search for.
	 ServiceDescription sd = new ServiceDescription();
	 sd.setType("blackjack");
	 AgentDescription dfadesc = new AgentDescription();
	 dfadesc.addService(sd);

	 // Use a subgoal to search for a translation agent
	 IGoal ft = createGoal("df_search");
	 ft.getParameter("description").setValue(dfadesc);

	 IEvent event = dispatchSubgoalAndWait(ft);
	 Object result = ft.getResult();

	 if((result != null) && (result instanceof AgentDescription[]))
	 {
	 AgentDescription[] desc = (AgentDescription[])result;
	 if(desc.length > 0)
	 return desc[0];
	 }
	 return null;
	 }*/

	/**
	 * Start a local dealer agent.
	 */
	protected void startLocalDealer()
	{
		// start dealer-agent
		try
		{
			IGoal start = agent.getGoalbase().createGoal("ams_create_agent");
			start.getParameter("type").setValue("jadex.examples.blackjack.dealer.Dealer");
			start.getParameter("name").setValue("BlackjackDealer");
			agent.dispatchTopLevelGoalAndWait(start);
			agent.getLogger().info("local DealerAgent started");
			//access.getBeliefbase().getBelief("localDealerAID").setFact(start.getResult());
			agent.getBeliefbase().getBelief("localDealerAID").setFact(start.getParameter("agentidentifier").getValue());
		}
		catch(Exception e)
		{
			agent.getLogger().warning("DealerAgent could not be created: "+e);
		}
	}

	/**
	 * Stop the local dealer agent.
	 */
	protected void stopLocalDealer()
	{
		AgentIdentifier dealer = (AgentIdentifier)agent.getBeliefbase().getBelief("localDealerAID").getFact();
		if(dealer!=null)
		{
			IGoal destroy = agent.getGoalbase().createGoal("ams_destroy_agent");
			destroy.getParameter("agentidentifier").setValue(dealer);
			agent.dispatchTopLevelGoalAndWait(destroy);
			agent.getBeliefbase().getBelief("localDealerAID").setFact(null);
		}
	}

	/**
	 * Kill all started agents.
	 */
	protected void stopAllAgents()
	{
		try
		{
			// Kill all Players
			Player[] players = (Player[])agent.getBeliefbase().getBeliefSet("players").getFacts();
			for(int i = 0; i<players.length; i++)
			{
				if(players[i].getAgentID()!=null)
				{
					IGoal destroy = agent.getGoalbase().createGoal("ams_destroy_agent");
					destroy.getParameter("agentidentifier").setValue(players[i].getAgentID());
					agent.dispatchTopLevelGoalAndWait(destroy);
				}
			}

			// kill the Dealer
			stopLocalDealer();
			
			// kill the Manager
			agent.killAgent();
		}
		catch(Exception e)
		{
			agent.getLogger().warning("At least one agent could not be stopped: "+e);
		}
	}

	/**
	 *
	 */
	public class ManagerPlayerPanel extends JPanel implements ActionListener
	{
		//-------- attributes --------

		private JButton colorButton;
		private JButton actionButton;
		private JTextField name;
		private JTextField initialAccount;
		private JComboBox strategy;

		private Player player;

		/**
		 * Timer, that enables the start/stop-button 1 second after the user
		 * pressed the button (pressing the button, disables it, for stability-reasons
		 */
		private Timer enableTimer;


		//-------- constructors --------

		/**
		 * Create a new playerPanel.
		 * A playerPanel contains all neccessary startup-information about the player
		 */
		public ManagerPlayerPanel(int id, Player player)
		{
			super();
			//System.out.println("Player: "+nameString+" "+colorString);
			this.player = player;

			this.setBackground(Color.WHITE);

			name = new JTextField(player.getName(), 4);
			name.setToolTipText("Agent's name");
			initialAccount = new JTextField(""+player.getAccount(), 4);
			initialAccount.setToolTipText("Agent's initial account");
			colorButton = new JButton("Color");
			colorButton.setBackground(player.getColor());
			colorButton.addActionListener(this);
			colorButton.setToolTipText("Agent's color");

			DefaultComboBoxModel cModel = new DefaultComboBoxModel(AbstractStrategy.getStrategyNames());
			cModel.addElement(AbstractStrategy.HUMAN_PLAYER);

			strategy = new JComboBox(cModel);
			strategy.setBackground(Color.WHITE);
			strategy.setSelectedItem(player.getStrategyName());
			strategy.setToolTipText("Agent's Strategy");

			actionButton = new JButton("Start");
			actionButton.setActionCommand("start");
			actionButton.addActionListener(this);
			actionButton.setToolTipText("start/stop Agent");
			//actionButton.setBackground(new Color(221,221,221));

			this.add(new JLabel(id+"."));
			this.add(name);
			this.add(initialAccount);
			this.add(colorButton);
			this.add(strategy);
			// this.add(localRemote); (used in next Jadex-Release)
			this.add(actionButton);

			enableTimer = new Timer(1000, new ActionListener()
			{
				public void actionPerformed(ActionEvent evt)
				{
					actionButton.setEnabled(true);
					enableTimer.stop();
				}
			});
		}

		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource()==colorButton)
			{
				Color newColor = JColorChooser.showDialog(this, "Choose Agent Color",
						colorButton.getBackground());
				if((newColor!=null) && (!newColor.equals(Color.WHITE)))
					colorButton.setBackground(newColor);
			}
			else if(e.getSource()==actionButton)
			{
				boolean startPlayerAgent = e.getActionCommand().equals("start");

				actionButton.setEnabled(false);
				enableTimer.start();

				colorButton.setEnabled(!startPlayerAgent);
				name.setEnabled(!startPlayerAgent);
				initialAccount.setEnabled(!startPlayerAgent);
				strategy.setEnabled(!startPlayerAgent);

				if(startPlayerAgent)
				{
					actionButton.setForeground(Color.RED);
					actionButton.setText("Stop");
					actionButton.setActionCommand("stop");

					// Copy values to player object.
					player.setState(Player.STATE_UNREGISTERED);
					player.setName(name.getText());
					player.setColor(colorButton.getBackground());
					player.setAccount(Integer.parseInt(initialAccount.getText()));
					player.setBet(0);
					player.setStrategyName((String)strategy.getSelectedItem());
					player.setStrategy(null);

					startPlayer(player);
					// this.remove(localRemote); (used in next Jadex-Release)
					// this.add(detailButton,5); (used in next Jadex-Release)
				}
				else
				{
					actionButton.setForeground(Color.BLACK);
					actionButton.setText("Start");
					actionButton.setActionCommand("start");
					// this.remove(detailButton); (used in next Jadex-Release)
					// this.add(localRemote,5); (used in next Jadex-Release)
					stopPlayer(player);
				}
			}
		}

		//-------- helper methods --------

		/**
		 * Start a player agent.
		 */
		protected void startPlayer(Player player)
		{
			// try to start player-Agent.
			try
			{
				agent.getLogger().info("starting playerAgent: "+player.getName());
				IGoal start = agent.getGoalbase().createGoal("ams_create_agent");
				start.getParameter("type").setValue("jadex.examples.blackjack.player.Player");
				start.getParameter("name").setValue(player.getName());
				Map args = new HashMap();
				args.put("myself", player);
				args.put("dealer", dealeraid);
				start.getParameter("arguments").setValue(args);
				agent.dispatchTopLevelGoalAndWait(start);
				player.setAgentID((BasicAgentIdentifier)start.getParameter("agentidentifier").getValue());
			}
			catch(Exception e)
			{
				e.printStackTrace();
				agent.getLogger().warning("PlayerAgent "+player+" could not be created: "+e);
			}
		}

		/**
		 * Stop a player agent.
		 */
		protected void stopPlayer(Player player)
		{
			IGoal destroy = agent.getGoalbase().createGoal("ams_destroy_agent");
			destroy.getParameter("agentidentifier").setValue(player.getAgentID());
			agent.dispatchTopLevelGoalAndWait(destroy);
			player.setAgentID(null);
		}
	}
}
