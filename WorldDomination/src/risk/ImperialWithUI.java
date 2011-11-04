package risk;
import agents.*;
import sim.engine.*;
import sim.display.*;
import sim.portrayal.grid.*;
import java.awt.*;
import java.text.DecimalFormat;
import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author Omar A. Guerrero
 */
public class ImperialWithUI extends GUIState{

    private Display2D display;
    private JFrame displayFrame;
    private HierarchiesGraph graph = new HierarchiesGraph((Imperial)state);
    DefaultCategoryDataset territoriesBars;
    DefaultCategoryDataset influenceBars;
    DefaultCategoryDataset wealthBars;
    XYDataset timeSeriesData;
    private JFreeChart territoriesChart;
    private JFreeChart influenceChart;
    private JFreeChart peopleChart;
    private JFreeChart timeSeriesChart;
    private JFrame frameForTerritories;
    private JFrame frameForInfluences;
    private JFrame frameForPeople;
    private JFrame graphFrame;
    private JFrame frameForTimeSeries;
    Day period;
    TimeSeries E0;
    TimeSeries E1;
    TimeSeries E2;
    TimeSeries E3;
    TimeSeries E4;
    TimeSeries E5;
    TimeSeries E6;
    private Color[] colorsMap = new Color[]{
        Color.white,
        Color.black,
        Color.red,
        Color.blue,
        Color.yellow,
        Color.pink,
        Color.green,
        Color.orange};
    private Color[] colorsBars = new Color[]{
        Color.black,
        Color.red,
        Color.blue,
        Color.yellow,
        Color.pink,
        Color.green,
        Color.orange};
    private DataWriter dataWriter;

    private CategoryDataset createDataset(String label) {
        String name0 = (new Catenaccio(2,2)).getName();
        String name1 = (new ChuckNorris(2,2)).getName();
        String name2 = (new Guevara(2,2)).getName();
        String name3 = (new Borg(2,2)).getName();
        String name4 = (new ItsGoodToBeTheKing(2,2)).getName();
        String name5 = (new Calvin(2,2)).getName();
        String name6 = (new SWMBO(2,2)).getName();
        String[] size = new String[]{label};
        String[] names = new String[]{name0,name1,name2,name3,name4,name5,name6};
        dataWriter = new DataWriter(names);
        final double[][] data = new double[][] {{0,0,0,0,0,0,0}};
        return DatasetUtilities.createCategoryDataset(
            size,
            names,
            data
        );
    }

    private XYDataset createSeriesDataset() {
        E0 = new TimeSeries((new Catenaccio(2,2)).getName());
        E1 = new TimeSeries((new ChuckNorris(2,2)).getName());
        E2 = new TimeSeries((new Guevara(2,2)).getName());
        E3 = new TimeSeries((new Borg(2,2)).getName());
        E4 = new TimeSeries((new ItsGoodToBeTheKing(2,2)).getName());
        E5 = new TimeSeries((new Calvin(2,2)).getName());
        E6 = new TimeSeries((new SWMBO(2,2)).getName());
        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(E0);
        dataset.addSeries(E1);
        dataset.addSeries(E2);
        dataset.addSeries(E3);
        dataset.addSeries(E4);
        dataset.addSeries(E5);
        dataset.addSeries(E6);
        return dataset;
    }

    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "",
            "Time",
            "Territorial Size",
            dataset,
            true,
            true,
            false
        );
        final XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        final StandardXYToolTipGenerator g = new StandardXYToolTipGenerator();
        renderer.setToolTipGenerator(g);
        XYPlot xyplot = chart.getXYPlot();
        DateAxis dateAxis = (DateAxis)xyplot.getDomainAxis();
        dateAxis.setTickLabelsVisible(false);
        NumberAxis numberAxis = (NumberAxis) xyplot.getRangeAxis();
        numberAxis.setNumberFormatOverride(new DecimalFormat("#"));
        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesPaint(2, Color.BLUE);
        renderer.setSeriesPaint(3, Color.YELLOW);
        renderer.setSeriesPaint(4, Color.PINK);
        renderer.setSeriesPaint(5, Color.GREEN);
        renderer.setSeriesPaint(6, Color.ORANGE);
        for(int i=0; i<7; i++)
            renderer.setSeriesStroke(i, new BasicStroke(5));
        return chart;
    }

    protected FastValueGridPortrayal2D countriesPortrayal = new FastValueGridPortrayal2D("World");

    public ImperialWithUI() { super(new Imperial(System.currentTimeMillis())); }
    public ImperialWithUI(SimState state) { super(state); }

    public static void main(String[] args){

      /*  SecurityManager security = System.getSecurityManager();
        if (security == null) {
            System.setSecurityManager(new java.security.manager());
        }*/

        ImperialWithUI riskGUI = new ImperialWithUI();
        Console c = new Console(riskGUI);
        c.setVisible(true);
    }

    public void setupPortrayals(){
        final Imperial risk = (Imperial)state;
        E0.clear();
        E1.clear();
        E2.clear();
        E3.clear();
        E4.clear();
        E5.clear();
        E6.clear();
        period = new Day();

        Steppable timeSeriesUpdater = new Steppable() {
            public void step(SimState state) {
                double size0 = 0;
                double size1 = 0;
                double size2 = 0;
                double size3 = 0;
                double size4 = 0;
                double size5 = 0;
                double size6 = 0;

                for(int i=0; i<risk.territories.numObjs; i++){
                    Territory territory = (Territory)risk.territories.get(i);
                    int type = territory.getType();
                    if(type==1 && territory.getSuperior()==null)
                        size0++;
                    if(type==2 && territory.getSuperior()==null)
                        size1++;
                    if(type==3 && territory.getSuperior()==null)
                        size2++;
                    if(type==4 && territory.getSuperior()==null)
                        size3++;
                    if(type==5 && territory.getSuperior()==null)
                        size4++;
                    if(type==6 && territory.getSuperior()==null)
                        size5++;
                    if(type==7 && territory.getSuperior()==null)
                        size6++;
                }

                for(int i=0; i<risk.territories.numObjs; i++){
                    Territory territory = (Territory)risk.territories.get(i);
                    int type = territory.getType();
                    
                    switch(type){
                        case 1:size0 += territory.getConquered().numObjs;
                            break;
                        case 2:size1 += territory.getConquered().numObjs;
                            break;
                        case 3:size2 += territory.getConquered().numObjs;
                            break;
                        case 4:size3 += territory.getConquered().numObjs;
                            break;
                        case 5:size4 += territory.getConquered().numObjs;
                            break;
                        case 6:size5 += territory.getConquered().numObjs;
                            break;
                        case 7:size6 += territory.getConquered().numObjs;
                            break;
                    }
                }
                E0.add(period, size0);
                E1.add(period, size1);
                E2.add(period, size2);
                E3.add(period, size3);
                E4.add(period, size4);
                E5.add(period, size5);
                E6.add(period, size6);
                period = (Day)period.next();
            }
        };
        risk.schedule.scheduleRepeating(timeSeriesUpdater);

        Steppable barsUpdater = new Steppable() {
            public void step(SimState state) {
                double[] types = new double[7];
                for(int i=0; i<types.length; i++){
                    types[i]=6;
                }
                for(int i=0; i<risk.territories.numObjs; i++){
                    Territory territory = (Territory)risk.territories.get(i);
                    types[territory.getType()-1] += territory.getConquered().numObjs;
                    if(territory.getSuperior()!=null){
                        types[territory.getType()-1] --;
                    }
                }
                for(int i=0; i<risk.territories.numObjs; i++){
                    Territory territory = (Territory)risk.territories.get(i);
                    String name = territory.getRuler().getName();
                    territoriesBars.addValue(types[territory.getType()-1], "Size", name);
                }
                double[] records = new double[territoriesBars.getColumnCount()];
                for(int i=0; i<records.length; i++){
                    records[i] = (Double)territoriesBars.getValue(0, i);
                }
                dataWriter.writeTerritories(records);
            }
        };
        risk.schedule.scheduleRepeating(barsUpdater);

        Steppable graphUpdater = new Steppable() {
            public void step(SimState state) {
                if(graphFrame.isVisible()){
                    graph.updateTree();
                }
            }
        };
        risk.schedule.scheduleRepeating(graphUpdater);

        Steppable influencesUpdater = new Steppable() {
            public void step(SimState state) {
                double[] types = new double[7];
                for(int i=0; i<risk.territories.numObjs; i++){
                    Territory territory = (Territory)risk.territories.get(i);
                    if(territory.countSameSuperiors(territory.getType())==0){
                        types[(int)territory.getType()-1] +=
                                territory.countDiffSubordinates(territory.getType());
                    }
                }

                for(int i=0; i<risk.territories.numObjs; i++){
                    Territory territory = (Territory)risk.territories.get(i);
                    String name = territory.getRuler().getName();
                    influenceBars.addValue(types[territory.getType()-1], "Influence", name);

                }
                double[] records = new double[influenceBars.getColumnCount()];
                for(int i=0; i<records.length; i++){
                    records[i] = (Double)influenceBars.getValue(0, i);
                }
                dataWriter.writeInfluence(records);
            }
        };
        risk.schedule.scheduleRepeating(influencesUpdater);

        Steppable wealthUpdater = new Steppable() {
            public void step(SimState state) {
                double[] types = new double[7];
                for(int i=0; i<risk.territories.numObjs; i++){
                    Territory territory = (Territory)risk.territories.get(i);
                    types[territory.getType()-1] += (territory.getSoldiers()+
                            territory.getNatRes()+territory.getPeasants());

                }
                for(int i=0; i<risk.territories.numObjs; i++){
                    Territory territory = (Territory)risk.territories.get(i);
                    String name = territory.getRuler().getName();
                    wealthBars.addValue(types[territory.getType()-1], "Wealth", name);
                }
                double[] records = new double[wealthBars.getColumnCount()];
                for(int i=0; i<records.length; i++){
                    records[i] = (Double)wealthBars.getValue(0, i);
                }
                dataWriter.writeWealth(records);
            }
        };
        risk.schedule.scheduleRepeating(wealthUpdater);

        countriesPortrayal.setField(risk.countriesGrid);
        countriesPortrayal.setMap(new sim.util.gui.SimpleColorMap(colorsMap));
        display.reset();
        display.repaint();
    }

    @Override
    public void init(Controller c){
        super.init(c);
        //Territorial TimeSeries
        timeSeriesData = createSeriesDataset();
        timeSeriesChart = createChart(timeSeriesData);
        frameForTimeSeries = new ChartFrame("Sizes of Empires Time Series",timeSeriesChart);
        frameForTimeSeries.setSize(500, 300);
        frameForTimeSeries.setLocation(501, 651);
        frameForTimeSeries.setVisible(false);
        c.registerFrame(frameForTimeSeries);


        //Extension GUI
        territoriesBars = (DefaultCategoryDataset) createDataset("Size");
        territoriesChart = ChartFactory.createBarChart
           ("", "Empire's Name", "Territories under direct control", territoriesBars, PlotOrientation.VERTICAL, false, false, false);

        final CategoryPlot plot = territoriesChart.getCategoryPlot();
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        CategoryAxis xAxis = (CategoryAxis)plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        final CategoryItemRenderer renderer = new CustomRenderer(colorsBars);
        plot.setRenderer(renderer);

        frameForTerritories = new ChartFrame("Size of Empires",territoriesChart);
        frameForTerritories.setSize(500, 300);
        frameForTerritories.setLocation(0, 350);
        frameForTerritories.setVisible(true);
        c.registerFrame(frameForTerritories);

        //Influence GUI
        influenceBars = (DefaultCategoryDataset) createDataset("Influence");
        influenceChart = ChartFactory.createBarChart
           ("", "Empire's Name", "Kingdoms under influence", influenceBars, PlotOrientation.VERTICAL, false, false, false);

        final CategoryPlot plot2 = influenceChart.getCategoryPlot();
        final NumberAxis rangeAxis2 = (NumberAxis) plot2.getRangeAxis();
        rangeAxis2.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        CategoryAxis xAxis2 = (CategoryAxis)plot2.getDomainAxis();
        xAxis2.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        final CategoryItemRenderer renderer2 = new CustomRenderer(colorsBars);
        plot2.setRenderer(renderer2);

        frameForInfluences = new ChartFrame("Influence of Empires",influenceChart);
        frameForInfluences.setSize(500, 300);
        frameForInfluences.setLocation(501, 350);
        frameForInfluences.setVisible(true);
        c.registerFrame(frameForInfluences);

        //Population GUI
        wealthBars = (DefaultCategoryDataset) createDataset("Wealth");
        peopleChart = ChartFactory.createBarChart
           ("", "Empire's Name", "Wealth", wealthBars, PlotOrientation.VERTICAL, false, false, false);

        final CategoryPlot plot3 = peopleChart.getCategoryPlot();
        final NumberAxis rangeAxis3 = (NumberAxis) plot3.getRangeAxis();
        rangeAxis3.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        CategoryAxis xAxis3 = (CategoryAxis)plot3.getDomainAxis();
        xAxis3.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        final CategoryItemRenderer renderer3 = new CustomRenderer(colorsBars);
        plot3.setRenderer(renderer3);

        frameForPeople = new ChartFrame("Empires' Wealth",peopleChart);
        frameForPeople.setSize(500, 300);
        frameForPeople.setLocation(1002, 350);
        frameForPeople.setVisible(true);
        c.registerFrame(frameForPeople);

        graphFrame = new JFrame("Hierarchies Graph");
        Container content = graphFrame.getContentPane();
        content.add(graph);
        graphFrame.pack();
        graphFrame.setLocation(620, 0);
        graphFrame.setVisible(true);
        c.registerFrame(graphFrame);

        //Map GUI
        display = new Display2D(300*2, 125*2, this,1);
        displayFrame = display.createFrame();
        displayFrame.setTitle("Map");
        display.attach(countriesPortrayal, "");
        displayFrame.setVisible(true);
        display.setBackdrop(Color.WHITE);
        c.registerFrame(displayFrame);
    }

    @Override
    public void start(){
        super.start();
        setupPortrayals();
    }

    @Override
    public void quit(){
        super.quit();
    }

    class CustomRenderer extends BarRenderer {
        private Paint[] colors;

        public CustomRenderer(final Paint[] colors) {
            this.colors = colors;
        }

        @Override
        public Paint getItemPaint(final int row, final int column) {
            return this.colors[column % this.colors.length];
        }
    }

}
