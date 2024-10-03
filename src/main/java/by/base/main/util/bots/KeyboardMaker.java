package by.base.main.util.bots;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@Component
public class KeyboardMaker {
	
	private HashMap<String, TariffInfo> palletToTariffMap = new HashMap<>();
	
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
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        
        return replyKeyboardMarkup;
    }
    
    /**
     * Главная клава для юзеров
     * @return
     */
    public ReplyKeyboardMarkup getMainKeyboard () {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Заявить машину на завтра"));
        row1.add(new KeyboardButton("Заявить машину на дату"));
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Список машин заявленных на сегодня и завтра"));
        row2.add(new KeyboardButton("Список всех заявленных машин"));
//        KeyboardButton reg = new KeyboardButton("Тест координат");
//        reg.setRequestContact(true);
//        reg.setRequestLocation(true);
//        row2.add(reg);
//        KeyboardRow row3 = new KeyboardRow();
        
        
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
//        keyboard.add(row3);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true); // Указываем, что клавиатура должна быть показана только для определенного пользователя
        replyKeyboardMarkup.setResizeKeyboard(true); // Устанавливаем флаг, который позволяет изменять размер кнопок в зависимости от содержимого
        replyKeyboardMarkup.setOneTimeKeyboard(false); // Указываем, что клавиатура будет скрыта после использования (если это одноразовая клавиатура)
        
        return replyKeyboardMarkup;
    }
    
    /**
     * Глобальная отмена действия
     * @return
     */
    public ReplyKeyboardMarkup getMainCancelKeyboard () {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Отменить действие"));
        
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row1);

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setSelective(true); // Указываем, что клавиатура должна быть показана только для определенного пользователя
        replyKeyboardMarkup.setResizeKeyboard(true); // Устанавливаем флаг, который позволяет изменять размер кнопок в зависимости от содержимого
        replyKeyboardMarkup.setOneTimeKeyboard(true); // Указываем, что клавиатура будет скрыта после использования (если это одноразовая клавиатура)
        
        return replyKeyboardMarkup;
    }
    
    
    
    /**
     * клава Войти по номеру телефона
     * @return
     */
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
     * старая. тупо от 4 до 33 паллет
     * @return
     */
    @Deprecated
    public InlineKeyboardMarkup getPallMessageKeyboardOld(String numTruck) {
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
     * Клавиатура с паллетами для сообщения
     * @param numTruck
     * @return
     */
    public InlineKeyboardMarkup getPallMessageKeyboardNew(String numTruck) {
    	InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<Integer> palletNumbers = Arrays.asList(4, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 32, 33, 36, 38);
        
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        for (int i = 0; i < palletNumbers.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            int palletNumber = palletNumbers.get(i);
            button.setText(palletNumber + " паллеты");
            button.setCallbackData(numTruck+"_" + palletNumber + "_pall");
            rowInline.add(button);
            
            // Add row to rowsInline and reset rowInline every 3 buttons
            if ((i + 1) % 3 == 0 || i == palletNumbers.size() - 1) {
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
            }
        }
        
        markupInline.setKeyboard(rowsInline);
		return markupInline;
    }
    
    /**
     * Клавиатура с весом для сообщения в зависимости от паллет
     * @param pall
     * @return
     */
    public InlineKeyboardMarkup getWeigthKeyboard(String pall) {
    	if(palletToTariffMap.size() == 0) {
            palletToTariffMap.put("4", new TariffInfo(2.0, 4.0));
            palletToTariffMap.put("6", new TariffInfo(2.0, 4.0));
            palletToTariffMap.put("7", new TariffInfo(2.0, 6.0));
            palletToTariffMap.put("8", new TariffInfo(2.0, 6.0));
            palletToTariffMap.put("9", new TariffInfo(2.0, 6.0));
            palletToTariffMap.put("10", new TariffInfo(2.0, 6.0));
            palletToTariffMap.put("11", new TariffInfo(4.1, 6.0));
            palletToTariffMap.put("12", new TariffInfo(4.1, 8.0));
            palletToTariffMap.put("13", new TariffInfo(4.1, 8.0));
            palletToTariffMap.put("14", new TariffInfo(4.1, 10.0));
            palletToTariffMap.put("15", new TariffInfo(4.1, 16.0));
            palletToTariffMap.put("16", new TariffInfo(4.1, 16.0));
            palletToTariffMap.put("17", new TariffInfo(6.1, 16.0));
            palletToTariffMap.put("18", new TariffInfo(6.1, 16.0));
            palletToTariffMap.put("19", new TariffInfo(8.1, 16.0));
            palletToTariffMap.put("20", new TariffInfo(8.1, 16.0));
            palletToTariffMap.put("21", new TariffInfo(8.1, 16.0));
            palletToTariffMap.put("22", new TariffInfo(8.1, 16.0));
            palletToTariffMap.put("23", new TariffInfo(12.1, 16.0));
            palletToTariffMap.put("32", new TariffInfo(16.1, 21.0));
            palletToTariffMap.put("33", new TariffInfo(16.1, 21.0));
            palletToTariffMap.put("36", new TariffInfo(21.1, 22.0));
            palletToTariffMap.put("38", new TariffInfo(21.1, 22.0));
    	}
    	
    	TariffInfo tariffInfo = palletToTariffMap.get(pall);
		return buttonWeigthCreater(tariffInfo.getMinTonnage(),tariffInfo.getMaxTonnage(),0.2);    	
    }
    
    /**
     * формирует сетку кнопок с весом, по минимальному и максимальному значениям весов
     * @param minWeight
     * @param maxWeight
     * @param step
     * @return
     */
    private InlineKeyboardMarkup buttonWeigthCreater(double minWeight, double maxWeight, double step) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        
        // создаём минимальное значение веса
        InlineKeyboardButton inlineKeyboardButtonStart = new InlineKeyboardButton();
        inlineKeyboardButtonStart.setText(roundDouble(minWeight, 1) + " т.");
        inlineKeyboardButtonStart.setCallbackData(roundDouble(minWeight, 1) + "_weight");
        
        rowInline.add(inlineKeyboardButtonStart);
        
        if(minWeight % 2 != 0) {
            minWeight = minWeight - 0.1;
        }

        for (double weight = minWeight + step; weight < maxWeight; weight += step) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(roundDouble(weight, 1) + " т.");
            inlineKeyboardButton.setCallbackData(roundDouble(weight, 1) + "_weight");
            
            rowInline.add(inlineKeyboardButton);

            // If we have 4 buttons in a row, add the row to rowsInline and start a new row
            if (rowInline.size() == 4) {
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
            }
        }

        // Добавляем кнопку с максимальным значением веса, если она ещё не была добавлена
        InlineKeyboardButton inlineKeyboardButtonEnd = new InlineKeyboardButton();
        inlineKeyboardButtonEnd.setText(roundDouble(maxWeight, 1) + " т.");
        inlineKeyboardButtonEnd.setCallbackData(roundDouble(maxWeight, 1) + "_weight");
        
        rowInline.add(inlineKeyboardButtonEnd);

        // Add the last row if it has less than 4 buttons and is not empty
        if (!rowInline.isEmpty()) {
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
	    
	    InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
	    inlineKeyboardButton3.setText("Изотерма");
	    inlineKeyboardButton3.setCallbackData(numTruck+"_Изотерма");
	    rowInline.add(inlineKeyboardButton3);
    	
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
    public InlineKeyboardMarkup getCancelDeleteEditKeyboard(String numTruck, Date date) {
    	InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
    	List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
    	List<InlineKeyboardButton> rowInline = new ArrayList<>();
    	
    	InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
	    inlineKeyboardButton1.setText("Отменить");
	    inlineKeyboardButton1.setCallbackData("cancelTruck_"+numTruck + "_" + date.toString());
	    rowInline.add(inlineKeyboardButton1);
	    
//	    InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
//	    inlineKeyboardButton2.setText("Удалить");
//	    inlineKeyboardButton2.setCallbackData("deleteTruck_"+numTruck);
//	    rowInline.add(inlineKeyboardButton2);
	    
//	    InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
//	    inlineKeyboardButton3.setText("Редактировать");
//	    inlineKeyboardButton3.setCallbackData("editTruck_"+numTruck);
//	    rowInline.add(inlineKeyboardButton3);
    	
	    rowsInline.add(rowInline);
		
		markupInline.setKeyboard(rowsInline);
        
        return markupInline;
    }
    
    private static double roundDouble(double value, int places) {
		double scale = Math.pow(10, places);
		return Math.round(value * scale) / scale;
	}
    
    class TariffInfo {
        private double minTonnage;
        private double maxTonnage;

        public TariffInfo(double minTonnage, double maxTonnage) {
            this.minTonnage = minTonnage;
            this.maxTonnage = maxTonnage;
        }

        public double getMinTonnage() {
            return minTonnage;
        }

        public double getMaxTonnage() {
            return maxTonnage;
        }

        @Override
        public String toString() {
            return "TariffInfo{" +
                    "minTonnage=" + minTonnage +
                    ", maxTonnage=" + maxTonnage +
                    '}';
        }
    }

}
