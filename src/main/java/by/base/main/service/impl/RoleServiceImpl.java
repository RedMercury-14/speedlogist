package by.base.main.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.RoleDAO;
import by.base.main.model.Role;
import by.base.main.service.RoleService;
@Service
public class RoleServiceImpl implements RoleService{
	
	@Autowired
	private RoleDAO roleDao;

	@Override
	@Transactional
	public List<Role> getRoleList() {
		return roleDao.getRoleList();
	}

	@Override
	@Transactional
	public Role getRole(int idRole) {
		return roleDao.getRole(idRole);
	}

}
