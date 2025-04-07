package by.base.main.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.TelegramChatQualityDAO;
import by.base.main.model.TelegramChatQuality;
import by.base.main.service.TelegramChatQualityService;

@Service
public class TelegramChatQualityServiceImpl implements TelegramChatQualityService{
	
	@Autowired
	private TelegramChatQualityDAO telegramChatQualityDAO;

	@Transactional
	@Override
	public List<TelegramChatQuality> getChatIdList() {
		return telegramChatQualityDAO.getChatIdList();
	}

	@Transactional
	@Override
	public boolean existsById(int chatId) {
		return telegramChatQualityDAO.existsById(chatId);
	}

	@Transactional
	@Override
	public void save(TelegramChatQuality telegramChatQuality) {
		telegramChatQualityDAO.save(telegramChatQuality);
	}

	@Transactional
	@Override
	public void deleteByChatId(int chatId) {
		telegramChatQualityDAO.deleteByChatId(chatId);
	}

	@Transactional
	@Override
	public List<Long> getChatIdLongList() {
		// TODO Auto-generated method stub
		return telegramChatQualityDAO.getChatIdList().stream()
                .map(s -> s.getChatId().longValue())
                .collect(Collectors.toList());
	}
	
	

}
