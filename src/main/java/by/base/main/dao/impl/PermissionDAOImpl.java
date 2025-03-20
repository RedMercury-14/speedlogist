package by.base.main.dao.impl;

import java.sql.Date;
import java.util.List;

import javax.persistence.TemporalType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.PermissionDAO;
import by.base.main.model.Order;
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

	private static final String queryCheckPermission = "from Permission where idObjectApprover =:idObjectApprover AND dateValid =:dateValid";
	@Override
	public boolean checkPermission(Permission permission) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Permission> theObject = currentSession.createQuery(queryCheckPermission, Permission.class);
		theObject.setParameter("idObjectApprover", permission.getIdObjectApprover());
		theObject.setParameter("dateValid", permission.getDateValid(), TemporalType.DATE);
		List<Permission> permissionResult = theObject.getResultList();
		if(!permissionResult.isEmpty()) {
			return true;
		}else {
			return false;
		}		
	}

	private static final String queryGetPermissionListFromDateValid = "from Permission where dateValid BETWEEN :dateStart and :dateEnd";
	@Override
	public List<Permission> getPermissionListFromDateValid(Date start, Date end) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Permission> theObject = currentSession.createQuery(queryGetPermissionListFromDateValid, Permission.class);
		theObject.setParameter("dateStart", start, TemporalType.DATE);
		theObject.setParameter("dateEnd", end, TemporalType.DATE);
		List<Permission> permissionResult = theObject.getResultList();
		return permissionResult;
	}

	private static final String queryDeletePermissionById = "delete from Permission where idPermissions=:id";
	@Override
	public void deletePermissionById(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeletePermissionById);
		theQuery.setParameter("id", id);
		theQuery.executeUpdate();
	}

	private static final String queryDeletePermissionByIdObject = "delete from Permission where idObjectApprover=:id";
	@Override
	public void deletePermissionByIdObject(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeletePermissionByIdObject);
		theQuery.setParameter("id", id);
		theQuery.executeUpdate();
	}

	private static final String queryСheckOrderForPermission = "from Permission where dateValid =:dateValid AND idObjectApprover =:idObjectApprover";
	@Override
	public Permission checkOrderForPermission(Order order) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Permission> theObject = currentSession.createQuery(queryСheckOrderForPermission, Permission.class);
		theObject.setParameter("dateValid", Date.valueOf(order.getTimeDelivery().toLocalDateTime().toLocalDate()), TemporalType.DATE);
		theObject.setParameter("idObjectApprover", order.getIdOrder());
		List<Permission> permissionResult = theObject.getResultList();
		if(!permissionResult.isEmpty()) {
			return permissionResult.get(0);
		}else {
			return null;
		}
		
	}

}
