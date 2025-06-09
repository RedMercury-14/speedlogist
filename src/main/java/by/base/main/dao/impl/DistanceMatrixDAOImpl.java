package by.base.main.dao.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import by.base.main.dao.DistanceMatrixDAO;
import by.base.main.model.DistanceMatrix;

@Repository
public class DistanceMatrixDAOImpl implements DistanceMatrixDAO{
	
    @Autowired
    @Qualifier("sessionFactoryLogistFile")
    private SessionFactory sessionFactoryLogistFile;

    private static final String QUERY_GET_ALL = "from DistanceMatrix";
//    private static final String QUERY_CHECK_EXIST = "SELECT count(dm.idDistanceMatrix) FROM DistanceMatrix dm WHERE dm.idDistanceMatrix = :key";
    private static final String QUERY_CHECK_EXIST = "SELECT count(dm) > 0 FROM DistanceMatrix dm WHERE dm.idDistanceMatrix = :id";

    @Override
    public List<DistanceMatrix> getAll() {
        Session currentSession = sessionFactoryLogistFile.getCurrentSession();
        Query<DistanceMatrix> query = currentSession.createQuery(QUERY_GET_ALL, DistanceMatrix.class);
        return query.getResultList();
    }

    @Override
    public Map<String, Double> getDistanceMatrix() {
        Session currentSession = sessionFactoryLogistFile.getCurrentSession();
        Query<DistanceMatrix> query = currentSession.createQuery(QUERY_GET_ALL, DistanceMatrix.class);
        List<DistanceMatrix> distances = query.getResultList();
        
        Map<String, Double> distanceMap = new HashMap<>();
        for (DistanceMatrix dm : distances) {
            String key = String.valueOf(dm.getIdDistanceMatrix());
            distanceMap.put(key, dm.getDistance());
        }
        return distanceMap;
    }

    @Override
    public boolean isExist(String key) {
        Session currentSession = sessionFactoryLogistFile.getCurrentSession();
        Boolean exists = (Boolean) currentSession.createQuery(QUERY_CHECK_EXIST)
                .setParameter("id", key)
                .uniqueResult();
        return exists != null && exists;
    }


    @Override
    public void save(DistanceMatrix distanceMatrix) {
        Session currentSession = sessionFactoryLogistFile.getCurrentSession();
        currentSession.save(distanceMatrix);
    }

    @Override
    public void update(DistanceMatrix distanceMatrix) {
        Session currentSession = sessionFactoryLogistFile.getCurrentSession();
        currentSession.update(distanceMatrix);
    }

    
    StringBuilder hql = new StringBuilder("FROM DistanceMatrix dm WHERE ");
	@Override
	public Map<String, Double> getDistanceMatrixInBatches() {
		
		Session currentSession = sessionFactoryLogistFile.getCurrentSession();

	    int batchSize = 10000;
	    int offset = 0;

	    Map<String, Double> distanceMap = new HashMap<>();

	    List<DistanceMatrix> batch;

	    do {
	        Query<DistanceMatrix> query = currentSession.createQuery(
	            "FROM DistanceMatrix",
	            DistanceMatrix.class
	        );
	        query.setFirstResult(offset);
	        query.setMaxResults(batchSize);

	        batch = query.getResultList();

	        for (DistanceMatrix dm : batch) {
	            distanceMap.put(dm.getIdDistanceMatrix(), dm.getDistance());
	        }

	        offset += batchSize;

	        // Можно вручную освободить память под загруженные объекты:
	        currentSession.clear(); // Очищает Persistence Context (1-й уровень кеша Hibernate)

	    } while (!batch.isEmpty());

	    return distanceMap;
	}

	@Override
	public Map<String, Double> getDistanceMatrixByShops(List<Integer> shops) {
		if (shops == null || shops.isEmpty()) {
	        return Collections.emptyMap();
	    }

	    // Преобразуем List<Integer> в List<String> для сравнения с текстовыми колонками
	    List<String> shopIdsAsString = shops.stream()
	        .map(String::valueOf)
	        .collect(Collectors.toList());

	    Session currentSession = sessionFactoryLogistFile.getCurrentSession();

	    Query<DistanceMatrix> query = currentSession.createQuery(
	        "FROM DistanceMatrix dm WHERE dm.shopFrom IN (:shops) AND dm.shopTo IN (:shops)",
	        DistanceMatrix.class
	    );
	    query.setParameter("shops", shopIdsAsString);

	    List<DistanceMatrix> distances = query.getResultList();

	    Map<String, Double> distanceMap = new HashMap<>();
	    for (DistanceMatrix dm : distances) {
	        distanceMap.put(dm.getIdDistanceMatrix(), dm.getDistance());
	    }

	    return distanceMap;
	}

}
