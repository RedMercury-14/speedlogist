package by.base.main.dao;

import java.util.List;

import by.base.main.model.TGTruck;
import by.base.main.model.TGUser;

public interface TGTruckDAO {
	
	List<TGTruck> getTGTruckList();

	Integer saveOrUpdateTGTruck(TGTruck tgTruck);
	
	List<TGTruck> getTGTruckByChatIdUser(long chatId);
	
	TGTruck getTGTruckByChatNumTruck(String numTruck, TGUser tgUser);
	
	void deleteTGTruckByNumTruck(String numTruck);
}
