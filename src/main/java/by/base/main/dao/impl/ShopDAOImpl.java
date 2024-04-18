package by.base.main.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.ShopDAO;
import by.base.main.model.RouteHasShop;
import by.base.main.model.Shop;
import by.base.main.model.User;

@Repository
public class ShopDAOImpl implements ShopDAO{
	
	@Autowired
	private SessionFactory sessionFactory;

	private static final String queryGetList = "from Shop s LEFT JOIN FETCH s.roteHasShopList rhs LEFT JOIN FETCH s.director d LEFT JOIN FETCH s.feedbackList f order by s.numshop";
	@Override
	@Transactional
	public List<Shop> getShopList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Shop> theObject = currentSession.createQuery(queryGetList, Shop.class);
		List <Shop> objects = theObject.getResultList();
		return objects;
	}

	//не сохраняет, если магаз имеется!
	private static final String queryGetObject = "select numshop from Shop s where numshop=:userName";
	@Override
	@Transactional
	public void saveShop(Shop shop) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query query = currentSession.createQuery(queryGetObject);
		query.setParameter("userName", shop.getNumshop()); 
		if (query.list().isEmpty()) {
			//currentSession.beginTransaction();//??
			currentSession.saveOrUpdate(shop);
			//currentSession.getTransaction().commit();//??
		}  
	}
	
	private static final String queryGetObjById = "from Shop s LEFT JOIN FETCH s.roteHasShopList rhs LEFT JOIN FETCH s.director d LEFT JOIN FETCH s.feedbackList f  where s.numshop=:numshop";

	@Override
	@Transactional
	public Shop getShopByNum(int id) {
		Session currentSession = sessionFactory.getCurrentSession();		
		Query<Shop> theObject = currentSession.createQuery(queryGetObjById, Shop.class);
		theObject.setParameter("numshop", id);		
		List<Shop> RhS = theObject.getResultList();				
		if(RhS.isEmpty()) {
			return null;
		}else {
			Shop object = RhS.stream().findFirst().get();
			return object;
		}
		
	}

	private static final String queryDeleteById = "delete from Shop where numshop=:setId";
	@Override
	@Transactional
	public void deleteShopByNum(int id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeleteById);
		theQuery.setParameter("setId", id);
		theQuery.executeUpdate();
		
	}

	@Override
	@Transactional
	public void updateShop(Shop shop) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(shop);		
	}

}
