package by.base.main.util.bots;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@Component
public class KeyboardMaker {
	
	/**
	 * Отдаёт две кнопки Включить рассылку и инфо
	 * @return
	 */
    public ReplyKeyboardMarkup getMainMenuKeyboard() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Включить рассылку"));
        row1.add(new KeyboardButton("Инфо"));
        
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }
    
	/**
	 * Отдаёт две кнопки ВЫКЛЮЧИТЬ рассылку и инфо
	 * @return
	 */
    public ReplyKeyboardMarkup getSecondMenuKeyboard() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Выключить рассылку"));
        row1.add(new KeyboardButton("Инфо"));
        
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        return replyKeyboardMarkup;
    }
    
    /**
     * Начальная клавиатура после приветствия
     * <br>Зарегистрироваться   Инфо
     * <br>Подключиться по номеру телефона
     * @return
     */
    public ReplyKeyboardMarkup getStartKeyboardTest () {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Зарегистрироваться"));
        row1.add(new KeyboardButton("Инфо"));
        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton reg = new KeyboardButton("Подключиться по номеру телефона");
        reg.setRequestContact(true);
//        reg.setRequestLocation(true);
        row2.add(reg);
        
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        
        return replyKeyboardMarkup;
    }
    
    /**
     * Главная клава для юзеров
     * @return
     */
    public ReplyKeyboardMarkup getMainKeyboard () {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Заявить машину на завтра"));
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Заявить машину на дату"));
        KeyboardButton reg = new KeyboardButton("Тест координат");
//        reg.setRequestContact(true);
        reg.setRequestLocation(true);
        row2.add(reg);
        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("Список машин заявленных на завтра"));
        
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        
        return replyKeyboardMarkup;
    }
    
    public ReplyKeyboardMarkup getStartKeyboard () {
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton reg = new KeyboardButton("Войти по номеру телефона");
        reg.setRequestContact(true);
//        reg.setRequestLocation(true);
        row1.add(reg);
        
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        
        return replyKeyboardMarkup;
    }
    
    /**
     * Клавиатура с паллетами для сообщения
     * @return
     */
    public InlineKeyboardMarkup getPallMessageKeyboard(String numTruck) {
    	InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
    	List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

    	for (int i = 4; i <= 33; i += 3) {
    	    List<InlineKeyboardButton> rowInline = new ArrayList<>();
    	    
    	    InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
    	    inlineKeyboardButton1.setText(i + " паллеты");
    	    inlineKeyboardButton1.setCallbackData(numTruck+"_"+i + "_pall");
    	    rowInline.add(inlineKeyboardButton1);
    	    
    	    if (i + 1 <= 33) {
    	        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
    	        inlineKeyboardButton2.setText((i + 1) + " паллеты");
    	        inlineKeyboardButton2.setCallbackData(numTruck+"_"+(i + 1) + "_pall");
    	        rowInline.add(inlineKeyboardButton2);
    	    }
    	    
    	    if (i + 2 <= 33) {
    	        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
    	        inlineKeyboardButton3.setText((i + 2) + " паллеты");
    	        inlineKeyboardButton3.setCallbackData(numTruck+"_"+(i + 2) + "_pall");
    	        rowInline.add(inlineKeyboardButton3);
    	    }
    	    
    	    rowsInline.add(rowInline);
    	}
		
		
		markupInline.setKeyboard(rowsInline);
        
        return markupInline;
    }
    
    /**
     * Клава с типами авто тент/реф для сообщения
     * @param numTruck
     * @return
     */
    public InlineKeyboardMarkup getTypeTruckKeyboard(String numTruck) {
    	InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
    	List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
    	List<InlineKeyboardButton> rowInline = new ArrayList<>();
    	
    	InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
	    inlineKeyboardButton1.setText("Тент");
	    inlineKeyboardButton1.setCallbackData(numTruck+"_Тент" );
	    rowInline.add(inlineKeyboardButton1);
	    
	    InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
	    inlineKeyboardButton2.setText("Реф");
	    inlineKeyboardButton2.setCallbackData(numTruck+"_Рефрижератор");
	    rowInline.add(inlineKeyboardButton2);
    	
	    rowsInline.add(rowInline);
		
		markupInline.setKeyboard(rowsInline);
        
        return markupInline;
    }
    
    /**
     * Кнопки да/нет для сообщения
     * @param numTruck
     * @return
     */
    public InlineKeyboardMarkup getYesNoKeyboard(String numTruck) {
    	InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
    	List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
    	List<InlineKeyboardButton> rowInline = new ArrayList<>();
    	
    	InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
	    inlineKeyboardButton1.setText("Да");
	    inlineKeyboardButton1.setCallbackData(numTruck+"_yes" );
	    rowInline.add(inlineKeyboardButton1);
	    
	    InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
	    inlineKeyboardButton2.setText("Нет");
	    inlineKeyboardButton2.setCallbackData(numTruck+"_no");
	    rowInline.add(inlineKeyboardButton2);
    	
	    rowsInline.add(rowInline);
		
		markupInline.setKeyboard(rowsInline);
        
        return markupInline;
    }
    
    /**
     * Кнопки отменить, удалить, редактировать для сообщения
     * @param numTruck
     * @return
     */
    public InlineKeyboardMarkup getCancelDeleteEditKeyboard(String numTruck) {
    	InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
    	List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
    	List<InlineKeyboardButton> rowInline = new ArrayList<>();
    	
    	InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
	    inlineKeyboardButton1.setText("Отменить");
	    inlineKeyboardButton1.setCallbackData(numTruck+"_cancel" );
	    rowInline.add(inlineKeyboardButton1);
	    
	    InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
	    inlineKeyboardButton2.setText("Удалить");
	    inlineKeyboardButton2.setCallbackData(numTruck+"_delete");
	    rowInline.add(inlineKeyboardButton2);
	    
	    InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
	    inlineKeyboardButton3.setText("Редактировать");
	    inlineKeyboardButton3.setCallbackData(numTruck+"_edit");
	    rowInline.add(inlineKeyboardButton3);
    	
	    rowsInline.add(rowInline);
		
		markupInline.setKeyboard(rowsInline);
        
        return markupInline;
    }

}
