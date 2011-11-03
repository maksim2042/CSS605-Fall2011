package risk;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import java.awt.Paint;
import java.util.ArrayList;
import org.apache.commons.collections15.Transformer;



/**
 * 
 * @author Omar Guerrero
 *
 */
@SuppressWarnings("serial")
public class HierarchiesGraph extends JApplet {

    private Imperial risk;
    protected Forest<Territory,Integer> graph;

    Factory<DirectedGraph<Territory,Integer>> graphFactory =
    	new Factory<DirectedGraph<Territory,Integer>>() {
            public DirectedGraph<Territory, Integer> create() {
                    return new DirectedSparseMultigraph<Territory,Integer>();
            }
        };

    Factory<Tree<Territory,Integer>> treeFactory =
        new Factory<Tree<Territory,Integer>> () {
        public Tree<Territory, Integer> create() {
                return new DelegateTree<Territory,Integer>(graphFactory);
        }
    };

    Factory<Integer> edgeFactory = new Factory<Integer>() {
        int i=0;
        public Integer create() {
                return i++;
        }
    };

    Factory<Territory> vertexFactory = new Factory<Territory>() {
        int i=0;
        public Territory create() {
                return new Territory();
        }
    };

    Transformer<Territory,Paint> vertexPaint = new Transformer<Territory,Paint>() {
        public Paint transform(Territory territory) {
            return territory.getRuler().rulerColor;
        }
    };

    public Paint getDrawPaint(Territory territory) {
        return territory.getRuler().rulerColor;
    }



    /**
     * the visual component and renderer for the graph
     */
    protected VisualizationViewer<Territory,Integer> vv;
    VisualizationServer.Paintable rings;
    Territory root;
    TreeLayout<Territory,Integer> treeLayout;
    RadialTreeLayout<Territory,Integer> radialLayout;

    public HierarchiesGraph(Imperial risk) {
        this.risk = risk;
        
        // create a simple graph for the demo
        graph = new DelegateForest<Territory,Integer>();


        treeLayout = new TreeLayout(graph);
        vv =  new VisualizationViewer(treeLayout, new Dimension(900,200));
        vv.setBackground(Color.white);
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.setVertexToolTipTransformer(new ToStringLabeller());
        vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        vv.getRenderContext().setVertexDrawPaintTransformer(vertexPaint);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));

        Container content = getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);

        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

        vv.setGraphMouse(graphMouse);

        JComboBox modeBox = graphMouse.getModeComboBox();
        modeBox.addItemListener(graphMouse.getModeListener());
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1/1.1f, vv.getCenter());
            }
        });

        JPanel scaleGrid = new JPanel(new GridLayout(1,0));
        scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));
        JPanel controls = new JPanel();
        scaleGrid.add(plus);
        scaleGrid.add(minus);
        controls.add(scaleGrid);
        content.add(controls, BorderLayout.SOUTH);

    }

    protected void updateTree() {

        Iterable<Integer> toRemoveEdge = new ArrayList<Integer>(graph.getEdges());
        for(Integer e:toRemoveEdge){
            graph.removeEdge(e);
        }

        Iterable<Territory> toRemoveVertex = new ArrayList<Territory>(graph.getVertices());
        for(Territory v:toRemoveVertex){
            graph.removeVertex(v);
        }

        for (int i=0; i<risk.territories.numObjs; i++){
            Territory territory = (Territory)risk.territories.get(i);
            if(territory.getSuperior()==null){
                graph.addVertex(territory);
                buildBranches(territory);
            }
        }
        
        treeLayout = new TreeLayout(graph);
        LayoutTransition<Territory,Integer> lt = new LayoutTransition(vv, treeLayout, radialLayout);
        vv.repaint();
    }

    private void buildBranches(Territory territory){
        for(int i=0; i<territory.getConquered().numObjs; i++){
            Territory subordinate = (Territory)territory.getConquered().get(i);
            graph.addEdge(edgeFactory.create(), territory, subordinate);
            buildBranches(subordinate);
        }
    }

}
