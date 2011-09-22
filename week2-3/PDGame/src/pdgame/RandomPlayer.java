/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pdgame;

import java.util.Random;


/**
 *
 * @author maksim
 */
public class RandomPlayer extends BasicPlayer {
    
    public int makeMove() {
        Random r=new Random();

        if (r.nextFloat()>=0.5)
            return GameMove.DEFECT;
        else
            return GameMove.COOPERATE;
        }


}
