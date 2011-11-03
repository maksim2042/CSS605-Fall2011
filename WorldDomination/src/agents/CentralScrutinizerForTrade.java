/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agents;
import risk.*;
import sim.util.Bag;


/**
 *
 * @author jgugliotti
 */
public class CentralScrutinizerForTrade {
    private static final int NATRES = 1;
    private static final int PEASANTS = 2;
    private static final int SOLDIERS = 3;
    private Territory territory;


    CentralScrutinizerForTrade() {
        
    }

    boolean acceptTrade(
        Territory me,
        Territory offerer,
	double demand,
	int typeDemand,
        double offer,
        int typeOffer)
    {

        double current[] = {0,me.getNatRes(), me.getPeasants(),me.getSoldiers()};
        double avgAmount = (me.getNatRes() + me.getPeasants() + me.getSoldiers()) / 3.0 ;
        boolean[] deficient = {
            false
            ,(current[NATRES] < avgAmount)
           ,(current[PEASANTS] < avgAmount)
           ,(current[SOLDIERS] < avgAmount)
        };

        //error protection:
        if (typeDemand < 0 || typeDemand  > 2 ) {
            //System.out.println("(typeDemand < 0 || typeDemand  > 2 ) from " + offerer.getId());
            return false; }
        if (offer < 0 ) {
            //System.out.println("offer < 0 from " + offerer.getId());
            return false;
        }
        if (demand > ((current[NATRES] + current[PEASANTS]+current[SOLDIERS] )) &&
                (!(offer > 50 * demand))) {
           // System.out.println("demand > avgAmount && (!(offer > 10 * demand)) from " + offerer.getId());
            return false;

        }

        if (typeDemand == typeOffer && offer > demand) {
            return true;
        }

        double pct_deficient = 0.0;

        switch (typeOffer) {
            case NATRES: {
                if (deficient[NATRES]) { //need natural resources
                    pct_deficient = current[NATRES] / avgAmount;
                    if (typeDemand == SOLDIERS) { //soldiers are more valuable than natural resources
                        return (offer > demand * ( pct_deficient * Math.random() * 10 + 5 )) //10 -15 fold gain
                           && (demand / current[SOLDIERS] < .5 * (pct_deficient) ); //give up no more than 5%
                    }
                    if (typeDemand == PEASANTS) {
                        return (offer > (demand * (5 + ( Math.random() * 5)))
                           && (demand / current[PEASANTS] < .10 ) );
                    }
                } else {
                    if (typeDemand == SOLDIERS) { //soldiers are more valuable than natural resources
                        return (offer > (demand * (5 + Math.random() * 5))
                           && (demand / current[SOLDIERS] < .01 ) );
                    }
                    if (typeDemand == PEASANTS) {
                        return (offer > (demand * (5 + ( Math.random() * 5)))
                           && (demand / current[PEASANTS] < .10 ) );
                    }
                }
                break ;
            }
            case PEASANTS: {
                if (deficient[PEASANTS]) {
                    pct_deficient = current[PEASANTS] / avgAmount;
                    if (typeDemand == SOLDIERS) { //soldiers are more valuable than natural resources
                        return (offer > demand * ( pct_deficient * Math.random() * 10 + 5 )) //10 -15 fold gain
                           && (demand / current[SOLDIERS] < .5 * (pct_deficient) ); //give up no more than 5%
                    }
                    if (typeDemand == NATRES) {
                        return (offer > (demand * (5 + ( Math.random() * 5)))
                           && (demand / current[NATRES] < .10 ) );
                    }
                } else {
                    if (typeDemand == SOLDIERS) { //soldiers are more valuable than natural resources
                        return (offer > (demand * (5 + Math.random() * 5))
                           && (demand / current[SOLDIERS] < .01 ) );
                    }
                    if (typeDemand == NATRES) {
                        return (offer > (demand * (5 + ( Math.random() * 5)))
                           && (demand / current[NATRES] < .10 ) );
                    }
                }
                break ;
            }
            case SOLDIERS: {
                if (deficient[SOLDIERS]) {
                    pct_deficient = current[PEASANTS] / avgAmount;
                    if (typeDemand == PEASANTS) {
                        return (offer > (demand * (5 + ( Math.random() * 5)))
                           && (demand / current[PEASANTS] < .10 ) );
                    }
                    if (typeDemand == NATRES) {
                        return (offer > (demand * (5 + ( Math.random() * 5)))
                           && (demand / current[NATRES] < .10 ) );
                    }
                } else {
                    if (typeDemand == PEASANTS) {
                        return (offer > (demand * (5 + ( Math.random() * 5)))
                           && (demand / current[PEASANTS] < .10 ) );
                    }
                    if (typeDemand == NATRES) {
                        return (offer > (demand * (5 + ( Math.random() * 5)))
                           && (demand / current[NATRES] < .10 ) );
                    }
                }
                break ;
            }
            default: return false;
        }
        return false;
    }

    double extraSoldiers () {
        return 0;
    }

    double extraPeasants () {
        return 0;
    }

    double extraNatRes () {
        return 0;
    }
}

enum TraderType { Bold, Doormat, Fair , Uncommitted};

class TradeData {
    private java.util.HashMap<Integer,java.util.LinkedList<TradeOffer>> boldTrades
            = new java.util.HashMap<Integer,java.util.LinkedList<TradeOffer>>();
    private java.util.HashMap<Integer,java.util.LinkedList<TradeOffer>> doormatTrades
            = new java.util.HashMap<Integer,java.util.LinkedList<TradeOffer>>();
    private java.util.HashMap<Integer,java.util.LinkedList<TradeOffer>> fairTrades
            = new java.util.HashMap<Integer,java.util.LinkedList<TradeOffer>>();

    TradeData () {}//constructor
    TraderType getTraderType (Territory territory) {
        int bold = 0; int doormat = 0 ; int fair = 0;
        bold = (boldTrades.containsKey(territory.getId()))
                ? boldTrades.get(territory.getId()).size()
                : 0;

        doormat = (doormatTrades.containsKey(territory.getId()))
                ? doormatTrades.get(territory.getId()).size()
                : 0;

        fair = (fairTrades.containsKey(territory.getId()))
                ? fairTrades.get(territory.getId()).size()
                : 0;

        if ( bold > doormat && bold > fair ) return TraderType.Bold;
        else if ( doormat > bold && doormat > fair ) return TraderType.Doormat;
        else if ( fair > doormat && fair > bold ) return TraderType.Fair;
        else return TraderType.Uncommitted;
    }
}


class TradeOffer {
    Territory offerer;
    double demand;
    int typeDemand;
    double offer;
    int typeOffer;

    TradeOffer ( Territory offerer,double demand,int typeDemand,double offer,int typeOffer ) {
        this.offerer 	= offerer;
        this.demand  	= demand;
        this.typeDemand = typeDemand;
        this.offer		= offer;
        this.typeOffer	= typeOffer;
    }
}