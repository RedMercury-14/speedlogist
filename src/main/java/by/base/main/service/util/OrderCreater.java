package by.base.main.service.util;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import by.base.main.dto.OrderBuyDTO;
import by.base.main.dto.OrderBuyGroupDTO;
import by.base.main.model.Order;
import by.base.main.service.OrderService;

/**
 * Класс с методами создания Order и расчётов
 */
@Service
public class OrderCreater {
	
	@Autowired
	private OrderService orderService;

	/**
	 * оздаёт объект Order из OrderBuyGroupDTO
	 * @param orderBuyGroupDTO
	 * @return
	 */
	public Order create(OrderBuyGroupDTO orderBuyGroupDTO) {
		if(orderBuyGroupDTO.getCheckx() != 50 && orderBuyGroupDTO.getCheckx() != 51) {
			Order order = new Order();
			order.setMessage("Заказ найден в Маркете, однако он не в 50 или 51 статусе");
			order.setIdOrder(-1);
			return order;
		}
		//cчитаем время и создаём Order
				Order order = new Order();
				order.setMarketNumber(orderBuyGroupDTO.getOrderBuyGroupId().toString());                   
		        order.setCounterparty(orderBuyGroupDTO.getContractorNameShort().trim());
		        order.setMarketContractGroupId(orderBuyGroupDTO.getContractGroupId() != null ? orderBuyGroupDTO.getContractGroupId().toString() : null);
		        order.setMarketContractNumber(orderBuyGroupDTO.getContractNumber() != null ? orderBuyGroupDTO.getContractNumber().toString() : null);
		        order.setMarketContractorId(orderBuyGroupDTO.getContractorId() != null ? orderBuyGroupDTO.getContractorId().toString() : null);
		        order.setMarketContractType(orderBuyGroupDTO.getContractType() != null ? orderBuyGroupDTO.getContractType().toString() : null);
		        
		        order.setMarketOrderSumFirst(orderBuyGroupDTO.getOrderSumFirst() != null ? orderBuyGroupDTO.getOrderSumFirst() : null);
		        order.setMarketOrderSumFinal(orderBuyGroupDTO.getOrderSumFinal() != null ? orderBuyGroupDTO.getOrderSumFinal() : null);
		        
		        Date dateDelivery = Date.valueOf(orderBuyGroupDTO.getDeliveryDate().toLocalDateTime().toLocalDate());
		        order.setDateDelivery(dateDelivery);
		        order.setNumStockDelivery(orderBuyGroupDTO.getWarehouseId().toString());
		        
		        order.setCargo(orderBuyGroupDTO.getOrderBuy().get(0).getGoodsName().trim() + ", ");
		        //записываем в поле информация
		        order.setMarketInfo(orderBuyGroupDTO.getInfo());
		        
		        Date dateCreateInMarket = Date.valueOf(orderBuyGroupDTO.getDatex().toLocalDateTime().toLocalDate());
		        order.setDateCreateMarket(dateCreateInMarket);
		        order.setChangeStatus("Заказ создан в маркете: " + dateCreateInMarket);
		        
		        int sku = 0;
		        for (OrderBuyDTO orderBuy : orderBuyGroupDTO.getOrderBuy()) {
		        	if(Double.parseDouble(orderBuy.getQuantityOrder()) <= 0) {
		        		continue;
		        	}
		        	if(order.getNumProduct() == null) {
		        		order.setNumProduct(orderBuy.getGoodsId()+"^");
		        	}else {
		        		order.setNumProduct(order.getNumProduct()+orderBuy.getGoodsId()+"^");
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
		        
//		      Расчет времени выгрузки авто в минутах.
//		      =10мин+ МОНО*2мин.+MIX*3мин.+((SKU-1мин)*3мин)
//		      Разъяснение:
//		      10 мин.= не зависимо от объема поставки есть действия специалиста требующие временных затрат.
//		      МОНО -количество моно паллет в заказе
//		      MIX - количество микс паллет в заказе
//		      SKU-1 – каждое SKU более одной требует дополнительных временных затрат.
		        Integer minutesUnload = 10 + pallMono * 2 + pallMix * 3 + ((skuTotal - 1) * 3);
		        try {
		        	order.setTimeUnload(Time.valueOf(LocalTime.ofSecondOfDay(minutesUnload*60)));
				} catch (Exception e) {
//					 return "Ошибка расчёта времени выгрузки! В номере из маркета " + order.getMarketNumber() + " рассчитано " + minutesUnload + " минут! \nРАСЧЁТ ЗАВЕРШЕН С ОШИБКОЙ!";
				}
		        String message = null;
		        message = orderService.saveOrderFromMarket(order); // здесь происходит пролверка и запись заявки
		        if(Integer.parseInt(message.split("<")[1].trim()) < 0) {//тут приходит сообщение об ошибке
		        	Order errorMessage = new Order();
		        	errorMessage.setMessage(message.split("<")[0].trim());
		        	errorMessage.setIdOrder(-1);
		        	return errorMessage;
		        }else {
		        	order.setIdOrder(Integer.parseInt(message.split("<")[1].trim()));
		        	order.setMessage(message.split("<")[0].trim());
		        	return order;
		        }
		        
		        
				
	}
}
