package by.base.main.util.bots;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class BotInitializer {

//	@Autowired
	private TelegramBot bot;
	private TelegramBotRouting botRouting;
	
	public BotInitializer(TelegramBot bot) {
		this.bot = bot;
	}
	public BotInitializer(TelegramBotRouting botRouting) {
		this.botRouting = botRouting;
	}

	@EventListener({ ContextRefreshedEvent.class })
	public void init() {
		
		try {			
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);			
			telegramBotsApi.registerBot((LongPollingBot) bot);
			bot.deSerializableIdAllUsers();
			bot.deSerializableIdUsers();
			bot.deSerializableIdAdmins();
			System.out.println("TelegramBot запущен");			
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
	
	@EventListener({ ContextRefreshedEvent.class })
	public void initRoutingBot() {
		
		try {			
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);			
			telegramBotsApi.registerBot((LongPollingBot) botRouting);
			System.out.println("TelegramBotRouting запущен");			
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	
}
