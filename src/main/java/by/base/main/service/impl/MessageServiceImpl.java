package by.base.main.service.impl;

import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.MessageDAO;
import by.base.main.model.Message;
import by.base.main.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService{
	
	@Autowired
	private MessageDAO messageDAO;

	@Transactional
	@Override
	public List<Message> getMEssageList() {
		return messageDAO.getMEssageList();
	}

	@Transactional
	@Override
	public void saveOrUpdateMessage(Message message) {
		messageDAO.saveOrUpdateMessage(message);
		
	}

	@Transactional
	@Override
	public Message getMessageById(Integer id) {
		return messageDAO.getMessageById(id);
	}

	@Transactional
	@Override
	public List<Message> getListMessageByFromUser(String login) {
		return messageDAO.getListMessageByFromUser(login);
	}

	@Transactional
	@Override
	public List<Message> getListMessageByToUser(String login) {
		return messageDAO.getListMessageByToUser(login);
	}

	@Transactional
	@Override
	public List<Message> getListMessageByStatus(String status) {
		return messageDAO.getListMessageByStatus(status);
	}

	@Transactional
	@Override
	public List<Message> getListMessageByCompanyName(String companyName) {
		return messageDAO.getListMessageByCompanyName(companyName);
	}

	@Transactional
	@Override
	public void deleteMessageById(Integer id) {
		messageDAO.deleteMessageById(id);		
	}

	@Transactional
	@Override
	public List<Message> getListMessageByIdRoute(String idRoute) {
		return messageDAO.getListMessageByIdRoute(idRoute);
	}

	@Transactional
	@Override
	public void singleSaveMessage(Message message) {
		messageDAO.singleSaveMessage(message);
		
	}

	@Transactional
	@Override
	public List<Message> getListMessageByComment(String comment) {
		return messageDAO.getListMessageByComment(comment);
	}

	@Transactional
	@Override
	public List<Message> getListMessageByPeriod(Date start, Date finish) {
		return messageDAO.getListMessageByPeriod(start, finish);
	}

	@Transactional
	@Override
	public int updateDate(Integer id, Date date) {
		return messageDAO.updateDate(id, date);
	}

	@Transactional
	@Override
	public List<Message> getListMessageByComment5Days(String comment) {
		return messageDAO.getListMessageByComment5Days(comment);
	}

	@Transactional
	@Override
	public List<Message> getListMessageByYNP5Days(String ynp) {
		return messageDAO.getListMessageByYNP5Days(ynp);
	}

}
