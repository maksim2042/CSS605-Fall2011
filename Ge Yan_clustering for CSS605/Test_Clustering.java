package test_modular;

import java.io.IOException;
import java.util.ArrayList;

import force.Clustering;
import force.Clustering;
import force.Clustering.Cluster;
import force.Clustering.ClusteringStatistic;
import force.Clustering.GroupingRequirements;
import force.Clustering.RawData;

public class Test_Clustering {
	

	
	public static void main(String[] args) throws IOException, InterruptedException {
		ArrayList<Double> xs=new ArrayList<>();
		ArrayList<Double> ys=new ArrayList<>();
		
		for (int i = 0; i < 100; i++) {
			xs.add(Math.random()*10);
			ys.add(Math.random()*10);
		}
		
		

		

		
		RawData rd=new Clustering().new RawData(xs, ys, "trial");
		rd.reportRawData();
		ClusteringStatistic cs=new Clustering().new ClusteringStatistic();
		
		
		
		
		Clustering aClustering=new Clustering();
		GroupingRequirements gr=aClustering.new GroupingRequirements("oneTrial", 0.65, 15);
		Cluster aCluster=aClustering.new Cluster(gr, rd, cs);
		aCluster.doClustering();
		aCluster.reportClustering();
		aCluster.coveredPoints();
	}
	
}
