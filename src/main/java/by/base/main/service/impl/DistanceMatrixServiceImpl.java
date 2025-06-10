package by.base.main.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.base.main.dao.DistanceMatrixDAO;
import by.base.main.model.DistanceMatrix;
import by.base.main.service.DistanceMatrixService;

@Service
public class DistanceMatrixServiceImpl implements DistanceMatrixService {
	
	@Autowired
	private DistanceMatrixDAO distanceMatrixDAO;

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public List<DistanceMatrix> getAll() {
		return distanceMatrixDAO.getAll();
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public Map<String, Double> getDistanceMatrix() {
		return distanceMatrixDAO.getDistanceMatrix();
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public boolean isExist(String key) {
		return distanceMatrixDAO.isExist(key);
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public void save(DistanceMatrix distanceMatrix) {
		distanceMatrixDAO.save(distanceMatrix);
		
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public void update(DistanceMatrix distanceMatrix) {
		update(distanceMatrix);		
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public Map<String, Double> getDistanceMatrixInBatches() {
		return distanceMatrixDAO.getDistanceMatrixInBatches();
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public Map<String, Double> getDistanceMatrixByShops(List<Integer> shops) {
		return distanceMatrixDAO.getDistanceMatrixByShops(shops);
	}

}
