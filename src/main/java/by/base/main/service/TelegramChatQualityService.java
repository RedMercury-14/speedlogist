package by.base.main.service;

import java.util.List;

import by.base.main.model.TelegramChatQuality;

public interface TelegramChatQualityService {
	
	List<TelegramChatQuality> getChatIdList();
	
	/**
	 * Возвращает лист с id чатов, для отправки сообщения
	 * @return
	 */
	List<Long> getChatIdLongList();
	
	boolean existsById(Long chatId);
	
	void save(TelegramChatQuality telegramChatQuality);
	
	void deleteByChatId (Long chatId);

}
