/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agentStrategy.k;

import risk.Territory;
import sim.util.Bag;

/**
 *
 * @author k
 */
public class Util
{

	public static String toString( Territory t )
	{
		return "Territory Id: " + Integer.toString(t.getId())
		       + " Type: " + Integer.toString(t.getType())
		       + " Resources: " + Double.toString(t.getNatRes())
		       + " Peasants: " + Double.toString(t.getPeasants())
		       + " Alpha: " + Double.toString(t.getAlpha())
		       + " Superior: " + t.getSuperior() == null ? "none" : ( "(Id:" + t.getSuperior().getId() + ", Type:" + t.getSuperior().getType() + ")" )
		       + " Subjects: " + toString(t.getSubordinates());
	}

	public static String toString(Bag b)
	{
		String t = "";
		for(Object o : b)		
		{
			t.concat(", ");
			t.concat(o.toString());
		}
		return t;
	}
}
