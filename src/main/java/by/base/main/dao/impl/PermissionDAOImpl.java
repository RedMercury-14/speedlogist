package by.base.main.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.PermissionDAO;
import by.base.main.model.Permission;

@Repository
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

	private static final String queryGetPermissionsByIdObject = "from Permission where idObjectApprover =:idObjectApprover order by idPermissions desc";
	@Override
	public List<Permission> getPermissionsByIdObject(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Permission> theObject = currentSession.createQuery(queryGetPermissionsByIdObject, Permission.class);
		theObject.setParameter("idObjectApprover", id);
		List<Permission> trucks = theObject.getResultList();
		return trucks;
	}

	private static final String queryGetPermissionById = "from Permission where idPermissions =:idPermissions";
	@Override
	public Permission getPermissionById(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Permission> theObject = currentSession.createQuery(queryGetPermissionById, Permission.class);
		theObject.setParameter("idPermissions", id);
		Permission permission = theObject.getSingleResult();
		return permission;
	}

	@Override
	public Integer savePermission(Permission permission) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(permission);
		return Integer.parseInt(currentSession.getIdentifier(permission).toString());
	}

	@Override
	public void updatePermission(Permission task) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.update(task);
	}

}
