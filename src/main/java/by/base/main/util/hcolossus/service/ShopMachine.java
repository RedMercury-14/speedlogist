package by.base.main.util.hcolossus.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import by.base.main.model.Shop;
import by.base.main.service.ShopService;
import by.base.main.util.hcolossus.exceptions.FatalInsufficientPalletTruckCapacityException;
import scala.annotation.bridge;

/**
 * Класс подготовки и обработки магазинов
 */
@Service
public class ShopMachine {
	
	@Autowired
	private MatrixMachine matrixMachine;
	
//	private Comparator<Shop> shopComparator = (o1, o2) -> (o2.getNeedPall() - o1.getNeedPall()); //сортирует от большей потребности к меньшей
	private Comparator<Shop> shopComparator = (o1, o2) -> Double.compare(o2.getNeedPall(), o1.getNeedPall()); // сортирует от большей потребности к меньшей. Переделал с прошлого (выше)

	
	public Map<Integer, Shop> getShopMap(List<Shop> shopList) {
		Map<Integer, Shop> allShop = new HashMap<Integer, Shop>();
		shopList.forEach(s-> allShop.put(s.getNumshop(), s));
		return allShop;
	}
	
	/**
	 * Метод подготавливает лист магазинов уже с потребностями
	 * <br>Метод сортирует потребность магазов от большего к меньшему
	 * @return
	 */
	public List<Shop> prepareShopList(List<Shop> shopList, List<Double> pallHasShops) {
		Map<Integer, Shop> allShop =  getShopMap(shopList);
		
		List<Shop> result = new ArrayList<Shop>();
		for (Shop shop : shopList) {
			int index = shopList.indexOf(shop.getIdShop());
			shop.setNeedPall(pallHasShops.get(index));
			result.add(shop);
		}
		result.sort(shopComparator);
		return result;
		
	}
	
	/**
	 * Метод подготавливает лист магазинов уже с потребностями
	 * <br>Метод сортирует потребность магазов от большего к меньшему и <b>от дальшего к ближнему <b>
	 * <br> т.е. самый дальний и самый требовательный магаз
	 * <br> Добавлена потребность магазина в килограммах
	 * @return
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public List<Shop> prepareShopList3Parameters(List<Integer> shopList, List<Double> pallHasShops, List<Integer> tonnageHasShops,  Integer stock,  Map<Integer, String> shopsWithCrossDockingMap, List<Shop> shops) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<Integer, Shop> allShop =  getShopMap(shops);
		ComparatorShops comparatorShops = new ComparatorShops();
		List<Shop> result = new ArrayList<Shop>();
		for (int i = 0; i < shopList.size(); i++) {
			Integer integer = shopList.get(i);
			Shop shop = (Shop) SerializationUtils.clone(allShop.get(integer));
			if(shop == null) {
				System.err.println("ShopMachine.prepareShopList3Parameters: Магазин " + integer + " не найден в базе данных!");
				return null;
			}
			shop.setNeedPall(pallHasShops.get(i));
			shop.setWeight(tonnageHasShops.get(i));
			if(shopsWithCrossDockingMap.get(integer) != null) {
				shop.setKrossPolugonName(shopsWithCrossDockingMap.get(integer));
			}
//			shop.createIdShop();
			result.add(shop);
		}
//		for (Integer integer : shopList) {
//			int index = shopList.indexOf(integer);
//			Shop shop = allShop.get(integer);
//			if(shop == null) {
//				System.err.println("ShopMachine.prepareShopList2Parameters: Магазин " + integer + " не найден в базе данных!");
//				return null;
//			}
//			shop.setNeedPall(pallHasShops.get(index));
//			shop.setWeight(tonnageHasShops.get(index));
//			shop.createIdShop();
//			result.add(shop);
//		}
		//задаём расстояния от склада до магазина, для последующей сортировки
		List<Shop> finalResult = new ArrayList<Shop>();
		for (Shop shop : result) {
			String keyForMatrix = stock+"-"+shop.getNumshop();
			Double distanceTarget = matrixMachine.matrix.get(keyForMatrix);
			if(distanceTarget == null) {
				System.err.println("ShopMachine.prepareShopList2Parameters: Магазин " + shop.getNumshop() + " не найдено расстояние от склада");
			}else {
				shop.setDistanceFromStock(distanceTarget);
				finalResult.add(shop);
			}
			
		}
		finalResult.sort(comparatorShops);
		return finalResult;
		
	}
	
	/**
	 * Метод подготавливает лист магазинов уже с потребностями
	 * <br>Метод сортирует потребность магазов от большего к меньшему и <b>от дальшего к ближнему <b>
	 * <br> т.е. самый дальний и самый требовательный магаз
	 * <br> Добавлена потребность магазина в килограммах
	 * <br> Добавлен парсинг возврещаемых паллет
	 * <br> Добавлен парсинг поля расчёта магазина от веса
	 * <br> <b>ComparatorShopsDistanceMain</b>
	 * @return
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public List<Shop> prepareShopList5Parameters(List<Integer> shopList, List<Double> pallHasShops, List<Integer> tonnageHasShops,  Integer stock,  Map<Integer, String> shopsWithCrossDockingMap, List<Double> pallReturn
			, List<Integer> weightDistributionList, List<Shop> shops) throws JsonMappingException, JsonProcessingException {
		Map<Integer, Shop> allShop =  getShopMap(shops);
		ComparatorShopsDistanceMain shopComparatorDistanceMain = new ComparatorShopsDistanceMain();
		List<Shop> result = new ArrayList<Shop>();
		for (int i = 0; i < shopList.size(); i++) {
			Integer integer = shopList.get(i);
			if(integer == stock) break;
			Shop shop = (Shop) SerializationUtils.clone(allShop.get(integer));
			if(shop == null) {
				System.err.println("ShopMachine.prepareShopList5Parameters: Магазин " + integer + " не найден в базе данных!");
				return null;
			}
			shop.setNeedPall(pallHasShops.get(i));
			shop.setWeight(tonnageHasShops.get(i));
			shop.setPallReturn(pallReturn.get(i));
			//тут проверяем, если паллеты которые нужно забрать больше ограничений - то выдаём ошибку!
			if(shop.getPallReturn()!= null && shop.getMaxPall() != null && shop.getPallReturn() > shop.getMaxPall()) {
				throw new FatalInsufficientPalletTruckCapacityException("Ограничение по магазину " + shop.getNumshop() + ": " + shop.getMaxPall() + " паллет. "
						+ " Нужно забрать: " + shop.getPallReturn() + " паллет. Невозможно забрать одной машиной");
			}
			if(shopsWithCrossDockingMap.get(integer) != null) {
				shop.setKrossPolugonName(shopsWithCrossDockingMap.get(integer));
			}
			if(weightDistributionList.contains(shop.getNumshop())) {//добавляем запись о том что магазин входит в полигон со спец. расчётами
				shop.setSpecialWeightDistribution(true);
			}else {
				shop.setSpecialWeightDistribution(false);
			}
			result.add(shop);
		}
		//задаём расстояния от склада до магазина, для последующей сортировки
		List<Shop> finalResult = new ArrayList<Shop>();
		for (Shop shop : result) {
			String keyForMatrix = stock+"-"+shop.getNumshop();
			Double distanceTarget = matrixMachine.matrix.get(keyForMatrix);
			if(distanceTarget == null) {
				System.err.println("ShopMachine.prepareShopList5Parameters: Магазин " + shop.getNumshop() + " не найдено расстояние от склада");
			}else {
				shop.setDistanceFromStock(distanceTarget);
				finalResult.add(shop);
			}
			
		}
		finalResult.sort(shopComparatorDistanceMain);
		return finalResult;
		
	}
	/**
	 * Метод подготавливает лист магазинов уже с потребностями
	 * <br>Метод сортирует потребность магазов от большего к меньшему и <b>от дальшего к ближнему <b>
	 * <br> т.е. самый дальний и самый требовательный магаз
	 * <br> Добавлена потребность магазина в килограммах
	 * <br> Добавлен парсинг возврещаемых паллет
	 * <br> <b>ComparatorShopsDistanceMain</b>
	 * @return
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 */
	public List<Shop> prepareShopList4Parameters(List<Integer> shopList, List<Double> pallHasShops, List<Integer> tonnageHasShops,  Integer stock,  Map<Integer, String> shopsWithCrossDockingMap, List<Double> pallReturn, List<Shop> shops) throws JsonMappingException, JsonProcessingException {
		Map<Integer, Shop> allShop =  getShopMap(shops);
		ComparatorShopsDistanceMain shopComparatorDistanceMain = new ComparatorShopsDistanceMain();
		List<Shop> result = new ArrayList<Shop>();
		for (int i = 0; i < shopList.size(); i++) {
			Integer integer = shopList.get(i);
			Shop shop = (Shop) SerializationUtils.clone(allShop.get(integer));
			if(shop == null) {
				System.err.println("ShopMachine.prepareShopList4Parameters: Магазин " + integer + " не найден в базе данных!");
				return null;
			}
			shop.setNeedPall(pallHasShops.get(i));
			shop.setWeight(tonnageHasShops.get(i));
			shop.setPallReturn(pallReturn.get(i));
			if(shopsWithCrossDockingMap.get(integer) != null) {
				shop.setKrossPolugonName(shopsWithCrossDockingMap.get(integer));
			}
			result.add(shop);
		}
		//задаём расстояния от склада до магазина, для последующей сортировки
		List<Shop> finalResult = new ArrayList<Shop>();
		for (Shop shop : result) {
			String keyForMatrix = stock+"-"+shop.getNumshop();
			Double distanceTarget = matrixMachine.matrix.get(keyForMatrix);
			if(distanceTarget == null) {
				System.err.println("ShopMachine.prepareShopList4Parameters: Магазин " + shop.getNumshop() + " не найдено расстояние от склада");
			}else {
				shop.setDistanceFromStock(distanceTarget);
				finalResult.add(shop);
			}
			
		}
		finalResult.sort(shopComparatorDistanceMain);
		return finalResult;
		
	}
	
	/**
	 * Метод подготавливает лист магазинов уже с потребностями
	 * <br>Метод сортирует потребность магазов от большего к меньшему и <b>от дальшего к ближнему <b>
	 * <br> т.е. самый дальний и самый требовательный магаз
	 * @return
	 */
	public List<Shop> prepareShopList2Parameters(List<Integer> shopList, List<Double> pallHasShops, Integer stock, List<Shop> shops) {
		Map<Integer, Shop> allShop =  getShopMap(shops);
		ComparatorShops comparatorShops = new ComparatorShops();
		List<Shop> result = new ArrayList<Shop>();
		for (Integer integer : shopList) {
			int index = shopList.indexOf(integer);
			Shop shop = allShop.get(integer);
			if(shop == null) {
				System.err.println("ShopMachine.prepareShopList2Parameters: Магазин " + integer + " не найден в базе данных!");
				return null;
			}
			shop.setNeedPall(pallHasShops.get(index));
			result.add(shop);
		}
		//задаём расстояния от склада до магазина, для последующей сортировки
		List<Shop> finalResult = new ArrayList<Shop>();
		for (Shop shop : result) {
			String keyForMatrix = stock+"-"+shop.getNumshop();
			Double distanceTarget = matrixMachine.matrix.get(keyForMatrix);
			if(distanceTarget == null) {
				System.err.println("ShopMachine.prepareShopList2Parameters: Магазин " + shop.getNumshop() + " не найдено расстояние от склада");
			}else {
				shop.setDistanceFromStock(distanceTarget);
				finalResult.add(shop);
			}
			
		}
		finalResult.sort(comparatorShops);
		return finalResult;
		
	}	
	
	/**
	 * Метод подготавливает лист магазинов уже с потребностями
	 * <br>Метод сортирует потребность магазов <b>от дальшего к ближнему <b> и только потом от большего к меньшему
	 * 
	 * @return
	 */
	public List<Shop> prepareShopList2ParametersDistanceMain(List<Integer> shopList, List<Double> pallHasShops, Integer stock, List<Shop> shops) {
		Map<Integer, Shop> allShop =  getShopMap(shops);
		ComparatorShopsDistanceMain comparatorShops = new ComparatorShopsDistanceMain();
		List<Shop> result = new ArrayList<Shop>();
		for (Integer integer : shopList) {
			int index = shopList.indexOf(integer);
			Shop shop = allShop.get(integer);
			if(shop == null) {
				System.err.println("ShopMachine.prepareShopList2Parameters: Магазин " + integer + " не найден в базе данных!");
				return null;
			}
			shop.setNeedPall(pallHasShops.get(index));
			result.add(shop);
		}
		//задаём расстояния от склада до магазина, для последующей сортировки
		List<Shop> finalResult = new ArrayList<Shop>();
		for (Shop shop : result) {
			String keyForMatrix = stock+"-"+shop.getNumshop();
			Double distanceTarget = matrixMachine.matrix.get(keyForMatrix);
			if(distanceTarget == null) {
				System.err.println("ShopMachine.prepareShopList2Parameters: Магазин " + shop.getNumshop() + " не найдено расстояние от склада");
			}else {
				shop.setDistanceFromStock(distanceTarget);
				finalResult.add(shop);
			}
			
		}
		finalResult.sort(comparatorShops);
		return finalResult;
		
	}
	
	public List<Shop> prepareShopList2ParametersTEST(List<Integer> shopList, List<Double> pallHasShops, Integer stock, List<Shop> shops) {
		Map<Integer, Shop> allShop = getShopMap(shops);
		ComparatorShopsTEST comparatorShops = new ComparatorShopsTEST();
		List<Shop> result = new ArrayList<Shop>();
		for (Integer integer : shopList) {
			int index = shopList.indexOf(integer);
			Shop shop = allShop.get(integer);
			if(shop == null) {
				System.err.println("ShopMachine.prepareShopList2Parameters: Магазин " + integer + " не найден в базе данных!");
				return null;
			}
			shop.setNeedPall(pallHasShops.get(index));
			result.add(shop);
		}
		//задаём расстояния от склада до магазина, для последующей сортировки
		List<Shop> finalResult = new ArrayList<Shop>();
		for (Shop shop : result) {
			String keyForMatrix = stock+"-"+shop.getNumshop();
			Double distanceTarget = matrixMachine.matrix.get(keyForMatrix);
			if(distanceTarget == null) {
				System.err.println("ShopMachine.prepareShopList2Parameters: Магазин " + shop.getNumshop() + " не найдено расстояние от склада");
			}else {
				shop.setDistanceFromStock(distanceTarget);
				finalResult.add(shop);
			}
			
		}
		finalResult.sort(comparatorShops);
		return finalResult;
		
	}

}