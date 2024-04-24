package by.base.main.util.bots;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import by.base.main.controller.MainController;

@Component
public class TelegramBot extends TelegramLongPollingBot{
	
	public boolean isRunning = false;
	
	@Autowired
	KeyboardMaker keyboardMaker;
	
	@Autowired
	MainController mainController;
	
	private long idAdmin = 907699213;
	private List<Long> idUsers = new ArrayList<Long>(); // все подписавшиеся к боту юзеты
	private List<Long> idAllUsers = new ArrayList<Long>(); // все подключенные к боту юзеры
	private Map<Long, String> idAdmins = new HashMap<Long, String>(); // админы
	
	@Override
	public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();            
            long chatId = update.getMessage().getChatId();
//            System.out.println(messageText + " idChat = " + chatId);
            String command = messageText.split("~")[0];
            if(!idAllUsers.contains(chatId)) {
            	idAllUsers.add(chatId);                    	
            	serializableIdAllUsers();
            }
            switch (command.toLowerCase()){
                case "/start":                	
                	if(!idUsers.contains(chatId)) {
                		idUsers.add(chatId);
                		serializableIdUsers();
                	}
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/mail":
                	if(idAdmins.get(chatId) != null) {
                		sendMessageAll(messageText.split("~")[1]);
                	}else {
                		sendMessage(chatId, "Недостаточно прав");    
                	}                	
                	break;
                case "/addadmin":
                case "/admin":
                case "/addAdmin":
                	if(chatId == 907699213) {
                		Long id = Long.parseLong(messageText.split("~")[1]);
                		String name = messageText.split("~")[2];                		
                		idAdmins.put(id, name);// /addAdmin~42523532523~Олег Пипченко
                		serializableIdAdmins();
                		sendMessage(chatId, "Пользователь с именем " + messageText.split("~")[2] + " и id " + messageText.split("~")[1] + " добавлен в список админов"); 
                	}else {
                		sendMessage(chatId, "Недостаточно прав");    
                	}                	
                	break;
                case "/delAdmin":
                	if(chatId == 907699213) {
                		Long id = Long.parseLong(messageText.split("~")[1]);               		
                		String name = idAdmins.remove(id);// /delAdmin~42523532523
                    	sendMessage(chatId, "Пользователь с именем " + name + " и id " + id + " удалён из списка админов");                		
                    	serializableIdAdmins();
                	}else {
                		sendMessage(chatId, "Недостаточно прав");    
                	}                	
                	break;
                case "/help":
                	if(chatId == 907699213 || idAdmins.containsKey(chatId)) {
                    	sendMessage(chatId, "/mail~text\n/addAdmin~42523532523~Олег Пипченко\n/delAdmin~42523532523\n/id\n/admins\n/stop\n/stat - статистика"); 
                	}else {
                		sendMessage(chatId, "Недостаточно прав");    
                	}                	
                	break;
                case "/id":
            		sendMessage(chatId, chatId+"");            
                	break;
                case "/admins":
                	if(chatId == 907699213 || idAdmins.containsKey(chatId)) {
                		if(!idAdmins.isEmpty()) {
                    		idAdmins.forEach((k,v) -> sendMessage(chatId, k+" - " + v));
                    	}else {
                    		sendMessage(chatId, "Список пуст");
						}	
                	}else {
                		sendMessage(chatId, "Недостаточно прав"); 
					}    
                	break;
                case "включить рассылку":
                	if(!idUsers.contains(chatId)) {
                		idUsers.add(chatId);
                		serializableIdUsers();
                		SendMessage sendMessage = new SendMessage(chatId+"", "Рассылка включена");
                		sendMessage.setReplyMarkup(keyboardMaker.getSecondMenuKeyboard());
                		try {
							execute(sendMessage);
						} catch (TelegramApiException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}else {
                		SendMessage sendMessage = new SendMessage(chatId+"", "Рассылка была включена");
                		sendMessage.setReplyMarkup(keyboardMaker.getSecondMenuKeyboard());
                		try {
							execute(sendMessage);
						} catch (TelegramApiException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                	break;
                case "выключить рассылку":
                	if(idUsers.contains(chatId)) {
                		idUsers.remove(chatId);
                		serializableIdUsers();
                		SendMessage sendMessage = new SendMessage(chatId+"", "Рассылка выключена");
                		sendMessage.setReplyMarkup(keyboardMaker.getMainMenuKeyboard());
                		try {
							execute(sendMessage);
						} catch (TelegramApiException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}else {
                		SendMessage sendMessage = new SendMessage(chatId+"", "Рассылка не была включена");
                		sendMessage.setReplyMarkup(keyboardMaker.getMainMenuKeyboard());
                		try {
							execute(sendMessage);
						} catch (TelegramApiException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                	break;
                case "инфо":
                	sendMessage(chatId,"Это официальный телеграмм-бот платформы SpeedLogist. Сюда приходят, в режиме реального времени, все сообщения о готовности маршрутов к торгам."
                			+ "\nДля того, чтобы получать уведомления, достаточно подписаться на бота.\nЧтобы прекратить получать уведомления, нажмите на кнопку \"Выключить рассылку\" или выйдете из бота");
                	break;
                case "/stop":
                	if(chatId == 907699213) {
                		super.onClosing();
                	}                	
                	break;
                case "/stat":
                	if(idAdmins.containsKey(chatId) || chatId == 907699213) {
                		sendMessage(chatId, "Колличество подключенных к боту пользователей: " + idAllUsers.size());
                		sendMessage(chatId, "Колличество подписанных к рассылке пользователей: " + idUsers.size());
                	}                	
                	break;
                case "пошел на хуй":
                case "пошел нахуй":
                	sendMessage(chatId, "Как оригинально! \nБот внёс Вас в некультурный список!");              	
                	break;
                case "пизда":
                	sendMessage(chatId, "Как оригинально! \nБот внёс Вас в некультурный список!");              	
                	break;
                case "хуй":
                	sendMessage(chatId, "Заборов мало?! \nБот внёс Вас в некультурный список!");              	
                	break;
                case "бот говно":
                case "Бот говно":
                	sendMessage(chatId, "╭∩╮ (`-`) ╭∩╮ \n\nБот внёс Вас в некультурный список!");              	
                	break;
                case "соси":
                	sendMessage(chatId, "Что сосать?");              	
                	break;
                case "соси хуй":
                	sendMessage(chatId, "Как неожиданно! \nБот внёс Вас в некультурный список!");              	
                	break;
                case "лох":
                	sendMessage(chatId, "Пароль принят! \nОтправьте фото карты с двух сторон для перевода денег!");              	
                	break;
                case "не работает":
                case "бот не работает":
                case "Бот не работает":
                case "Не работает":
                	sendMessage(chatId, "Всё работает! Перезагрузите телефон.");              	
                	break;
                case "мудак":
                	sendMessage(chatId, "Правильно писать чудак.");              	
                	break;
                case "педик":
                case "пидор":
                case "пидр":
                	sendMessage(chatId, "Новый логин для входа в SpeedLogist принят!");              	
                	sendMessage(chatId, "Бот внёс Вас в некультурный список!");             	
                	break;
                default:
                    sendMessage(chatId,"Неизвестная команда");
            }
        }
	}

	@Override
	public String getBotUsername() {
		return "LogistBot";
	}
	
	@Override
	public String getBotToken() {
		return "6382807113:AAEIAY4PRRzwQcati6z7d85geHYB8TpFHVY";
	}
	
	@Override
	public void onRegister() {
		if(!isRunning) {
			super.onRegister();
			isRunning = true;
		}else {
			System.err.println("Бот уже запущен");
		}
	}
	
	
	
	public void sendMessage(long chatId, String textToSend) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setText(textToSend);
		sendMessage.setChatId(chatId);
		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			System.err.println("execute не сработал");
			e.printStackTrace();
			
		}
	}
	
	public void sendMessageForStart(long chatId, String textToSend) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setText(textToSend);
		sendMessage.setChatId(chatId);
//		sendMessage.setReplyMarkup(keyboardMaker.getMainMenuKeyboard());
		sendMessage.setReplyMarkup(keyboardMaker.getSecondMenuKeyboard());
		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			System.err.println("execute не сработал");
			e.printStackTrace();
			
		}
	}
	/**
	 * Отправляет сообщение ТГ боту всем кто согласился
	 * @param chatId
	 * @param textToSend
	 */
	public void sendMessageHasSubscription(String textToSend) {
		for (Long id : idUsers) {
			SendMessage sendMessage = new SendMessage(id.toString(),textToSend);
			try {
				execute(sendMessage);
			} catch (TelegramApiException e) {
				System.err.println("Сработала ошибка в методе: TelegramBot.sendMessageHasSubscription. IdUser = " + id);
//				e.printStackTrace();				
			}
		}
	}
	
	/**
	 * Отправляет сообщение ТГ боту всем кто вошел
	 * @param chatId
	 * @param textToSend
	 */
	public void sendMessageAll(String textToSend) {
		for (Long id : idAllUsers) {
			SendMessage sendMessage = new SendMessage(id.toString(),textToSend);
			try {
				execute(sendMessage);
			} catch (TelegramApiException e) {
				e.printStackTrace();
				
			}
		}
	}
	
	private void startCommandReceived(long chatId, String name) {
        String answer = "Добро пожаловать " +name+ ".\nЭто официальный телеграмм-бот платформы SpeedLogist. Сюда приходят, в режиме реального времени, все сообщения о готовности маршрутов к торгам."
        		+ "\nДля того, чтобы получать уведомления, достаточно подписаться на бота.\nЧтобы прекратить получать уведомления, нажмите на кнопку \"Выключить рассылку\" или выйдете из бота";
        sendMessageForStart(chatId, answer);
        
    }
	
	/**
	 * Сериализация листа idUsers
	 */
	private void serializableIdUsers() {
		try {
			FileOutputStream fos = new FileOutputStream(mainController.path + "resources/others/telegrammIdUser.ser");
                  ObjectOutputStream oos = new ObjectOutputStream(fos);
                  oos.writeObject(this.idUsers);
                  oos.close();
                  fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Сериализация листа idAllUsers
	 */
	private void serializableIdAllUsers() {
		try {
			FileOutputStream fos = new FileOutputStream(mainController.path + "resources/others/telegrammIdAllUser.ser");
                  ObjectOutputStream oos = new ObjectOutputStream(fos);
                  oos.writeObject(this.idAllUsers);
                  oos.close();
                  fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Сериализация листа idAdmins
	 */
	private void serializableIdAdmins() {
		try {
			FileOutputStream fos = new FileOutputStream(mainController.path + "resources/others/telegrammIdAdmins.ser");
                  ObjectOutputStream oos = new ObjectOutputStream(fos);
                  oos.writeObject(this.idAdmins);
                  oos.close();
                  fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Десериализация листа idAllUsers
	 */
	public void deSerializableIdAllUsers() {
		try {
			FileInputStream fis = new FileInputStream(mainController.path + "resources/others/telegrammIdAllUser.ser");
		         ObjectInputStream ois = new ObjectInputStream(fis);
		         this.idAllUsers = (ArrayList) ois.readObject();
		         ois.close();
		         fis.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * Десериализация листа idUsers
	 */
	public void deSerializableIdUsers() {
		try {
			FileInputStream fis = new FileInputStream(mainController.path + "resources/others/telegrammIdUser.ser");
		         ObjectInputStream ois = new ObjectInputStream(fis);
		         this.idUsers = (ArrayList) ois.readObject();
		         ois.close();
		         fis.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * Десериализация листа idAdmins
	 */
	public void deSerializableIdAdmins() {
		try {
			FileInputStream fis = new FileInputStream(mainController.path + "resources/others/telegrammIdAdmins.ser");
		         ObjectInputStream ois = new ObjectInputStream(fis);
		         this.idAdmins = (Map<Long, String>) ois.readObject();
		         ois.close();
		         fis.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
	}
}
