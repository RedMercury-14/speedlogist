package by.base.main.service;

import java.sql.Date;
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
	
	/**
	 * Проверяет, есть ли в базе данных разрешение по параметрма:
	 * <br> dateValid - дату на которое должно действовать разрешение
	 * <br> idObjectApprover - id объекта.
	 * Если такой объект имеется - возвращает true
	 * @param permission
	 * @return
	 */
	boolean checkPermission(Permission permission);
	
	/**
	 * Возвращает лист разрешений по дате dateValid 
	 * @param start
	 * @param end
	 * @return
	 */
	List<Permission> getPermissionListFromDateValid(Date start, Date end);
	
	void deletePermissionById(Integer id);
	
	void deletePermissionByIdObject(Integer id);

}
