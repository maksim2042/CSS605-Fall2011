package risk;
import sim.util.Bag;

/**
 *
 * @author Omar A. Guerrero
 */
public class TradeProtocol {
    private static final int NATRES = 1;
    private static final int PEASANTS = 2;
    private static final int SOLDIERS = 3;

    public TradeProtocol(){}

    protected void trade(Bag territories, Bag rulers, long period){
        for (int i=0; i<territories.numObjs; i++){
            Territory trader = ((Territory)territories.get(i));

            // Let's trade: ask each agent's trading positions.
            trader.getRuler().trade();
            double[] trading = new double[trader.getRuler().trade.length];
            for (int j=0; j<trader.getRuler().trade.length; j++){
                trading[j] = trader.getRuler().trade[j];
            }
            if((int)trading[0]-1<territories.numObjs && (int)trading[0]>0){
                if (!territories.get((int)trading[0]-1).equals(trader)){
                    //Get a trading partner
                    Territory partner = ((Territory)territories.get((int)trading[0]-1));
                    //If conditions for proposing trade are met (been neighbors of been of the same type),
                    // trade is performed for each posible pair of distinct goods to be axchanged
                    if(trader.getRuler().getType()==partner.getRuler().getType() ||
                            trader.getNeighborTerritories().contains(partner)){
                        if(trading[1]==NATRES && trading[3]==PEASANTS && trading[2]>=0 && trading[4]>=0){
                            if(trading[4]>partner.getPeasants() || trading[2]>trader.getNatRes()){
                                continue;
                            }
                            // Send the trade proposal to the potential trading partner, providing the pertinent information
                            // The partner should update its acceptTrade according to it's strategy, which will express
                            // the response of accepting or rejecting the offer
                            partner.getRuler().acceptTrade(partner, trading[2], (int)trading[1], trading[4],
                                    (int)trading[3]);
                            if(partner.getRuler().acceptTrade && trading[2]>=partner.getNatRes()){
                                trader.addNatRes(trading[2]);
                                trader.addPeasants(-trading[4]);
                                partner.addNatRes(-trading[2]);
                                partner.addPeasants(trading[4]);
                                trader.getRuler().tradeOutcome(period, trader.getId(), trading, true);
                                partner.getRuler().tradeOutcome(period, trader.getId(), trading, true);
                            }
                            else{
                                trader.getRuler().tradeOutcome(period, trader.getId(), trading, false);
                                partner.getRuler().tradeOutcome(period, trader.getId(), trading, false);
                            }
                        }
                        if(trading[1]==NATRES && trading[3]==SOLDIERS && trading[2]>=0 && trading[4]>=0){
                            if(trading[4]>partner.getSoldiers() || trading[2]>trader.getNatRes()){
                                continue;
                            }
                            // Send the trade proposal to the potential trading partner, providing the pertinent information
                            // The partner should update its acceptTrade according to it's strategy, which will express
                            // the response of accepting or rejecting the offer
                            partner.getRuler().acceptTrade(partner, trading[2], (int)trading[1], trading[4],
                                    (int)trading[3]);
                            // If the proposal is accepted by the partner and the partner has enough goods to exchange
                            // then trade proceeds
                            if(partner.getRuler().acceptTrade && trading[2]>=partner.getNatRes()){
                                trader.addNatRes(trading[2]);
                                trader.addSoldiers(-trading[4]);
                                partner.addNatRes(-trading[2]);
                                partner.addSoldiers(trading[4]);
                                trader.getRuler().tradeOutcome(period, trader.getId(), trading, true);
                                partner.getRuler().tradeOutcome(period, trader.getId(), trading, true);
                            }
                            else{
                                trader.getRuler().tradeOutcome(period, trader.getId(), trading, false);
                                partner.getRuler().tradeOutcome(period, trader.getId(), trading, false);
                            }
                        }
                        if(trading[1]==PEASANTS && trading[3]==NATRES && trading[2]>=0 && trading[4]>=0){
                            if(trading[4]>partner.getNatRes() || trading[2]>trader.getPeasants()){
                                continue;
                            }
                            // Send the trade proposal to the potential trading partner, providing the pertinent information
                            // The partner should update its acceptTrade according to it's strategy, which will express
                            // the response of accepting or rejecting the offer
                            partner.getRuler().acceptTrade(partner, trading[2], (int)trading[1], trading[4],
                                    (int)trading[3]);
                            // If the proposal is accepted by the partner and the partner has enough goods to exchange
                            // then trade proceeds
                            if(partner.getRuler().acceptTrade && trading[2]>=partner.getPeasants()){
                                trader.addPeasants(trading[2]);
                                trader.addNatRes(-trading[4]);
                                partner.addPeasants(-trading[2]);
                                partner.addNatRes(trading[4]);
                                trader.getRuler().tradeOutcome(period, trader.getId(), trading, true);
                                partner.getRuler().tradeOutcome(period, trader.getId(), trading, true);
                            }
                            else{
                                trader.getRuler().tradeOutcome(period, trader.getId(), trading, false);
                                partner.getRuler().tradeOutcome(period, trader.getId(), trading, false);
                            }
                        }
                        if(trading[1]==PEASANTS && trading[3]==SOLDIERS && trading[2]>=0 && trading[4]>=0){
                            if(trading[4]>partner.getSoldiers() || trading[2]>trader.getPeasants()){
                                continue;
                            }
                            // Send the trade proposal to the potential trading partner, providing the pertinent information
                            // The partner should update its acceptTrade according to it's strategy, which will express
                            // the response of accepting or rejecting the offer
                            partner.getRuler().acceptTrade(partner, trading[2], (int)trading[1], trading[4],
                                    (int)trading[3]);
                            // If the proposal is accepted by the partner and the partner has enough goods to exchange
                            // then trade proceeds
                            if(partner.getRuler().acceptTrade && trading[2]>=partner.getPeasants()){
                                trader.addPeasants(trading[2]);
                                trader.addSoldiers(-trading[4]);
                                partner.addPeasants(-trading[2]);
                                partner.addSoldiers(trading[4]);
                                trader.getRuler().tradeOutcome(period, trader.getId(), trading, true);
                                partner.getRuler().tradeOutcome(period, trader.getId(), trading, true);
                            }
                            else{
                                trader.getRuler().tradeOutcome(period, trader.getId(), trading, false);
                                partner.getRuler().tradeOutcome(period, trader.getId(), trading, false);
                            }
                        }
                        if(trading[1]==SOLDIERS && trading[3]==NATRES && trading[2]>=0 && trading[4]>=0){
                            if(trading[4]>partner.getNatRes() || trading[2]>trader.getSoldiers()){
                                continue;
                            }
                            // Send the trade proposal to the potential trading partner, providing the pertinent information
                            // The partner should update its acceptTrade according to it's strategy, which will express
                            // the response of accepting or rejecting the offer
                            partner.getRuler().acceptTrade(partner, trading[2], (int)trading[1], trading[4],
                                    (int)trading[3]);
                            // If the proposal is accepted by the partner and the partner has enough goods to exchange
                            // then trade proceeds
                            if(partner.getRuler().acceptTrade && trading[2]>=partner.getSoldiers()){
                                trader.addSoldiers(trading[2]);
                                trader.addNatRes(-trading[4]);
                                partner.addSoldiers(-trading[2]);
                                partner.addNatRes(trading[4]);
                                trader.getRuler().tradeOutcome(period, trader.getId(), trading, true);
                                partner.getRuler().tradeOutcome(period, trader.getId(), trading, true);
                            }
                            else{
                                trader.getRuler().tradeOutcome(period, trader.getId(), trading, false);
                                partner.getRuler().tradeOutcome(period, trader.getId(), trading, false);
                            }
                        }
                        if(trading[1]==SOLDIERS && trading[3]==PEASANTS && trading[2]>=0 && trading[4]>=0){
                            if(trading[4]>partner.getPeasants() || trading[2]>trader.getSoldiers()){
                                continue;
                            }
                            // Send the trade proposal to the potential trading partner, providing the pertinent information
                            // The partner should update its acceptTrade according to it's strategy, which will express
                            // the response of accepting or rejecting the offer
                            partner.getRuler().acceptTrade(partner, trading[2], (int)trading[1], trading[4],
                                    (int)trading[3]);
                            // If the proposal is accepted by the partner and the partner has enough goods to exchange
                            // then trade proceeds
                            if(partner.getRuler().acceptTrade && trading[2]>=partner.getSoldiers()){
                                trader.addSoldiers(trading[2]);
                                trader.addPeasants(-trading[4]);
                                partner.addSoldiers(-trading[2]);
                                partner.addPeasants(trading[4]);
                                trader.getRuler().tradeOutcome(period, trader.getId(), trading, true);
                                partner.getRuler().tradeOutcome(period, trader.getId(), trading, true);
                            }
                            else{
                                trader.getRuler().tradeOutcome(period, trader.getId(), trading, false);
                                partner.getRuler().tradeOutcome(period, trader.getId(), trading, false);
                            }
                        }
                    }
                }
            }
        }
    }
}
