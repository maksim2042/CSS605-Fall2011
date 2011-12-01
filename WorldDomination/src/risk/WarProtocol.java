package risk;

import ec.util.MersenneTwisterFast;
import sim.field.grid.DoubleGrid2D;
import sim.util.Bag;

/**
 *
 * @author Omar A. Guerrero
 */
public class WarProtocol
{

	private MersenneTwisterFast rand = new MersenneTwisterFast();
	private int MAPHIGHT;
	private int MAPWIDTH;
	private int[][] countries;
	private DoubleGrid2D countriesGrid;

	public WarProtocol( int MAPHIGHT, int MAPWIDTH, int[][] countries, DoubleGrid2D countriesGrid )
	{
		this.MAPHIGHT = MAPHIGHT;
		this.MAPWIDTH = MAPWIDTH;
		this.countries = countries;
		this.countriesGrid = countriesGrid;
	}

	protected void war( Bag territories, Bag rulers, long period ) throws CloneNotSupportedException
	{
		// For each territory, call their attack strategies
		for ( int i = 0; i < territories.numObjs; i++ )
		{
			// Pick an attacker
			Territory attacker = ( (Territory) territories.get(i) );
			// Tell ruler to set attack strategy
			attacker.getRuler().attack();
			double attackIntensity = attacker.getRuler().attackingSoldiers;
			int attackedTerritoryID = attacker.getRuler().attackedTerritoryID;
			// Check that the amount of soldiers attacking is coherent with the stock of soldiers in the territory
			if ( attackIntensity <= 0 || attackIntensity > attacker.getSoldiers()
			     || attackedTerritoryID <= 0 || attackedTerritoryID > territories.numObjs )  // mod_k: attackedTerritoryID is valid from 1 to territories.numObjs, not numObjs -1 ...
			{
				continue;
			}
			// Pick a defender
			Territory defender = ( (Territory) territories.get(attackedTerritoryID - 1) );
			if ( defender.equals(attacker) )
			{
				continue;
			}
			// Informs the defender that it is being attacked, and provides the attaker and the
			// number of soldiers attacking to take the pertinent action for defense
			defender.getRuler().defend(attacker, attackIntensity);
			double defenseIntensity = defender.getRuler().defendingSoldiers;
			// Check that the amount of soldiers defending is coherent with the stock of soldiers in the territory
			if ( defenseIntensity < 0 || defenseIntensity > defender.getSoldiers() )
			{
				defenseIntensity = 0;
			}
			// Pick the head of the hierarchy (for comparison purposes)
			Territory head = attacker.getHead();
			// Check that the conditions for attacking are met, and determine if the attacker wins the battle
			if ( attacker.getNeighborTerritories().contains(defender) || attacker.isAbove(defender) )
			{
				boolean attackSucceeded = true;
				if ( rand.nextDouble() >= ( attackIntensity ) / ( attackIntensity + defenseIntensity ) )
				{
					attackSucceeded = false;
				}
				// remove soldiers killed in battle, winner loses 1/4, loser loses 1/2
				attacker.addSoldiers(-attackIntensity / ( attackSucceeded ? 4 : 2 ));
				defender.addSoldiers(-defenseIntensity / ( !attackSucceeded ? 4 : 2 ));


				// inform rulers of outcome
				attacker.getRuler().battleOutcome(period, attacker.getId(), attackIntensity, defender.getId(), defenseIntensity, attackSucceeded);
				defender.getRuler().battleOutcome(period, attacker.getId(), attackIntensity, defender.getId(), defenseIntensity, !attackSucceeded);

				// If the attacker won, update the lists of subordinates, the superiors, and the map
				if ( attackSucceeded )
				{
					if ( defender.getSuperior() != null )
					{
						defender.getSuperior().removeSubordinate(defender);
					}

					if ( attacker.isAbove(defender) )
					{
						attacker.getSuperior().removeSubordinate(attacker);
						attacker.setSuperior(null);
					}
					defender.setSuperior(attacker);
					attacker.addSubordinate(defender);
					attacker.updateNeighbors();
					defender.updateNeighbors();
					updateMap(attacker, defender);
				}
			}
		}
	}

	// Updates the map GUI
	private void updateMap( Territory attacker, Territory defender )
	{
		for ( int i = 0; i < MAPHIGHT - 2; i++ )
		{
			for ( int j = 0; j < MAPWIDTH; j++ )
			{
				if ( countries[i][j] == defender.getId() )
				{
					countriesGrid.field[j][i] = attacker.getType();
				}
			}
		}
	}
}
