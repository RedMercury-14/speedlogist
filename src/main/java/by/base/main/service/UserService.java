package by.base.main.service;

import java.util.List;

import by.base.main.model.User;

public interface UserService {
	
	List<User> getUserList();
	
	/**
	 * Отдаёт всех сотрудников добронома
	 * @return
	 */
	List<User> getEmployeesList();

	void saveOrUpdateUser(User user, int idRole);
	
	User saveNewDriver(User user);

	User getUserById(int id);
	
	User getUserByLogin(String login);
	
	List<User> getUserByYNP(String YNP);

	void deleteUserById(int id);
	
	void deleteUserByLogin(String login);

	List<User> getDriverList(String companyName);
	
	User getUserByDriverCard(String num);
	
	List<User> getCarrierList();
	
	/**
	 * Отдаёт всех перевозчиков, без машин
	 * @return
	 */
	List<User> getCarrierListV2();
	
	List<User> getDesableCarrierList();
	
	Integer getCountUserInDB();
	
	void echo();
	
	int updateUserInBaseDocuments(int idUser, String text);
	
	User getUserByLoginV2(String login);
	
	List<User> getUserLoginList();
}
