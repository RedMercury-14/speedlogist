package by.base.main.util.bots;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import by.base.main.controller.MainController;
import by.base.main.model.TGUser;
import by.base.main.model.TGTruck;
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
	private Map<Long, TGUser> users = new HashMap<Long, TGUser>(); // юзеры, которые заявляют авто
	private List<Long> idAllUsers = new ArrayList<Long>(); // все подключенные к боту юзеры
	private Map<Long, String> idAdmins = new HashMap<Long, String>(); // админы
	
	private static final InlineCalendarBuilder inlineCalendarBuilder = new InlineCalendarBuilder(LanguageEnum.RU);
	private Map<Long, Integer> chatAndMessageIdMap = new HashMap<>();
	
	@Override
	public void onUpdateReceived(Update update) {
		
		
		if(update.hasMessage() && update.getMessage().hasContact()){	
			long chatId = update.getMessage().getChatId();
			TGUser user = new TGUser();
			user.setChatId(chatId);
			user.setTelephone(update.getMessage().getContact().getPhoneNumber());
			user.setCommand("/login");
			users.put(chatId, user);
			serializableUsers();
			sendMessage(chatId, "Номер принят. Напишите название фирмы");
		}
		if(update.hasMessage() && update.getMessage().hasLocation()){
			long chatId = update.getMessage().getChatId();
			sendMessage(chatId, update.getMessage().getLocation().toString());
		}
		
		//лавный блок обработки CallbackData т.е. сообщений которые призодят с кнопок прикрепленных к сообщениям
		if(update.hasCallbackQuery()){
			System.out.println("CallbackData -> " + update.getCallbackQuery().getData());
			System.out.println("CallbackData.getMessage().getChatId() -> " + update.getCallbackQuery().getMessage().getChatId());
			
			long chatId = update.getCallbackQuery().getMessage().getChatId();
			//тут достаём id старого сообщения
			long messageId = update.getCallbackQuery().getMessage().getMessageId();
			String message = update.getCallbackQuery().getMessage().getText();
			String data = update.getCallbackQuery().getData();
			TGUser user = users.get(chatId);
			
			switch (data.split("_")[0]) {
			case "CAL": //отдельная обработка на календарь
				 SendMessage sendMessage = new SendMessage();
			        sendMessage.setChatId(chatId);

			        // Проверяем, была ли нажата кнопка календаря		        
			        if (InlineCalendarCommandUtil.isInlineCalendarClicked(update)) {
			            // Обрабатываем навигацию по месяцам
			            if (InlineCalendarCommandUtil.isCalendarNavigationButtonClicked(update)) {
//			                sendMessage.setReplyMarkup(inlineCalendarBuilder.build(update));
			                EditMessageText newMessage = EditMessageText.builder()
			    		            .chatId(chatId)
			    		            .messageId(Math.toIntExact(messageId))
			    		            .text("Выберите дату")
			    		            .replyMarkup(inlineCalendarBuilder.build(update)) // сюда указываем колличество паллет, дальше делает клава
			    		            .build();
			    		        try {
			    		            execute(newMessage);
			    		        } catch (TelegramApiException e) {
			    		            e.printStackTrace();
			    		        }
			                // Выполняем отправку сообщения
			                return;
			            }

			            // Извлекаем выбранную дату
			            LocalDate localDate = InlineCalendarCommandUtil.extractDate(update);
			            if(localDate.isBefore(LocalDate.now())) {//если юзер пытается заявить прошлым числом
			            	String currentText = "Нельзя выбрать дату до "+LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+"\nВыберите дату"; 
			            	String newText = "Еще раз : <b>нельзя выбрать дату до "+LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+"</b>\nВыберите новую дату"; // Ваш новый текст
			            	EditMessageText newMessage = null;
			            	if(message.equals(currentText)) {
			            		newMessage = EditMessageText.builder()
			        		            .chatId(chatId)
			        		            .messageId(Math.toIntExact(messageId))
			        		            .text(newText)
			        		            .replyMarkup(inlineCalendarBuilder.build(update))
			        		            .parseMode("HTML")
			        		            .build();
			            	}else {
			            		newMessage = EditMessageText.builder()
			        		            .chatId(chatId)
			        		            .messageId(Math.toIntExact(messageId))
			        		            .text(currentText)
			        		            .replyMarkup(inlineCalendarBuilder.build(update))
			        		            .build();
			            	}
			            	
		        		        try {
		        		            execute(newMessage);
		        		        } catch (TelegramApiException e) {
		        		            e.printStackTrace();
		        		        }
		                    return;
			            }
			            
			            TGUser userForTruck = users.get(chatId);
	                	userForTruck.setCommand("/numtruck");
	                	userForTruck.setDateOrderTruckOptimization(localDate);
	                	users.put(chatId, userForTruck);
	                	EditMessageText newMessage = EditMessageText.builder()
	        		            .chatId(chatId)
	        		            .messageId(Math.toIntExact(messageId))
	        		            .text("Введите номер авто: ")
	        		            .build();
	        		        try {
	        		            execute(newMessage);
	        		        } catch (TelegramApiException e) {
	        		            e.printStackTrace();
	        		        }
			        }
				break;
			case "cancelTruck" :
				user.removeTrucksForBot(data.split("_")[1]);
				DeleteMessage deleteMessage = new DeleteMessage();
				deleteMessage.setChatId(chatId);  // Укажите идентификатор чата
				deleteMessage.setMessageId(Math.toIntExact(messageId));  // Укажите идентификатор сообщения
				serializableUsers();
				try {
				    execute(deleteMessage);  // Выполняем удаление сообщения
				} catch (TelegramApiException e) {
				    e.printStackTrace();
				}

				return;
				
			case "editTruck" :
				System.out.println("тут обработка оредактирования авто");
				break;

			default:
				break;
			}
			
			
			
			
			switch (user.getCommand()) {
			case "/setpall":
				String numTruck = data.split("_")[0];
				String pall = data.split("_")[1];
				TGTruck truck = new TGTruck();
				truck.setNumTruck(numTruck);
				truck.setPall(Integer.parseInt(pall));
				user.putTrucksForBot(numTruck, truck);				
				user.setValidityTruck(numTruck); // сюза временно записываем номер авто которое обрабатывается
				//создали машину, присвоили номер и записали сколько паллет.
				
				user.setCommand("/setweigth");
				users.put(chatId, user);
				//тут достаём старое сообщение и меняем его
//				long messageId = update.getCallbackQuery().getMessage().getMessageId();
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
				
				TGTruck truckForType = user.getTrucksForBot(numTruckForType);
				truckForType.setTypeTrailer(type);
				
				
				String text;
				String dateNext = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
				if(user.getDateOrderTruckOptimization() != null) {
					text = "Заявляем машину на ("+user.getDateOrderTruckOptimization().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+")\n Авто: "+truckForType.getTruckForBot();
					truckForType.setDateRequisition(user.getDateOrderTruckOptimization());
				}else {
					text = "Заявляем машину на завтра ("+dateNext+")\n Авто: "+truckForType.getTruckForBot();
					truckForType.setDateRequisition(LocalDate.now().plusDays(1));
				}
				
				user.putTrucksForBot(numTruckForType, truckForType);				
				user.setValidityTruck(null); // сюза временно записываем номер авто которое обрабатывается но записывем null т.к. типо закончили
				//создали машину, присвоили номер и записали сколько паллет.
				
				user.setCommand("/proofTruck");
				users.put(chatId, user);
				
				long messageIdType = update.getCallbackQuery().getMessage().getMessageId();
				
				
				EditMessageText messageProof = EditMessageText.builder()
						.chatId(chatId)
						.messageId(Math.toIntExact(messageIdType))
						.text(text)
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
				TGUser userForTruck3 = users.get(chatId);
				TGTruck truckForWeigth = userForTruck3.getTrucksForBot(userForTruck3.getValidityTruck());
            	truckForWeigth.setCargoCapacity(data.split("_")[0]);
            	userForTruck3.setCommand("/settype");
            	userForTruck3.putTrucksForBot(userForTruck3.getValidityTruck(), truckForWeigth);
            	users.put(chatId, userForTruck3);
            	
            	
				EditMessageText messageWeigthEdit = EditMessageText.builder()
						.chatId(chatId)
						.messageId(Math.toIntExact(messageId))
						.text("Вес принял. \nУкажите тип авто")
						.replyMarkup(keyboardMaker.getTypeTruckKeyboard(truckForWeigth.getNumTruck()))
						.build();   
        		try {
					execute(messageWeigthEdit);
				} catch (TelegramApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                break;
			case "/proofTruck":
				String numTruckProof = data.split("_")[0];
				String answer = data.split("_")[1];
				
				SendMessage sendKeyboard = new SendMessage();                	
            	sendKeyboard.setChatId(chatId);
				
				if(answer.equals("yes")) {
					user.setCommand(null);
					user.setValidityTruck(null);
					user.setDateOrderTruckOptimization(null);
					users.put(chatId, user);
					serializableUsers();
					EditMessageText messageEditYes = EditMessageText.builder()
							.chatId(chatId)
							.messageId(Math.toIntExact(messageId))
							.text("Машина заявлена!")
							.build();   
	        		try {
						execute(messageEditYes); // меняем прошлое сообщение
					} catch (TelegramApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sendKeyboard.setReplyMarkup(keyboardMaker.getMainKeyboard()); //следом кидаем  клаву для юзеров
				}else {
					user.removeTrucksForBot(numTruckProof);
					user.setCommand(null);
					user.setValidityTruck(null);
					users.put(chatId, user);
					serializableUsers();
					EditMessageText messageEditNo = EditMessageText.builder()
							.chatId(chatId)
							.messageId(Math.toIntExact(messageId))
							.text("Машина отменена!")
							.build();   
	        		try {
						execute(messageEditNo); // меняем прошлое сообщение
					} catch (TelegramApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sendKeyboard.setReplyMarkup(keyboardMaker.getMainKeyboard()); //следом кидаем  клаву для юзеров
				}
				sendKeyboard.setText("Выберите следующее действие");
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
            if(users.size() != 0 && users.containsKey(chatId) && users.get(chatId).getCommand() != null && messageText.split("~")[0].equals("/start")) {
            	TGUser user = users.get(chatId);
            	user.setCommand(null);
            	users.put(chatId, user);
            }
            
            if(users.size() != 0 && users.containsKey(chatId) && users.get(chatId).getCommand() != null && !messageText.split("~")[0].equals("/start")) {
            	messageText = users.get(chatId).getCommand()+"~"+messageText;
            }
//            System.out.println(messageText + " idChat = " + chatId);
            String command;
            if(messageText.split("~").length > 1 && messageText.split("~")[1].equals("Отменить действие")) {
            	command = messageText.split("~")[1];
            }else {
            	command = messageText.split("~")[0];
            }
            if(!idAllUsers.contains(chatId)) {
            	idAllUsers.add(chatId);                    	
            	serializableIdAllUsers();
            }
            
            TGUser user = users.get(chatId);
            Map<String, TGTruck> trucks = null;
            if(user != null) {
            	trucks = user.getTrucksForBot();
            }
        	 
            
            System.err.println(command);
            switch (command.toLowerCase()){
                case "/start": 
                	SendMessage sendKeyboard = new SendMessage();                	
                	sendKeyboard.setChatId(chatId);
					if(users != null && users.containsKey(chatId)) {
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
                	String companyName = messageText.contains("~") ? messageText.split("~")[1] : messageText;
                	user.setCompanyName(companyName);
                	user.setCommand(null);
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
                	user.setCommand("/numtruck");
                	users.put(chatId, user);
                	SendMessage sendMessage = new SendMessage();
            		sendMessage.setText("Введите номер авто: ");
            		sendMessage.setChatId(chatId);
            		sendMessage.setReplyMarkup(keyboardMaker.getMainCancelKeyboard());
            		try {
            			execute(sendMessage);
            		} catch (TelegramApiException e) {
            			System.err.println("execute не сработал");
            			e.printStackTrace();
            			
            		}
                    break;
                case "отменить действие": 
                	user.setCommand(null);
                	Map<String, TGTruck> mapForDel = new HashMap<String, TGTruck>(user.getTrucksForBot());
                	for (Entry<String, TGTruck> entry : mapForDel.entrySet()) {
						if(entry.getValue().getPall() == null 
								|| entry.getValue().getTypeTrailer() == null
								|| entry.getValue().getCargoCapacity() == null) {
							user.removeTrucksForBot(entry.getKey());						
						}
					}
                	
                	users.put(chatId, user);
                	serializableUsers();
                	SendMessage sendMessageCancel = new SendMessage();
                	sendMessageCancel.setText("Действия отменены");
                	sendMessageCancel.setChatId(chatId);
                	sendMessageCancel.setReplyMarkup(keyboardMaker.getMainKeyboard());
            		try {
            			execute(sendMessageCancel);
            		} catch (TelegramApiException e) {
            			System.err.println("execute не сработал");
            			e.printStackTrace();
            			
            		}
                    break;
                case "/numtruck": 
                	user.setCommand("/setpall");
                	users.put(chatId, user);
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
                    
                case "список машин заявленных на сегодня и завтра":                	
                	Map<String, TGTruck> filteredMap = trucks.entrySet().stream()
                    .filter(e -> e.getValue().getDateRequisitionLocalDate().equals(LocalDate.now().plusDays(1)) || e.getValue().getDateRequisitionLocalDate().equals(LocalDate.now()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                	if(filteredMap.isEmpty()) {
                		SendMessage messageTruckList = new SendMessage();
                    	messageTruckList.setChatId(chatId);
                    	messageTruckList.setParseMode("HTML");  // Устанавливаем режим HTML
                    	messageTruckList.setText("Машины заявленные на завтра (<b>" + LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+"</b>) отсутствуют.");
                		try {
    						execute(messageTruckList);
    					} catch (TelegramApiException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
                	}else {
                		filteredMap.entrySet().forEach(entry->{
                			SendMessage messageTruckList = new SendMessage();
                        	messageTruckList.setChatId(chatId);
                        	messageTruckList.setParseMode("HTML");  // Устанавливаем режим HTML
                        	messageTruckList.setText(entry.getValue().getTruckForBot() + " на <b>" + entry.getValue().getDateRequisitionLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+"</b>");
                        	messageTruckList.setReplyMarkup(keyboardMaker.getCancelDeleteEditKeyboard(entry.getKey()));
                    		try {
        						execute(messageTruckList);
        					} catch (TelegramApiException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        					}
                		});
                	} 
                    break;
                case "список всех заявленных машин":                 	
                	trucks.entrySet().stream().forEach(entry->{                		
                    	SendMessage messageTruckList = new SendMessage();
                    	messageTruckList.setChatId(chatId);   
                    	messageTruckList.setParseMode("HTML");  // Устанавливаем режим HTML
                    	messageTruckList.setText(entry.getValue().getTruckForBot() + " на <b>" + entry.getValue().getDateRequisitionLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+"</b>");
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
                	SendMessage sendMessage1 = new SendMessage();
                    sendMessage1.setChatId(chatId);
                    sendMessage1.setText("Выберите дату");
                    sendMessage1.setReplyMarkup(inlineCalendarBuilder.build(update));
                    
				try {
					execute(sendMessage1);
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
//	public void deSerializableIdUsers() {
//		try {
//			FileInputStream fis = new FileInputStream(mainController.path + usersDir);
//		         ObjectInputStream ois = new ObjectInputStream(fis);
//		         this.users =  (Map<Long, User>) ois.readObject();
//		         ois.close();
//		         fis.close();
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
//	}
	
	public void deSerializableIdUsers() {
	    // Полный путь к файлу
	    String fullPath = mainController.path + usersDir;

	    // Проверка на существование директории
	    File userFile = new File(fullPath);
	    File userDirectory = userFile.getParentFile();
	    
	    if (!userDirectory.exists()) {
	        userDirectory.mkdirs(); // Создаем директорию, если она не существует
	    }

	    // Проверка на существование файла
	    if (!userFile.exists()) {
	        try {
	            userFile.createNewFile(); // Создаем файл, если он не существует
	            // Можно инициализировать его пустым Map
	            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userFile))) {
	                oos.writeObject(new HashMap<Long, TGUser>());
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    // Десериализация данных из файла
	    try (FileInputStream fis = new FileInputStream(fullPath);
	         ObjectInputStream ois = new ObjectInputStream(fis)) {
	        this.users = (Map<Long, TGUser>) ois.readObject();
	    } catch (Exception e) {
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
