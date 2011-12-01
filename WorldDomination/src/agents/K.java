/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import risk.Agent;
import agentStrategy.k.StrategySupport;
import risk.Territory;
import sim.util.Bag;

/**
 *
 * @author k
 */
public class K extends Agent
{

	static Bag myAgents = new Bag(); // Bag of ids of my Agents
	static Bag myCoreTerritories = new Bag(); // Bag of ids of myCoreTerritories

	public K( int id, int type )
	{
		super(id, type);
		empireName = "K.";
		output("Me: Type: " + type + " Id: " + id);
		if ( type == 1 )
		{
			myAgents.add(id);
		}
	}
	static boolean needsInit = true;

	void init()
	{
		if ( needsInit )
		{
			Bag worldState = StrategySupport.WorldState(myTerritory);
			for ( Object o : worldState )
			{
				Territory t = (Territory) o;
				if ( t.getType() == this.getType() && !myCoreTerritories.contains(t.getId()) )
				{
					myCoreTerritories.add(t.getId());
				}
			}
			needsInit = false;
		}
	}
	int r = 0;

	void generateSoldiers()
	{
		Territory attacker = this.myTerritory;
		double alpha = attacker.getAlpha();
		double cs = attacker.getSoldiers();
		double cr = attacker.getNatRes();
		double cp = attacker.getPeasants();
		double reservePercent = .65;

		double potential_soldiers = ( Math.pow(cr, alpha) * Math.pow(cp, 1 - alpha) );

		// make sure we'll have enough resources left to feed the soldiers
		// produce soldiers up to that amount
		if ( cr > cs * reservePercent )
		{
			attacker.produceSoldiers(cr - cs * reservePercent, cp);
		}

	}

	@Override
	protected void attack()
	{
		output("Empire:");
		for ( Object o : getEmpire() )
		{
			output(( (Territory) o ).getId());
		}
		generateSoldiers();
		this.attackedTerritoryID = 0;
		this.attackingSoldiers = 0;
		Territory attacker = this.myTerritory;
		double s = attacker.getSoldiers() * 1;
		Bag availableTargets = StrategySupport.attackableTerritories(attacker, true);
		output("Available Targets:");
		for ( Object o : availableTargets )
		{
			output(( (Territory) o ).getId());
		}
		Territory target = null;
		// rebel first
		if ( StrategySupport.getEmpire(this.myTerritory) != this.getType() )
		{
			target = attacker.getSuperior();
			if ( target != null && target.getType() == this.getType() )
			{
				target = null;
			}
			else
			{

				output("Rebel");
			}
		}

		target = StrategySupport.largestAttackableNeighbor(attacker);
		output("Larget target:");
		output(target);

		while ( target == null && !availableTargets.isEmpty() )
		{
			Territory t = (Territory) availableTargets.pop();
			output("Looking at:");
			output(t.getType());
			output(t.getId());
			if ( t.getSoldiers() < s * 1.2 || availableTargets.isEmpty() )
			{
				target = t;
			}
		}

		if ( target == null )
		{
			return;
		}

		this.attackedTerritoryID = target.getId();
		this.attackingSoldiers = s;
	}

	public Bag getEmpire()
	{
		Bag b = new Bag();
		b.add(myTerritory);
		return StrategySupport.getAllSubordinates(myTerritory, b);
	}

	@Override
	protected void battleOutcome( long period, int attackerID, double soldiersAttack, int deffenderID, double soldiersDefend, boolean youWon )
	{
		output("Battle: time=" + period + "; attackerId =" + attackerID + "; soldiersAttak = " + soldiersAttack + "; defender=" + deffenderID + "; defenderSoldiers=" + defendingSoldiers + ( youWon ? " Won " : " Lost " ));

	}

	// this is the first thing called, use it to get state stuff
	@Override
	protected void chooseTax()
	{
		this.tax = .5;
		init();
	}

	@Override
	protected void defend( Territory attacker, double soldiersAttacking )
	{
		Territory defender = this.myTerritory;
		double currentS = defender.getSoldiers();
		double allocatedS = 0;

		currentS = defender.getSoldiers();
		allocatedS = currentS;
		if ( soldiersAttacking > currentS * 3 )
		{
			allocatedS = 0;
		}
		this.defendingSoldiers = allocatedS;
	}

	@Override
	public boolean isAcceptTrade()
	{
		return false;
	}

	@Override
	protected void setRetributionsAndBeneficiaries()
	{
		this.beneficiaries.clear();

		return;
		/*
		Bag subs = edgeOfEmpire();
		
		double soldiers = this.myTerritory.getSoldiers();
		double e = soldiers / subs.size();
		
		this.retributions = new double[subs.size()];
		for ( int i = 0; i < subs.size(); i++ )
		{
		this.retributions[i] = e;
		}
		 * 
		 */
	}

	@Override
	protected void trade()
	{
		// clear array
		this.trade[0] = 0; //receiving territory id
		this.trade[1] = 0; // demand resource type
		this.trade[2] = 0; // demand amount
		this.trade[3] = 0; // offer resource type; 1 for natural resources, 2 for peasants, and 3 for soldiers)
		this.trade[4] = 0; // offer resource amount


		// print status of empire
		output(String.format("%1$d)  Soldiers: %2$f  NatRes: %3$f  Peasants: %4$f  Alpha: %5$f", this.myTerritory.getId(), this.myTerritory.getSoldiers(), this.myTerritory.getNatRes(), this.myTerritory.getPeasants(), this.myTerritory.getAlpha()));
		//output("Empire: ");
		/*
		for ( Object o : edgeOfEmpire() )
		{
		output(o);
		}
		 * 
		 */
	}

	@Override
	public double getTax()
	{
		if ( this.myTerritory.getSuperior() != null )
		{
			return 0;
		}
		return .5;
	}

	@Override
	protected void acceptTrade( Territory offerer, double demand, int typeDemand, double offer, int typeOffer )
	{
		this.acceptTrade = false; // don't accept trades
	}

	@Override
	protected void tradeOutcome( long period, int proposerID, double[] tradeProposal, boolean tradeCompleted )
	{
		super.tradeOutcome(period, proposerID, tradeProposal, tradeCompleted);
	}

	protected static void output( Object o )
	{

		System.out.println("K: " + o);
	}

	boolean isPartOfEmpire( int territory )
	{
		return myCoreTerritories.contains(territory);
	}
}
