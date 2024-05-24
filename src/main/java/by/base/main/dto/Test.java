package by.base.main.dto;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

import by.base.main.model.Order;
import by.base.main.service.OrderService;
import by.base.main.service.util.CustomJSONParser;

public class Test {

	private static String jwt;
	
	@Autowired
	private static OrderService orderService;
	
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
		MarketDataForLoginDto dataDto = new MarketDataForLoginDto("SpeedLogist", "12345678", "101");
//		MarketDataForLoginDtoTEST dataDto = new MarketDataForLoginDtoTEST("SpeedLogist", "12345678", 101);
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
		
		
		// главный тест
		String str = null;
		if(jwt == null){
			//запрашиваем jwt
			System.err.println(gson.toJson(requestDto));
			str = postRequest(url, gson.toJson(requestDto));
			System.out.println(str);
		}		
		//достаём его из ответа и записываем для последующих запросов (jwt живёт 180 дней)
		MarketTableDto marketRequestDto = gson.fromJson(str, MarketTableDto.class);
		System.out.println(marketRequestDto);
		jwt = marketRequestDto.getTable()[0].toString().split("=")[1].split("}")[0];
		
		//тест запроса 
//		MarketDataForRequestDto dataDto2 = new MarketDataForRequestDto("19480218");
//		MarketPacketDto packetDto2 = new MarketPacketDto(jwt, "SpeedLogist.GetOrderBuyInfo", "CD6AE87C-2477-4852-A4E7-8BA5BD01C156", dataDto2);
//		MarketRequestDto requestDto2 = new MarketRequestDto("", packetDto2);
//		String marketOrder = postRequest(url, gson.toJson(requestDto2));
//		System.err.println(gson.toJson(requestDto2));
//		System.out.println(marketOrder);
		
		MarketDataForRequestDto dataDto3 = new MarketDataForRequestDto("19480220");
		MarketPacketDto packetDto3 = new MarketPacketDto(jwt, "SpeedLogist.GetOrderBuyInfo", "CD6AE87C-2477-4852-A4E7-8BA5BD01C156", dataDto3);
		MarketRequestDto requestDto3 = new MarketRequestDto("", packetDto3);
		String marketOrder2 = postRequest(url, gson.toJson(requestDto3));
		System.out.println(marketOrder2);
		
		//тут избавляемся от мусора в json
		String str2 = marketOrder2.split("\\[", 2)[1];
		String str3 = str2.substring(0, str2.length()-2);
		
		CustomJSONParser customJSONParser = new CustomJSONParser();
		OrderBuyGroupDTO orderBuyGroupDTO = customJSONParser.parseOrderBuyGroupFromJSON(str3);
		System.out.println(orderBuyGroupDTO);
		System.out.println();
		orderBuyGroupDTO.getOrderBuy().forEach(o->System.out.println(o));
		
		//cчитаем время и создаём Order
		Order order = new Order();
		order.setMarketNumber(orderBuyGroupDTO.getOrderBuyGroupId().toString());                   
        order.setCounterparty(orderBuyGroupDTO.getContractorNameShort().trim());
        //добавить номер контрагента (код поставщика)
        // добавить Тип контракта
        // добавить Код  контракта
        // добавить Номер контракта
        Date dateDelivery = Date.valueOf(orderBuyGroupDTO.getDeliveryDate().toLocalDateTime().toLocalDate());
        order.setDateDelivery(dateDelivery);
        order.setNumStockDelivery(orderBuyGroupDTO.getWarehouseId().toString());
        
        order.setCargo(orderBuyGroupDTO.getOrderBuy().get(0).getGoodsName().trim() + ", ");
        //записываем в поле информация
        order.setMarketInfo(orderBuyGroupDTO.getInfo().trim());
        
        Date dateCreateInMarket = Date.valueOf(orderBuyGroupDTO.getDatex().toLocalDateTime().toLocalDate());
        order.setDateCreateMarket(dateCreateInMarket);
        order.setChangeStatus("Заказ создан в маркете: " + dateCreateInMarket);
        
        int sku = 0;
        for (OrderBuyDTO orderBuy : orderBuyGroupDTO.getOrderBuy()) {
        	if(Double.parseDouble(orderBuy.getQuantityOrder()) <= 0) {
        		continue;
        	}
        	Double pall = Math.ceil(Double.parseDouble(orderBuy.getQuantityOrder().toString().trim()) / Double.parseDouble(orderBuy.getQuantityInPallet().toString().trim()));
            String pallStr = pall+"";

            // Вычисляем pallNew
            Double pallNew = Double.parseDouble(orderBuy.getQuantityOrder().toString().trim()) / Double.parseDouble(orderBuy.getQuantityInPallet().toString().trim());
            // Получаем целую и дробную части из pallNew
            int integerPart = (int) Math.floor(pallNew);
            double fractionalPart = pallNew - integerPart;
            String pallMono = Integer.toString(integerPart);
            String pallMix;
            if (fractionalPart > 0) {
                // Если есть дробная часть, паллет микс = 1
                pallMix = "1";
            } else {
                // Иначе записываем дробную часть
                pallMix = "0";
            }
            
            if(order.getPall() == null) {
            	order.setPall(pallStr.split("\\.")[0]);
            }else {
            	Integer intPall = (int) Double.parseDouble(pallStr);
                Integer oldIntPall = Integer.parseInt(order.getPall());
                Integer totalPall = intPall + oldIntPall;
                order.setPall(totalPall.toString());
            }
            
            if(order.getMonoPall() == null) {
            	order.setMonoPall(Integer.parseInt(pallMono));
            }else {
            	Integer intPallMono = Integer.valueOf(pallMono);
                Integer oldIntPallMono = order.getMonoPall();
                Integer totalPallMono = intPallMono + oldIntPallMono;
                order.setMonoPall(totalPallMono);
            }
            
            if(order.getMixPall() == null) {
            	order.setMixPall(Integer.parseInt(pallMix));
            }else {
            	Integer intPallMix = Integer.valueOf(pallMix);
            	Integer oldIntPallMix = Integer.valueOf(order.getMixPall());
            	Integer totalPallMix = intPallMix + oldIntPallMix;
            	order.setMixPall(totalPallMix);
            }
            sku++;
            order.setSku(sku);
		}
        
        order.setStatus(5);

        Integer pallMono = Integer.valueOf(order.getMonoPall());
        Integer pallMix = Integer.valueOf(order.getMixPall());
        Integer skuTotal = Integer.valueOf(order.getSku());
        
//      Расчет времени выгрузки авто в минутах.
//      =10мин+ МОНО*2мин.+MIX*3мин.+((SKU-1мин)*3мин)
//      Разъяснение:
//      10 мин.= не зависимо от объема поставки есть действия специалиста требующие временных затрат.
//      МОНО -количество моно паллет в заказе
//      MIX - количество микс паллет в заказе
//      SKU-1 – каждое SKU более одной требует дополнительных временных затрат.
        Integer minutesUnload = 10 + pallMono * 2 + pallMix * 3 + ((skuTotal - 1) * 3);
        try {
        	order.setTimeUnload(Time.valueOf(LocalTime.ofSecondOfDay(minutesUnload*60)));
		} catch (Exception e) {
//			 return "Ошибка расчёта времени выгрузки! В номере из маркета " + order.getMarketNumber() + " рассчитано " + minutesUnload + " минут! \nРАСЧЁТ ЗАВЕРШЕН С ОШИБКОЙ!";
		}
        String message = null;
//        message = message + " \n " + orderService.saveOrderFromExcel(order); // здесь происходит пролверка и запись заявки
        System.out.println(order);
        System.err.println(order.getTimeUnload());
	}
	
	 private static String postRequest(String url, String payload) {
	        try {
	            URL urlForPost = new URL(url);
	            HttpURLConnection connection = (HttpURLConnection) urlForPost.openConnection();
	            connection.setRequestMethod("POST");
	            connection.setRequestProperty("Content-Type", "application/json");
	            connection.setDoOutput(true);
	           
	            byte[] postData = payload.getBytes(StandardCharsets.UTF_8);
	            
	            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
	                wr.write(postData);
	            }
	            
	            int getResponseCode = connection.getResponseCode();
	            System.out.println("POST Response Code: " + getResponseCode);

	            if (getResponseCode == HttpURLConnection.HTTP_OK) {
	            	StringBuilder response = new StringBuilder();
	            	try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
	                    String inputLine;	 
	                    while ((inputLine = in.readLine()) != null) {
	                    	response.append(inputLine.trim());
	                    }
	                    in.close();
	                }	           
//	            	System.out.println(connection.getContentType());
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
