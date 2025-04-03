package by.base.main.util.bots;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText().trim();
            
            if ("/start".equalsIgnoreCase(text)) {
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

            if (text.startsWith("send ")) {
                String messageToSend = text.substring(5).trim();
                if (!messageToSend.isEmpty()) {
                    // Получаем все chatId
                	List<Long> chatIds = telegramChatQualityService.getChatIdList().stream().map(s-> s.getChatId().longValue()).collect(Collectors.toList()) ;
                    
                    // Отправляем сообщение всем
                	sendTextMessage(chatIds, messageToSend);
                    
                    // Подтверждение отправителю
                    SendMessage confirmation = new SendMessage(chatId, "Сообщение отправлено " + chatIds.size() + " подписчикам");
                    try {
                        execute(confirmation);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    SendMessage error = new SendMessage(chatId, "Пожалуйста, укажите текст сообщения после команды send");
                    try {
                        execute(error);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

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


