package by.base.main.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.RatesDAO;
import by.base.main.model.Rates;
import by.base.main.service.RatesService;
@Service
public class RatesServiceImpl implements RatesService{
	
	@Autowired
	RatesDAO ratesDAO;

	@Override
	public List<Rates> getRatesList() {
		return ratesDAO.getRatesList();
	}

	@Override
	public void saveOrUpdateRates(Rates rates) {
		ratesDAO.saveOrUpdateRates(rates);
		
	}

	@Override
	public Rates getRatesById(int id) {
		return ratesDAO.getRatesById(id);
	}

	@Override
	public List<Rates> getListRatesByCasteForIsoterm(String caste) {
		return ratesDAO.getListRatesByCasteForIsoterm(caste);
	}

	@Override
	public List<Rates> getListRatesByCasteForRef(String caste) {
		return ratesDAO.getListRatesByCasteForRef(caste);
	}

	@Override
	public void deleteRatesById(int id) {
		ratesDAO.deleteRatesById(id);
	}

}
