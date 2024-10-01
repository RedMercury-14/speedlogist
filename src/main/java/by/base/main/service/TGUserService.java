package by.base.main.service;

import java.util.List;

import by.base.main.model.TGUser;

public interface TGUserService {
	
	List<TGUser> getTGUserkList();
	
	Integer saveOrUpdateTGUser(TGUser tgUser);

	TGUser getTGUserById(int id);
	
	TGUser getTGUserByChatId(long chatId);

}
