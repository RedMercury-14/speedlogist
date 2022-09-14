package by.base.main.dao;

import java.util.List;

import by.base.main.model.Message;

public interface MessageDAO {
	
	List<Message> getMEssageList();
	
	void saveOrUpdateMessage(Message message);
	
	void singleSaveMessage(Message message);
	
	Message getMessageById(Integer id);
	
	List<Message> getListMessageByFromUser(String login);
	
	List<Message> getListMessageByToUser(String login);
	
	List<Message> getListMessageByIdRoute(String idRoute);
	
	List<Message> getListMessageByStatus(String status);
	
	List<Message> getListMessageByCompanyName(String companyName);
	
	List<Message> getListMessageByComment(String comment);
	
	void deleteMessageById(Integer id);

}
