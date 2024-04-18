package by.base.main.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import by.base.main.dao.TruckDAO;
import by.base.main.dao.UserDAO;
import by.base.main.model.Truck;
import by.base.main.model.User;
import by.base.main.service.TruckService;

@Service
public class TruckServiceImpl implements TruckService{
	
	@Autowired
	TruckDAO truckDAO;
	
	@Autowired
	UserDAO userDAO;

	@Override
	public List<Truck> getTruckList() {
		return truckDAO.getTruckList();
	}

	@Override
	public List<Truck> getTruckListByUser() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();	
		User user = userDAO.getUserByLogin(name);
		return truckDAO.getTruckListByUser(user);
	}

	@Override
	public void saveOrUpdateTruck(Truck truck) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();	
		User user = userDAO.getUserByLogin(name);
		truck.setUser(user);
		truckDAO.saveOrUpdateTruck(truck);		
	}
	
	@Override
	public void saveOrUpdateTruck(Truck truck, User user) {
		truck.setUser(user);
		truckDAO.saveOrUpdateTruck(truck);		
	}

	@Override
	public Truck getTruckById(int id) {
		return truckDAO.getTruckById(id);
	}

	@Override
	public Truck getTruckByNum(String login) {
		return truckDAO.getTruckByNum(login);
	}

	@Override
	public void deleteTruckById(int id) {
		truckDAO.deleteTruckById(id);		
	}

	@Override
	public void deleteTruckByNum(String login) {
		truckDAO.deleteTruckByNum(login);		
	}

	@Override
	public List<Truck> getTruckListByUser(User user) {
		return truckDAO.getTruckListByUser(user);
	}

	@Override
	public Truck saveNewTruck(Truck truck) {
		return truckDAO.saveNewTruck(truck);
	}

	@Override
	public void updateTruck(Truck truck) {
		truckDAO.updateTruck(truck);		
	}

}
