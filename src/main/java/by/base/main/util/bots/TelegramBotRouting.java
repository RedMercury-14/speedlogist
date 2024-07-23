package by.base.main.util.bots;

import java.io.File;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import by.base.main.controller.MainController;
import by.base.main.model.User;

@Component
public class TelegramBotRouting extends TelegramLongPollingBot{
	
	public boolean isRunning = false;
	
	@Autowired
	KeyboardMaker keyboardMaker;
	
	@Autowired
	MainController mainController;
	
	private long idAdmin = 907699213;
	private Map<Long, User> users = new HashMap<Long, User>(); // юзеры, которые заявляют авто
	private List<Long> idAllUsers = new ArrayList<Long>(); // все подключенные к боту юзеры
	private Map<Long, String> idAdmins = new HashMap<Long, String>(); // админы
	
	@Override
	public void onUpdateReceived(Update update) {
		
		if(update.hasMessage() && update.getMessage().hasContact()){
			System.out.println(update.getMessage().getText());
			long chatId = update.getMessage().getChatId();
			User user = new User();
			user.setLogin(chatId+"");
			user.setTelephone(update.getMessage().getContact().getPhoneNumber());
			user.setStatus("/login");
			users.put(chatId, user);
			serializableUsers();
			sendMessage(chatId, "Номер принят. Напишите название фирмы");
		}
		
		if(update.hasCallbackQuery()){
			System.out.println("CallbackData -> " + update.getCallbackQuery().getData());
			System.out.println("CallbackData.getId -> " + update.getCallbackQuery().getId());
			System.out.println("CallbackData.getMessage().getChatId() -> " + update.getCallbackQuery().getMessage().getChatId());
			System.err.println("CallbackDataToString -> " + update.getCallbackQuery());
			
		}
		
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();   
            long chatId = update.getMessage().getChatId();
            if(users.containsKey(chatId) && users.get(chatId).getStatus() != null) {
            	messageText = users.get(chatId).getStatus()+"~"+messageText;
            }
//            System.out.println(messageText + " idChat = " + chatId);
            String command = messageText.split("~")[0];
            if(!idAllUsers.contains(chatId)) {
            	idAllUsers.add(chatId);                    	
            	serializableIdAllUsers();
            }
            System.err.println(command);
            switch (command.toLowerCase()){
                case "/start": 
                	SendMessage sendKeyboard = new SendMessage();
                	sendKeyboard.setText("Приветики пистолетики");
                	sendKeyboard.setChatId(chatId);
					if(users.containsKey(chatId)) {
						sendKeyboard.setReplyMarkup(keyboardMaker.getMainKeyboard()); // клава для юзеров
					}else {						
	                	sendKeyboard.setReplyMarkup(keyboardMaker.getStartKeyboard()); // клава со входом	
					}                	
            		try {
						execute(sendKeyboard);
					} catch (TelegramApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    break;
                case "/login": 
                	User user = users.get(chatId);
                	user.setCompanyName(messageText);
                	user.setStatus(null);
                	users.put(chatId, user);
                	serializableUsers();
                	SendMessage sendKeyboard2 = new SendMessage();
                	sendKeyboard2.setText("Название фирмы принято. Теперь бот Вас запомнил");
                	sendKeyboard2.setChatId(chatId);
                	sendKeyboard2.setReplyMarkup(keyboardMaker.getMainKeyboard()); // клава для юзеров               	
            		try {
						execute(sendKeyboard2);
					} catch (TelegramApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    break;
                case "заявить машину на завтра": 
                	User userForTruck = users.get(chatId);
                	userForTruck.setStatus("/numtruck");
                	users.put(chatId, userForTruck);
                	sendMessage(chatId, "Введите номер авто: ");
                    break;
                    //остановился тут
                case "/numtruck": 
                	User userForTruck2 = users.get(chatId);
                	userForTruck2.setStatus(null);
                	users.put(chatId, userForTruck2);
                	//создаём и записываем авто
                	String numTruck = messageText.split("~")[1];
                	SendMessage message = new SendMessage();
                    message.setChatId(chatId);                    
                    message.setText("Номер " + numTruck +" принят. \nВведите сколько паллет вмещает авто");
            		message.setReplyMarkup(keyboardMaker.getPallMessageKeyboard(numTruck));
            		try {
						execute(message);
					} catch (TelegramApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    break;
                    
                case "/mail":
                	if(idAdmins.get(chatId) != null) {
                		sendMessageAll(messageText.split("~")[1]);
                	}else {
                		sendMessage(chatId, "Недостаточно прав");    
                	}                	
                	break;
                case "/clearusers":
                	if(chatId == idAdmin) users.clear();
                	break;
                case "/addadmin":
                case "/admin":
                case "/addAdmin":
                	if(chatId == idAdmin) {
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
                	if(chatId == idAdmin) {
                		Long id = Long.parseLong(messageText.split("~")[1]);               		
                		String name = idAdmins.remove(id);// /delAdmin~42523532523
                    	sendMessage(chatId, "Пользователь с именем " + name + " и id " + id + " удалён из списка админов");                		
                    	serializableIdAdmins();
                	}else {
                		sendMessage(chatId, "Недостаточно прав");    
                	}                	
                	break;
                case "/help":
                	if(chatId == idAdmin || idAdmins.containsKey(chatId)) {
                    	sendMessage(chatId, "/mail~text\n/addAdmin~42523532523~Олег Пипченко\n/delAdmin~42523532523\n/id\n/admins\n/stop\n/stat - статистика"); 
                	}else {
                		sendMessage(chatId, "Недостаточно прав");    
                	}                	
                	break;
                case "/id":
            		sendMessage(chatId, chatId+"");            
                	break;
                case "/admins":
                	if(chatId == idAdmin || idAdmins.containsKey(chatId)) {
                		if(!idAdmins.isEmpty()) {
                    		idAdmins.forEach((k,v) -> sendMessage(chatId, k+" - " + v));
                    	}else {
                    		sendMessage(chatId, "Список пуст");
						}	
                	}else {
                		sendMessage(chatId, "Недостаточно прав"); 
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
		return "DobronomRouting";
	}
	
	@Override
	public String getBotToken() {
		return "6742392768:AAEG5ZNDXmEdpxfb0bDkiBrhBHwd6LuEOgw";
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
		sendMessage.setReplyMarkup(null);
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
        String answer = "Добро пожаловать " + name + "Новый бот!";
        sendMessageForStart(chatId, answer);
        
    }
	
	private static final String usersDir = "resources/others/telegramm/route/Users.ser";
	
	/**
	 * Сериализация users
	 */
	private void serializableUsers() {
		//проверка директории
        File fileTest= new File(mainController.path + "resources/others/telegramm/");
        if (!fileTest.exists()) {
            fileTest.mkdir();
            File fileTest2= new File(mainController.path + "resources/others/telegramm/route/");
	        if (!fileTest2.exists()) {
	            fileTest2.mkdir();
	        }
        }
		try {
			FileOutputStream fos = new FileOutputStream(mainController.path + usersDir);
                  ObjectOutputStream oos = new ObjectOutputStream(fos);
                  oos.writeObject(this.users);
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
			FileInputStream fis = new FileInputStream(mainController.path + usersDir);
		         ObjectInputStream ois = new ObjectInputStream(fis);
		         this.users =  (Map<Long, User>) ois.readObject();
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
