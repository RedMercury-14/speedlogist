package by.base.main.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import by.base.main.service.util.CustomJSONParser;

public class Test2 {

	public static void main(String[] args) throws ParseException {
		CustomJSONParser customJSONParser = new CustomJSONParser();
		String str = "{\"OrderBuyGroup\":[{\"OrderBuyGroupId\":19480219,\"Checkx\":50,\"Datex\":\"2024-05-15T13:37:52.730\",\"DeliveryDate\":\"2021-05-17T00:00:00\",\"ShipmentDateLast\":\"2024-05-17T00:00:00\",\"ContractType\":null,\"ContractGroupId\":null,\"ContractNumber\":null,\"WarehouseId\":1700,\"ContractorId\":114108,\"ContractorNameShort\":\"ФИЛИАЛ №1 НЕСТ ИООО АЛИДИ-ВЕСТ\",\"OrderSumFirst\":165750.0000,\"OrderSumFinal\":198900.00,\"Info\":\"корма для котиков\",\"OrderBuy\":[{\"GoodsId\":674860,\"GoodsName\":\"Корм \\\"ONE\\\" (курица\\/цель.злаки) 750г\",\"GoodsGroupName\":\"P.17.2.1. Промтовары,сезонные товары и товары для животных\\/Товары для домашних животных\\/Корма для кошек\\/Корма для кошек\\/Сухие корма для кошек\",\"Barcode\":\"7613034275080\",\"QuantityInPack\":8.000,\"QuantityInPallet\":320.000,\"QuantityOrder\":9600.000},{\"GoodsId\":674863,\"GoodsName\":\"Корм \\\"ONE\\\" (лосось\\/пшеница) 750г\",\"GoodsGroupName\":\"P.17.2.1. Промтовары,сезонные товары и товары для животных\\/Товары для домашних животных\\/Корма для кошек\\/Корма для кошек\\/Сухие корма для кошек\",\"Barcode\":\"7613034275448\",\"QuantityInPack\":8.000,\"QuantityInPallet\":320.000,\"QuantityOrder\":5400.000}]}]}\r\n";
		//тут избавляемся от мусора в json
		String str2 = str.split("\\[", 2)[1];
		String str3 = str2.substring(0, str2.length()-4);
		
		System.out.println(customJSONParser.parseOrderBuyGroupFromJSON(str3));
		
//		LocalDateTime localDateTime = LocalDateTime.parse("2024-05-15T13:37:52.730");
//		System.out.println(localDateTime);
//		Timestamp timestamp = Timestamp.valueOf(localDateTime);
//		System.out.println(timestamp);

	}

}
