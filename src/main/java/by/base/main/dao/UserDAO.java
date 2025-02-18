package by.base.main.dao;

import java.sql.Date;
import java.util.List;

import by.base.main.model.User;


public interface UserDAO {
	
	List<User> getUserList();
	
	/**
	 * Отдаёт всех сотрудников добронома
	 * @return
	 */
	List<User> getEmployeesList();

	void saveOrUpdateUser(User user);
	
	User saveNewDriver(User user);

	User getUserById(int id);
	
	User getUserByLogin(String login);
	
	List<User> getUserByYNP(String YNP);

	void deleteUserById(int id);
	
	void deleteUserByLogin(String login);
	
	List<User> getDriverList(String companyName);
	
	User getUserByNumDriverCard(String num);
	
	List<User> getCarrierList();

	List<User> getCarrierListV2();
	
	List<User> getDesableCarrierList();
	
	Integer getCountUserInDB();
	
	int updateUserInBaseDocuments(int idUser, String text);
	
	User getUserByLoginV2(String login);
	
	List<User> getUserLoginList();

}
