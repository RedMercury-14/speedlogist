package by.base.main.service.impl;

import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.PermissionDAO;
import by.base.main.model.Order;
import by.base.main.model.Permission;
import by.base.main.service.PermissionService;

@Service
public class PermissionServiceImpl implements PermissionService{
	
	@Autowired
	private PermissionDAO permissionDAO;

	@Transactional
	@Override
	public List<Permission> getPermissionList() {
		return permissionDAO.getPermissionList();
	}

	@Transactional
	@Override
	public List<Permission> getPermissionsByIdObject(Integer id) {
		return permissionDAO.getPermissionsByIdObject(id);
	}

	@Transactional
	@Override
	public Permission getPermissionById(Integer id) {
		return permissionDAO.getPermissionById(id);
	}

	@Transactional
	@Override
	public Integer savePermission(Permission permission) {
		return permissionDAO.savePermission(permission);
	}

	@Transactional
	@Override
	public void updatePermission(Permission permission) {
		permissionDAO.updatePermission(permission);		
	}

	@Transactional
	@Override
	public boolean checkPermission(Permission permission) {
		return permissionDAO.checkPermission(permission);
	}

	@Transactional
	@Override
	public List<Permission> getPermissionListFromDateValid(Date start, Date end) {
		return permissionDAO.getPermissionListFromDateValid(start, end);
	}

	@Transactional
	@Override
	public void deletePermissionById(Integer id) {
		permissionDAO.deletePermissionById(id);
		
	}
	
	@Transactional
	@Override
	public void deletePermissionByIdObject(Integer id) {
		permissionDAO.deletePermissionByIdObject(id);
		
	}

	@Transactional
	@Override
	public Permission checkOrderForPermission(Order order) {
		return permissionDAO.checkOrderForPermission(order);
	}

}
