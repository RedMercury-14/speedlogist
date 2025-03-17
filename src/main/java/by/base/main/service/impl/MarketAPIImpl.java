package by.base.main.service.impl;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import by.base.main.controller.ajax.MainRestController;
import by.base.main.dto.MarketDataArrayForRequestDto;
import by.base.main.dto.MarketErrorDto;
import by.base.main.dto.MarketPacketDto;
import by.base.main.dto.MarketRequestDto;
import by.base.main.dto.OrderBuyGroupDTO;
import by.base.main.model.Order;
import by.base.main.service.MarketAPI;
import by.base.main.service.util.CustomJSONParser;
import by.base.main.service.util.OrderCreater;

@Service
public class MarketAPIImpl implements MarketAPI{
	
	@Autowired
	private MainRestController mainRestController;
	
	@Autowired
	private OrderCreater orderCreater;
	
	private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
				@Override
				public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
					return context.serialize(src.getTime());  // Сериализация даты в миллисекундах
				}
            })
            .create();

	@Override
	public Map<String, Order> getMarketOrders(String idMarket) throws Exception {
		try {			
			mainRestController.checkJWT(mainRestController.marketUrl);			
		} catch (Exception e) {
			System.err.println("Ошибка получения jwt токена");
		}
		
		Map<String, Object> response = new HashMap<String, Object>();
		Object[] goodsId = idMarket.split(",");
		MarketDataArrayForRequestDto dataDto3 = new MarketDataArrayForRequestDto(goodsId);
		MarketPacketDto packetDto3 = new MarketPacketDto(mainRestController.marketJWT, "SpeedLogist.OrderBuyArrayInfoGet", mainRestController.serviceNumber, dataDto3);
		MarketRequestDto requestDto3 = new MarketRequestDto("", packetDto3);
		String marketOrder2 = mainRestController.postRequest(mainRestController.marketUrl, gson.toJson(requestDto3));
		
//		System.out.println("request -> " + gson.toJson(requestDto3));
//		System.out.println("responce -> " + marketOrder2);
		
//		System.out.println(marketOrder2);
		
		if(marketOrder2.equals("503")) { // означает что связь с маркетом потеряна
			//в этом случае проверяем бд
			System.err.println("Связь с маркетом потеряна");
			mainRestController.marketJWT = null; // сразу говорим что jwt устарел
			return null;
		}else{//если есть связь с маркетом
			//создаём свой парсер и парсим json в объекты, с которыми будем работать.
			CustomJSONParser customJSONParser = new CustomJSONParser();
			
			//создаём лист OrderBuyGroup
			Map<Long, OrderBuyGroupDTO> OrderBuyGroupDTOMap = new HashMap<Long, OrderBuyGroupDTO>();
			Map<String, Order> orderMap = new HashMap<String, Order>();
			JSONParser parser = new JSONParser();
			JSONObject jsonMainObject = (JSONObject) parser.parse(marketOrder2);
			JSONArray numShopsJSON = (JSONArray) jsonMainObject.get("OrderBuyGroup");
			for (Object object : numShopsJSON) {
				//создаём OrderBuyGroup
				OrderBuyGroupDTO orderBuyGroupDTO = customJSONParser.parseOrderBuyGroupFromJSON(object.toString());
				OrderBuyGroupDTOMap.put(orderBuyGroupDTO.getOrderBuyGroupId(), orderBuyGroupDTO);
				Order order = orderCreater.createSimpleOrder(orderBuyGroupDTO);
				orderMap.put(order.getMarketNumber(), order);
			}
			return orderMap;
		}
			
	}

}
