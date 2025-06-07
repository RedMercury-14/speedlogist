package by.base.main.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
