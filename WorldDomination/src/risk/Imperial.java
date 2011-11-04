package risk;
import agents.*;
import ec.util.MersenneTwisterFast;
import sim.engine.*;
import sim.field.grid.*;
import sim.util.Bag;

/**
 *
 * @author Omar A. Guerrero
 */
public class Imperial extends SimState{
    private int[][] countries;
    private int MAPHIGHT = 100;
    private int MAPWIDTH = 300;
    Bag territories = new Bag();
    Bag territori = new Bag();
    Bag lords = new Bag();
    private String dataFile;
    DoubleGrid2D countriesGrid = new DoubleGrid2D(MAPWIDTH, MAPHIGHT, 0);
    private MersenneTwisterFast rand = new MersenneTwisterFast();
    private TradeProtocol tradeProtocol = new TradeProtocol();
    private WarProtocol warProtocol;
    MapMaker mapMaker;
    Bag colorsBag;

    
    public Imperial(long seed){
        super(seed);
    }

    // This method starts up the simulation:
    // Sets the geographical map, instantiates the 42 lords and their corresponding territories.
    @Override
    public void start(){
        super.start();
        territories.clear();
        lords.clear();
        countriesGrid = new DoubleGrid2D(MAPWIDTH, MAPHIGHT, 0);
        tradeProtocol = new TradeProtocol();

        dataFile = "./mapFile.txt";
        mapMaker = new MapMaker(this);
        countries = mapMaker.readData(dataFile);
        warProtocol = new WarProtocol(MAPHIGHT, MAPWIDTH, countries, countriesGrid);
        for (int i=0; i<42; i++){
            territories.add(new Territory(i+1, rand.nextDouble()*5, rand.nextDouble()*5, rand.nextDouble()));
        }
        for (int i=0; i<42; i++){
            if(i<6){
                Agent ruler = new Catenaccio(i+1, 1);
                lords.add(ruler);
            }
            else if(i<12){
                Agent ruler = new ChuckNorris(i+1, 2);
                lords.add(ruler);
            }else if(i<18){
                Agent ruler = new Guevara(i+1, 3);
                lords.add(ruler);
            }else if(i<24){
                Agent ruler = new Borg(i+1, 4);
                lords.add(ruler);
            }else if(i<30){
                Agent ruler = new ItsGoodToBeTheKing(i+1, 5);
                lords.add(ruler);
            }else if(i<36){
                Agent ruler = new Calvin(i+1, 6);
                lords.add(ruler);
            }else if(i<42){
                Agent ruler = new SWMBO(i+1, 7);
                lords.add(ruler);
            }
        }

        for (int i=0; i<lords.numObjs; i++){
            int assign = rand.nextInt(42);
                while (((Territory)territories.get(assign)).getRuler() != null){
                    assign = rand.nextInt(42);
            }
            ((Territory)territories.get(assign)).setRuler((Agent)lords.get(i));
            ((Agent)lords.get(i)).setMyTerritory((Territory)territories.get(assign));
        }
        try {
            mapMaker.setNeighbors(territories, countries);
        } catch (CloneNotSupportedException ex) {}
        mapMaker.randomColors(territories, countries);
        schedule.scheduleRepeating(turns);

        colorsBag = new Bag();
        for (int i=0; i<territories.numObjs; i++){
            Territory territory = (Territory)territories.get(i);
            if(!colorsBag.contains(territory.getRuler().rulerColor)){
                colorsBag.add(territory.getRuler().rulerColor);
            }
        }
        
        try {
			territori = (Bag) territories.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // This method is inherited from the SimState superclass from MASON
    // Basicaly, this method is called every period by the MASON schedule
    // In this case, it is used by our system to run the sequence of steps of the game
    Steppable turns = new Steppable(){
        public void step(SimState state){
            // Shuffle the list of territories so that the lords' activation order is random
            territories.shuffle(rand);
            for(int i=0; i<territories.numObjs; i++){
                Territory territory = (Territory)territories.get(i);
                // Calls the lord to update the tax rate
                territory.getRuler().chooseTax();
                // Enforces the tax paymentsfor all lords
                payTax(territory);
                // Calls the lord to establish which territories will receive soldiers and how many for each one
                territory.getRuler().setRetributionsAndBeneficiaries();
                // Enforces the desired redistributive policy of all lords
                reDistribute(territory);
            }

            for (int i=0; i<territories.numObjs; i++){
                Territory t = (Territory)territories.get(i);
                // Grows the resources of each territory
                t.grow();
                // Called to feed the soldiers
                t.feedSoldiers();
            }

            // Makes trade happen
            tradeProtocol.trade(territori, lords, schedule.getSteps());
            try {
                // Makes war happen
                warProtocol.war(territori, lords, schedule.getSteps());
            } catch (CloneNotSupportedException ex) {}
        }
    };

    // This method collects the taxes of each subordinate of each lord, and transfers them to the lord.
    private void payTax(Territory territory){
        for(int i=0; i<territory.getConquered().numObjs; i++){
            Territory subordinate = (Territory)territory.getConquered().get(i);
            payTax(subordinate);
        }
        if(territory.getSuperior()!=null){
            double tax = territory.getSuperior().getRuler().getTax();
            if(tax<0 || tax>0.5){
                tax=0;
            }
            territory.getSuperior().addNatRes(territory.getNatRes()*tax);
            territory.getSuperior().addPeasants(territory.getPeasants()*tax);
            territory.addNatRes(-territory.getNatRes()*tax);
            territory.addPeasants(-territory.getPeasants()*tax);
        }
    }

    // This method goes through the list of beneficiaries of the lord, and transfers the
    // chosen amount of soldiers from to them.
    private void reDistribute(Territory territory){
        if (territory.getRuler().getBeneficiaries().numObjs!=territory.getRuler().getRetributions().length){
            return;
        }
        double totalRetribution=0;
        for(int i=0; i<territory.getRuler().getRetributions().length; i++){
            if (territory.getRuler().getRetributions()[i]<0){
                return;
            }
            totalRetribution += territory.getRuler().getRetributions()[i];
        }
        if (totalRetribution > territory.getSoldiers()){
            return;
        }
        for(int i=0; i<territory.getRuler().getBeneficiaries().numObjs; i++){
            Territory beneficiary = (Territory)territory.getRuler().getBeneficiaries().get(i);
            if (territory.isInHierarchy(beneficiary)){
                beneficiary.addSoldiers(territory.getRuler().getRetributions()[i]);
            }
        }
        territory.addSoldiers(-totalRetribution);
    }

    // This is just the main method from SimState that runs the entire simulation
    public static void main(String[] args){
        doLoop(Imperial.class, args);
        System.exit(0);
    }
}