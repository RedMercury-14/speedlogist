package by.base.main.dao;

import java.util.List;

import by.base.main.model.Rates;

public interface RatesDAO {
	
	List<Rates> getRatesList();

	void saveOrUpdateRates(Rates rates);

	Rates getRatesById(int id);
	
	List<Rates> getListRatesByCasteForIsoterm(String caste);
	
	List<Rates> getListRatesByCasteForRef(String caste);	

	void deleteRatesById(int id);
}
