package by.base.main.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.DAOException;
import by.base.main.dao.TGTruckDAO;
import by.base.main.dao.TGUserDAO;
import by.base.main.model.TGTruck;
import by.base.main.model.TGUser;
import by.base.main.model.User;
import by.base.main.service.TGTruckService;
import by.base.main.service.TGUserService;

@Service
public class TGUserServiceImpl implements TGUserService{
	
	@Autowired
	private TGUserDAO tgUserDAO;
	
	@Autowired
	private TGTruckService tgTruckService;

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
		TGUser tgUser = tgUserDAO.getTGUserByChatId(chatId);		
		Map<String, TGTruck> map = tgTruckService.getTGTruckByChatIdUser(chatId);
		if(map != null && tgUser != null) {
			tgUser.setTrucksForBot(map);
		}
		return tgUser;
	}

	@Override
	public TGUser getTGUserByMainUser(User user) {
		return tgUserDAO.getTGUserByMainUser(user);
	}

	@Override
	public TGUser getTGUserByIdUser(Integer idUser) {
		return tgUserDAO.getTGUserByIdUser(idUser);
	}

	@Override
	public TGUser getTGUserByTelephone(String telephone) throws DAOException {
		return tgUserDAO.getTGUserByTelephone(telephone);
	}

	


}
