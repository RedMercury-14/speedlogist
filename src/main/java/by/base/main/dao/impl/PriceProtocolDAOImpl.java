package by.base.main.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.PriceProtocolDAO;
import by.base.main.model.PriceProtocol;

@Repository
public class PriceProtocolDAOImpl implements PriceProtocolDAO{
	
	@Autowired
    private SessionFactory sessionFactory;

    private static final String QUERY_ALL = "from PriceProtocol order by idPriceProtocol";
    private static final String QUERY_BY_PRODUCT_CODE = "from PriceProtocol where productCode = :productCode";
    private static final String QUERY_BY_BARCODE = "from PriceProtocol where barcode = :barcode";
    private static final String QUERY_BY_CONTRACT_NUMBER = "from PriceProtocol where contractNumber = :contractNumber";

    @Override
    public List<PriceProtocol> getAll() {
        Session session = sessionFactory.getCurrentSession();
        Query<PriceProtocol> query = session.createQuery(QUERY_ALL, PriceProtocol.class);
        return query.getResultList();
    }

    @Override
    public PriceProtocol getById(int idPriceProtocol) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(PriceProtocol.class, idPriceProtocol);
    }

    @Override
    public PriceProtocol getByProductCode(String productCode) {
        Session session = sessionFactory.getCurrentSession();
        Query<PriceProtocol> query = session.createQuery(QUERY_BY_PRODUCT_CODE, PriceProtocol.class);
        query.setParameter("productCode", productCode);
        return query.uniqueResult();
    }

    @Override
    public PriceProtocol getByBarcode(String barcode) {
        Session session = sessionFactory.getCurrentSession();
        Query<PriceProtocol> query = session.createQuery(QUERY_BY_BARCODE, PriceProtocol.class);
        query.setParameter("barcode", barcode);
        return query.uniqueResult();
    }

    @Override
    public List<PriceProtocol> getByContractNumber(String contractNumber) {
        Session session = sessionFactory.getCurrentSession();
        Query<PriceProtocol> query = session.createQuery(QUERY_BY_CONTRACT_NUMBER, PriceProtocol.class);
        query.setParameter("contractNumber", contractNumber);
        return query.getResultList();
    }

    @Override
    public int save(PriceProtocol priceProtocol) {
        Session session = sessionFactory.getCurrentSession();
        session.save(priceProtocol);
		return Integer.parseInt(session.getIdentifier(priceProtocol).toString());
    }

    @Override
    public void update(PriceProtocol priceProtocol) {
        Session session = sessionFactory.getCurrentSession();
        session.update(priceProtocol);
    }

}
