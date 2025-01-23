package by.base.main.dao;

import java.sql.Date;
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
	
	List<Message> getListMessageByPeriod(Date start,  Date finish);
	
	int updateDate(Integer id, Date date);
	
	void deleteMessageById(Integer id);
	
	/**
	 * Отдаёт сообщения за последние 5 дней, по таргетному комментарию (который равен логину)
	 * <br> а так же если toUser = логину или international	 * 
	 * @param comment
	 * @return
	 */
	List<Message> getListMessageByComment5Days(String comment);
	
	/**
	 * Отдаёт сообщения за последние 5 дней, по УНП
	 * <br>  или international	 * 
	 * @param comment
	 * @return
	 */
	List<Message> getListMessageByYNP5Days(String comment);

}
