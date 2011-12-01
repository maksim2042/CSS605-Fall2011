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
import ec.util.MersenneTwisterFast;


public class GAPopulation 
{
    //SimState pf;
    int numGAInd;    
    Bag gaInds = new Bag();
    
    int startPosAttack;
    int startPosDef;
    int startPosTrad;
    //int startPosAttack = 0;
    //int startPosDef = 2;
    //int startPosTrad = 4;    
    
    //int numAttackGenes = 2;
    //int numDefGenes = 2;
    //int numTradGenes = 2;  
    //int numGAGenes = numAttackGenes + numDefGenes + numTradGenes;
    int numAttackGenes;
    int numDefGenes;
    int numTradGenes;  
    int numGAGenes;
    int base = 1000;
        
    float crossProb;
    float mutateProb;
    float pointMutateProb;
    float migrateProb;
    float reproProb;    
    
    int crossTournamentSize = 2;
    int mutateTournamentSize = 2;
    int reproTournamentSize = 2;
    
    
    //private MersenneTwisterFast rand = new MersenneTwisterFast(System.currentTimeMillis());
    private static MersenneTwisterFast rand = new MersenneTwisterFast();
    
    //public GAPopulation(int numGAInd, int numGAGene, double crossProb, double mutateProb, double pointMutateProb, double reproProb, SimState state)
    public GAPopulation(int numGAInd, int numAttackStra, int numDefStra, int numTradStra, float crossProb, float mutateProb, float pointMutateProb, float migrateProb, float reproProb)
    {
        //pf = (PolityFormation) state;
        
        this.numGAInd = numGAInd;
        //this.numGAGenes = numGAGene;
        numAttackGenes = numAttackStra;
        numDefGenes = numDefStra;
        numTradGenes = numTradStra;          
        numGAGenes = numAttackStra + numDefStra + numTradStra;
        
        startPosAttack = 0;
        startPosDef = startPosAttack + numDefGenes;
        startPosTrad = startPosDef + numTradGenes;        
        
        this.crossProb = crossProb;
        this.mutateProb = mutateProb;
        this.pointMutateProb = pointMutateProb;
        this.migrateProb = migrateProb;
        this.reproProb = reproProb;
        
        init();    
    }        
    
    private void init()
    {
        for (int i = 0; i < numGAInd; i++)
        {
            //gaInds.add(new GAIndividual(i, numGAGenes, state));                    
            gaInds.add(new GAIndividual(i, numAttackGenes, numDefGenes, numTradGenes, 0, base));                    
        }
    }        
    
    //public boolean EvolvePop(GAIndividual indivFather, GAIndividual indivMom)
    public boolean EvolvePop(double fitness, int selectedID)
    {
        rand.setSeed(System.currentTimeMillis());
        int tmpRandBase = 100;
        
//        for (int i = 0; i < numGAInd; i++)
//            System.out.println("GA " + ((GAIndividual)gaInds.get(i)).GAIndID + ": " + ((GAIndividual)gaInds.get(i)).getStrength());
        
        //int tmpEvolDice = pf.random.nextInt(tmpRandBase);;
        //double tmpEvolDice = pf.random.nextDouble() * 1000;        
        //double tmpEvolDice = rand.nextDouble() * 1000;        
        int tmpEvolDice = rand.nextInt(tmpRandBase);;
        
        float crossThreshold = crossProb * tmpRandBase; 
        float mutateThreshold = mutateProb * tmpRandBase + crossThreshold;
        float migrateThreshold = migrateProb * tmpRandBase + mutateThreshold;
        float reproThreshold = reproProb * tmpRandBase + migrateThreshold;
                
        
        if (selectedID != -1)
        {    
            ((GAIndividual)gaInds.get(selectedID)).setFitness(fitness);
            ((GAIndividual)gaInds.get(selectedID)).updateStrength();            
        }    
        else            
            for (int i = 0; i < numGAInd; i++)
            {    
                ((GAIndividual)gaInds.get(i)).setFitness(fitness);
                ((GAIndividual)gaInds.get(i)).updateStrength();
            }    
        
        
        if (tmpEvolDice < crossThreshold)
            CrossOver();
        else if (tmpEvolDice < mutateThreshold)
            Mutation(pointMutateProb);
        else if (tmpEvolDice < migrateThreshold)      
            Mutation(1.0f);       
        else if (tmpEvolDice < reproThreshold)      
            Reproduction();                
                
        return true;
    }   
    
    /*
    //public void Evaluate(double fitness)
    public void Evaluate(double fitness, int selectedID)
    {
        for (int i = 0; i < numGAInd; i++)
        {
            GAIndividual tmpGAIndividual = (GAIndividual)gaInds.get(i);
            
            if (tmpGAIndividual.flagSelected);
            {
                
                tmpGAIndividual.updateStrength();
                //tmpGAIndividual.updateFitness(fitness);
                
                //tmpGAIndividual.flagSelected = false;
                
                tmpGAIndividual.flagSelected = false;
            }    
        }    
    }     
    */
            
    
    public int SelectTournament(int tournamentSize, boolean selectBestFlag)
    {                        
        int tmpSize = tournamentSize;
        int[] tournamentIndsID = new int[tmpSize];
        
        int tournamentArrayIndex = 0;
        int extremeIndID;
        
        while (tournamentArrayIndex < tmpSize)
        {
            //rand.setSeed(System.currentTimeMillis());
            tournamentIndsID[tournamentArrayIndex] = rand.nextInt(numGAInd);
            
            //for (int i = 0; i < tmpSize; i++)
            for (int i = 0; i < tournamentArrayIndex; i++)
            {
                //tmpTournamentIndsID[i] = rand.nextInt(numGAInd);
                if (tournamentIndsID[i] == tournamentIndsID[tournamentArrayIndex])                                    
                {                    
                    tournamentArrayIndex--;
                    
                    break;
                }
            }
            
            tournamentArrayIndex++;            
        }                   
            
//        if (tournamentSize != 20)
////        for (int i = 0; i < tmpSize; i++)
////            System.out.print(tournamentIndsID[i] + " ");
////        System.out.println("");
        
        /*
        boolean duplicateFlag = false;
        
        for (int i = 0; i < tmpSize; i++)
        {
            tmpTournamentIndsID[i] = rand.nextInt(numGAInd);
            
            for (int j = 0; j < i; j++)
            {
                if (tmpTournamentIndsID[i] == tmpTournamentIndsID[j])
                {    
                    i--;
                    break;
                }    
            }    
        }        
         */        
        
        extremeIndID = tournamentIndsID[0];                                
        
        for (int i = 1; i < tmpSize; i++)
        {
            if (selectBestFlag == true)                
            {                    
                //if (((GAIndividual)gaInds.get(tournamentIndsID[i])).getFitness() >= ((GAIndividual)gaInds.get(tournamentIndsID[extremeIndID])).getFitness())                
                if ((((GAIndividual)gaInds.get(tournamentIndsID[i])).getStrength()) > (((GAIndividual)gaInds.get(extremeIndID)).getStrength()))                
                {
//                    System.out.println(((GAIndividual)gaInds.get(tournamentIndsID[i])).GAIndID + " > " + ((GAIndividual)gaInds.get(extremeIndID)).GAIndID);
//                    System.out.println(((GAIndividual)gaInds.get(tournamentIndsID[i])).getStrength() + " > " + ((GAIndividual)gaInds.get(extremeIndID)).getStrength());
                    extremeIndID = tournamentIndsID[i];                         
                }    
            }        
            else
            {    
                //if (((GAIndividual)gaInds.get(tournamentIndsID[i])).getFitness() <= ((GAIndividual)gaInds.get(tournamentIndsID[extremeIndID])).getFitness());                
                if (((GAIndividual)gaInds.get(tournamentIndsID[i])).getStrength() < ((GAIndividual)gaInds.get(extremeIndID)).getStrength())
                    extremeIndID = tournamentIndsID[i];
            }        
        }    
                
        //System.out.println("ExtremeID: " + extremeIndID);
        return extremeIndID;        
    }        
                 
    
    public boolean CrossOver()
    {   
        //rand.setSeed(System.currentTimeMillis());
//        System.out.println("CrossOver");
        
        int segStartPoint;
        int segEndPoint;
        
        int tmpTournamentSelectAbandonID = SelectTournament(numGAInd, false);
        
//        System.out.println("Abandon ID: " + tmpTournamentSelectAbandonID);
        GAIndividual tmpGAIndividualAbandon = (GAIndividual)gaInds.get(tmpTournamentSelectAbandonID);  
//        System.out.println("Abandon Before: ");          
        for (int i = 0; i  < numGAGenes; i++)
        {                        
            System.out.print(tmpGAIndividualAbandon.GAChromosome[i] + " ");          
        }  
        
        int[] tmpCrossoverTemplate = new int[numGAGenes];
        
        int tmpTournamentSelectDadID;
        int tmpTournamentSelectMomID;
        
        do
        {    
            tmpTournamentSelectDadID = SelectTournament(crossTournamentSize, true);            
        }    
        while (tmpTournamentSelectDadID == tmpTournamentSelectAbandonID);                
        
        do
        {    
            tmpTournamentSelectMomID = SelectTournament(crossTournamentSize, true);            
        } while ((tmpTournamentSelectMomID == tmpTournamentSelectDadID) || (tmpTournamentSelectMomID == tmpTournamentSelectAbandonID));    
        
        //System.out.println("Dad ID: " + tmpTournamentSelectDadID);
        //System.out.println("Mom ID: " + tmpTournamentSelectMomID);
        
        GAIndividual tmpGAIndividualDad = (GAIndividual)gaInds.get(tmpTournamentSelectDadID);  
        GAIndividual tmpGAIndividualMom = (GAIndividual)gaInds.get(tmpTournamentSelectMomID);          
                
////        System.out.println("Dad: "); 
//        for (int i = 0; i  < numGAGenes; i++)
//        {                                 
//            System.out.print(tmpGAIndividualDad.GAChromosome[i] + " ");          
//        }
//        System.out.println("Mom: "); 
//        for (int i = 0; i  < numGAGenes; i++)
//        {            
//                     
//            System.out.print(tmpGAIndividualMom.GAChromosome[i] + " ");          
//        }
//        System.out.println(""); 
        
        segStartPoint = rand.nextInt(numGAGenes);
        
        do {            
            segEndPoint = rand.nextInt(numGAGenes);
        } while (segStartPoint > segEndPoint);    
        /*
        if (segStartPoint != (numGAGenes - 1))
            segEndPoint = segStartPoint + (rand.nextInt(numGAGenes - (segStartPoint + 1)) + 1);
        else
            segEndPoint = segStartPoint;                     
         */
//        if (segStartPoint == segEndPoint)
//            System.out.println("Same Seg Points");
//        System.out.print("Start Seg Point: " + segStartPoint + " ");
//        System.out.println("End Seg Point: " + segEndPoint);
        
        for (int i = 0; i  < numGAGenes; i++)
        {
            if ((i < segStartPoint) || (i > segEndPoint))
                tmpCrossoverTemplate[i] = tmpGAIndividualDad.GAChromosome[i];
            else
                tmpCrossoverTemplate[i] = tmpGAIndividualMom.GAChromosome[i];
        }    
        
        ((GAIndividual)gaInds.get(tmpTournamentSelectAbandonID)).GAChromosome = tmpCrossoverTemplate;
            
//        System.out.println("Abandon After: "); 
//        for (int i = 0; i  < numGAGenes; i++)
//        {            
//                     
//            System.out.print(tmpGAIndividualAbandon.GAChromosome[i]  + " ");          
//        }  
        
        return true;
    }        
    
    public boolean Mutation(float pointMutateProb)
    {
        //rand.setSeed(System.currentTimeMillis());
        //System.out.println("Mutation");
        
        int tmpTournamentSelectAbandonID = SelectTournament(numGAInd, false);
        
        //System.out.println("Abandon ID: " + tmpTournamentSelectAbandonID);
        
        int[] tmpMutateTemplate = new int[numGAGenes];
        
        int tmpTournamentSelectParentID;        
        
        do
            tmpTournamentSelectParentID = SelectTournament(mutateTournamentSize, true);        
        while (tmpTournamentSelectParentID == tmpTournamentSelectAbandonID); 
        
        for (int i = 0; i < numGAGenes; i++)
        {
            //if (rand.nextDouble() < pointMutateProb)
            if (rand.nextFloat() < pointMutateProb)
            {    
                do
                    tmpMutateTemplate[i] = rand.nextInt(base) + 1;
                while (tmpMutateTemplate[i] == ((GAIndividual)gaInds.get(tmpTournamentSelectParentID)).GAChromosome[i]);
            }                
            else            
                tmpMutateTemplate[i] = ((GAIndividual)gaInds.get(tmpTournamentSelectParentID)).GAChromosome[i];                                        
        }        
        
        ((GAIndividual)gaInds.get(tmpTournamentSelectAbandonID)).GAChromosome = tmpMutateTemplate;
        
        return true;
    }
    
    public boolean Reproduction()
    {
        //rand.setSeed(System.currentTimeMillis());
        //System.out.println("Reproduction");
        
        int tmpTournamentSelectAbandonID = SelectTournament(numGAInd, false);
        
        //System.out.println("Abandon ID: " + tmpTournamentSelectAbandonID);
        
        int[] tmpReproTemplate = new int[numGAGenes];
        
        int tmpTournamentSelectParentID;        
        
        do
            tmpTournamentSelectParentID = SelectTournament(reproTournamentSize, true);        
        while (tmpTournamentSelectParentID == tmpTournamentSelectAbandonID); 
        
        for (int i = 0; i < numGAGenes; i++)
        {           
            tmpReproTemplate[i] = ((GAIndividual)gaInds.get(tmpTournamentSelectParentID)).GAChromosome[i];                                        
        }        
        
        ((GAIndividual)gaInds.get(tmpTournamentSelectAbandonID)).GAChromosome = tmpReproTemplate;
        
        return true;
    }         
    
    /*    
    public double CalcuFitnessValue()
    {
        
    }
    
    public double BornIndividual()
    {
        
    }    
    
    public double ReplaceIndividual()
    {
        
    }            
     */
    
    public GAIndividual getGAIndividual(int IndID)
    {
        return (GAIndividual)gaInds.get(IndID);
    }        
    
    
    public float getCrossProb()
    {
        return crossProb;
    }        
    
    public void setCrossProb(float value)
    {
        if ((value <= 1.0f) && (value >= 0.0f))
            crossProb = value;
    }       
    
    public float getMutateProb()
    {
        return mutateProb;
    }   
    
    public void setMutateProb(float value)
    {
        if ((value <= 1.0f) && (value >= 0.0f))
            mutateProb = value;
    }   
    
   public float getPointMutateProb()
    {
        return pointMutateProb;
    }   
    
    public void setPointMutateProb(float value)
    {
        if ((value <= 1.0f) && (value >= 0.0f))
            pointMutateProb = value;
    }       
    

    public float getReproProb()
    {
        return reproProb;
    }   
    
    public void setReproProb(float value)
    {
        if ((value <= 1.0f) && (value >= 0.0f))
            reproProb = value;
    }   
}
