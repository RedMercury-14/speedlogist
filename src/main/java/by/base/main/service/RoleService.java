package by.base.main.service;

import java.util.List;

import by.base.main.model.Role;

public interface RoleService {
	
	public List<Role> getRoleList();

	public Role getRole(int idRole);

}
