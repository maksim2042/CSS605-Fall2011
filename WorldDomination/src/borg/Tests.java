/*
 * Author: Chris Kirkos
 */
package borg;

import agents.Borg;
import java.util.HashMap;
import risk.*;
//import sim.util.Bag;

public class Tests {

    public static void main(String[] args){
        test2();
    }

    public static void test1(){
        HashMap<Integer,Integer> map1 = new HashMap<Integer,Integer>();
        map1.put(1, 4);
        map1.put(2, 3);
        map1.put(3, 2);
        map1.put(4, 1);
        map1.put(5, 1);
        System.out.println(map1.toString());
        HashMap<Integer,Integer> map1S = StrategyUtility.sortByValueDescending(map1);
        System.out.println(map1S.toString());
    }

    public static void test2(){
        HashMap<Double,Double> stuff = new HashMap<Double,Double>();
        stuff.put(new Double(1.0), new Double(1.1));
        stuff.put(new Double(2.0), new Double(2.1));
        stuff.put(new Double(2.0), new Double(2.1));
        double temp = stuff.get(new Double(1.0));
        System.out.println(temp);
    }

    public static void test3(){
        Agent a1 = new Borg(1,1);
        Agent a2 = new Borg(2,1);

        Territory t1 = new Territory(1, 1, 1, 1);
        Territory t2 = new Territory(1, 1, 1, 1);



    }

}
