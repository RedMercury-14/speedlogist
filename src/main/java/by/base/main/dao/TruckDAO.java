package by.base.main.dao;

import java.util.List;

import by.base.main.model.Truck;
import by.base.main.model.User;

public interface TruckDAO {
	
	List<Truck> getTruckList();
	
	List<Truck> getTruckListByUser(User user);

	void saveOrUpdateTruck(Truck truck);

	Truck getTruckById(int id);
	
	Truck getTruckByNum(String login);

	void deleteTruckById(int id);
	
	void deleteTruckByNum(String login);

}
