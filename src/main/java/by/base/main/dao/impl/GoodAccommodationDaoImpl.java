package by.base.main.dao.impl;

import by.base.main.dao.GoodAccommodationDao;
import by.base.main.model.GoodAccommodation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GoodAccommodationDaoImpl implements GoodAccommodationDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public long save(GoodAccommodation goodAccommodation) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.save(goodAccommodation);
        return Long.parseLong(currentSession.getIdentifier(goodAccommodation).toString());
    }

    @Override
    public void updateGoodAccommodation(GoodAccommodation goodAccommodation) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.update(goodAccommodation);
    }

    private static final String queryGetAccommodationByGoodIdAndStock = "from GoodAccommodation where productCode=:productCode and status = 20";
    @Override
    public GoodAccommodation getActualGoodAccommodationByGoodIdAndStock(Long productCode) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<GoodAccommodation> theObject = currentSession.createQuery(queryGetAccommodationByGoodIdAndStock, GoodAccommodation.class);
        theObject.setParameter("productCode", productCode);
        if (theObject.getResultList().isEmpty()) {
            return null;
        } else {
            return theObject.getResultList().get(0);
        }
    }

    private static final String queryGetAccommodationById = "from GoodAccommodation where idGoodAccommodation=:idGoodAccommodation";
    @Override
    public GoodAccommodation getGoodAccommodationById(Long idGoodAccommodation) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<GoodAccommodation> theObject = currentSession.createQuery(queryGetAccommodationById, GoodAccommodation.class);
        theObject.setParameter("idGoodAccommodation", idGoodAccommodation);
        return theObject.getResultList().get(0);

    }

    private static final String queryGetActualGoodAccommodationByGoodId = "from GoodAccommodation where productCode=:productCode";
	@Override
	public List<GoodAccommodation> getActualGoodAccommodationByGoodId(Long productCode) {
		Session currentSession = sessionFactory.getCurrentSession();
        Query<GoodAccommodation> theObject = currentSession.createQuery(queryGetActualGoodAccommodationByGoodId, GoodAccommodation.class);
        theObject.setParameter("productCode", productCode);
        if (theObject.getResultList().isEmpty()) {
            return null;
        } else {
            return theObject.getResultList();
        }
	}

	private static final String queryGetActualGoodAccommodationByCodeProductList = "from GoodAccommodation where productCode IN (:codeProduct)";
	@Override
	public Map<Long,GoodAccommodation> getActualGoodAccommodationByCodeProductList(List<Long> productCode) {
		Session currentSession = sessionFactory.getCurrentSession();
        Query<GoodAccommodation> theObject = currentSession.createQuery(queryGetActualGoodAccommodationByCodeProductList, GoodAccommodation.class);
        theObject.setParameterList("codeProduct", productCode);
        if (theObject.getResultList().isEmpty()) {
            return new HashMap<Long, GoodAccommodation>(); // важно! не возвращаем null!
        } else {
        	Map<Long,GoodAccommodation> result = new HashMap<Long, GoodAccommodation>();
        	for (GoodAccommodation goodAccommodation : theObject.getResultList()) {
        		result.put(goodAccommodation.getProductCode(), goodAccommodation);
			}
            return result;
        }
	}

	private static final String queryGetList = "from GoodAccommodation order by idGoodAccommodation";
	@Override
	public List<GoodAccommodation> getAll() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<GoodAccommodation> theRole = currentSession.createQuery(queryGetList, GoodAccommodation.class);
		List <GoodAccommodation> roles = theRole.getResultList();
		return roles;

	}
}
