package by.base.main.dao;

import java.util.List;

import by.base.main.model.User;


public interface UserDAO {
	
	List<User> getUserList();

	void saveOrUpdateUser(User user);

	User getUserById(int id);
	
	User getUserByLogin(String login);

	void deleteUserById(int id);
	
	void deleteUserByLogin(String login);
	
	List<User> getDriverList(String companyName);
	
	User getUserByNumDriverCard(String num);
	
	List<User> getCarrierList();
	
	List<User> getDesableCarrierList();

}
