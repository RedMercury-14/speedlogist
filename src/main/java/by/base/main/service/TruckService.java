package by.base.main.service;

import java.util.List;

import by.base.main.model.Truck;
import by.base.main.model.User;

public interface TruckService {
	
	List<Truck> getTruckList();
	
	List<Truck> getTruckListByUser();
	
	List<Truck> getTruckListByUser(User user);

	void saveOrUpdateTruck(Truck truck);
	
	void saveOrUpdateTruck(Truck truck, User user);

	Truck getTruckById(int id);
	
	Truck getTruckByNum(String login);

	void deleteTruckById(int id);
	
	void deleteTruckByNum(String login);

}
