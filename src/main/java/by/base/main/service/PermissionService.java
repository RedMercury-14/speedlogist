package by.base.main.service;

import java.util.List;

import by.base.main.model.Permission;

public interface PermissionService {
	
	List<Permission> getPermissionList();
	
	/**
	 * отдаёт разрешения связанные с таргетным объектом
	 * <br> <b>В обратном порядке, начиная от самого раннего внесенного в БД</b>
	 * @return
	 */
	List<Permission> getPermissionsByIdObject(Integer id);
	
	Permission getPermissionById(Integer id);
	
	Integer savePermission(Permission permission);
	
	void updatePermission(Permission permission);

}
