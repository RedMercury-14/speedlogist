package by.base.main.service.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class PrilesieService {
	
	/**
	 * <br>Заменяет кириллицу на латиницу в номерах машин</br>.
	 * @author Ira
	 */
	public String transliterateToVisualLatin(String text) {
	    Map<Character, Character> transliterationMap = new HashMap<>();
	    transliterationMap.put('А', 'A');
	    transliterationMap.put('В', 'B');
	    transliterationMap.put('Е', 'E');
	    transliterationMap.put('К', 'K');
	    transliterationMap.put('М', 'M');
	    transliterationMap.put('Н', 'H');
	    transliterationMap.put('О', 'O');
	    transliterationMap.put('Р', 'P');
	    transliterationMap.put('С', 'C');
	    transliterationMap.put('Т', 'T');
	    transliterationMap.put('У', 'Y');
	    transliterationMap.put('Х', 'X');
	    if (text == null || text.isEmpty()) {
	        return text;
	    }
	    StringBuilder result = new StringBuilder(text.length());
	    for (char c : text.toCharArray()) {
	        result.append(transliterationMap.getOrDefault(c, c));
	    }
	    return result.toString();
	}
	
	/**
	 * <br>Приведение номеров телефонов к виду 80*********</br>.
	 * @author Ira
	 */
	public String phoneConverter(String phone) {
	    String result;
	        if (phone != null && !phone.isEmpty()) {
	            String regex = "(?:\\+375|375|8)[\\s(]?0?(29|33|44|25)[)\\s-]*(\\d{3})[\\s-]?(\\d{2})[\\s-]?(\\d{2})";
	            Pattern pattern = Pattern.compile("(\\+375|375|8)[\\s\\-]*\\(?0?(29|33|44|25)\\)?([\\s\\-]*\\d){7,}");
	            Matcher matcher = pattern.matcher(phone);

	            if (matcher.find()) {
	                String match = matcher.group();
	                String digits = match.replaceAll("\\D", "");

	                if (digits.startsWith("375")) {
	                    result = "80" + digits.substring(3);
	                } else if (digits.startsWith("80")) {
	                    result = digits;// всё ок
	                } else if (digits.startsWith("8") && !digits.startsWith("80")) {
	                    result = "80" + digits.substring(1);
	                } else if (digits.startsWith("0")) {
	                    result = "80" + digits.substring(1);
	                } else {
	                    result  = phone;
	                }
	            }
	            else {
	                result = phone;
	            }
	        } else {
	            result = phone;
	        }
	    return result;
	}

}
