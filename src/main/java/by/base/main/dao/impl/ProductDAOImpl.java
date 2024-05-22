package by.base.main.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import by.base.main.dao.ProductDAO;
import by.base.main.model.Product;

public class ProductDAOImpl implements ProductDAO{

	@Autowired
	private SessionFactory sessionFactory;
	
	private static final String queryGetList = "from Product order by idProduct";
	@Override
	@Transactional
	public List<Product> getAllProductList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Product> theRole = currentSession.createQuery(queryGetList, Product.class);
		List <Product> roles = theRole.getResultList();
		return roles;
	}

	@Override
	@Transactional
	public Integer saveProduct(Product product) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(product);
		return Integer.parseInt(currentSession.getIdentifier(product).toString());
	}

	
	private static final String queryGetObjByCode = "from Product where codeProduct=:codeProduct";
	@Transactional
	@Override
	public Product getProductByCode(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Product> theObject = currentSession.createQuery(queryGetObjByCode, Product.class);
		theObject.setParameter("codeProduct", id);
		List<Product> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}
		Product object = trucks.stream().findFirst().get();
		return object;
	}

	@Override
	@Transactional
	public void updateProduct(Product product) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.update(product);		
	}

}
