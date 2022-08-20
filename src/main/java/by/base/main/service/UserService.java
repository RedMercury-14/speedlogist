package by.base.main.service;

import java.util.List;

import by.base.main.model.User;

public interface UserService {
	
	List<User> getUserList();

	void saveOrUpdateUser(User user, int idRole);

	User getUserById(int id);
	
	User getUserByLogin(String login);

	void deleteUserById(int id);
	
	void deleteUserByLogin(String login);

	List<User> getDriverList(String companyName);
	
	User getUserByDriverCard(String num);
	
	List<User> getCarrierList();
	
	List<User> getDesableCarrierList();
}
