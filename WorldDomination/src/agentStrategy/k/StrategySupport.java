/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agentStrategy.k;

import agents.K;
import java.util.Queue;
import risk.Territory;
import sim.util.Bag;

/**
 *
 * @author k
 */
public class StrategySupport
{

	public static Bag attackableTerritories( K ruler )
	{
		Bag empire = ruler.getEmpire();
		Bag attackable = new Bag();
		for ( Object o : empire )
		{
			for ( Object t : attackableTerritories((Territory) o, true) )
			{
				if ( !attackable.contains(t) )
				{
					attackable.add(t);
				}
			}
		}
		return attackable;
	}

	public static Bag attackableTerritories( Territory attacker, boolean ignoreAttackerTerritories )
	{
		Bag vt = new Bag();
		int m = getEmpire(attacker);

		for ( Object i : attacker.getNeighbors() )
		{
			Territory t = (Territory) i;
			if ( ignoreAttackerTerritories && t.getSuperior() == attacker )
			{
				continue;
			}
			vt.add(t);
		}

		return vt;
	}

	public static Bag getAllSubordinates( Territory root, Bag existing )
	{
		Bag unclaimedT = root.getSubordinates();
		unclaimedT.removeAll(existing);

		for ( Object o : unclaimedT )
		{
			existing.add(o);
			for ( Object i : getAllSubordinates((Territory) o, existing) )
			{
				if ( !existing.contains(i) )
				{
					existing.add(i);
				}
			}
		}

		return existing;
	}

	public static Territory largestAttackableNeighbor( Territory attacker )
	{
		Bag attackable = attackableTerritories(attacker, true);
		if ( attackable.isEmpty() )
		{
			return null;
		}
		int largestIndex = 0;
		int largest = getAllSubordinates((Territory) attackable.get(0), new Bag()).size();
		for ( int i = 1; i < attackable.size(); i++ )
		{
			int t = getAllSubordinates((Territory) attackable.get(i), new Bag()).size();
			if ( t > largest )
			{
				largestIndex = i;
				largest = t;
				break;
			}
		}
		return (Territory) attackable.get(largestIndex);
	}

	public static boolean isPartOfSameEmpire( Territory t1, Territory t2 )
	{
		return getEmpire(t1) == getEmpire(t2);
	}

	public static boolean isPartOfSameEmpire( int e, Territory t2 )
	{
		return e == getEmpire(t2);
	}

	public static boolean enclosed( Territory t )
	{
		int e = getEmpire(t);
		for ( Object o : t.getNeighbors() )
		{
			if ( !isPartOfSameEmpire(e, (Territory) o) )
			{
				return false;
			}
		}
		return true;
	}

	public static int getEmpire( Territory t )
	{
		if ( t.getSuperior() == null )
		{
			return t.getType();
		}
		return getEmpire(t.getSuperior());
	}

	public static Bag WorldState( Territory root )
	{
		Bag world = new Bag();
		Bag iworld = new Bag();
		Bag q = new Bag();
		Bag iq = new Bag();
		q.add(root);
		iq.add(root.getId());

		do
		{
			Territory t = (Territory) q.pop();
			Object it = iq.pop();
			if ( !iworld.contains(t.getId()) )
			{
				world.add(t);
				iworld.add(t.getId());
				for ( Object o : t.getNeighbors() )
				{
					Territory tt = (Territory) o;
					if ( !iq.contains(tt.getId()) )
					{
						q.add(tt);
						iq.add(tt.getId());
					}
				}
			}
		}
		while ( !q.isEmpty() );

		return world;
	}
}
