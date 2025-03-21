package by.base.main.dao;

import java.sql.Date;
import java.util.List;

import by.base.main.model.Order;
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
	
	/**
	 * Проверяет, есть ли в базе данных разрешение по параметрма:
	 * <br> dateValid - дату на которое должно действовать разрешение
	 * <br> idObjectApprover - id объекта.
	 * Если такой объект имеется - возвращает true
	 * @param permission
	 * @return
	 */
	boolean checkPermission(Permission permission);
	
	void updatePermission(Permission task);
	
	/**
	 * Возвращает лист разрешений по дате dateValid 
	 * @param start
	 * @param end
	 * @return
	 */
	List<Permission> getPermissionListFromDateValid(Date start, Date end);
	
	void deletePermissionById(Integer id);
	
	void deletePermissionByIdObject(Integer id);
	
	/**
	 * Метод который проверяет, есть ли разрешение на текущую дату или нет
	 * <br>Возвращает само разрешение
	 * @param order
	 * @return
	 */
	Permission checkOrderForPermission (Order order);

}
