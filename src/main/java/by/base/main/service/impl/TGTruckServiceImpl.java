package by.base.main.service.impl;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.TGTruckDAO;
import by.base.main.model.TGTruck;
import by.base.main.model.TGUser;
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
	@Deprecated
	public Map<String, TGTruck> getTGTruckByChatIdUser(long chatId) {
		List<TGTruck> trucks = tgTruckDAO.getTGTruckByChatIdUser(chatId);
		if(trucks == null || trucks.isEmpty()) return null;
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

	@Override
	public List<TGTruck> getTGTruckByChatIdUserList(long chatId) {
		return tgTruckDAO.getTGTruckByChatIdUser(chatId);
	}

	@Override
	public TGTruck getTGTruckByChatNumTruck(String numTruck, TGUser tgUser) {
		return tgTruckDAO.getTGTruckByChatNumTruck(numTruck, tgUser);
	}

	@Override
	public void deleteTGTruckByNumTruck(String numTruck, TGUser tgUser) {
		tgTruckDAO.deleteTGTruckByNumTruck(numTruck, tgUser);
		
	}

	@Override
	public List<TGTruck> getActualTGTruckList() {
		return tgTruckDAO.getActualTGTruckList();
	}

	@Override
	public boolean checkListName(String name, Date date) {
		return tgTruckDAO.checkListName(name, date);
	}

	@Override
	public TGTruck getTGTruckByChatId(Integer id) {
		return tgTruckDAO.getTGTruckByChatId(id);
	}

	@Override
	public void deleteTGTruckByNumTruck(String numTruck, Date date) {
		tgTruckDAO.deleteTGTruckByNumTruck(numTruck, date);
	}

	@Override
	public TGTruck getTGTruckByChatNumTruckStrict(String numTruck, Date date, TGUser tgUser) {
		return tgTruckDAO.getTGTruckByChatNumTruckStrict(numTruck, date, tgUser);
	}

	@Override
	public List<TGTruck> getTGTruckByidUserPeriod(Integer idUser, Date dateStart, Date dateFinish) {
		return tgTruckDAO.getTGTruckByidUserPeriod(idUser, dateStart, dateFinish);
	}

	
}
