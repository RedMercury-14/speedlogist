package by.base.main.service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import by.base.main.dao.RoleDAO;
import by.base.main.dao.UserDAO;
import by.base.main.model.Role;
import by.base.main.model.User;
import by.base.main.service.UserService;
@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private RoleDAO roleDAO;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	@Transactional
	public List<User> getUserList() {
		return userDAO.getUserList();
	}

	@Override
	@Transactional
	public void saveOrUpdateUser(User user, int idRole) {	
		Set<Role> rolest = new HashSet<Role>();
		Role role;
		if(user.getBlock() == null) {
			user.setBlock(false);
		}
		switch (idRole) {
		case 1:
			user.setEnablet(true);
			user.setIsDriver(false);
			user.setStatus("0");
			role = roleDAO.getRole(1);
			rolest.add(role);
			user.setRoles(rolest);			
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			break;
		case 2:
			user.setEnablet(true);
			user.setIsDriver(false);
			user.setStatus("0");
			role = roleDAO.getRole(2);
			rolest.add(role);
			user.setRoles(rolest);			
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			break;
		case 3:
			user.setEnablet(true);
			user.setIsDriver(false);
			user.setStatus("0");
			role = roleDAO.getRole(3);
			rolest.add(role);
			user.setRoles(rolest);			
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			break;
		case 4:
			user.setEnablet(true);
			user.setIsDriver(false);
			role = roleDAO.getRole(4);
			rolest.add(role);
			user.setRoles(rolest);
			user.setDepartment("Директор магазина");
			String company = "Доброном";			
			user.setCompanyName(company);
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			break;
		case 6:
			user.setEnablet(true);
			user.setIsDriver(false);
			user.setStatus("0");
			role = roleDAO.getRole(6);
			rolest.add(role);
			user.setRoles(rolest);			
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			break;
		case 7:
			user.setEnablet(true);
			user.setIsDriver(false);
			user.setStatus("0");
			user.setDepartment("Директор");
			role = roleDAO.getRole(7);
			rolest.add(role);
			user.setRoles(rolest);
			if(user.getCheck() == null) {
				user.setCheck("step1");
			}else {
				user.setDepartment("Директор");
			}			
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			break;
		case 8:
			user.setEnablet(true);
			user.setIsDriver(true);
			user.setStatus("0");
			role = roleDAO.getRole(8);
			rolest.add(role);
			user.setRoles(rolest);			
			String name = SecurityContextHolder.getContext().getAuthentication().getName();	
			String companyName = userDAO.getUserByLogin(name).getCompanyName();			
			user.setCompanyName(companyName);
			if (user.getPassword() != null) {				
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			}
			break;
		case 9: //регистрация младшего менеджере перевозчика
			user.setEnablet(true);
			user.setIsDriver(false);
			user.setStatus("0");
			user.setCheck("international");
			User boss = getThisUser();
			user.setNumYNP(boss.getNumYNP());
			role = roleDAO.getRole(7);
			rolest.add(role);
			user.setRoles(rolest);
			user.setCompanyName(boss.getCompanyName());
			if (user.getPassword() != null) {				
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			}
			break;	
		case 99: //регистрация закупера
			user.setEnablet(true);
			user.setIsDriver(false);
			user.setStatus("0");
			role = roleDAO.getRole(9);
			rolest.add(role);
			user.setRoles(rolest);
			user.setCompanyName("Доброном");
			if (user.getPassword() != null) {				
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			}
			break;
		case 10: //регистрация закупера
			user.setEnablet(true);
			user.setIsDriver(false);
			user.setStatus("0");
			role = roleDAO.getRole(10);
			rolest.add(role);
			user.setRoles(rolest);
			user.setCompanyName("Доброном");
			if (user.getPassword() != null) {				
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			}
			break;
		case 11: //регистрация наблюдателя за слотами
			user.setEnablet(true);
			user.setIsDriver(false);
			user.setStatus("0");
			role = roleDAO.getRole(11);
			rolest.add(role);
			user.setRoles(rolest);
			user.setCompanyName("Доброном");
			if (user.getPassword() != null) {				
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			}
			break;
		case 12: //регистрация наблюдателя за аналитикой
			user.setEnablet(true);
			user.setIsDriver(false);
			user.setStatus("0");
			role = roleDAO.getRole(12);
			rolest.add(role);
			user.setRoles(rolest);
			user.setCompanyName("Доброном");
			if (user.getPassword() != null) {				
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			}
			break;
		case 13: //регистрация наблюдателя за аналитикой
			user.setEnablet(true);
			user.setIsDriver(false);
			user.setStatus("0");
			role = roleDAO.getRole(13);
			rolest.add(role);
			user.setRoles(rolest);
			user.setCompanyName("Доброном");
			if (user.getPassword() != null) {				
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			}
			break;
		case 14: //регистрация пециалиста ОРЛ
			user.setEnablet(true);
			user.setIsDriver(false);
			user.setStatus("0");
			role = roleDAO.getRole(14);
			rolest.add(role);
			user.setRoles(rolest);
			user.setCompanyName("Доброном");
			if (user.getPassword() != null) {				
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			}
			break;
		case 15: //регистрация Специалист отдела транспортной логистики (занимается развозом)
			user.setEnablet(true);
			user.setIsDriver(false);
			user.setStatus("0");
			role = roleDAO.getRole(15);
			rolest.add(role);
			user.setRoles(rolest);
			user.setCompanyName("Доброном");
			if (user.getPassword() != null) {				
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			}
			break;
		case 16: //Роль для наблюдателя за всей системой без возможности внесение каких либо данных
			user.setEnablet(true);
			user.setIsDriver(false);
			user.setStatus("0");
			role = roleDAO.getRole(16);
			rolest.add(role);
			user.setRoles(rolest);
			user.setCompanyName("Доброном");
			if (user.getPassword() != null) {				
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			}
			break;
		default:
			break;
		}
		if(user.getRoles() == null || user.getRoles().isEmpty()) {
			EntityManager em = sessionFactory.createEntityManager();
			Set<Role> roles = em.find(User.class, user.getIdUser()).getRoles();
			em.close();		
			user.setRoles(roles);	
		}
		if (!user.isEnablet()) {
			user.setEnablet(false);
		}else {
			user.setEnablet(true);	
		}
		user.setDateRegistration(Date.valueOf(LocalDate.now()));
		userDAO.saveOrUpdateUser(user);			
	}

	@Override
	@Transactional
	public User getUserById(int id) {
		return userDAO.getUserById(id);
	}

	@Override
	@Transactional
	public User getUserByLogin(String login) {
		return userDAO.getUserByLogin(login);
	}

	@Override
	@Transactional
	public void deleteUserById(int id) {
		userDAO.deleteUserById(id);
	}

	@Override
	@Transactional
	public void deleteUserByLogin(String login) {
		userDAO.deleteUserByLogin(login);
		
	}

	@Override
	public List<User> getDriverList(String companyName) {
		return userDAO.getDriverList(companyName);
	}

	@Override
	public User getUserByDriverCard(String num) {
		return userDAO.getUserByNumDriverCard(num);
	}

	@Override
	public List<User> getCarrierList() {
		return userDAO.getCarrierList();
	}

	@Override
	public List<User> getDesableCarrierList() {
		return userDAO.getDesableCarrierList();
	}

	@Override
	public void echo() {
		System.out.println("UserService -- echo");		
	}

	@Override
	public List<User> getUserByYNP(String YNP) {
		return userDAO.getUserByYNP(YNP);
	}
	
	private String getCompanyName() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();	
		String companyName = userDAO.getUserByLogin(name).getCompanyName();
		return companyName;
		
	}
	private User getThisUser() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();	
		return userDAO.getUserByLogin(name);
	}

	@Override
	public Integer getCountUserInDB() {		
		return userDAO.getCountUserInDB();
	}

	@Override
	public List<User> getCarrierListV2() {
		return userDAO.getCarrierListV2();
	}

	@Override
	public int updateUserInBaseDocuments(int idUser, String text) {
		return userDAO.updateUserInBaseDocuments(idUser, text);
	}

	@Override
	public User saveNewDriver(User user) {
		return userDAO.saveNewDriver(user);
	}

	@Override
	public List<User> getEmployeesList() {
		return userDAO.getEmployeesList();
	}

}
