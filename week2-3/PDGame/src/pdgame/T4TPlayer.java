/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pdgame;

/**
 *
 * @author maksim
 */
public class T4TPlayer extends BasicPlayer {

    int oppLastMove=GameMove.COOPERATE;
    public T4TPlayer() {
        myID="T4T Player";
    }

    public int makeMove() {
        if (oppLastMove==GameMove.COOPERATE)
                return GameMove.COOPERATE;
        else
                return GameMove.DEFECT;
    }

    @Override
    public void setScore(int myMove, int oppMove, int myScore, int oppScore, String oppID) {
        oppLastMove=oppMove;
        super.setScore(myMove,oppMove,myScore,oppScore,oppID);
    }


}
