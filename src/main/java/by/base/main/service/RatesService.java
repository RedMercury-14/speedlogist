package by.base.main.service;

import java.util.List;

import by.base.main.model.Rates;

public interface RatesService {
	
	List<Rates> getRatesList();

	void saveOrUpdateRates(Rates rates);

	Rates getRatesById(int id);
	
	List<Rates> getListRatesByCasteForIsoterm(String caste);
	
	List<Rates> getListRatesByCasteForRef(String caste);	

	void deleteRatesById(int id);

}
