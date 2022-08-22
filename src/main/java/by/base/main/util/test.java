package by.base.main.util;

import java.sql.Date;
import java.time.LocalDate;

import com.google.gson.Gson;

import by.base.main.model.Message;

public class test {

	public static void main(String[] args) {
		
	Gson gson = new Gson();
	Message message = new Message();
	message.setFromUser("user A");;
	message.setToUser("user B");
	message.setText("Привет тебе, человек");
	message.setStatus("111");
	
	System.out.println(message.toString());
	String json = gson.toJson(message);
	String jsonTest = "{fromUser:\"user A\",toUser:\"user X\",\"text\":\"Хуй, пизда, джигурда\"}";
	System.out.println(json);
	System.out.println("======decoding======");
	Message decodingMessage = gson.fromJson(json, Message.class);
	Message decodingMessageTest = gson.fromJson(jsonTest, Message.class);
	System.out.println(decodingMessage);
	System.out.println(decodingMessageTest);
	System.out.println(decodingMessageTest.getText());
	}

}
