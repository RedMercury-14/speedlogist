package by.base.main.service.impl;

import java.util.List;

import javax.transaction.Transactional;

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
	private TruckDAO truckDAO;
	
	@Autowired
	private UserDAO userDAO;

	@Override
	@Transactional
	public List<Truck> getTruckList() {
		return truckDAO.getTruckList();
	}

	@Override
	@Transactional
	public List<Truck> getTruckListByUser() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();	
		User user = userDAO.getUserByLogin(name);
		return truckDAO.getTruckListByUser(user);
	}

	@Override
	@Transactional
	public void saveOrUpdateTruck(Truck truck) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();	
		User user = userDAO.getUserByLogin(name);
		truck.setUser(user);
		truckDAO.saveOrUpdateTruck(truck);		
	}
	
	@Override
	@Transactional
	public void saveOrUpdateTruck(Truck truck, User user) {
		truck.setUser(user);
		truckDAO.saveOrUpdateTruck(truck);		
	}

	@Override
	@Transactional
	public Truck getTruckById(int id) {
		return truckDAO.getTruckById(id);
	}

	@Override
	@Transactional
	public Truck getTruckByNum(String login) {
		return truckDAO.getTruckByNum(login);
	}

	@Override
	@Transactional
	public void deleteTruckById(int id) {
		truckDAO.deleteTruckById(id);		
	}

	@Override
	@Transactional
	public void deleteTruckByNum(String login) {
		truckDAO.deleteTruckByNum(login);		
	}

	@Override
	@Transactional
	public List<Truck> getTruckListByUser(User user) {
		return truckDAO.getTruckListByUser(user);
	}

	@Override
	@Transactional
	public Truck saveNewTruck(Truck truck) {
		return truckDAO.saveNewTruck(truck);
	}

	@Override
	@Transactional
	public void updateTruck(Truck truck) {
		truckDAO.updateTruck(truck);		
	}

}
