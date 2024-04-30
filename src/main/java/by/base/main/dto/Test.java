package by.base.main.dto;

import com.google.gson.Gson;

public class Test {

	public static void main(String[] args) {
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
		MarketDataDto dataDto = new MarketDataDto("dobro", "12414", "1");
		MarketPacketDto packetDto = new MarketPacketDto("null", "GetJWT", "serviseNum124", dataDto);
		MarketRequestDto requestDto = new MarketRequestDto("", packetDto);
//		System.out.println(requestDto);
//		System.err.println(gson.toJson(requestDto));
		//работает
		
		//тестируем ответ от маркета
		//ошибки и JWT
		String shortStr = result.split("\\[")[1].split("\\]")[0];
		System.out.println(shortStr);
		MarketErrorDto test1 = gson.fromJson(shortStr, MarketErrorDto.class);
		MarketJWTDto test2 = gson.fromJson(shortStr, MarketJWTDto.class);
		
		if(test1.getErrorDescription() != null) {
			System.err.println(test1);
		}
		if(test2.getJWT() != null) {
			System.out.println(test2);
		}
		//работает
		
		
		
	}

}
