package by.base.main.dao;

import java.util.List;

import by.base.main.model.TelegramChatQuality;

public interface TelegramChatQualityDAO {
	
	List<TelegramChatQuality> getChatIdList();
	
	boolean existsById(int chatId);
	
	void save(TelegramChatQuality telegramChatQuality);
	
	void deleteByChatId (int chatId);

}
