/**
 * 
 */
package by.base.main.service.util;

import java.util.ArrayList;
import java.util.List;

import by.base.main.dto.OrderCheckPalletsDto;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import by.base.main.dto.OrderBuyDTO;
import by.base.main.dto.OrderBuyGroupDTO;

/**
 * 
 */
public class CustomJSONParser {

	/**
	 * Парсер с json в объект OrderBuyGroupDTO информации по заказу из маркета
	 * <br>Так же парсит и массив по каждому SKU
	 * @param str - json объект согласно инструкции (принимает чистый подготовленный json!)
	 * @return OrderBuyGroupDTO
	 */
	public OrderBuyGroupDTO parseOrderBuyGroupFromJSON(String str) {
		JSONParser parser = new JSONParser();
		OrderBuyGroupDTO orderBuyGroupDTO = new OrderBuyGroupDTO();
		try {
			JSONObject jsonpObject = (JSONObject) parser.parse(str);			
			orderBuyGroupDTO.setCheckx(jsonpObject.get("Checkx") != null ? Integer.parseInt(jsonpObject.get("Checkx").toString()) : null);
			orderBuyGroupDTO.setContractGroupId(jsonpObject.get("ContractGroupId") != null ? Long.parseLong(jsonpObject.get("ContractGroupId").toString()) : null);
			orderBuyGroupDTO.setContractNumber(jsonpObject.get("ContractNumber") != null ? jsonpObject.get("ContractNumber").toString() : null);
			orderBuyGroupDTO.setContractorId(jsonpObject.get("ContractorId") != null ? Long.parseLong(jsonpObject.get("ContractorId").toString()) : null);
			orderBuyGroupDTO.setContractorNameShort(jsonpObject.get("ContractorNameShort") != null ? jsonpObject.get("ContractorNameShort").toString() : null);
			orderBuyGroupDTO.setContractType(jsonpObject.get("ContractType") != null ? Long.parseLong(jsonpObject.get("ContractType").toString()) : null);
			orderBuyGroupDTO.setDatex(jsonpObject.get("Datex") != null ? jsonpObject.get("Datex").toString() : null);
			orderBuyGroupDTO.setDeliveryDate(jsonpObject.get("DeliveryDate") != null ? jsonpObject.get("DeliveryDate").toString() : null);
			if(jsonpObject.get("Info") != null) {
//				if(!jsonpObject.get("Info").toString().split(" ")[0].equals("Создано")) {
//					orderBuyGroupDTO.setInfo(jsonpObject.get("Info").toString());
//            	}
				orderBuyGroupDTO.setInfo(jsonpObject.get("Info").toString());
			}			
			orderBuyGroupDTO.setOrderBuyGroupId(jsonpObject.get("OrderBuyGroupId") != null ? Long.parseLong(jsonpObject.get("OrderBuyGroupId").toString()) : null);
			orderBuyGroupDTO.setOrderSumFinal(jsonpObject.get("OrderSumFinal") != null ? Double.parseDouble(jsonpObject.get("OrderSumFinal").toString()) : null);
			orderBuyGroupDTO.setOrderSumFirst(jsonpObject.get("OrderSumFirst") != null ? Double.parseDouble(jsonpObject.get("OrderSumFirst").toString()) : null);
			orderBuyGroupDTO.setShipmentDateLast(jsonpObject.get("ShipmentDateLast") != null ? jsonpObject.get("ShipmentDateLast").toString() : null);
			orderBuyGroupDTO.setWarehouseId(jsonpObject.get("WarehouseId") != null ? Integer.parseInt(jsonpObject.get("WarehouseId").toString()) : null);
			if(jsonpObject.get("OrderBuy") != null) {
				List<OrderBuyDTO> orderBuyDTOList = new ArrayList<OrderBuyDTO>();
				JSONArray array = (JSONArray) parser.parse(jsonpObject.get("OrderBuy").toString());	
				for (Object object : array) {
					OrderBuyDTO orderBuyDTO = new OrderBuyDTO();
					JSONObject jsonObjectArray = (JSONObject) object;
					orderBuyDTO.setBarcode(jsonObjectArray.get("Barcode") != null ? jsonObjectArray.get("Barcode").toString() : null);
					orderBuyDTO.setGoodsGroupName(jsonObjectArray.get("GoodsGroupName") != null ? jsonObjectArray.get("GoodsGroupName").toString() : null);
					orderBuyDTO.setGoodsId(jsonObjectArray.get("GoodsId") != null ? Long.parseLong(jsonObjectArray.get("GoodsId").toString()) : null);
					orderBuyDTO.setGoodsName(jsonObjectArray.get("GoodsName") != null ? jsonObjectArray.get("GoodsName").toString() : null);
					orderBuyDTO.setQuantityInPack(jsonObjectArray.get("QuantityInPack") != null ? jsonObjectArray.get("QuantityInPack").toString() : null);
					orderBuyDTO.setQuantityInPallet(jsonObjectArray.get("QuantityInPallet") != null ? jsonObjectArray.get("QuantityInPallet").toString() : null);
					orderBuyDTO.setQuantityOrder(jsonObjectArray.get("QuantityOrder") != null ? jsonObjectArray.get("QuantityOrder").toString() : null);
					orderBuyDTOList.add(orderBuyDTO);
				}
				orderBuyGroupDTO.setOrderBuy(orderBuyDTOList);
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return orderBuyGroupDTO;		
	}

	public OrderCheckPalletsDto parseOrderFromJSON(String key, JSONObject jsonObject) throws ParseException {
		OrderCheckPalletsDto dto = new OrderCheckPalletsDto();

		dto.setIdOrder(Integer.parseInt(key));
		dto.setMarketNumber(jsonObject.get("marketNumber") != null ? jsonObject.get("marketNumber").toString() : null);
		dto.setPallets(jsonObject.get("pall") != null ? Integer.parseInt(jsonObject.get("pall").toString()) : null);
		dto.setStatus(jsonObject.get("status") != null ? Integer.parseInt(jsonObject.get("status").toString()) : null);
		dto.setLoginManager(jsonObject.get("loginManager") != null ? jsonObject.get("loginManager").toString() : null);

		return dto;
	}
}
