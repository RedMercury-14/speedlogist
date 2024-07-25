package by.base.main.util.bots;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import by.base.main.controller.MainController;
import by.base.main.model.Truck;
import by.base.main.model.User;
import io.github.dostonhamrakulov.CalendarBot;
import io.github.dostonhamrakulov.DateTimeUtil;
import io.github.dostonhamrakulov.InlineCalendarBuilder;
import io.github.dostonhamrakulov.InlineCalendarCommandUtil;
import io.github.dostonhamrakulov.LanguageEnum;

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
	
	private static final InlineCalendarBuilder inlineCalendarBuilder = new InlineCalendarBuilder(LanguageEnum.RU);
	private Map<Long, Integer> chatAndMessageIdMap = new HashMap<>();
	
	@Override
	public void onUpdateReceived(Update update) {
		
		if(update.hasMessage() && update.getMessage().hasContact()){
			long chatId = update.getMessage().getChatId();
			User user = new User();
			user.setLogin(chatId+"");
			user.setTelephone(update.getMessage().getContact().getPhoneNumber());
			user.setStatus("/login");
			users.put(chatId, user);
			serializableUsers();
			sendMessage(chatId, "Номер принят. Напишите название фирмы");
		}
		if(update.hasMessage() && update.getMessage().hasLocation()){
			long chatId = update.getMessage().getChatId();			
			sendMessage(chatId, update.getMessage().getLocation().toString());
		}
		
		if(update.hasCallbackQuery()){
			System.out.println("CallbackData -> " + update.getCallbackQuery().getData());
//			System.out.println("CallbackData.getId -> " + update.getCallbackQuery().getId());
			System.out.println("CallbackData.getMessage().getChatId() -> " + update.getCallbackQuery().getMessage().getChatId());
//			System.err.println("CallbackDataToString -> " + update.getCallbackQuery());
			
			
			long chatId = update.getCallbackQuery().getMessage().getChatId();
			String data = update.getCallbackQuery().getData();
			if(data.split("_")[0].equals("CAL")) {
				System.err.println(1111);
				Message message = update.getMessage();
		        SendMessage sendMessage = new SendMessage();
		        sendMessage.setChatId(message.getChatId());
				EditMessageText editMessageText = new EditMessageText();
		        editMessageText.setChatId(message.getChatId());
		        editMessageText.setMessageId(chatAndMessageIdMap.get(message.getChatId()));

		        if (InlineCalendarCommandUtil.isInlineCalendarClicked(update)) {
		            if (InlineCalendarCommandUtil.isCalendarIgnoreButtonClicked(update)) {
		                return;
		            }

		            if (InlineCalendarCommandUtil.isCalendarNavigationButtonClicked(update)) {
		                editMessageText.setText("Selected date: ");
		                editMessageText.setReplyMarkup(inlineCalendarBuilder.build(update));
		                executeCommand(editMessageText);

		                return;
		            }

		            LocalDate localDate = InlineCalendarCommandUtil.extractDate(update);
		            sendMessage.setText("Selected date: " + DateTimeUtil.convertToString(localDate));
		            executeCommand(sendMessage);
		        }

		        sendMessage.setText("Please, send /start command to the bot");
		        executeCommand(sendMessage);
			}
			
			User user = users.get(chatId);
			switch (user.getStatus()) {
			case "/setpall":
				String numTruck = data.split("_")[0];
				String pall = data.split("_")[1];
				Truck truck = new Truck();
				truck.setNumTruck(numTruck);
				truck.setPallCapacity(pall);
				user.putTrucksForBot(numTruck, truck);				
				user.setValidityPass(numTruck); // сюза временно записываем номер авто которое обрабатывается
				//создали машину, присвоили номер и записали сколько паллет.
				
				user.setStatus("/setweigth");
				users.put(chatId, user);
				//тут достаём старое сообщение и меняем его
				long messageId = update.getCallbackQuery().getMessage().getMessageId();
				String answer1 = "Выберите грузоподъемность авто";
		        EditMessageText newMessage = EditMessageText.builder()
		            .chatId(chatId)
		            .messageId(Math.toIntExact(messageId))
		            .text(answer1)
		            .replyMarkup(keyboardMaker.getWeigthKeyboard(pall.trim())) // сюда указываем колличество паллет, дальше делает клава
		            .build();
		        try {
		            execute(newMessage);
		        } catch (TelegramApiException e) {
		            e.printStackTrace();
		        }
				break;
			case "/settype":
				String numTruckForType = data.split("_")[0];
				String type = data.split("_")[1];
				
				Truck truckForType = user.getTrucksForBot(numTruckForType);
				truckForType.setTypeTrailer(type);
				truckForType.setDateRequisition(LocalDate.now().plusDays(1));
				
				user.putTrucksForBot(numTruckForType, truckForType);				
				user.setValidityPass(null); // сюза временно записываем номер авто которое обрабатывается но записывем null т.к. типо закончили
				//создали машину, присвоили номер и записали сколько паллет.
				
				user.setStatus("/proofTruck");
				users.put(chatId, user);
				
				long messageIdType = update.getCallbackQuery().getMessage().getMessageId();
				String dateNext = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
				EditMessageText messageProof = EditMessageText.builder()
						.chatId(chatId)
						.messageId(Math.toIntExact(messageIdType))
						.text("Заявляем машину на завтра ("+dateNext+")\n Авто: "+truckForType.getTruckForBot())
						.replyMarkup(keyboardMaker.getYesNoKeyboard(numTruckForType))
						.build();   
        		try {
					execute(messageProof);
				} catch (TelegramApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case "/setweigth":
				System.out.println("у ру ру ");
				break;
			case "/proofTruck":
				String numTruckProof = data.split("_")[0];
				String answer = data.split("_")[1];
				
				SendMessage sendKeyboard = new SendMessage();                	
            	sendKeyboard.setChatId(chatId);
				
				if(answer.equals("yes")) {
					user.setStatus(null);
					user.setValidityPass(null);
					users.put(chatId, user);
					serializableUsers();
					sendKeyboard.setText("Машина заявлена!");
					sendKeyboard.setReplyMarkup(keyboardMaker.getMainKeyboard()); // клава для юзеров
				}else {
					user.removeTrucksForBot(numTruckProof);
					user.setStatus(null);
					user.setValidityPass(null);
					users.put(chatId, user);
					serializableUsers();
					sendKeyboard.setText("Машина отменена!");
					sendKeyboard.setReplyMarkup(keyboardMaker.getMainKeyboard()); // клава для юзеров
				}
				try {
					execute(sendKeyboard);
				} catch (TelegramApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
				
			default:
				break;
			} 			
		}
		
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();   
            long chatId = update.getMessage().getChatId();
            
            if(users.size() != 0 && users.containsKey(chatId) && users.get(chatId).getStatus() != null && messageText.split("~")[0].equals("/start")) {
            	User user = users.get(chatId);
            	user.setStatus(null);
            	users.put(chatId, user);
            }
            
            if(users.size() != 0 && users.containsKey(chatId) && users.get(chatId).getStatus() != null && !messageText.split("~")[0].equals("/start")) {
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
                	sendKeyboard.setChatId(chatId);
					if(users.containsKey(chatId)) {
						sendKeyboard.setText("Приветствую " + users.get(chatId).getCompanyName() + "!");
						sendKeyboard.setReplyMarkup(keyboardMaker.getMainKeyboard()); // клава для юзеров
					}else {		
						sendKeyboard.setText("Приветствую!");
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
                	String companyName = messageText.contains("~") ? messageText.split("~")[1] : messageText;
                	user.setCompanyName(companyName);
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
                case "/numtruck": 
                	User userForTruck2 = users.get(chatId);
                	userForTruck2.setStatus("/setpall");
                	users.put(chatId, userForTruck2);
                	//создаём и записываем авто
                	String numTruck = messageText.split("~")[1];
                	SendMessage message = new SendMessage();
                    message.setChatId(chatId);                    
                    message.setText("Номер " + numTruck +" принят. \nВведите сколько паллет вмещает авто");
            		message.setReplyMarkup(keyboardMaker.getPallMessageKeyboardNew(numTruck));
            		try {
						execute(message);
					} catch (TelegramApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    break;
//                case "/setweigth": 
//                	User userForTruck3 = users.get(chatId);
//                	Truck truck = userForTruck3.getTrucksForBot(userForTruck3.getValidityPass());
//                	truck.setCargoCapacity(messageText.split("~")[1]);
//                	userForTruck3.setStatus("/settype");
//                	userForTruck3.putTrucksForBot(userForTruck3.getValidityPass(), truck);
//                	users.put(chatId, userForTruck3);
//                	
//                	//создаём и записываем авто
//                	SendMessage messageBeforeWeigth = new SendMessage();
//                	messageBeforeWeigth.setChatId(chatId);                    
//                	messageBeforeWeigth.setText("Вес принял. \nУкажите тип авто");
//                	messageBeforeWeigth.setReplyMarkup(keyboardMaker.getTypeTruckKeyboard(truck.getNumTruck()));
//            		try {
//						execute(messageBeforeWeigth);
//					} catch (TelegramApiException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//                    break;
                    
                case "список машин заявленных на завтра": 
                	User userForList = users.get(chatId);
                	Map<String, Truck> trucks = userForList.getTrucksForBot();
                	
                	trucks.entrySet().stream().filter(e-> e.getValue().getDateRequisitionLocalDate().equals(LocalDate.now().plusDays(1))).forEach(entry->{                		
                    	SendMessage messageTruckList = new SendMessage();
                    	messageTruckList.setChatId(chatId);                    
                    	messageTruckList.setText(entry.getValue().getTruckForBot() + " на " + entry.getValue().getDateRequisitionLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                    	messageTruckList.setReplyMarkup(keyboardMaker.getCancelDeleteEditKeyboard(entry.getKey()));
                		try {
    						execute(messageTruckList);
    					} catch (TelegramApiException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
                	});  
                    break;
                case "заявить машину на дату": 
                	SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Выберите дату");
                    sendMessage.setReplyMarkup(inlineCalendarBuilder.build(update));
                    Message message1 = executeCommand(sendMessage);
                    chatAndMessageIdMap.put(chatId, message1.getMessageId());
                    
				try {
					execute(sendMessage);
				} catch (TelegramApiException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
		return "6742392768:AAGof2DXKQEmYDw5hdb6MxMAq1fgTufmR-Q";
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
	
    private Message executeCommand(SendMessage sendMessage) {
        try {
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void executeCommand(EditMessageText editMessageText) {
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
