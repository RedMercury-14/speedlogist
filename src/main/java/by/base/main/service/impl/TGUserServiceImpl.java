package by.base.main.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.TGTruckDAO;
import by.base.main.dao.TGUserDAO;
import by.base.main.model.TGTruck;
import by.base.main.model.TGUser;
import by.base.main.service.TGUserService;

@Service
public class TGUserServiceImpl implements TGUserService{
	
	@Autowired
	private TGUserDAO tgUserDAO;

	@Override
	public List<TGUser> getTGUserkList() {
		return tgUserDAO.getUserList();
	}

	@Override
	public Integer saveOrUpdateTGUser(TGUser tgUser) {
		return tgUserDAO.saveOrUpdateUser(tgUser);
	}

	@Override
	public TGUser getTGUserById(int id) {
		return tgUserDAO.getTGUserById(id);
	}

	@Override
	public TGUser getTGUserByChatId(long chatId) {
		return tgUserDAO.getTGUserByChatId(chatId);
	}


}
