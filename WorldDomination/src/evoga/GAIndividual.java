/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ckhyde
 */

package evoga;

import sim.util.Bag;
//import sim.engine.SimState;
import java.lang.Double;
import java.lang.Integer;
import java.lang.Boolean;
import ec.util.MersenneTwisterFast;

public class GAIndividual 
{
    int GAIndID;
    int numGAGenes;
    int numAttackStraGenes;
    int numDefStraGenes;
    int numTradStraGenes;
    protected boolean flagEvaluated;
    protected boolean flagSelected;
    //Bag GAChromosome = new Bag();
    protected int[] GAChromosome;
    double fitnessValue;    
    double strength;
    float betaStrength = 0.1f;
    int base;        
    
    //private MersenneTwisterFast rand = new MersenneTwisterFast(System.currentTimeMillis());
    private static MersenneTwisterFast rand = new MersenneTwisterFast();    

    
    public GAIndividual() 
    {
        this(0, 2, 2, 2, 0.0d, 10);
    }
    
    //public GAIndividual(int id, int numGAGene, double fitness) 
    public GAIndividual(int id, int numAttackStra, int numDefStra, int numTradStra, double fitness, int base) 
    {        
        //this.numGPGene = numGPGene;
        this.GAIndID = id;
        
        numAttackStraGenes = numAttackStra;
        numDefStraGenes = numDefStra;
        numTradStraGenes = numTradStra;
        this.base = base;
        
        numGAGenes = numAttackStra + numDefStra + numTradStra;
        
        flagEvaluated = false;
        flagSelected = false;
        fitnessValue = fitness;        
        
        GAChromosome = new int[numGAGenes];
        
        // Probability of an attacking strategy is: its own influence / (sum of all strategies' influence)
        // 1: Influence of Attacking strategy 1 (1 ~ base)
        // 2: Influence of Attacking strategy 2 (1 ~ base)        
        // 3: Influence of Defending strategy 1 (1 ~ base)
        // 4: Influence of Defending strategy 2 (1 ~ base)  
        // 5: Influence of Trading strategy 1 (1 ~ base)
        // 6: Influence of Trading strategy 2 (1 ~ base)  
        
        //GAChromosome.add(new Double(state.random.nextDouble()));
        //GAChromosome.add(state.random.nextInt(1000));
        for (int i = 0; i < numGAGenes; i++)
        {    
            //rand.setSeed(System.currentTimeMillis());
            GAChromosome[i] = rand.nextInt(base) + 1;                     
        }    
    }
    
    public double[] getStraProb(int startPos, int numGenes)
    //public double[] getStraProb(int typeStra)
    {               
        //int startPos;
        //int numGenes;                        
            
        double tmpSumInfluence = 0;
        double[] probStra = new double[numGenes];
       
        for (int i = startPos; i < (startPos + numGenes); i++)
        {     
            tmpSumInfluence += (double)GAChromosome[i];
//            System.out.print("bit " + i + ": " + GAChromosome[i]);
        }    
        
        for (int i = 0; i < numGenes; i++)
            probStra[i] = (double)GAChromosome[startPos + i] / tmpSumInfluence;
        
        return probStra;
    }            
    
    /*
    public double[] getAttackStraProb(int startPos)
    {
        //Bag attackStraProb = new Bag();
        //double probStrategy = 0.0d;
        int tmpSumInfluence = 0;
        double[] probAttackStra = new double[numAttackStraGenes];
       
        for (int i = startPos; i < (startPos + numAttackStraGenes); i++)
            tmpSumInfluence += GAChromosome[i];
        
        for (int i = 0; i < numAttackStraGenes; i++)
            probAttackStra[i] = GAChromosome[startPos + i] / tmpSumInfluence;
        
        return probAttackStra;
    }            
     */
    
        
    public void setGAChromosome(int[] arrayChromosome)
    {
        if (arrayChromosome.length == numGAGenes)
            GAChromosome = arrayChromosome;
    }        
    
    public boolean updateStrength()
    {
        flagEvaluated = true;
        
        strength = (1-(double)betaStrength) * strength + (double)betaStrength * fitnessValue;
        //setFitness();
        
        return true;
    }        
    
    public void setFitness(double val)
    {
        fitnessValue = val;
    }
    
    public double getFitness()
    {
        return fitnessValue;
    }
    
    public void setStrength(double val)
    {
        if (val >= 0)
            this.strength = strength;
    }
    
    public double getStrength()
    {
        return strength;
    }            
    
    public void setFlagSelected(boolean flag)
    {
        flagSelected = flag;
    }      
    
    public boolean getFlagSelected()
    {
        return flagSelected;
    }          
    
       
    
}
