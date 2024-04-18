package by.base.main.util.hcolossus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.model.Shop;
import by.base.main.util.hcolossus.pojo.VehicleWay;

/**
 * Класс реализующий методы и принципы проверки на логичность маршрутов
 */
@Service
public class LogicAnalyzer {
	
	@Autowired
	private MatrixMachine matrixMachine;	
	
	private static Double percentOverWay; //процент перепробега
	private static Double percentGoodOverWay = 0.0; //процент допустимиго перепробега, где маршрут считается линейным
	private static Double penaltyForClusterCheck = 1.6;
	private static Double radiusSearch = 40000.0; // радиус поиска
	
	/**
	 * Основной метод проверки на логичность
	 * <br>Если число положительное, то маршрут логичен и проверка пройдена
	 * <br>Если число отрицательное, то маршрут не логичен и число показывает кол-во перебробега (в метрах)
	 * @param vehicleWay
	 * @return
	 */
	public Double logicalСheck(VehicleWay vehicleWay, Double koeff) {
		this.percentOverWay = (koeff-1)*100;
		List<Shop> correctRoute =  correctRouteMaker(vehicleWay.getWay());
		//определяем цепочку расстояний
		List<Double> distanceСhain = new ArrayList<Double>(); // цепочка расстояний 
		for (int i = 0; i < correctRoute.size(); i++) {
			if(i == correctRoute.size()-1) {
				break;
			}
			Shop from = correctRoute.get(i);
			Shop to = correctRoute.get(i+1);
			String keyForMatrix = from.getNumshop()+"-"+to.getNumshop();
			Double km = matrixMachine.matrix.get(keyForMatrix);
			distanceСhain.add(km);
		}
		
		//проверка на минские магазины от 1700 склада
		Double maxDistance = 0.0;
		
		for (int i = 1; i < distanceСhain.size()-1; i++) {
			Double double1 = distanceСhain.get(i);
			if(maxDistance < double1) {
				maxDistance = double1;
				
			}
		}
		
		if(correctRoute.get(0).getNumshop() == 1700) {
			if(distanceСhain.get(0) > distanceСhain.get(distanceСhain.size()-1) && maxDistance < 40000) {
				System.err.println("Определён минский магазин!");
				Double oldDIOstance = distanceСhain.remove(0);
				Double newDistance = oldDIOstance-5000.0;
				distanceСhain.add(0, newDistance);
			}
		}		
		// конец проврки на минские магазины от 1700 склада
		
		Double logicDistanceTo = 0.0;
		Double logicDistanceReturn;
		
//		System.out.println("LogicAnalyzer.logicalСheck: Цепочка расстояний и магазинов");
//		correctRoute.forEach(s-> System.out.print(s.getNumshop()+"->"));
//		System.out.println();
//		distanceСhain.forEach(s-> System.out.print(s+"->"));
//		System.out.println();
		
		for (int i = 0; i < distanceСhain.size()-1; i++) {
			Double targetKm = distanceСhain.get(i);
			logicDistanceTo = logicDistanceTo + targetKm;
		}
		

		
		logicDistanceReturn = distanceСhain.get(distanceСhain.size()-1) * (1.0+(percentOverWay/100));
		Double distanceGoodOverWay = distanceСhain.get(distanceСhain.size()-1) * (1.0+(percentGoodOverWay/100)) - distanceСhain.get(distanceСhain.size()-1);
		Double result = logicDistanceReturn-logicDistanceTo; //сам перепробег
		Double resultHasGood = logicDistanceTo-distanceСhain.get(distanceСhain.size()-1); //сам перепробег
		
		if(result>0) {
//			System.out.println(resultHasGood + " <= " + distanceGoodOverWay);
			if(resultHasGood<0 || resultHasGood<=distanceGoodOverWay) { // если перепробег незначителе, то мы не проверяем на кластерность
//				System.err.println(" входит в минимальную погрешность %");
				return result;	
			}else {
				//проект тестовой проверки на кластерность
				//берем самое большое расстояние от магазина к магазину и сравниваем его с расстоянием до склада деленное на 2
				//если оно больше этого значение - умножаем на коэф. 1,35 и проверяем опять на логичность
				
				System.err.print("LogicAnalyzer.logicalСheck: Задействована проверка на кластерность: ");
				
				Double clusterresult = clusterCheck(correctRoute, distanceСhain, result);
				return clusterresult; 
			}
			
		}else {
			return result;	
		}
		
			
	}
	
	/**
	 * Метод преобразует любой маршрут в правильный, т.е. точки выстраиваются по отдалению от склада, то меньшего к большему расстоянию
	 * <br>Сама сортировка прохордит с помощью помещения расстояний в TreeMap
	 * @param points
	 * @return
	 */
	public List<Shop> correctRouteMaker(List<Shop> points){
		List<Shop> mainList = new ArrayList<Shop>(points);
//		System.out.println("получаем на вход:");
//		mainList.forEach(s-> System.out.println(s));
		
		Shop stock = points.get(0);
		mainList.remove(0);
		mainList.remove(mainList.size()-1);
		
//		System.out.println("после удаления слкада:");
//		mainList.forEach(s-> System.out.println(s));
		
		Map<Double, Shop> map = new TreeMap<Double, Shop>();
		
		for (Shop shop : mainList) {
			String keyForMatrix = stock.getNumshop()+"-"+shop.getNumshop();
			Double km = matrixMachine.matrix.get(keyForMatrix);
			if(km == null) {
				System.err.println("LogicAnalyzer.correctRouteMaker: Расстояние " + keyForMatrix + " не найдено в матрице!");
				//генерим exception
			}else {
				map.put(km, shop);
			}			
		}
		
//		System.out.println("После обработки");
//		map.forEach((k,v) -> System.out.println(k + "  " + v));
		
		List<Shop> result = new ArrayList<Shop>();
		result.add(stock);
		map.forEach((k,v) -> result.add(v));
		result.add(stock);
		
//		System.out.println("реузльтат:");
//		result.forEach(s-> System.out.println(s));
		
		return result;		
	}
	

	/**
	 * проект тестовой проверки на кластерность
	 * берем самое большое расстояние от магазина к магазину и сравниваем его с расстоянием до склада деленное на 2
	 * если оно больше этого значение - умножаем на коэф. 1,35 (?) и проверяем опять на логичность
	 * @param correctRoute - правильный маршрут
	 * @param distanceСhain - расстояние в порядке правильного маршрута
	 * @return
	 */
	private Double clusterCheck (List<Shop> correctRoute, List<Double> distanceСhain, Double result) {
		
		
		Double maxDistance = 0.0;
		int index = 0;
		
		for (int i = 1; i < distanceСhain.size()-1; i++) {
			Double double1 = distanceСhain.get(i);
			if(maxDistance < double1) {
				maxDistance = double1;
				index = distanceСhain.indexOf(double1);
				
			}
		}
		
		String keyForMatrix = correctRoute.get(correctRoute.size()-2).getNumshop() + "-" +correctRoute.get(correctRoute.size()-1).getNumshop();
		Double distanceBack = matrixMachine.matrix.get(keyForMatrix);
		
		List<Double> distanceСhaiNew = new ArrayList<Double>(); // цепочка расстояний 
		
//		System.out.println("\n\n");
//		System.out.println("Максимальное расстояние " + maxDistance);
//		System.out.println("Обратное расстояние " + distanceBack);
//		System.out.println("Тестируемый маршрут");
//		correctRoute.forEach(s-> System.out.print(s.getNumshop()+"->"));
//		System.out.println();
		if(maxDistance > radiusSearch) { // тут косяк  (distanceBack/maxDistance) < 2.0
			
			for (int i = 0; i < correctRoute.size(); i++) {
				if(i == correctRoute.size()-1) {
					break;
				}				
				
				Shop from = correctRoute.get(i);
				Shop to = correctRoute.get(i+1);
				String keyForMatrixFix = from.getNumshop()+"-"+to.getNumshop();
				Double km = matrixMachine.matrix.get(keyForMatrixFix);
				if(i == index) {
//					System.out.println("штрафуем расстояние: " + km);
					km  = km * penaltyForClusterCheck;
//					System.out.println("Получилось: " + km);
				}
				distanceСhaiNew.add(km);
			}
			
			Double logicDistanceTo = 0.0;
			Double logicDistanceReturn;
			
//			System.out.println("Цепочка расстояний при проверке на кластерность");
//			distanceСhaiNew.forEach(s-> System.out.print(s+"->"));
//			System.out.println();
			
			for (int i = 0; i < distanceСhaiNew.size()-1; i++) {
				Double targetKm = distanceСhaiNew.get(i);
				logicDistanceTo = logicDistanceTo + targetKm;
			}
			
			logicDistanceReturn = distanceСhaiNew.get(distanceСhaiNew.size()-1) * (1.0+(percentOverWay/100));
			
			Double resultnew= logicDistanceReturn-logicDistanceTo;
//			System.out.println("\n\n");
//			System.out.println("Максимальное расстояние " + maxDistance);
//			System.out.println("Обратное расстояние " + distanceBack);
//			System.out.println("Тестируемый маршрут");
//			correctRoute.forEach(s-> System.out.print(s.getNumshop()+"->"));
//			System.out.println();
			return resultnew;
		}else {
			return result;
		}		
	}

}
