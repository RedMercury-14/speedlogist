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
	
	/**
	 * Отдаёт матрицу расстояний, порционно. Для прода!
	 * @param shops
	 * @return
	 */
	Map<String,Double> getDistanceMatrixInBatches();
	
	/**
	 * Отдаёт усеченную матрицу, только те значения, которые были в листе
	 * @param shops
	 * @return
	 */
	public Map<String, Double> getDistanceMatrixByShops(List<Integer> shops);
	
	/**
	 * Отдаёт усеченную матрицу, только те значения, которые были в листе
	 * расстояние и время
	 * 0 - матрица расстояний
	 * 1- матрица времени
	 * @param shops
	 * @return
	 */
	public List<Map<String, Double>> getMatrixByShopsDistanceAndTime(List<Integer> shops);
	
}
