/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package agents;
import risk.*;


/**
 *
 * @author JPANG
 */


 public class chameleon extends Agent {

     protected Agent role;

      public chameleon(int id, int type) {
        super(id, type);
        empireName = "chameleon";
      }




     public static void main (String[] args) {


     double flag1 = Math.random();

     //  depending on the vlaue of flag1, chameleon will
     //  adopt a different identity


            if(flag1 < 1/7){
                Agent ruler = new Catenaccio(1, 1);
            }
            else if(flag1 < 2/7){
                Agent ruler = new ChuckNorris(2, 2);
            }else if(flag1 < 3/7){
                Agent ruler = new Guevara(3, 3);
            }else if(flag1 < 4/7){
                Agent ruler = new Borg(4, 4);
            }else if(flag1 < 5/7){
                Agent ruler = new ItsGoodToBeTheKing(5, 5);
            }else if(flag1 < 6/7){
                Agent ruler = new Calvin(6, 6);
            }else {
                Agent ruler = new SWMBO(7, 7);
            }



     }
}
