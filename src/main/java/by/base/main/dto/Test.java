package by.base.main.dto;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

public class Test {

	public static void main(String[] args) {
		//serviceNumber CD6AE87C-2477-4852-A4E7-8BA5BD01C156
		String url = "https://api.dobronom.by:10896/Json";
		Gson gson = new Gson();
		MarketErrorDto errorDto = new MarketErrorDto("ERROR", "Fatal ERROR");
//		MarketJWTDto errorDto = new MarketJWTDto("fdjihghidfhgoidhufg");
		MarketTableDto tableDto = new MarketTableDto();
		Object[] obj = new Object [] {errorDto};
		tableDto.setTable(obj);
		String result = gson.toJson(tableDto);
//		System.out.println(result);
//		String json = "{\"Table\":[{\"Error\":\"ERROR\",\"ErrorDescription\":\"Fatal ERROR\"}]}";
//		System.out.println(json);
		MarketTableDto tableDtoNEW = new MarketTableDto();
		tableDtoNEW = gson.fromJson(result, MarketTableDto.class);
		
		//тестируем запрос:
		MarketDataDto dataDto = new MarketDataDto("SpeedLogist", "12345678", "1");
		MarketPacketDto packetDto = new MarketPacketDto("null", "GetJWT", "CD6AE87C-2477-4852-A4E7-8BA5BD01C156", dataDto);
		MarketRequestDto requestDto = new MarketRequestDto("", packetDto);
//		System.out.println(requestDto);
//		System.err.println(gson.toJson(requestDto));
		//работает
		
		//тестируем ответ от маркета
		//ошибки и JWT
		String shortStr = result.split("\\[")[1].split("\\]")[0];
//		System.out.println(shortStr);
		MarketErrorDto test1 = gson.fromJson(shortStr, MarketErrorDto.class);
		MarketJWTDto test2 = gson.fromJson(shortStr, MarketJWTDto.class);
		
//		if(test1.getErrorDescription() != null) {
//			System.err.println(test1);
//		}
//		if(test2.getJWT() != null) {
//			System.out.println(test2);
//		}
		//работает
		System.err.println(gson.toJson(requestDto));
		String str = postRequest(url, gson.toJson(requestDto));
		System.out.println(str);
		String urlTest = "https://postman-echo.com/post";
//		System.out.println(postRequest(urlTest, gson.toJson(requestDto)));
	}
	
	 private static String postRequest(String url, String payload) {
	        try {
	            URL urlForPost = new URL(url);
	            HttpURLConnection connection = (HttpURLConnection) urlForPost.openConnection();
	            connection.setRequestMethod("POST");
	            connection.setRequestProperty("Accept", "application/json");
	            connection.setDoOutput(true);
	           
	            byte[] postData = payload.getBytes(StandardCharsets.UTF_8);
	            
	            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
	                wr.write(postData);
	            }
	            
	            int getResponseCode = connection.getResponseCode();
	            System.out.println("POST Response Code: " + getResponseCode);

	            if (getResponseCode == HttpURLConnection.HTTP_OK) {
	            	StringBuilder response = new StringBuilder();
	            	StringBuilder response2 = new StringBuilder();
	            	try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
	                    String inputLine;	 
	                    while ((inputLine = in.readLine()) != null) {
	                    	response.append(inputLine);
	                    }
	                    in.close();
	                }	           
	            	System.out.println(connection.getContentType());
	                return response.toString();
	            } else {
	                return null;
	            }
	        } catch (IOException e) {
	            System.out.println("Подключение недоступно");
	            return "error";
	        }
	    }

}
