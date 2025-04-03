package by.base.main.util.bots;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import by.base.main.model.TelegramChatQuality;
import by.base.main.service.TelegramChatQualityService;

@Component
public class TelegrammBotQuantityYard extends TelegramLongPollingBot {

    private String botUsername = "quantityYardBot";
    private String botToken = "8086048050:AAE3ZVUyw-vGshFSYMIiLERou-pUNcvHdWE";

    @Value("${yard.web.urlPart}")
    public String urlPart;
    
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private TelegramChatQualityService chatRepository;
    
    @Autowired
    private TelegramChatQualityService telegramChatQualityService;

    /**
     * Метод для отправки сообщения с фотографиями и тегами
     * @param chatIds
     * @param message
     * @param photoIds
     * @param tags
     */
    public void sendMessageWithPhotos(List<Long> chatIds, String message, List<String> photoIds, List<String> tags) {
        try {
            // Формируем текст сообщения с тегами
            String fullMessage = formatFullMessage(message, tags);
            
            for (Long chatId : chatIds) {
                try {
                    if (photoIds == null || photoIds.isEmpty()) {
                        // Отправка простого текстового сообщения
                        sendTextMessage(chatId, fullMessage);
                    } else {
                        // Отправка медиагруппы
                        sendMediaGroup(chatId, fullMessage, photoIds);
                    }
                } catch (Exception e) {
                    handleSendError(chatId, e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String formatFullMessage(String message, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return message;
        }
        return message + "\n\n" + tags.stream()
                .map(tag -> "#" + tag.replace(" ", "_"))
                .collect(Collectors.joining(" "));
    }
    
    private void handleSendError(Long chatId, Exception e) {
        if (e instanceof TelegramApiException) {
            String errorMessage = e.getMessage();
            
            if (errorMessage != null && (errorMessage.contains("USER_IS_BLOCKED") || 
                                       errorMessage.contains("bot was blocked"))) {
                // Удаляем заблокировавшего пользователя из базы
                telegramChatQualityService.deleteByChatId(chatId.intValue());
                System.out.println("Удален заблокировавший пользователь: " + chatId);
                return;
            }
        }
        e.printStackTrace();
    }
    
    private void sendMediaGroup(Long chatId, String caption, List<String> photoIds) throws TelegramApiException {
        List<InputMedia> mediaGroup = new ArrayList<>();
        
        for (int i = 0; i < photoIds.size(); i++) {
            String photoId = photoIds.get(i);
            ResponseEntity<Resource> photoResponse = getImage(photoId);
            
            if (photoResponse.getStatusCode() == HttpStatus.OK && photoResponse.getBody() != null) {
                byte[] photoBytes = ((ByteArrayResource) photoResponse.getBody()).getByteArray();
                InputMediaPhoto media = new InputMediaPhoto();
                media.setMedia(new ByteArrayInputStream(photoBytes), "photo" + i + ".jpg");
                
                // Добавляем подпись только к первому фото
                if (i == 0) {
                    media.setCaption(caption);
                }
                
                mediaGroup.add(media);
            }
        }
        
        if (!mediaGroup.isEmpty()) {
            SendMediaGroup group = new SendMediaGroup();
            group.setChatId(chatId.toString());
            group.setMedias(mediaGroup);
            execute(group);
            try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }

    // Метод для получения изображения (без изменений)
    public ResponseEntity<Resource> getImage(String id) {
        String externalUrl = urlPart + id;

        ResponseEntity<byte[]> response = restTemplate.exchange(
                externalUrl,
                HttpMethod.GET,
                null,
                byte[].class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            org.springframework.core.io.ByteArrayResource resource = 
                new org.springframework.core.io.ByteArrayResource(response.getBody());

            return ResponseEntity.ok()
                    .contentLength(response.getBody().length)
                    .contentType(response.getHeaders().getContentType() != null ?
                            response.getHeaders().getContentType() :
                            MediaType.IMAGE_JPEG)
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    private static final Long ADMIN_CHAT_ID = 907699213L;
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            Long senderChatId = update.getMessage().getChatId();
            String text = update.getMessage().getText().trim();
            String senderName = update.getMessage().getFrom().getFirstName();

            // Обработка команды /start
            if ("/start".equalsIgnoreCase(text)) {
                handleStartCommand(chatId);
                return;
            }

            // Проверяем что отправитель подписан
            if (chatRepository.existsById(Integer.parseInt(chatId))) {
                // Форматируем сообщение
                String formattedMessage = formatMessage(text, senderName, senderChatId);

                // Получаем всех подписчиков, исключая отправителя
                List<Long> recipients = getRecipients(senderChatId);

                // Отправляем сообщение с обработкой ошибок
                sendMessagesWithErrorHandling(formattedMessage, recipients);
            }
        }
    }

    private void sendMessagesWithErrorHandling(String message, List<Long> recipients) {
        for (Long chatId : recipients) {
            try {
                SendMessage msg = new SendMessage();
                msg.setChatId(chatId.toString());
                msg.setText(message);
                msg.enableHtml(true);
                execute(msg);
                Thread.sleep(100);
            } catch (TelegramApiException e) {
                if (e.getMessage() != null && e.getMessage().contains("USER_IS_BLOCKED")) {
                    // Удаляем заблокировавшего пользователя из базы
                    telegramChatQualityService.deleteByChatId(chatId.intValue());
                    System.out.println("Удален заблокировавший пользователь: " + chatId);
                } else {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String formatMessage(String text, String senderName, Long senderChatId) {
        return ADMIN_CHAT_ID.equals(senderChatId)
            ? "<b>🚨 ADMIN " + senderName + ":</b>\n<strong>" + text + "</strong>"
            : "<b>" + senderName + ":</b>\n" + text;
    }

    private List<Long> getRecipients(Long excludeChatId) {
        return telegramChatQualityService.getChatIdList().stream()
                .map(s -> s.getChatId().longValue())
                .filter(id -> !id.equals(excludeChatId))
                .collect(Collectors.toList());
    }

    private void handleStartCommand(String chatId) {
        SendMessage welcome;
        if (!chatRepository.existsById(Integer.parseInt(chatId))) {
            chatRepository.save(new TelegramChatQuality(Integer.parseInt(chatId)));
            welcome = new SendMessage(chatId, "Привет! Ты подписан на рассылку.");
        } else {
            welcome = new SendMessage(chatId, "Приветствую.");
        }

        try {
            execute(welcome);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onUpdateReceived(Update update) {
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            String chatId = update.getMessage().getChatId().toString();
//            Long senderChatId = update.getMessage().getChatId();
//            String text = update.getMessage().getText().trim();
//            String senderName = update.getMessage().getFrom().getFirstName();
//
//            // Обработка команды /start (без изменений)
//            if ("/start".equalsIgnoreCase(text)) {
//                SendMessage welcome;
//                if (!chatRepository.existsById(Integer.parseInt(chatId))) {
//                    chatRepository.save(new TelegramChatQuality(Integer.parseInt(chatId)));
//                    welcome = new SendMessage(chatId, "Привет! Ты подписан на рассылку.");
//                } else {
//                    welcome = new SendMessage(chatId, "Приветствую.");
//                }
//
//                try {
//                    execute(welcome);
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }
//                return;
//            }
//
//            // Проверяем что отправитель подписан
//            if (chatRepository.existsById(Integer.parseInt(chatId))) {
//                // Форматируем сообщение
//                String formattedMessage;
//                if (ADMIN_CHAT_ID.equals(senderChatId)) {
//                    formattedMessage = "<b>🚨 ADMIN " + senderName + ":</b>\n<strong>" + text + "</strong>";
//                } else {
//                    formattedMessage = "<b>" + senderName + ":</b>\n" + text;
//                }
//
//                // Получаем всех подписчиков, исключая отправителя
//                List<Long> chatIds = telegramChatQualityService.getChatIdList().stream()
//                        .map(s -> s.getChatId().longValue())
//                        .filter(id -> !id.equals(senderChatId)) // Вот ключевое изменение!
//                        .collect(Collectors.toList());
//
//                // Отправляем сообщение всем, кроме отправителя
//                for (Long id : chatIds) {
//                    try {
//                        SendMessage msg = new SendMessage();
//                        msg.setChatId(id.toString());
//                        msg.setText(formattedMessage);
//                        msg.enableHtml(true);
//                        execute(msg);
//                        Thread.sleep(100); // Небольшая задержка
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }

 // Новый метод для отправки только текстовых сообщений
    private void sendTextMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText(text);
        msg.enableHtml(true);
        execute(msg);
        try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}


