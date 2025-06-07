package by.base.main.dao;

import java.util.List;
import java.util.Map;

import by.base.main.model.DistanceMatrix;

public interface DistanceMatrixDAO {
	
	List<DistanceMatrix> getAll();
	
	Map<String,Double> getDistanceMatrix();
	
	boolean isExist (String key);
	
	void save(DistanceMatrix distanceMatrix);
	
	void update(DistanceMatrix distanceMatrix);
	
}
