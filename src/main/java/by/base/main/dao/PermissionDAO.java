package by.base.main.dao;

import java.util.List;

import by.base.main.model.Permission;

public interface PermissionDAO {
	
	List<Permission> getPermissionList();
	
	/**
	 * отдаёт разрешения связанные с таргетным объектом
	 * @return
	 */
	List<Permission> getPermissionsByIdObject(Integer id);
	
	Permission getPermissionById(Integer id);
	
	Integer savePermission(Permission permission);
	
	void updatePermission(Permission task);

}
