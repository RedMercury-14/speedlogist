package by.base.main.service.impl;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.MessageDAO;
import by.base.main.model.Message;
import by.base.main.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService{
	
	@Autowired
	MessageDAO messageDAO;

	@Override
	public List<Message> getMEssageList() {
		return messageDAO.getMEssageList();
	}

	@Override
	public void saveOrUpdateMessage(Message message) {
		messageDAO.saveOrUpdateMessage(message);
		
	}

	@Override
	public Message getMessageById(Integer id) {
		return messageDAO.getMessageById(id);
	}

	@Override
	public List<Message> getListMessageByFromUser(String login) {
		return messageDAO.getListMessageByFromUser(login);
	}

	@Override
	public List<Message> getListMessageByToUser(String login) {
		return messageDAO.getListMessageByToUser(login);
	}

	@Override
	public List<Message> getListMessageByStatus(String status) {
		return messageDAO.getListMessageByStatus(status);
	}

	@Override
	public List<Message> getListMessageByCompanyName(String companyName) {
		return messageDAO.getListMessageByCompanyName(companyName);
	}

	@Override
	public void deleteMessageById(Integer id) {
		messageDAO.deleteMessageById(id);		
	}

	@Override
	public List<Message> getListMessageByIdRoute(String idRoute) {
		return messageDAO.getListMessageByIdRoute(idRoute);
	}

	@Override
	public void singleSaveMessage(Message message) {
		messageDAO.singleSaveMessage(message);
		
	}

	@Override
	public List<Message> getListMessageByComment(String comment) {
		return messageDAO.getListMessageByComment(comment);
	}

	@Override
	public List<Message> getListMessageByPeriod(Date start, Date finish) {
		return messageDAO.getListMessageByPeriod(start, finish);
	}

	@Override
	public int updateDate(Integer id, Date date) {
		return messageDAO.updateDate(id, date);
	}

}
