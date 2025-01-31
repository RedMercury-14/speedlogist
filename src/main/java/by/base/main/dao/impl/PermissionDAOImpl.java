package by.base.main.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import by.base.main.dao.PermissionDAO;
import by.base.main.model.OrderProduct;
import by.base.main.model.Permission;

public class PermissionDAOImpl implements PermissionDAO{

	@Autowired
	private SessionFactory sessionFactory;
	
	private static final String queryGetList = "from Permission order by idPermissions";
	@Override
	public List<Permission> getPermissionList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Permission> theRole = currentSession.createQuery(queryGetList, Permission.class);
		List <Permission> roles = theRole.getResultList();
		return roles;
	}

	
	@Override
	public List<Permission> getPermissionsByIdObject(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Permission getPermissionById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer savePermission(Permission permission) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updatePermission(Permission task) {
		// TODO Auto-generated method stub
		
	}

}
