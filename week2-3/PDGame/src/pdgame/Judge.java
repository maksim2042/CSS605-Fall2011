/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pdgame;

/**
 *
 * @author maksim
 */
public class Judge {

    public Judge() {
    }
    
    
    public void playMatch(Player p1, Player p2, int rounds){

        //TODO: Add Scorekeeping

        for (int i=0;i<rounds;i++) { //i++ means i=i+1
            playRound(p1,p2);
        }
    }

    public void playRound(Player p1, Player p2) {
        int m1=p1.makeMove();
        int m2=p2.makeMove();

        if (m1==0 && m2==0) {
            p1.setScore(m1, m2, 1, 1,p2.getID());
            p2.setScore(m2, m1, 1, 1,p1.getID());
        } else if (m1==0 && m2==1) {
            p1.setScore(m1, m2, 12, 0,p2.getID());
            p2.setScore(m2, m1, 0, 12,p1.getID());
        } else if (m1==1 && m2==0) {
            p1.setScore(m1, m2, 0, 12,p2.getID());
            p2.setScore(m2, m1, 12, 0,p1.getID());
        } else if (m1==1 && m2==1) {
            p1.setScore(m1, m2, 3,3,p2.getID());
            p2.setScore(m2, m1, 3,3,p1.getID());
        }

    }


}
