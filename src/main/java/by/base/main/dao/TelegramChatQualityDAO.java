package by.base.main.dao;

import java.util.List;

import by.base.main.model.TelegramChatQuality;

public interface TelegramChatQualityDAO {
	
	List<TelegramChatQuality> getChatIdList();
	
	boolean existsById(Long chatId);
	
	void save(TelegramChatQuality telegramChatQuality);
	
	void deleteByChatId (Long chatId);

}
