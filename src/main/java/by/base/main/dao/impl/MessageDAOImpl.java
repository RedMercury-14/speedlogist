package by.base.main.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import by.base.main.dao.MessageDAO;
import by.base.main.model.Message;
@Repository
public class MessageDAOImpl implements MessageDAO{
	
	@Autowired
	private SessionFactory sessionFactory;

	private static final String queryGetList = "from Message order by idMessage";
	@Override
	@Transactional
	public List<Message> getMEssageList() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Message> theObject = currentSession.createQuery(queryGetList, Message.class);
		List<Message> objects = theObject.getResultList();
		return objects;
	}

	@Override
	@Transactional
	public void saveOrUpdateMessage(Message message) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.saveOrUpdate(message);
	}

	@Override
	@Transactional
	public Message getMessageById(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Message message = currentSession.get(Message.class, id);
		currentSession.flush();
		return message;
	}

	private static final String queryGetObjToUser = "from Message where toUser=:login";
	@Override
	@Transactional
	public List<Message> getListMessageByToUser(String login) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Message> theObject = currentSession.createQuery(queryGetObjToUser, Message.class);
		theObject.setParameter("login", login);
		List<Message> objects = theObject.getResultList();	
		return objects;
	}

	private static final String queryGetObjByStatus = "from Message where status=:login";
	@Override
	@Transactional
	public List<Message> getListMessageByStatus(String status) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Message> theObject = currentSession.createQuery(queryGetObjByStatus, Message.class);
		theObject.setParameter("login", status);
		List<Message> objects = theObject.getResultList();	
		return objects;
	}

	private static final String queryGetObjByCompanyName = "from Message where status=:login";
	@Override
	@Transactional
	public List<Message> getListMessageByCompanyName(String companyName) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Message> theObject = currentSession.createQuery(queryGetObjByCompanyName, Message.class);
		theObject.setParameter("login", companyName);
		List<Message> objects = theObject.getResultList();	
		return objects;
	}
	
	private static final String queryGetListObj = "from Message where fromUser=:login";
	@Override
	@Transactional
	public List<Message> getListMessageByFromUser(String login) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Message> theObject = currentSession.createQuery(queryGetListObj, Message.class);
		theObject.setParameter("login", login);
		List<Message> objects = theObject.getResultList();	
		return objects;
	}

	private static final String queryDeleteById = "delete from Message where idMessage=:id";
	@Override
	@Transactional
	public void deleteMessageById(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeleteById);
		theQuery.setParameter("id", id);
		theQuery.executeUpdate();
		
	}

	private static final String queryGetListObjIdRoute = "from Message where idRoute=:login";
	@Override
	@Transactional
	public List<Message> getListMessageByIdRoute(String idRoute) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Message> theObject = currentSession.createQuery(queryGetListObjIdRoute, Message.class);
		theObject.setParameter("login", idRoute);
		List<Message> objects = theObject.getResultList();	
		return objects;
	}

	private static final String queryGetObject = "select idRoute from Message where idRoute=:idRoute AND text=:disposition";
	@Override
	@Transactional
	public void singleSaveMessage(Message message) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query query = currentSession.createQuery(queryGetObject);
		query.setParameter("idRoute", message.getIdRoute()); 
		query.setParameter("disposition",message.getText());
		if (query.list().isEmpty()) {
			currentSession.saveOrUpdate(message);
		}  
		
	}

}
