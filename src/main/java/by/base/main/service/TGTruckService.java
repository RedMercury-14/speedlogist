package by.base.main.service;

import java.util.List;
import java.util.Map;

import by.base.main.model.TGTruck;

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
	 * Обнавляет всю мапу сразу
	 * @param map
	 */
	void updateTGTruckMap(Map<String, TGTruck> map);

}
