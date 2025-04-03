package by.base.main.util.bots;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
            // Формируем строку с тегами
            String tagsString = "";
            if (tags != null && !tags.isEmpty()) {
                tagsString = "\n\n" + tags.stream()
                        .map(tag -> "#" + tag.replace(" ", "_"))
                        .collect(Collectors.joining(" "));
            }

            for (Long chatId : chatIds) {
                if (photoIds != null && !photoIds.isEmpty()) {
                    for (int i = 0; i < photoIds.size(); i += 10) {
                        List<String> batch = photoIds.subList(i, Math.min(photoIds.size(), i + 10));

                        // Формируем полный текст для этой группы (сообщение + теги)
                        String fullCaption = null;
                        if (message != null || tagsString != null) {
                            fullCaption = (message != null ? message : "") + 
                                       (tagsString != null ? tagsString : "");
                        }

                        if (batch.size() == 1) {
                            // Отправка одной фотографии
                            String photoId = batch.get(0);
                            ResponseEntity<Resource> photoResponse = getImage(photoId);
                            
                            if (photoResponse.getStatusCode() == HttpStatus.OK && photoResponse.getBody() != null) {
                                byte[] photoBytes = ((org.springframework.core.io.ByteArrayResource) photoResponse.getBody()).getByteArray();
                                
                                SendPhoto photo = new SendPhoto();
                                photo.setChatId(chatId.toString());
                                photo.setPhoto(new InputFile(new ByteArrayInputStream(photoBytes), "photo.jpg"));
                                
                                // Добавляем полный текст (сообщение + теги)
                                if (fullCaption != null && !fullCaption.isEmpty()) {
                                    photo.setCaption(fullCaption);
                                }
                                
                                execute(photo);
                            }
                        } else {
                            // Отправка группы фотографий (2-10)
                            List<InputMedia> mediaGroup = new ArrayList<>();
                            
                            for (int j = 0; j < batch.size(); j++) {
                                String photoId = batch.get(j);
                                ResponseEntity<Resource> photoResponse = getImage(photoId);
                                
                                if (photoResponse.getStatusCode() == HttpStatus.OK && photoResponse.getBody() != null) {
                                    byte[] photoBytes = ((org.springframework.core.io.ByteArrayResource) photoResponse.getBody()).getByteArray();
                                    
                                    InputMediaPhoto mediaPhoto = new InputMediaPhoto();
                                    mediaPhoto.setMedia(new ByteArrayInputStream(photoBytes), "photo" + j + ".jpg");
                                    
                                    // Добавляем полный текст только к первой фотографии в группе
                                    if (j == 0 && fullCaption != null && !fullCaption.isEmpty()) {
                                        mediaPhoto.setCaption(fullCaption);
                                    }
                                    
                                    mediaGroup.add(mediaPhoto);
                                }
                            }
                            
                            if (!mediaGroup.isEmpty()) {
                                SendMediaGroup mediaGroupMessage = new SendMediaGroup();
                                mediaGroupMessage.setChatId(chatId.toString());
                                mediaGroupMessage.setMedias(mediaGroup);
                                execute(mediaGroupMessage);
                            }
                        }
                        
                        // Небольшая пауза между группами
                        Thread.sleep(300);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Обработка ошибок
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
    public void sendTextMessage(List<Long> chatIds, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        for (Long chatId : chatIds) {
            try {
                SendMessage textMessage = new SendMessage();
                textMessage.setChatId(chatId.toString());
                textMessage.setText(message);
                execute(textMessage);
                
                // Небольшая задержка, чтобы не превысить лимиты Telegram
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
                // Можно добавить логирование ошибок
            }
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


