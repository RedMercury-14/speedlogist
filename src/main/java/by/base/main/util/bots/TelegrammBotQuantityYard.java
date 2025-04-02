package by.base.main.util.bots;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import by.base.main.model.TelegramChatQuality;
import by.base.main.service.TelegramChatQualityService;

@Component
public class TelegrammBotQuantityYard extends TelegramLongPollingBot {

//    @Value("${telegram.bot.username}")
    private String botUsername = "quantityYardBot";

//    @Value("${telegram.bot.token}")
    private String botToken = "8086048050:AAE3ZVUyw-vGshFSYMIiLERou-pUNcvHdWE";

    @Value("${yard.web.urlPart}")
    public String urlPart;
    
    private final RestTemplate restTemplate = new RestTemplate();

    
    @Autowired
    private TelegramChatQualityService chatRepository;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText().trim();
            
            System.out.println(chatId + "   " + text);
            System.out.println("chatRepository.existsById(Integer.parseInt(chatId)) = "+chatRepository.existsById(Integer.parseInt(chatId)));

            if ("/start".equalsIgnoreCase(text)) {
            	SendMessage welcome;
                if (!chatRepository.existsById(Integer.parseInt(chatId))) {
                	TelegramChatQuality telegramChatQuality = new TelegramChatQuality(Integer.parseInt(chatId));
                	System.out.println(telegramChatQuality);
                    chatRepository.save(new TelegramChatQuality(Integer.parseInt(chatId)));
                    welcome = new SendMessage(chatId, "Привет! Ты подписан на рассылку.");
                }else {
                	welcome = new SendMessage(chatId, "Приветствую.");                	
                }

                try {
                    execute(welcome);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

            if (text.startsWith("send ")) {
                String[] ids = text.substring(5).split(",");
                sendPhotosWithMessage(Integer.parseInt(chatId), "Вот твои фотки", Arrays.asList(ids));
            }
        }
    }

    public void sendPhotosWithMessage(Integer chatId, String message, List<String> ids) {
        List<InputMedia> mediaList = new ArrayList<>();

        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i).trim();
            byte[] imageBytes = fetchImageBytes(id);

            if (imageBytes != null) {
                InputMediaPhoto mediaPhoto = new InputMediaPhoto();
                mediaPhoto.setMedia(new ByteArrayInputStream(imageBytes), "photo" + i + ".jpg");

                if (i == 0) {
                    mediaPhoto.setCaption(message);
                    mediaPhoto.setParseMode("HTML");
                }

                mediaList.add(mediaPhoto);
            }
        }

        if (!mediaList.isEmpty()) {
            SendMediaGroup sendMediaGroup = new SendMediaGroup();
            sendMediaGroup.setChatId(chatId.toString());
            sendMediaGroup.setMedias(mediaList);

            try {
                execute(sendMediaGroup);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * основной метод отправки сообщения с фото
     * @param message
     * @param ids
     */
    public void sendMessageWithPhotosToAll(String message, List<String> ids) {
        List<TelegramChatQuality> allChats = chatRepository.getChatIdList();
        for (TelegramChatQuality chat : allChats) {
            sendPhotosWithMessage(chat.getChatId(), message, ids);
        }
    }

    private byte[] fetchImageBytes(String id) {
        try {
            String url = urlPart + id;
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    byte[].class
            );
            return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
        } catch (Exception e) {
            return null;
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

