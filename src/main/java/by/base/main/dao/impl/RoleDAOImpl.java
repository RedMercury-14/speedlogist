package by.base.main.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.RoleDAO;
import by.base.main.model.Role;

@Repository

public class RoleDAOImpl implements RoleDAO{
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private static final String queryGetList = "from Role order by idrole";

	@Override
	@Transactional
	public List<Role> getRoleList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Role> theRole = currentSession.createQuery(queryGetList, Role.class);
		List <Role> roles = theRole.getResultList();
		return roles;
	}

	@Override
	@Transactional
	public Role getRole(int idRole) {
		Session currentSession = sessionFactory.getCurrentSession();
		Role role = currentSession.get(Role.class, idRole);
		return role;
	}

}
