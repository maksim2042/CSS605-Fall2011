/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pdgame;

/**
 *
 * @author maksim
 */
public interface RuleSet {

	int[] getScores(Player p1, Player p2, int moveP1, int moveP2);
	String getName();

}
