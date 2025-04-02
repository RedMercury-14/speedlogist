package by.base.main.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.TelegramChatQualityDAO;
import by.base.main.model.TelegramChatQuality;

@Repository
public class TelegramChatQualityDAOImpl implements TelegramChatQualityDAO{

	@Autowired
	private SessionFactory sessionFactory;
	
	private static final String queryGetList = "from TelegramChatQuality order by chatId";
	@Override
	public List<TelegramChatQuality> getChatIdList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<TelegramChatQuality> theObject = currentSession.createQuery(queryGetList, TelegramChatQuality.class);
		List <TelegramChatQuality> objects = theObject.getResultList();
		if(objects.isEmpty()) {
			return null;		
		}else {
			return objects;
		}
	}
	
	private static final String queryGetListObjByUser = "from TelegramChatQuality where chatId=:chatId";
	@Override
	public boolean existsById(int chatId) {
		Session currentSession = sessionFactory.getCurrentSession();		
		Query<TelegramChatQuality> theObject = currentSession.createQuery(queryGetListObjByUser, TelegramChatQuality.class);
		theObject.setParameter("chatId", chatId);		
		List<TelegramChatQuality> trucks = theObject.getResultList();			
		if(trucks.isEmpty()) {
			return false;
		}else {
			return true;			
		}
	}
	@Override
	public void save(TelegramChatQuality telegramChatQuality) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(telegramChatQuality);		
	}
	
	

}
