package by.base.main.service;

import java.util.List;
import java.util.Map;

import by.base.main.model.TGTruck;
import by.base.main.model.TGUser;

public interface TGTruckService {

	List<TGTruck> getTGTruckList();

	Integer saveOrUpdateTGTruck(TGTruck tgTruck);
	
	/**
	 * Map<String, TGTruck> ключ - номер авто, значение авто
	 * @param chatId
	 * @return
	 */
	Map<String, TGTruck> getTGTruckByChatIdUser(long chatId);
	
	/**
	 * @param chatId
	 * @return
	 */
	List<TGTruck> getTGTruckByChatIdUserList(long chatId);
	
	/**
	 * Обнавляет всю мапу сразу
	 * @param map
	 */
	void updateTGTruckMap(Map<String, TGTruck> map);
	
	/**
	 * Отдаёт TGTruck по номеру авто и User
	 * Юзер нужен для определения даты (записывается в date_order_truck_optimization)
	 * @param numTruck
	 * @return
	 */
	TGTruck getTGTruckByChatNumTruck(String numTruck, TGUser tgUser);
	
	void deleteTGTruckByNumTruck(String numTruck);

}
