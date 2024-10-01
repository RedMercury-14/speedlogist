package by.base.main.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.TGTruckDAO;
import by.base.main.model.TGTruck;
import by.base.main.service.TGTruckService;

@Service
public class TGTruckServiceImpl implements TGTruckService{
	
	@Autowired
	private TGTruckDAO tgTruckDAO;

	@Override
	public List<TGTruck> getTGTruckList() {
		return tgTruckDAO.getTGTruckList();
	}

	@Override
	public Integer saveOrUpdateTGTruck(TGTruck tgTruck) {
		return tgTruckDAO.saveOrUpdateTGTruck(tgTruck);
	}

	@Override
	public Map<String, TGTruck> getTGTruckByChatIdUser(long chatId) {
		List<TGTruck> trucks = tgTruckDAO.getTGTruckByChatIdUser(chatId);
		 Map<String, TGTruck> response = new HashMap<String, TGTruck>();

		 for (TGTruck truck : trucks) {
		     response.put(truck.getNumTruck(), truck);
		 }
		return response;
	}

	@Override
	public void updateTGTruckMap(Map<String, TGTruck> map) {
		for (Entry<String, TGTruck> entry: map.entrySet()) {
			saveOrUpdateTGTruck(entry.getValue());
		}
		
	}

	
}
