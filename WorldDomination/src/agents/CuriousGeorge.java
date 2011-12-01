package agents;
import java.util.Random;
import org.jfree.chart.ChartFrame;
import org.jfree.data.xy.XYDataset;
import risk.*;

/**
 *
 * @author Ben Miller
 */

public class CuriousGeorge extends Agent{
int attacks = 0;

    public CuriousGeorge(int id, int type){
        super(id, type);
        empireName = "CuriousGeorge";

    }
   
    public void attack(){
        
//FIND THE WEAKEST NEIGHBOR THAT IS NOT A SUBORDINATE

        myTerritory.produceSoldiers(myTerritory.getNatRes(), myTerritory.getPeasants());
        attackingSoldiers = myTerritory.getSoldiers();

        attackedTerritoryID = (new Random()).nextInt(myTerritory.getNeighbors().numObjs) + 1;
    }
    
    
    
        public void chooseTax(){
// Learning to find the right levels?
        if (myTerritory.getSuperior() != null){
            tax = 0.5;
            
        } else {
            tax = 0.5;
            
        }
    }
        
    protected void defend(Territory attacker, double soldiersAttacking){
        // Example of a defending strategy: if the attacker is my subordinate, and attacks me with
        // more soldiers than my stock, then I will surender. Otherwise, attack will all soldiers
        
        if(myTerritory.getSubordinates().contains(attacker) && soldiersAttacking > myTerritory.getSoldiers()){
            defendingSoldiers = 0;
        }
 
        else {defendingSoldiers = soldiersAttacking*2/4;}
               System.out.println("Attacked info: "+attacker.hashCode()+" Alpha = "+attacker.getAlpha());
    }
}
