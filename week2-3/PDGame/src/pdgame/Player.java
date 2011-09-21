/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pdgame;

/**
 *
 * @author maksim
 */
public interface Player {
    int makeMove();
    void setScore(int myMove, int oppMove, int myScore, int oppScore);

}
