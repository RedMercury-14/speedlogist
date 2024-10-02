package by.base.main.dao;

import java.util.List;

import by.base.main.model.TGTruck;
import by.base.main.model.TGUser;

public interface TGTruckDAO {
	
	List<TGTruck> getTGTruckList();
	
	/**
	 * Отдаёт машины начная с сегодняшней даты и дальше
	 * @return
	 */
	List<TGTruck> getActualTGTruckList();

	Integer saveOrUpdateTGTruck(TGTruck tgTruck);
	
	List<TGTruck> getTGTruckByChatIdUser(long chatId);
	
	TGTruck getTGTruckByChatNumTruck(String numTruck, TGUser tgUser);
	
	void deleteTGTruckByNumTruck(String numTruck);
}
