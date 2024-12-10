package by.base.main.dao;

import java.util.List;

import by.base.main.model.TGUser;
import by.base.main.model.User;


public interface TGUserDAO {
	
	List<TGUser> getUserList();
	
	Integer saveOrUpdateUser(TGUser tgUser);

	TGUser getTGUserById(int id);
	
	TGUser getTGUserByChatId(long chatId);
	
	/**
	 * Консолидированный метод для поиска и выдачи ТГюзера по обычному юзеру
	 * <br> 1. сначала проверяет по номеру телефона
	 * 
	 * @param user
	 * @return
	 * @author DIma Hrushevsky
	 */
	TGUser getTGUserByMainUser (User user);

	/**
	 * Возвращает юзера по id_user
	 * @param user
	 * @return
	 * @author DIma Hrushevsky
	 */
	TGUser getTGUserByIdUser(Integer idUser);
	
	TGUser getTGUserByTelephone(String telephone) throws DAOException;
}
