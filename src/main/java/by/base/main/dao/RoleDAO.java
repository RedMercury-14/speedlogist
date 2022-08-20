package by.base.main.dao;

import java.util.List;

import by.base.main.model.Role;

public interface RoleDAO {
	
	public List<Role> getRoleList();

	public Role getRole(int idRole);

}
