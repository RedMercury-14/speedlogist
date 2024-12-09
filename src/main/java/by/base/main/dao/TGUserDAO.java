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
	 * Возвращает юзера по номеру телефона <b>без + </b>
	 * @param user
	 * @return
	 * @author DIma Hrushevsky
	 */
	TGUser getTGUserByTelephone (String telephone);
}
