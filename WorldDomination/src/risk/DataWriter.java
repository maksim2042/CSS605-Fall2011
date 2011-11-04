/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package risk;

import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author tequilamambo
 */
public class DataWriter {
    FileWriter territoriesWriter;
    FileWriter influenceWriter;
    FileWriter wealthWriter;
    public String territoriesFile = "./outputFiles/outputTerritories.csv";
    public String influenceFile = "./outputFiles/outputInfluence.csv";
    public String wealthFile = "./outputFiles/outputWealth.csv";

    public DataWriter(String[] names){
        setTitles(names);
    }

    public void setTitles(String[] names){
        try{
            territoriesWriter = new FileWriter(territoriesFile);
            influenceWriter = new FileWriter(influenceFile);
            wealthWriter = new FileWriter(wealthFile);

            for(int i=0; i<names.length; i++){
                territoriesWriter.append(names[i]);
                territoriesWriter.append(',');
                influenceWriter.append(names[i]);
                influenceWriter.append(',');
                wealthWriter.append(names[i]);
                wealthWriter.append(',');
            }
            territoriesWriter.append('\n');
            territoriesWriter.flush();
            influenceWriter.append('\n');
            influenceWriter.flush();
            wealthWriter.append('\n');
            wealthWriter.flush();

        }
        catch(IOException e){}
    }

    public void writeTerritories(double[] records){
        try{
            for(int i=0; i<records.length; i++){
                String record = Double.toString(records[i]);
                territoriesWriter.append(record);
                territoriesWriter.append(',');
            }
            territoriesWriter.append('\n');
            territoriesWriter.flush();
        }
        catch(IOException e){}
    }

    public void writeInfluence(double[] records){
        try{
            for(int i=0; i<records.length; i++){
                String record = Double.toString(records[i]);
                influenceWriter.append(record);
                influenceWriter.append(',');
            }
            influenceWriter.append('\n');
            influenceWriter.flush();
        }
        catch(IOException e){}
    }

    public void writeWealth(double[] records){
        try{
            for(int i=0; i<records.length; i++){
                String record = Double.toString(records[i]);
                wealthWriter.append(record);
                wealthWriter.append(',');
            }
            wealthWriter.append('\n');
            wealthWriter.flush();
        }
        catch(IOException e){}
    }
}
