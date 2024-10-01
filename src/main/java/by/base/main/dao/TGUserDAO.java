package by.base.main.dao;

import java.util.List;

import by.base.main.model.TGUser;


public interface TGUserDAO {
	
	List<TGUser> getUserList();
	
	Integer saveOrUpdateUser(TGUser tgUser);

	TGUser getTGUserById(int id);
	
	TGUser getTGUserByChatId(long chatId);

}
