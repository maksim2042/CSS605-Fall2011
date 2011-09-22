/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pdgame;

/**
 *
 * @author maksim
 */
public abstract class BasicPlayer implements Player {

    String myID="";
    int totalScore=0;

    public BasicPlayer() {
        myID="Basic Player";
    }

    public void setID(String id) {
        myID=id;
    }

    public String getID() {
        return(myID);
    }

    public void setScore(int myMove, int oppMove, int myScore, int oppScore, String oppID) {
       totalScore+=myScore;
       System.out.println("Score"+myScore+" over "+oppScore+" My total: "+totalScore);
    }

    public String toString() {
        return(myID+" "+ totalScore);
    }

}
