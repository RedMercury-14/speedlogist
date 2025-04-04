package by.base.main.util.bots;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

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

    private static final Long ADMIN_CHAT_ID = 907699213L;
    private static final int MAX_MEDIA_GROUP_SIZE = 10;

    private static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024; // 5MB
    private static final float COMPRESSION_QUALITY = 0.5f; // Качество сжатия (70%)
    
    private byte[] processImageDimensions(byte[] imageData) throws IOException {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));
        
        // Минимальные требования Telegram
        int minWidth = 160;
        int minHeight = 160;
        int maxWidth = 5000;
        int maxHeight = 5000;
        
        // Получаем текущие размеры
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        
        // Проверяем минимальные размеры
        if (width < minWidth || height < minHeight) {
            throw new IOException("Изображение слишком маленькое");
        }
        
        // Если размеры в допустимых пределах - возвращаем как есть
        if (width <= maxWidth && height <= maxHeight) {
            return imageData;
        }
        
        // Масштабируем слишком большие изображения
        double ratio = Math.min((double)maxWidth/width, (double)maxHeight/height);
        int newWidth = (int)(width * ratio);
        int newHeight = (int)(height * ratio);
        
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        return baos.toByteArray();
    }
    
    /**
     * Метод для получения и обработки изображения
     * Меняет их в зависимости от размера изображения и веса изображения 
     */
//    public byte[] getProcessedImage(String id) throws IOException {
//        ResponseEntity<Resource> response = getImage(id);
//        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
//            throw new IOException("Не удалось загрузить изображение");
//        }
//
//        byte[] imageBytes = ((ByteArrayResource) response.getBody()).getByteArray();
//        
//        // Если изображение меньше максимального размера - возвращаем как есть
//        if (imageBytes.length <= MAX_IMAGE_SIZE) {
//        	System.out.println("возвращаем как есть");
//            return imageBytes;
//        }
//        
//        // Сжимаем изображение
////        return compressImage(imageBytes);
//        return processImageDimensions(imageBytes);
//        
//    }
    public byte[] getProcessedImage(String id) throws IOException {
        ResponseEntity<Resource> response = getImage(id);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new IOException("Не удалось загрузить изображение");
        }

        byte[] imageBytes = ((ByteArrayResource) response.getBody()).getByteArray();

        // Проверка размеров (ширина/высота)
        if (isPhotoInvalidForTelegram(imageBytes)) {
        	System.out.println("возвращаем уменьшенное изображение");
            return processImageDimensions(imageBytes);
        }

        // Проверка размера файла
        if (imageBytes.length > MAX_IMAGE_SIZE) {
        	System.out.println("возвращаем сжатое изображение");
            return compressImage(imageBytes);
        }

        return imageBytes;
    }
    
    /**
     * Метод проверки размеров изображения
     * @param imageBytes
     * @return
     * @throws IOException
     */
    private boolean isPhotoInvalidForTelegram(byte[] imageBytes) throws IOException {
        try (InputStream in = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(in);
            if (image == null) {
                throw new IOException("Не удалось прочитать изображение");
            }

            int width = image.getWidth();
            int height = image.getHeight();
            
//            System.out.println("width = " + width + ";   height" + height);

            return width < 160 || height < 160 || width > 6000 || height > 6000;
        }
    }

    
    /**
     * Метод для сжатия изображения
     */
    private byte[] compressImage(byte[] originalImage) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(originalImage));
        
        // Проверяем, что изображение было успешно прочитано
        if (image == null) {
            throw new IOException("Неподдерживаемый формат изображения");
        }

        // Создаем буфер для сжатого изображения
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        
        // Получаем writer для JPEG
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(compressed);
        writer.setOutput(ios);
        
        // Настраиваем параметры сжатия
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(COMPRESSION_QUALITY);
        }
        
        // Записываем сжатое изображение
        writer.write(null, new IIOImage(image, null, null), param);
        
        // Освобождаем ресурсы
        writer.dispose();
        ios.close();
        
        return compressed.toByteArray();
    }
    
    /**
     * Метод для отправки сообщения с фотографиями и тегами
     * @param chatIds - список ID чатов для отправки
     * @param message - текст сообщения
     * @param photoIds - список ID фотографий
     * @param tags - список тегов
     */
    public void sendMessageWithPhotos(List<Long> chatIds, String message, List<String> photoIds, List<String> tags) {
        try {
            String fullMessage = formatFullMessage(message, tags);
            
            for (Long chatId : chatIds) {
                try {
                    if (photoIds == null || photoIds.isEmpty()) {
                        sendTextMessage(chatId, fullMessage);
                    } else {
                        sendPhotosInBatches(chatId, fullMessage, photoIds);
                    }
                } catch (Exception e) {
                    handleSendError(chatId, e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Разбивает фото на группы и отправляет их
     */
    private void sendPhotosInBatches(Long chatId, String caption, List<String> photoIds) throws TelegramApiException {
        for (int i = 0; i < photoIds.size(); i += MAX_MEDIA_GROUP_SIZE) {
            List<String> batch = photoIds.subList(i, Math.min(photoIds.size(), i + MAX_MEDIA_GROUP_SIZE));
            
            if (batch.size() == 1) {
                sendSinglePhoto(chatId, caption, batch.get(0));
            } else {
                sendMediaGroup(chatId, caption, batch);
            }
            
            try {
                Thread.sleep(500); // Задержка между группами
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Отправка одного фото
     */
    private void sendSinglePhoto(Long chatId, String caption, String photoId) throws TelegramApiException {
        try {
            byte[] imageBytes = getProcessedImage(photoId);
            SendPhoto photo = new SendPhoto();
            photo.setChatId(chatId.toString());
            photo.setPhoto(new InputFile(new ByteArrayInputStream(imageBytes), "photo.jpg"));
            if (caption != null) {
                photo.setCaption(caption);
                photo.setParseMode("HTML");
            }
            execute(photo);
        } catch (Exception e) {
            throw new TelegramApiException("Ошибка отправки фото: " + e.getMessage());
        }
    }

    /**
     * Отправка группы фото (2-10)
     */
    private void sendMediaGroup(Long chatId, String caption, List<String> photoIds) throws TelegramApiException {
        List<InputMedia> mediaGroup = new ArrayList<>();
        
        for (int i = 0; i < photoIds.size(); i++) {
            String photoId = photoIds.get(i);
            try {
                byte[] imageBytes = getProcessedImage(photoId);
                
             // Получаем размер в байтах
                long sizeInBytes = imageBytes.length;

                // Конвертируем в мегабайты (1 MB = 1024 * 1024 bytes)
                double sizeInMB = (double) sizeInBytes / (1024 * 1024);

                // Форматируем вывод до 2 знаков после запятой
                String formattedSize = String.format("%.2f", sizeInMB);

                System.out.println("Размер изображения: " + formattedSize + " MB");
                
                InputMediaPhoto media = new InputMediaPhoto();
                media.setMedia(new ByteArrayInputStream(imageBytes), "photo_" + i + ".jpg");
                media.setParseMode("HTML");
                
                if (i == 0 && caption != null) {
                    media.setCaption(caption);
                }
                
                mediaGroup.add(media);
            } catch (Exception e) {
                System.err.println("Ошибка обработки фото " + photoId + ": " + e.getMessage());
            }
        }
        
        if (!mediaGroup.isEmpty()) {
            SendMediaGroup group = new SendMediaGroup();
            group.setChatId(chatId.toString());
            group.setMedias(mediaGroup);
            execute(group);
        } else {
            throw new TelegramApiException("Не удалось подготовить ни одного фото для отправки");
        }
    }

    /**
     * Форматирование сообщения с тегами
     */
    private String formatFullMessage(String message, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return message;
        }
        return message + "\n\n" + tags.stream()
                .map(tag -> "#" + tag.replace(" ", "_"))
                .collect(Collectors.joining(" "));
    }

    /**
     * Обработка ошибок отправки
     */
    private void handleSendError(Long chatId, Exception e) {
        if (e instanceof TelegramApiException) {
            String errorMessage = e.getMessage();
            
            if (errorMessage != null && (errorMessage.contains("USER_IS_BLOCKED") || 
                                       errorMessage.contains("bot was blocked"))) {
                telegramChatQualityService.deleteByChatId(chatId.intValue());
                System.out.println("Удален заблокировавший пользователь: " + chatId);
                return;
            }
        }
        e.printStackTrace();
    }

    /**
     * Получение изображения по ID
     */
    public ResponseEntity<Resource> getImage(String id) {
        String externalUrl = urlPart + id;

        ResponseEntity<byte[]> response = restTemplate.exchange(
                externalUrl,
                HttpMethod.GET,
                null,
                byte[].class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            ByteArrayResource resource = new ByteArrayResource(response.getBody());
            return ResponseEntity.ok()
                    .contentLength(response.getBody().length)
                    .contentType(response.getHeaders().getContentType() != null ?
                            response.getHeaders().getContentType() :
                            MediaType.IMAGE_JPEG)
                    .body(resource);
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            Long senderChatId = update.getMessage().getChatId();
            String text = update.getMessage().getText().trim();
            String senderName = update.getMessage().getFrom().getFirstName();

            if ("/start".equalsIgnoreCase(text)) {
                handleStartCommand(chatId);
                return;
            }

            if (chatRepository.existsById(Integer.parseInt(chatId))) {
                String formattedMessage = formatMessage(text, senderName, senderChatId);
                List<Long> recipients = getRecipients(senderChatId);
                sendMessagesWithErrorHandling(formattedMessage, recipients);
            }
        }
    }

    private void sendMessagesWithErrorHandling(String message, List<Long> recipients) {
        for (Long chatId : recipients) {
            try {
                sendTextMessage(chatId, message);
            } catch (TelegramApiException e) {
                if (e.getMessage() != null && e.getMessage().contains("USER_IS_BLOCKED")) {
                    telegramChatQualityService.deleteByChatId(chatId.intValue());
                    System.out.println("Удален заблокировавший пользователь: " + chatId);
                } else {
                    e.printStackTrace();
                }
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

    private void sendTextMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId.toString());
        msg.setText(text);
        msg.enableHtml(true);
        execute(msg);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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


