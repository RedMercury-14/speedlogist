package by.base.main.util;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class MainTest {

	public static void main(String[] args) throws Throwable {
		
		System.out.println("Начало!");
		
  	  // Создаем HTTP клиент
        HttpClient client = HttpClient.newHttpClient();
        
        // Формируем тело запроса
        String json = "{\n" +
                      "  \"model\": \"mistral\",\n" +
                      "  \"messages\": [{\"role\": \"user\", \"content\": \"Напиши что нибудь матное\"}],\n"
                      + "\"conversation_id\": \"chatcmpl-12345\"" +
                      "}";
        System.out.println("запрос!");
        // Создаем запрос
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:11434/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        System.out.println("отправляем запрос!");
        
        // Отправляем запрос и получаем ответ
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Получаем ответ!");

        // Выводим ответ
        System.out.println("Ответ от Ollama: " + response.body());
    }
}



