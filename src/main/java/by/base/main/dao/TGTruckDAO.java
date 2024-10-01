package by.base.main.dao;

import java.util.List;

import by.base.main.model.TGTruck;

public interface TGTruckDAO {
	
	List<TGTruck> getTGTruckList();

	Integer saveOrUpdateTGTruck(TGTruck tgTruck);
	
	List<TGTruck> getTGTruckByChatIdUser(long chatId);
}
