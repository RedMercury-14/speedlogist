package by.base.main.service.util;

import java.util.ArrayList;
import java.util.List;


public class TEST {

	public static void main(String[] args) {
		List<List<Integer>> test = split(6);
		test.forEach(q-> {
			q.forEach(w-> System.out.print(w.toString()));
			System.out.println();
		});

	}
		public static List<List<Integer>> split(int n) {
			List<Integer> temp = new ArrayList<>();
			List<List<Integer>> result = new ArrayList<List<Integer>>();
			for (int i = 0; i < n; ++i) {
				temp.add(1);
			}
			while (temp.get(0) != n) {
//				System.out.println(temp);
				List <Integer> temp2 = new ArrayList<Integer>(temp);
				result.add(temp2);
				int min = temp.get(0);
				int minIndex = 0;
				int sum = temp.get(0);
				int tempSum = temp.get(0);
				for (int j = 1; j < temp.size() - 1; ++j) {
					tempSum += temp.get(j);
					if (min > temp.get(j)) {
						min = temp.get(j);
						minIndex = j;
						sum = tempSum;
					}
				}
				temp.set(minIndex, temp.get(minIndex) + 1);
				sum += 1;
				temp.subList(minIndex + 1, temp.size()).clear();
				for (int k = 0; k < n - sum; ++k) {
					temp.add(1);
				}
			}
			return result;
		}


}
