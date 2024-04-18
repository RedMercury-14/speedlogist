package by.base.main.util.hcolossus.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

/**
 * Класс подготовки и обработки магазинов
 */
@Service
public class ShopMachine {

	@Autowired
	private ShopService shopService;
	
	@Autowired
	private MatrixMachine matrixMachine;
	
	private Comparator<Shop> shopComparator = (o1, o2) -> (o2.getNeedPall() - o1.getNeedPall()); //сортирует от большей потребности к меньшей
	
	/**
	 * Метод подготавливает лист магазинов уже с потребностями
	 * <br>Метод сортирует потребность магазов от большего к меньшему
	 * @return
	 */
	public List<Shop> prepareShopList(List<Integer> shopList, List<Integer> pallHasShops) {
		Map<Integer, Shop> allShop =  shopService.getShopMap();
		List<Shop> result = new ArrayList<Shop>();
		for (Integer integer : shopList) {
			int index = shopList.indexOf(integer);
			Shop shop = allShop.get(integer);
			if(shop == null) {
				System.err.println("ShopMachine.prepareShopList: Магазин " + integer + " не найден в базе данных!");
				return null;
			}
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
	public List<Shop> prepareShopList3Parameters(List<Integer> shopList, List<Integer> pallHasShops, List<Integer> tonnageHasShops,  Integer stock) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<Integer, Shop> allShop =  shopService.getShopMap();
		ComparatorShops comparatorShops = new ComparatorShops();
		List<Shop> result = new ArrayList<Shop>();
		for (int i = 0; i < shopList.size(); i++) {
			Integer integer = shopList.get(i);
			Shop shop = (Shop) SerializationUtils.clone(allShop.get(integer));
			if(shop == null) {
				System.err.println("ShopMachine.prepareShopList2Parameters: Магазин " + integer + " не найден в базе данных!");
				return null;
			}
			shop.setNeedPall(pallHasShops.get(i));
			shop.setWeight(tonnageHasShops.get(i));
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
	 * @return
	 */
	public List<Shop> prepareShopList2Parameters(List<Integer> shopList, List<Integer> pallHasShops, Integer stock) {
		Map<Integer, Shop> allShop =  shopService.getShopMap();
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
	public List<Shop> prepareShopList2ParametersDistanceMain(List<Integer> shopList, List<Integer> pallHasShops, Integer stock) {
		Map<Integer, Shop> allShop =  shopService.getShopMap();
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
	
	public List<Shop> prepareShopList2ParametersTEST(List<Integer> shopList, List<Integer> pallHasShops, Integer stock) {
		Map<Integer, Shop> allShop =  shopService.getShopMap();
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
