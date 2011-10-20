package force;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import force.Analysis.GeneralAnalysis.OccurrenceCounting;
import force.Help.ArraylistSort;
import force.Help.Report;

public class Clustering {
	class ProposedGroups {
		public Point2D getGroupCenter() {
			return groupCenter;
		}

		public void setGroupCenter(Point2D groupCenter) {
			this.groupCenter = groupCenter;
		}

		public void setaGroup(ArrayList<Point2D> aGroup) {
			this.aGroup = aGroup;
		}

		public ArrayList<Point2D> getaGroup() {
			return aGroup;
		}

		String groupName;
		double aggregateEntrope;
		ArrayList<Point2D> aGroup = new ArrayList<>();
		Point2D groupCenter;
		ArrayList<Double> aGroupStaistic = new ArrayList<>();
	}

	public class GroupingRequirements {
		public double getTotalCoverage() {
			return totalCoverage;
		}

		String groupingName;
		double totalCoverage;
		double eachGroupMemberNumber;

		public double getEachGroupMemberNumber() {
			return eachGroupMemberNumber;
		}

		public GroupingRequirements(String groupingName, double totalCoverage,
		int eachGroupMemberNumber) {
			super();
			this.groupingName = groupingName;
			this.totalCoverage = totalCoverage;
			this.eachGroupMemberNumber = eachGroupMemberNumber;
		}

		public String getGroupingName() {
			return groupingName;
		}

		public void setGroupingName(String groupingName) {
			this.groupingName = groupingName;
		}
	}

	public class RawData {
		ArrayList<Point2D> rawData = new ArrayList<>();
		String rawDataName;

		public RawData(ArrayList<Double> xCoordinate,
		ArrayList<Double> yCoordinate, String rawDataName) {
			this.rawDataName = rawDataName;
			ArrayList<Point2D> p2d = new ArrayList<>();
			for (int i = 0; i < xCoordinate.size(); i++) {
				Point2D aPoint = new Point2D.Double(xCoordinate.get(i),
				yCoordinate.get(i));
				p2d.add(aPoint);
			}
			rawData = p2d;
		}

		public ArrayList<Point2D> getRawData() {
			return rawData;
		}

		public void setRawData(ArrayList<Point2D> rawData) {
			this.rawData = rawData;
		}

		public String getRawDataName() {
			return rawDataName;
		}

		public void setRawDataName(String rawDataName) {
			this.rawDataName = rawDataName;
		}

		public void reportRawData() throws IOException {
			Report r = new Help().new Report("raw data.txt");
			r.report("all 2d points, first line for X, second line for Y\n X:	");

			for (int i = 0; i < rawData.size(); i++) {
				r.reportHorizontal(rawData.get(i).getX());
			}
			r.report("\n Y:		");
			for (int i = 0; i < rawData.size(); i++) {
				r.reportHorizontal(rawData.get(i).getY());
			}
		}
	}

	public class Cluster {
		boolean isExpanded = false;
		double currentCoverage;
		ArraylistSort groupSort = new Help().new ArraylistSort();
		GroupingRequirements clusterGroupingRequirements;
		RawData clusterRawData;
		ClusteringStatistic clusterClusteringStatistic;

		ArrayList<ProposedGroups> groupList = new ArrayList<>();
		ArrayList<ProposedGroups> tempGroupList = new ArrayList<>();
		ArrayList<Point2D> coveredPoints = new ArrayList<>();

		ArrayList<Double> alpgTotalEntrope = new ArrayList<>();

		public Cluster(GroupingRequirements gr, RawData rd,
		ClusteringStatistic cs) {
			clusterClusteringStatistic = cs;
			clusterGroupingRequirements = gr;
			clusterRawData = rd;
		}

		public void doClustering() throws IOException, InterruptedException {
			clusterClusteringStatistic.calculateStatistic(clusterRawData);
			clusterClusteringStatistic.reportClusteringStatistic();
			clusterClusteringStatistic.reportHowSorted();

			propseGroup_Fisrt();
			if (ifSatisfied()) {
				System.out.println("the first step works.");
				Thread.sleep(5111);
				return;
				} else {
				isExpanded = true;
				expandTheGroup();
				currentCoverage = (double) coveredPoints.size()
				/ (double) clusterRawData.getRawData().size();
				System.out.println("the second step works.");
				Thread.sleep(5111);
			}
		}

		public void reportClustering() throws IOException {
			Report r = new Help().new Report("clustering results.txt");
			r.report("\n\n*******************\nclustering results are here:");
			r.report("required minimum group member:,"
			+ clusterGroupingRequirements.getEachGroupMemberNumber()
			+ "\nrequired coverage percentage:,"
			+ clusterGroupingRequirements.getTotalCoverage()
			+ "\ntotal number of data:,"
			+ clusterRawData.getRawData().size()
			+ "\ncurrent coverage:," + currentCoverage);
			if (isExpanded == true) {
				r.report("clustering status:,Expanded clustering");
				} else {
				r.report("clustering status:,Non-expanded clustering");
			}
			r.report("***************************\n");

			for (int i = 0; i < groupList.size(); i++) {
				r.report("Group number:," + i);
				for (Point2D aPoinInGroup : groupList.get(i).getaGroup()) {
					r.report("x:," + aPoinInGroup.getX() + " ,Y:,"
					+ aPoinInGroup.getY());
				}
			}
		}

		void expandTheGroup() {
			for (ProposedGroups aPG : tempGroupList) {
				for (int i = 0; i < aPG.getaGroup().size(); i++) {
					for (ProposedGroups aDeterminedPG : groupList) {
						if (aDeterminedPG.getaGroup().contains(
						aPG.getaGroup().get(i))) {
							for (Point2D aP : aPG.getaGroup()) {
								if (coveredPoints.contains(aP) == false) {
									aDeterminedPG.getaGroup().add(aP);
									coveredPoints.add(aP);
								}
							}
						}
					}
				}
			}
		}

		public int coveredPoints() {
			int a = 0;

			for (ProposedGroups p : groupList) {
				for (Point2D a2Dpoint : p.getaGroup()) {
					a++;
				}
			}
			System.out.println("total points covered:" + a);
			return a;
		}

		private boolean ifSatisfied() throws InterruptedException {
			currentCoverage = (double) coveredPoints.size()
			/ (double) clusterRawData.getRawData().size();
			System.out.println("thrashhold:"
			+ clusterGroupingRequirements.totalCoverage);
			System.out.println("actual percentage:" + currentCoverage);
			Thread.sleep(2000);

			if (currentCoverage >= clusterGroupingRequirements.totalCoverage) {
				return true;

				} else {
				return false;
			}
		}

		void propseGroup_Fisrt() {
			for (int i = 0; i < clusterClusteringStatistic
			.getClustringStatistic().size(); i++) {
				double totalEntrope = 0;
				for (int j = 0; j < clusterGroupingRequirements
				.getEachGroupMemberNumber(); j++) {
					totalEntrope = totalEntrope
					+ clusterClusteringStatistic
					.getClustringStatistic().get(i).get(j);
				}
				alpgTotalEntrope.add(totalEntrope);
			}

			groupSort.sortArraylistMembers(alpgTotalEntrope);
			groupSort.howSorted();

			for (int i = 0; i < groupSort.getHowSorted().size(); i++) {
				ArrayList<Double> original = this.clusterClusteringStatistic
				.getClustringStatistic().get(
				groupSort.getHowSorted().get(i));

				ArrayList<Point2D> tempP2D = new ArrayList<>();
				int count = 0;

				for (int j = 0; j < clusterGroupingRequirements
				.getEachGroupMemberNumber(); j++) {
					Point2D aPoint = clusterRawData.getRawData().get(
					clusterClusteringStatistic.howSorted.get(
					groupSort.getHowSorted().get(i)).get(j));

					if (coveredPoints.contains(aPoint) == false) {
						tempP2D.add(aPoint);
						count++;
					}
				}

				ProposedGroups pg;

				Point2D aGoupCenter = clusterRawData.getRawData().get(
				groupSort.getHowSorted().get(i));
				pg = new ProposedGroups();
				pg.setGroupCenter(aGoupCenter);
				for (Point2D point2d : tempP2D) {
					pg.getaGroup().add(point2d);
				}

				if (count == clusterGroupingRequirements
				.getEachGroupMemberNumber()) {
					for (Point2D onePoint2d : pg.getaGroup()) {
						coveredPoints.add(onePoint2d);
					}
					groupList.add(pg);
					} else {
					tempGroupList.add(pg);
				}
			}
		}
	}

	public class ClusteringStatistic {
		RawData rawData;
		ArraylistSort als = null;
		String clustringStatisticName;
		ArrayList<ArrayList<Double>> clustringStatistic = new ArrayList<>();
		ArrayList<ArrayList<Integer>> howSorted = new ArrayList<>();

		public void reportClusteringStatistic() throws IOException {
			Report r = new Help().new Report(
			"reportSortedClusteringStatistic.txt");
			r.reportD2(clustringStatistic);
		}

		public void reportHowSorted() throws IOException {
			Report r = new Help().new Report(
			"reportClustringStatisticHowSorted.txt");
			r.reportI2(howSorted);
		}

		public void calculateStatistic(RawData rd) throws IOException {
			rawData = rd;

			for (int i = 0; i < rd.getRawData().size(); i++) {
				Point2D center = rd.getRawData().get(i);
				ArrayList<Double> distances = new ArrayList<>();
				for (int j = 0; j < rd.getRawData().size(); j++) {
					Point2D target = rd.getRawData().get(j);

					double distance = Math.sqrt(Math.pow(
					target.getY() - center.getY(), 2)
					+ Math.pow(target.getX() - center.getX(), 2));
					distances.add(distance);
				}
				als = new Help().new ArraylistSort();
				als.sortArraylistMembers(distances);
				als.howSorted();
				als.reportSortedlist();
				clustringStatistic.add(als.getSortedArraylist());
				howSorted.add(als.getHowSorted());
			}
		}

		public String getClustringStatisticName() {
			return clustringStatisticName;
		}

		public void setClustringStatisticName(String clustringStatisticName) {
			this.clustringStatisticName = clustringStatisticName;
		}

		public ArrayList<ArrayList<Double>> getClustringStatistic() {
			return clustringStatistic;
		}

		public void setClustringStatistic(
		ArrayList<ArrayList<Double>> clustringStatistic) {
			this.clustringStatistic = clustringStatistic;
		}
	}
}
