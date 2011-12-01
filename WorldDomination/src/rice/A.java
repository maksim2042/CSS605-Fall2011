package rice;

import java.io.IOException;
import java.util.ArrayList;

public class A {
	public class OccurrenceCounting {

		public void displayOccurrenceCountingResult() {
			System.out.println("displayOccurrenceCountingResult");
			for (int i = 0; i < headCount.size(); i++) {
				System.out.println("data value:"
						+ this.getData_occurrence().get(i)
						+ "\ndata occurrence:" + this.getHeadCount().get(i));
			}
		}

		// ����ֵ��һ���б����������г��ֵ����ֵ �ڶ����б��Ǹ����ֵ���ֵĴ��� �����б��index number��ͬ
		ArrayList<Double> data_occurrence, headCount;

		public ArrayList<ArrayList<Double>> oneDimesionOccurenceCounting(
				ArrayList<Double> OneDimension) {
			data_occurrence = new ArrayList<Double>();
			for (int i = 0; i < OneDimension.size(); i++) {
				double thisPiece = OneDimension.get(i);
				boolean existPiorOccurrence = false;

				// System.out.println("OneDimension.size():"+OneDimension.size());
				// System.out.println("data_occurrence.size():"+data_occurrence.size());

				for (int j = 0; j < data_occurrence.size(); j++) {
					if (data_occurrence.get(j) == thisPiece) {
						existPiorOccurrence = true;
					}

				}
				if (existPiorOccurrence == false) {
					data_occurrence.add(thisPiece);
				}
				// System.out.println("in first two for functions");
			}

			System.out.println("data_occurrence.size()~:"
					+ data_occurrence.size());

			headCount = new ArrayList<Double>();
			for (int i = 0; i < data_occurrence.size(); i++) {
				headCount.add(new Double(0));

				// System.out.println("in one for functions");
			}

			for (int i = 0; i < OneDimension.size(); i++) {
				double thisPiece = OneDimension.get(i);

				for (int j = 0; j < data_occurrence.size(); j++) {
					// System.out.println(headCount.get(j));
					if (data_occurrence.get(j) == thisPiece) {
						headCount.set(j, headCount.get(j) + 1);
					}

				}

			}

			ArrayList<ArrayList<Double>> results = new ArrayList<ArrayList<Double>>();
			results.add(data_occurrence);
			results.add(headCount);
			return results;

		}

		public void oneDimensionResults() {
			for (int i = 0; i < data_occurrence.size(); i++) {
				System.out.println("value: " + data_occurrence.get(i)
						+ "   number of occurence: " + headCount.get(i)
						+ " at index of:" + i);

			}
		}

		public ArrayList<Double> getData_occurrence() {
			return data_occurrence;
		}

		public void setData_occurrence(ArrayList<Double> dataOccurrence) {
			data_occurrence = dataOccurrence;
		}

		public ArrayList<Double> getHeadCount() {
			return headCount;
		}

		// public void setHeadCount(ArrayList<Double> headCount) {
		// this.headCount = headCount;
		// }
	}

	public class ArraylistSort {

		boolean isSorted = false;
		ArrayList<Double> unsortedArraylist = new ArrayList<Double>();
		ArrayList<Double> sortedArraylist = new ArrayList<Double>();

		// ��¼unsorted��sorted�����ת����,����index=1����Ԫ�ر�ʾoriginal�������indexΪ1��Ԫ������ȥ��sorted����ĵڼ���
		ArrayList<Integer> howSorted = new ArrayList<Integer>();

		// ��һ��arraylist�����Ԫ�ش�С��������
		public void sortArraylistMembers(ArrayList<Double> unsortedAL) {

			unsortedArraylist = unsortedAL;
			sortedArraylist=new ArrayList<Double>(unsortedAL);
		
			for (int j = 0; j < unsortedAL.size(); j++) {
				for (int i = 0; i < unsortedAL.size()-1; i++) {
					if (sortedArraylist.get(i)>sortedArraylist.get(i+1)) {
						double temp=sortedArraylist.get(i);
						sortedArraylist.set(i, sortedArraylist.get(i+1));
						sortedArraylist.set(i+1, temp);
						
					}
			
						
					
				}
				
			}
			
			isSorted = true;
		}
		
	public	void howSorted(){//�˺���Ŀǰ��֧��arraylist�����ظ���ݵ����
			for (int i = 0; i < unsortedArraylist.size(); i++) {
				for (int j = 0; j < unsortedArraylist.size(); j++) {
				if (sortedArraylist.get(i).equals(unsortedArraylist.get(j))
				//		==unsortedArraylist.get(j)
						) {
					howSorted.add(j);
				}
			}
			}
			
		}

		public boolean isSorted() {
			return isSorted;
		}

		public ArrayList<Double> getUnsortedArraylist() {
			return unsortedArraylist;
		}

		public ArrayList<Double> getSortedArraylist() {
			return sortedArraylist;
		}

		public ArrayList<Integer> getHowSorted() {
			return howSorted;
		}

		

	}
	}
