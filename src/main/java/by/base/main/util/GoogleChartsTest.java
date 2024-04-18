package by.base.main.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GoogleChartsTest {

	public static void main(String[] args) {
		List<List<Integer>> num2 = new ArrayList<List<Integer>>();
		for(int i = 1; i<8; i++) {
			for(int j = 0; j<2; j++) {
				List<Integer> num1 = new ArrayList<Integer>();
				num1.add(1);
				num1.add(50);
				num2.add(num1);
			}
		}
		System.out.println(num2);
		


	}

}
