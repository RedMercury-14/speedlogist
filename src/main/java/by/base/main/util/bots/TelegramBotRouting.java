package by.base.main.util.bots;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Date;
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
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import by.base.main.controller.MainController;
import by.base.main.model.TGUser;
import by.base.main.service.TGTruckService;
import by.base.main.service.TGUserService;
import by.base.main.util.SlotWebSocket;
import by.base.main.model.Message;
import by.base.main.model.TGTruck;
import io.github.dostonhamrakulov.InlineCalendarBuilder;
import io.github.dostonhamrakulov.InlineCalendarCommandUtil;
import io.github.dostonhamrakulov.LanguageEnum;

@Component
public class TelegramBotRouting extends TelegramLongPollingBot{
	
	public boolean isRunning = false;
	
	@Autowired
	public KeyboardMaker keyboardMaker;
	
	@Autowired
	public MainController mainController;
	
	@Autowired
	public TGTruckService tgTruckService;
	
	@Autowired
	public TGUserService tgUserService;
	
	@Autowired
	private SlotWebSocket slotWebSocket;
	
	private long idAdmin = 907699213;
//	private Map<Long, TGUser> users = new HashMap<Long, TGUser>(); // юзеры, которые заявляют авто
	private List<Long> idAllUsers = new ArrayList<Long>(); // все подключенные к боту юзеры
	private Map<Long, String> idAdmins = new HashMap<Long, String>(); // админы
	
	private static final InlineCalendarBuilder inlineCalendarBuilder = new InlineCalendarBuilder(LanguageEnum.RU);
	private Map<Long, Integer> chatAndMessageIdMap = new HashMap<>();
	
	private String description = "Приветстсвую!\r\n"
			+ "РазвозДоброномBot 🚚\r\n"
			+ "\r\n"
			+ "📋 *Описание*:\r\n"
			+ "Бот, который поможет вам заявить свою машину (и не одну) на определённую дату загрузки. Информация будет оперативно предоставлена транспортным логистам.\r\n"
			+ "\r\n"
			+ "✨ *Функции*:\r\n"
			+ "- 🗓️ Заявка на определённую дату\r\n"
			+ "- ⏳ Заявка на завтра\r\n"
			+ "- 🚛 Управление уже заявленными машинами\r\n";
	
	@Override
	public void onUpdateReceived(Update update) {
		try {
			if(update.hasMessage() && update.getMessage().hasContact()){	
				long chatId = update.getMessage().getChatId();
				TGUser user = new TGUser();
				user.setChatId(chatId);
				user.setTelephone(update.getMessage().getContact().getPhoneNumber());
				user.setCommand("/login");
				tgUserService.saveOrUpdateTGUser(user);
				sendMessage(chatId, "Номер принят. Напишите название фирмы");
			}
			if(update.hasMessage() && update.getMessage().hasLocation()){
				long chatId = update.getMessage().getChatId();
				sendMessage(chatId, update.getMessage().getLocation().toString());
			}
			
			//лавный блок обработки CallbackData т.е. сообщений которые призодят с кнопок прикрепленных к сообщениям
			if(update.hasCallbackQuery()){
//				System.out.println("CallbackData -> " + update.getCallbackQuery().getData());
//				System.out.println("CallbackData.getMessage().getChatId() -> " + update.getCallbackQuery().getMessage().getChatId());
				
				long chatId = update.getCallbackQuery().getMessage().getChatId();
				//тут достаём id старого сообщения
				long messageId = update.getCallbackQuery().getMessage().getMessageId();
				String message = update.getCallbackQuery().getMessage().getText();
				String data = update.getCallbackQuery().getData();
				TGUser user = tgUserService.getTGUserByChatId(chatId);
				String nextText = null;
				switch (data.split("_")[0]) {
				case "CAL": //отдельная обработка на календарь
					 SendMessage sendMessage = new SendMessage();
				        sendMessage.setChatId(chatId);

				        // Проверяем, была ли нажата кнопка календаря		        
				        if (InlineCalendarCommandUtil.isInlineCalendarClicked(update)) {
				            // Обрабатываем навигацию по месяцам
				            if (InlineCalendarCommandUtil.isCalendarNavigationButtonClicked(update)) {
//				                sendMessage.setReplyMarkup(inlineCalendarBuilder.build(update));
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
				            
				            TGUser userForTruck = tgUserService.getTGUserByChatId(chatId);
		                	userForTruck.setCommand("/numtruck");
		                	userForTruck.setDateOrderTruckOptimization(Date.valueOf(localDate));
		                	nextText = localDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		                	tgUserService.saveOrUpdateTGUser(userForTruck);
		                	EditMessageText newMessage = EditMessageText.builder()
            				.chatId(chatId)
            				.messageId(Math.toIntExact(messageId))
            				.text("Введите номер авто ")
            				.build();
            		try {
            			execute(newMessage);
            		} catch (TelegramApiException e) {
            			e.printStackTrace();
            		}
				        }
				        
				        SendMessage sendKeyboard = new SendMessage();      //следом кидаем  клаву для юзеров          	
				        sendKeyboard.setChatId(chatId);
				        sendKeyboard.setReplyMarkup(keyboardMaker.getMainCancelKeyboard()); //следом кидаем  клаву для юзеров
				        sendKeyboard.setText("На " + nextText + ":");
				        try {
				        	execute(sendKeyboard);
				        } catch (TelegramApiException e) {
				        	// TODO Auto-generated catch block
				        	e.printStackTrace();
				        }
				        
				        
					return;
				case "cancelTruck" :
					
					TGTruck tgTruckForDelete = tgTruckService.getTGTruckByChatNumTruckStrict(data.split("_")[1], Date.valueOf(data.split("_")[2]), user);
					
					if(tgTruckForDelete.getStatus() == 50) {
						EditMessageText newMessage = EditMessageText.builder()
	        		            .chatId(chatId)
	        		            .messageId(Math.toIntExact(messageId))
	        		            .parseMode("HTML")
	        		            .text("Машина <b>" + tgTruckForDelete.getNumTruck() + "</b> уже используется в планировании сотрудникаим транспортной логистики")
	        		            .build();
						try {
        		            execute(newMessage);
        		        } catch (TelegramApiException e) {
        		            e.printStackTrace();
        		        }
						
						
						return;
					}else {
						Message messageObject = new Message("TGBotRouting", "tgBot", null, "200", tgTruckService.getTGTruckByChatNumTruckStrict(data.split("_")[1], Date.valueOf(data.split("_")[2]), user).toJSON(), null, "delete");
						slotWebSocket.sendMessage(messageObject);
						tgTruckService.deleteTGTruckByNumTruck(data.split("_")[1], Date.valueOf(data.split("_")[2]));	
						user.removeTrucksForBot(data.split("_")[1]);
						
						DeleteMessage deleteMessage = new DeleteMessage();
						deleteMessage.setChatId(chatId);  // Укажите идентификатор чата
						deleteMessage.setMessageId(Math.toIntExact(messageId));  // Укажите идентификатор сообщения
						tgUserService.saveOrUpdateTGUser(user);
						try {
						    execute(deleteMessage);  // Выполняем удаление сообщения
						} catch (TelegramApiException e) {
						    e.printStackTrace();
						}
						return;
					}
					
					
					
					
				case "editTruck" :
					System.out.println("тут обработка оредактирования авто");
					break;
					
				case "copyTruck" :
					System.out.println("тут обработка копирования авто");
					break;

				default:
					break;
				}
				
				
				
				
				switch (user.getCommand()) {
				case "/setpall":
					String numTruck = data.split("_")[0];
					String pall = data.split("_")[1];
//					TGTruck truck = tgTruckService.getTGTruckByChatNumTruck(numTruck, user);
					TGTruck truck = new TGTruck();
					if(user.getDateOrderTruckOptimization() != null) {
						truck.setDateRequisition(user.getDateOrderTruckOptimization());
					}else {
						truck.setDateRequisition(Date.valueOf(LocalDate.now().plusDays(1)));
					}
					truck.setNumTruck(numTruck);
					truck.setPall(Integer.parseInt(pall));
					truck.setChatIdUserTruck(user.getChatId());
					user.putTrucksForBot(numTruck, truck);	
					tgTruckService.saveOrUpdateTGTruck(truck);
					user.setValidityTruck(numTruck); // сюза временно записываем номер авто которое обрабатывается
					//создали машину, присвоили номер и записали сколько паллет.
					
					user.setCommand("/setweigth");
					tgUserService.saveOrUpdateTGUser(user);
					//тут достаём старое сообщение и меняем его
//					long messageId = update.getCallbackQuery().getMessage().getMessageId();
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
			        return;
				case "/settype":
					String numTruckForType = data.split("_")[0];
					String type = data.split("_")[1];
					
					TGTruck truckForType = tgTruckService.getTGTruckByChatNumTruck(numTruckForType, user);
					truckForType.setTypeTrailer(type);
					truckForType.setCompanyName(user.getCompanyName());
					
					
					tgTruckService.saveOrUpdateTGTruck(truckForType);					
					user.setCommand("/setdriver");
					tgUserService.saveOrUpdateTGUser(user);
					
					String text = "Тип принят. Укажите ФИО и телефон водителя";
					
//					long messageIdType = update.getCallbackQuery().getMessage().getMessageId();
					EditMessageText messageProof = EditMessageText.builder()
							.chatId(chatId)
							.messageId(Math.toIntExact(messageId))
							.text(text)
							.build();   
	        		try {
						execute(messageProof);
					} catch (TelegramApiException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		return;				
				case "/setweigth":
					TGUser userForTruck3 = tgUserService.getTGUserByChatId(chatId);
					TGTruck truckForWeigth = tgTruckService.getTGTruckByChatNumTruck(userForTruck3.getValidityTruck(), userForTruck3);
					
	            	truckForWeigth.setCargoCapacity(data.split("_")[0]);
	            	userForTruck3.setCommand("/settype");
	            	userForTruck3.putTrucksForBot(userForTruck3.getValidityTruck(), truckForWeigth);
	            	tgUserService.saveOrUpdateTGUser(userForTruck3);
	            	tgTruckService.saveOrUpdateTGTruck(truckForWeigth);            	
	            	
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
	        		return;
				case "/proofTruck":
					String numTruckProof = data.split("_")[0];
					String answer = data.split("_")[1];
					
					TGTruck proofTruck = tgTruckService.getTGTruckByChatNumTruck(numTruckProof, user);
					proofTruck.setStatus(10);
					
					tgTruckService.saveOrUpdateTGTruck(proofTruck);					
					SendMessage sendKeyboard = new SendMessage();                	
	            	sendKeyboard.setChatId(chatId);
	            	
					if(answer.equals("yes")) {					
						Message messageObject = new Message("TGBotRouting", "tgBot", null, "200", proofTruck.toJSON(), null, "add");
						slotWebSocket.sendMessage(messageObject);
						
						user.setCommand(null);
						user.setValidityTruck(null);
						user.setDateOrderTruckOptimization(null);
						tgUserService.saveOrUpdateTGUser(user);
						//сюда вставить статус машины (5 статус)
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
						tgTruckService.deleteTGTruckByNumTruck(numTruckProof, user);
						user.removeTrucksForBot(numTruckProof);
						user.setCommand(null);
						user.setValidityTruck(null);
						tgUserService.saveOrUpdateTGUser(user);
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
					
					return;
					
				default:
					break;
				} 			
			}
			long chatId = update.getMessage().getChatId();
	        if(update.hasMessage() && update.getMessage().hasText()){
	            String messageText = update.getMessage().getText();            
	            String onlyText = new String(update.getMessage().getText());     
	            TGUser user = tgUserService.getTGUserByChatId(chatId);
	            
	            if(user != null && user.getCommand() != null && messageText.split("~")[0].equals("/start")) {
	            	TGUser user1 = tgUserService.getTGUserByChatId(chatId);
	            	user1.setCommand(null);
	            	tgUserService.saveOrUpdateTGUser(user1);
	            }
	            
	            if(user != null && user.getCommand() != null && !messageText.split("~")[0].equals("/start")) {
	            	messageText = user.getCommand()+"~"+messageText;
	            }
	            
//	            System.out.println(messageText + " idChat = " + chatId);
	            String command;
	            if(messageText.split("~").length > 1 && messageText.split("~")[1].equals("Отменить действие")) {
	            	command = messageText.split("~")[1];
	            }else {
	            	command = messageText.split("~")[0];
	            }
	            
	            
	            System.err.println(command);
	            switch (command.toLowerCase()){
	                case "/start": 
	                	SendMessage sendKeyboard = new SendMessage();                	
	                	sendKeyboard.setChatId(chatId);
						if(user != null) {
							sendKeyboard.setText("Приветствую " + user.getCompanyName() + "!");
							sendKeyboard.setReplyMarkup(keyboardMaker.getMainKeyboard()); // клава для юзеров
						}else {		
							sendKeyboard.setText(description);
							sendKeyboard.enableMarkdown(true);
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
	                	companyName = companyName.replaceAll("\"", "");
	                	user.setCompanyName(companyName);
	                	user.setCommand(null);
	                	tgUserService.saveOrUpdateTGUser(user);
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
	                	tgUserService.saveOrUpdateTGUser(user);
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
	                	tgTruckService.deleteTGTruckByNumTruck(user.getValidityTruck(), user);
	                	user.setCommand(null);
	                	user.setValidityTruck(null);
	                	user.setDateOrderTruckOptimization(null);
	                	tgUserService.saveOrUpdateTGUser(user);
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
	                case "/setdriver": 
	                	//тут проверяем, есть ли машина с таким номером и датой заявки
	                	TGTruck truckDriver = tgTruckService.getTGTruckByChatNumTruckStrict(user.getValidityTruck(), user.getDateOrderTruckOptimization() == null ? Date.valueOf(LocalDate.now().plusDays(1)) : user.getDateOrderTruckOptimization(), user);
	                	onlyText = onlyText.replaceAll("\"", "");
	                	truckDriver.setFio(onlyText);
	                	user.setCommand("/setinfo");
	                	tgUserService.saveOrUpdateTGUser(user);
	                	tgTruckService.saveOrUpdateTGTruck(truckDriver);
	                	//создаём и записываем авто
	                	SendMessage messageChat = new SendMessage();
	                	messageChat.setChatId(chatId);                    
	                	messageChat.setText("Водитель принят. \nУкажите Информацию о подаче машины (время) и направление");
	            		try {
							execute(messageChat);
						} catch (TelegramApiException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    break;
	                case "/setinfo": 
	                	//тут проверяем, есть ли машина с таким номером и датой заявки
	                	TGTruck truckInfo = tgTruckService.getTGTruckByChatNumTruckStrict(user.getValidityTruck(), user.getDateOrderTruckOptimization() == null ? Date.valueOf(LocalDate.now().plusDays(1)) : user.getDateOrderTruckOptimization(), user);
	                	onlyText = onlyText.replaceAll("\"", "");
	                	truckInfo.setOtherInfo(onlyText);
	                	user.setCommand("/proofTruck");
	                	user.setValidityTruck(null); // сюза временно записываем номер авто которое обрабатывается но записывем null т.к. типо закончили
	                	tgUserService.saveOrUpdateTGUser(user);
	                	tgTruckService.saveOrUpdateTGTruck(truckInfo);
	                	//создаём и записываем авто
	                	String textEnd;
						String dateNext = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
						if(user.getDateOrderTruckOptimization() != null) {
							textEnd = "Заявляем машину на ("+user.getDateOrderTruckOptimization().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+")\n Авто: "+truckInfo.getTruckForBot();
							truckInfo.setDateRequisition(user.getDateOrderTruckOptimization());
						}else {
							textEnd = "Заявляем машину на завтра ("+dateNext+")\n Авто: "+truckInfo.getTruckForBot();
							truckInfo.setDateRequisition(LocalDate.now().plusDays(1));
						}
						
						SendMessage messageProof = SendMessage.builder()
								.chatId(chatId)
								.text(textEnd)
								.replyMarkup(keyboardMaker.getYesNoKeyboard(truckInfo.getNumTruck()))
								.build();   
		        		try {
							execute(messageProof);
						} catch (TelegramApiException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    break;
	                case "/numtruck": 
	                	String numTruck = messageText.split("~")[1];
	                	//тут проверяем, есть ли машина с таким номером и датой заявки
	                	TGTruck testTruck = tgTruckService.getTGTruckByChatNumTruckStrict(numTruck, user.getDateOrderTruckOptimization(), user);
	                	if(testTruck != null) {
		                	user.setCommand(null);
		                	user.setValidityTruck(null);
		                	user.setDateOrderTruckOptimization(null);
		                	tgUserService.saveOrUpdateTGUser(user);
		                	SendMessage sendMessageCancelExeption = new SendMessage();
		                	sendMessageCancelExeption.setText("Машина с номером " + numTruck + " уже заявлена на " + testTruck.getDateRequisitionLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		                	sendMessageCancelExeption.setChatId(chatId);
		                	sendMessageCancelExeption.setReplyMarkup(keyboardMaker.getMainKeyboard());
		            		try {
		            			execute(sendMessageCancelExeption);
		            		} catch (TelegramApiException e) {
		            			System.err.println("execute не сработал");
		            			e.printStackTrace();
		            		}
		            		return;
	                	}
	                	
	                	
	                	user.setCommand("/setpall");
	                	user.setValidityTruck(numTruck);
	                	tgUserService.saveOrUpdateTGUser(user);
	                	//создаём и записываем авто
//	                	TGTruck tgTruckFirst = new TGTruck();
//	                	tgTruckFirst.setNumTruck(numTruck);
//	                	tgTruckFirst.setChatIdUserTruck(chatId);
//	                	tgTruckService.saveOrUpdateTGTruck(tgTruckFirst);
	                	
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
	            		return;
	                    
	                case "список ближайших заявленных машин":                	
	                	Map<String, TGTruck> filteredMap = user.getTrucksForBot();
	                	if(filteredMap == null ||filteredMap.isEmpty()) {
	                		SendMessage messageTruckList = new SendMessage();
	                    	messageTruckList.setChatId(chatId);
	                    	messageTruckList.setParseMode("HTML");  // Устанавливаем режим HTML
	                    	messageTruckList.setText("Машины заявленные на сегодня или завтра (<b>" + LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+"</b>) отсутствуют.");
	                		try {
	    						execute(messageTruckList);
	    					} catch (TelegramApiException e) {
	    						// TODO Auto-generated catch block
	    						e.printStackTrace();
	    					}
	                	}else {
	                		filteredMap.entrySet().stream()
		                    .filter(e -> e.getValue().getDateRequisitionLocalDate().equals(LocalDate.now().plusDays(1)) ||  e.getValue().getDateRequisitionLocalDate().equals(LocalDate.now()))
		                    .forEach(entry->{
	                			SendMessage messageTruckList = new SendMessage();
	                        	messageTruckList.setChatId(chatId);
	                        	messageTruckList.setParseMode("HTML");  // Устанавливаем режим HTML
	                        	messageTruckList.setText(entry.getValue().getTruckForBot() + " на <b>" + entry.getValue().getDateRequisitionLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+"</b>");
	                        	messageTruckList.setReplyMarkup(keyboardMaker.getCancelDeleteEditKeyboard(entry.getKey(), entry.getValue().getDateRequisition()));	                        	
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
	                	List<TGTruck> filteredMapAll = tgTruckService.getTGTruckByChatIdUserList(user.getChatId());
	                	if(filteredMapAll == null || filteredMapAll.isEmpty()) {
	                		SendMessage messageTruckList = new SendMessage();
	                    	messageTruckList.setChatId(chatId);
	                    	messageTruckList.setParseMode("HTML");  // Устанавливаем режим HTML
	                    	messageTruckList.setText("Заявленные машины отсутствуют.");
	                		try {
	    						execute(messageTruckList);
	    					} catch (TelegramApiException e) {
	    						// TODO Auto-generated catch block
	    						e.printStackTrace();
	    					}
	                	}else {
//	                		tgTruckService.getTGTruckByChatIdUserList(user.getChatId()).stream().forEach(entry->{                		
	                		filteredMapAll.stream()
	                		.filter(t-> t.getDateRequisition().toLocalDate().isAfter(LocalDate.now().minusDays(1)))
	                		.forEach(entry->{                		
		                    	SendMessage messageTruckList = new SendMessage();
		                    	messageTruckList.setChatId(chatId);   
		                    	messageTruckList.setParseMode("HTML");  // Устанавливаем режим HTML
		                    	messageTruckList.setText(entry.getTruckForBot() + " на <b>" + entry.getDateRequisitionLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))+"</b>");
		                    	messageTruckList.setReplyMarkup(keyboardMaker.getCancelDeleteEditKeyboard(entry.getNumTruck(), entry.getDateRequisition()));
		                		try {
		    						execute(messageTruckList);
		    					} catch (TelegramApiException e) {
		    						// TODO Auto-generated catch block
		    						e.printStackTrace();
		    					}
		                	});
	                	}	                	  
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
	                		sendMessage(chatId, "Недостаточно прав", keyboardMaker.getMainKeyboard());    
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
	                	sendMessage(chatId, "Как оригинально! \nБот внёс Вас в некультурный список!", keyboardMaker.getMainKeyboard());              	
	                	break;
	                case "пизда":
	                	sendMessage(chatId, "Как оригинально! \nБот внёс Вас в некультурный список!", keyboardMaker.getMainKeyboard());              	
	                	break;
	                case "хуй":
	                	sendMessage(chatId, "Заборов мало?! \nБот внёс Вас в некультурный список!", keyboardMaker.getMainKeyboard());              	
	                	break;
	                case "бот говно":
	                case "Бот говно":
	                	sendMessage(chatId, "╭∩╮ (`-`) ╭∩╮ \n\nБот внёс Вас в некультурный список!", keyboardMaker.getMainKeyboard());              	
	                	break;
	                case "соси":
	                	sendMessage(chatId, "Что сосать?", keyboardMaker.getMainKeyboard());              	
	                	break;
	                case "соси хуй":
	                	sendMessage(chatId, "Как неожиданно! \nБот внёс Вас в некультурный список!", keyboardMaker.getMainKeyboard());              	
	                	break;
	                case "лох":
	                	sendMessage(chatId, "Пароль принят! \nОтправьте фото карты с двух сторон для перевода денег!", keyboardMaker.getMainKeyboard());              	
	                	break;
	                case "не работает":
	                case "бот не работает":
	                case "Бот не работает":
	                case "Не работает":
	                	sendMessage(chatId, "Всё работает! Перезагрузите телефон.", keyboardMaker.getMainKeyboard());              	
	                	break;
	                case "мудак":
	                	sendMessage(chatId, "Правильно писать чудак.", keyboardMaker.getMainKeyboard());              	
	                	break;
	                case "педик":
	                case "пидор":
	                case "пидр":
	                	sendMessage(chatId, "Новый логин для входа в SpeedLogist принят!", keyboardMaker.getMainKeyboard());              	
	                	sendMessage(chatId, "Бот внёс Вас в некультурный список!", keyboardMaker.getMainKeyboard());             	
	                	break;
	                default:
	                	SendMessage sendMessageUnknown  = new SendMessage();
	                	sendMessageUnknown.setText("Неизвестная команда");
	                	sendMessageUnknown.setChatId(chatId);
	                	sendMessageUnknown.setReplyMarkup(keyboardMaker.getMainKeyboard());
	            		try {
	            			execute(sendMessageUnknown);
	            		} catch (TelegramApiException e) {
	            			System.err.println("execute не сработал");
	            			e.printStackTrace();
	            		}
	            }
	        }
		} catch (Exception e) {
			e.printStackTrace();
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
	
	public void sendMessage(long chatId, String textToSend, ReplyKeyboardMarkup replyKeyboardMarkup) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setText(textToSend);
		sendMessage.setChatId(chatId);
		sendMessage.setReplyMarkup(replyKeyboardMarkup);
		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			System.err.println("execute не сработал");
			e.printStackTrace();
			
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
//	private void serializableUsers() {
//		//проверка директории
//        File fileTest= new File(mainController.path + "resources/others/telegramm/");
//        if (!fileTest.exists()) {
//            fileTest.mkdir();
//            File fileTest2= new File(mainController.path + "resources/others/telegramm/route/");
//	        if (!fileTest2.exists()) {
//	            fileTest2.mkdir();
//	        }
//        }
//		try {
//			FileOutputStream fos = new FileOutputStream(mainController.path + usersDir);
//                  ObjectOutputStream oos = new ObjectOutputStream(fos);
//                  oos.writeObject(this.users);
//                  oos.close();
//                  fos.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	/**
	 * Сериализация листа idAllUsers
	 */
//	private void serializableIdAllUsers() {
//		try {
//			FileOutputStream fos = new FileOutputStream(mainController.path + "resources/others/telegrammIdAllUser.ser");
//                  ObjectOutputStream oos = new ObjectOutputStream(fos);
//                  oos.writeObject(this.idAllUsers);
//                  oos.close();
//                  fos.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
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
//	public void deSerializableIdAllUsers() {
//		try {
//			FileInputStream fis = new FileInputStream(mainController.path + "resources/others/telegrammIdAllUser.ser");
//		         ObjectInputStream ois = new ObjectInputStream(fis);
//		         this.idAllUsers = (ArrayList) ois.readObject();
//		         ois.close();
//		         fis.close();
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
//	}
	
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
	
//	public void deSerializableIdUsers() {
//	    // Полный путь к файлу
//	    String fullPath = mainController.path + usersDir;
//
//	    // Проверка на существование директории
//	    File userFile = new File(fullPath);
//	    File userDirectory = userFile.getParentFile();
//	    
//	    if (!userDirectory.exists()) {
//	        userDirectory.mkdirs(); // Создаем директорию, если она не существует
//	    }
//
//	    // Проверка на существование файла
//	    if (!userFile.exists()) {
//	        try {
//	            userFile.createNewFile(); // Создаем файл, если он не существует
//	            // Можно инициализировать его пустым Map
//	            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userFile))) {
//	                oos.writeObject(new HashMap<Long, TGUser>());
//	            }
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
//
//	    // Десериализация данных из файла
//	    try (FileInputStream fis = new FileInputStream(fullPath);
//	         ObjectInputStream ois = new ObjectInputStream(fis)) {
//	        this.users = (Map<Long, TGUser>) ois.readObject();
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//	}
	
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
