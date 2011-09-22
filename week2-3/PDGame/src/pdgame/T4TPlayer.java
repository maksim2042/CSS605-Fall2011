/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pdgame;

/**
 *
 * @author maksim
 */
public class T4TPlayer implements Player {

    int totalScore=0;
    int oppLastMove=1;

    public int makeMove() {
        if (oppLastMove==1)
                return 1;
        else
                return 0;
    }

    public void setScore(int myMove, int oppMove, int myScore, int oppScore) {
        oppLastMove=oppMove;
        totalScore+=myScore;
        System.out.println("Score"+myScore+" over "+oppScore+" My total: "+totalScore);
    }


}
