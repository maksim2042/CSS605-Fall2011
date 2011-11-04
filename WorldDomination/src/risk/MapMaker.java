package risk;
import ec.util.MersenneTwisterFast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFrame;
import sim.util.Bag;

/**
 *
 * @author Omar A. Guerrero
 */
public class MapMaker extends JFrame{

    private int MAPHIGHT = 100;
    private int MAPWIDTH = 300;
    private char[][] mapTerritories;
    private Imperial world;
    private MersenneTwisterFast rand = new MersenneTwisterFast();

    public MapMaker(Imperial world){
        this.world = world;
    }

    public int[][] readData(String filePath){
        mapTerritories = new char[MAPHIGHT][MAPWIDTH];
        String[] mapStrings = new String[111];
        File inputFile = new File(filePath);
        BufferedReader bufRdr = null;
        int[][] countries = new int[MAPHIGHT][MAPWIDTH];

        try {
            bufRdr = new BufferedReader(new FileReader(inputFile));
            for (int i=0; i<MAPHIGHT+11; i++){
                mapStrings[i] = bufRdr.readLine();
            }

        } catch(IOException e) {
            e.printStackTrace();
        }

        for(int i=0; i<MAPHIGHT-2; i++){
            char[] tempString = mapStrings[i+11].toCharArray();
            for (int j=0; j<MAPWIDTH; j++){
                mapTerritories[i][j] = tempString[j];
            }
        }

        for(int i=0; i<MAPHIGHT-2; i++){
            for (int j=0; j<MAPWIDTH; j++){
                switch(mapTerritories[i][j]){
                    case '-': countries[i][j] = 0;
                    break;
                    case '.': countries[i][j] = 0;
                    break;
                    case '1': countries[i][j] = 1;
                    break;
                    case '2': countries[i][j] = 2;
                    break;
                    case 'c': countries[i][j] = 3;
                    break;
                    case 'b': countries[i][j] = 4;
                    break;
                    case 'q': countries[i][j] = 5;
                    break;
                    case '4': countries[i][j] = 6;
                    break;
                    case 'Z': countries[i][j] = 7;
                    break;
                    case ':': countries[i][j] = 8;
                    break;
                    case 's': countries[i][j] = 9;
                    break;
                    case 'D': countries[i][j] = 11;
                    break;
                    case '3': countries[i][j] = 12;
                    break;
                    case 'v': countries[i][j] = 13;
                    break;
                    case 't': countries[i][j] = 10;
                    break;
                    case 'i': countries[i][j] = 14;
                    break;
                    case 'f': countries[i][j] = 15;
                    break;
                    case 'n': countries[i][j] = 16;
                    break;
                    case 'I': countries[i][j] = 19;
                    break;
                    case 'e': countries[i][j] = 17;
                    break;
                    case 'k': countries[i][j] = 18;
                    break;
                    case 'w': countries[i][j] = 20;
                    break;
                    case '+': countries[i][j] = 21;
                    break;
                    case 'G': countries[i][j] = 22;
                    break;
                    case '7': countries[i][j] = 23;
                    break;
                    case 'o': countries[i][j] = 24;
                    break;
                    case '5': countries[i][j] = 25;
                    break;
                    case 'm': countries[i][j] = 26;
                    break;
                    case 'g': countries[i][j] = 35;
                    break;
                    case 'u': countries[i][j] = 31;
                    break;
                    case 'h': countries[i][j] = 36;
                    break;
                    case '8': countries[i][j] = 37;
                    break;
                    case 'r': countries[i][j] = 38;
                    break;
                    case 'N': countries[i][j] = 27;
                    break;
                    case 'j': countries[i][j] = 28;
                    break;
                    case 'y': countries[i][j] = 29;
                    break;
                    case '9': countries[i][j] = 30;
                    break;
                    case 'M': countries[i][j] = 32;
                    break;
                    case 'x': countries[i][j] = 33;
                    break;
                    case 'L': countries[i][j] = 34;
                    break;
                    case '6': countries[i][j] = 40;
                    break;
                    case 'W': countries[i][j] = 41;
                    break;
                    case 'E': countries[i][j] = 42;
                    break;
                    case 'l': countries[i][j] = 39;
                    break;
                }
            }
        }
        return countries;
    }

    public void randomColors(Bag territories, int[][] countries){
        for (int iter=0; iter<territories.numObjs; iter++){
            int value = (((Territory)territories.get(iter))).getType();
            for(int i=0; i<MAPHIGHT-2; i++){
                for (int j=0; j<MAPWIDTH; j++){
                    if (countries[i][j] == ((Territory)territories.get(iter)).getId()){
                        world.countriesGrid.field[j][i] = value;
                    }
                }
            }
        }
    }

    public void setNeighbors(Bag territories, int[][] countries) throws CloneNotSupportedException {
        for(int i=0; i<territories.numObjs; i++){
            Territory terr = (Territory)territories.get(i);
            for (int x=0; x<MAPWIDTH; x++){
                for (int y=0; y<MAPHIGHT; y++){
                    if(countries[y][x] == terr.getId()){
                        if(countries[y-1][x] != terr.getId() && countries[y-1][x] > 0 &&
                                !terr.getNeighborTerritories().contains(territories.get(countries[y-1][x]-1))){
                            terr.getNeighborTerritories().add(territories.get(countries[y-1][x]-1));
                        }
                        if(countries[y+1][x] != terr.getId() && countries[y+1][x] > 0 &&
                                !terr.getNeighborTerritories().contains(territories.get(countries[y+1][x]-1))){
                            terr.getNeighborTerritories().add(territories.get(countries[y+1][x]-1));
                        }
                        if(countries[y][x+1] != terr.getId() && countries[y][x+1] > 0 &&
                                !terr.getNeighborTerritories().contains(territories.get(countries[y][x+1]-1))){
                            terr.getNeighborTerritories().add(territories.get(countries[y][x+1]-1));
                        }
                        if(countries[y][x-1] != terr.getId() && countries[y][x-1] > 0 &&
                                !terr.getNeighborTerritories().contains(territories.get(countries[y][x-1]-1))){
                            terr.getNeighborTerritories().add(territories.get(countries[y][x-1]-1));
                        }
                    }
                }
            }
        }
        ((Territory)territories.get(1-1)).getNeighborTerritories().add(territories.get(30-1));
        ((Territory)territories.get(30-1)).getNeighborTerritories().add(territories.get(1-1));

        ((Territory)territories.get(2-1)).getNeighborTerritories().add(territories.get(9-1));
        ((Territory)territories.get(9-1)).getNeighborTerritories().add(territories.get(2-1));

        ((Territory)territories.get(5-1)).getNeighborTerritories().add(territories.get(9-1));
        ((Territory)territories.get(9-1)).getNeighborTerritories().add(territories.get(5-1));

        ((Territory)territories.get(4-1)).getNeighborTerritories().add(territories.get(9-1));
        ((Territory)territories.get(9-1)).getNeighborTerritories().add(territories.get(4-1));

        ((Territory)territories.get(12-1)).getNeighborTerritories().add(territories.get(21-1));
        ((Territory)territories.get(21-1)).getNeighborTerritories().add(territories.get(12-1));

        ((Territory)territories.get(14-1)).getNeighborTerritories().add(territories.get(9-1));
        ((Territory)territories.get(9-1)).getNeighborTerritories().add(territories.get(14-1));

        ((Territory)territories.get(16-1)).getNeighborTerritories().add(territories.get(14-1));
        ((Territory)territories.get(14-1)).getNeighborTerritories().add(territories.get(16-1));

        ((Territory)territories.get(14-1)).getNeighborTerritories().add(territories.get(15-1));
        ((Territory)territories.get(15-1)).getNeighborTerritories().add(territories.get(14-1));

        ((Territory)territories.get(16-1)).getNeighborTerritories().add(territories.get(15-1));
        ((Territory)territories.get(15-1)).getNeighborTerritories().add(territories.get(16-1));

        ((Territory)territories.get(16-1)).getNeighborTerritories().add(territories.get(17-1));
        ((Territory)territories.get(17-1)).getNeighborTerritories().add(territories.get(16-1));

        ((Territory)territories.get(15-1)).getNeighborTerritories().add(territories.get(17-1));
        ((Territory)territories.get(17-1)).getNeighborTerritories().add(territories.get(15-1));

        ((Territory)territories.get(16-1)).getNeighborTerritories().add(territories.get(19-1));
        ((Territory)territories.get(19-1)).getNeighborTerritories().add(territories.get(16-1));

        ((Territory)territories.get(19-1)).getNeighborTerritories().add(territories.get(21-1));
        ((Territory)territories.get(21-1)).getNeighborTerritories().add(territories.get(19-1));

        ((Territory)territories.get(22-1)).getNeighborTerritories().add(territories.get(20-1));
        ((Territory)territories.get(20-1)).getNeighborTerritories().add(territories.get(22-1));

        ((Territory)territories.get(23-1)).getNeighborTerritories().add(territories.get(35-1));
        ((Territory)territories.get(35-1)).getNeighborTerritories().add(territories.get(23-1));

        ((Territory)territories.get(23-1)).getNeighborTerritories().add(territories.get(26-1));
        ((Territory)territories.get(26-1)).getNeighborTerritories().add(territories.get(23-1));

        ((Territory)territories.get(26-1)).getNeighborTerritories().add(territories.get(25-1));
        ((Territory)territories.get(25-1)).getNeighborTerritories().add(territories.get(26-1));

        ((Territory)territories.get(38-1)).getNeighborTerritories().add(territories.get(39-1));
        ((Territory)territories.get(39-1)).getNeighborTerritories().add(territories.get(38-1));

        ((Territory)territories.get(30-1)).getNeighborTerritories().add(territories.get(34-1));
        ((Territory)territories.get(34-1)).getNeighborTerritories().add(territories.get(30-1));

        ((Territory)territories.get(33-1)).getNeighborTerritories().add(territories.get(34-1));
        ((Territory)territories.get(34-1)).getNeighborTerritories().add(territories.get(33-1));

        ((Territory)territories.get(39-1)).getNeighborTerritories().add(territories.get(40-1));
        ((Territory)territories.get(40-1)).getNeighborTerritories().add(territories.get(39-1));

        ((Territory)territories.get(40-1)).getNeighborTerritories().add(territories.get(41-1));
        ((Territory)territories.get(41-1)).getNeighborTerritories().add(territories.get(40-1));

        ((Territory)territories.get(40-1)).getNeighborTerritories().add(territories.get(42-1));
        ((Territory)territories.get(42-1)).getNeighborTerritories().add(territories.get(40-1));

        ((Territory)territories.get(39-1)).getNeighborTerritories().add(territories.get(41-1));
        ((Territory)territories.get(41-1)).getNeighborTerritories().add(territories.get(39-1));

        for(int i=0; i<territories.numObjs; i++){
            Territory territory = (Territory)territories.get(i);
            territory.physicalNeighbors = (Bag)territory.getNeighborTerritories().clone();
        }
    }

}
