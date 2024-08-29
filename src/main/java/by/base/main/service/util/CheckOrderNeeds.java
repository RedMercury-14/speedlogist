package by.base.main.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import by.base.main.model.Order;
import by.base.main.model.OrderLine;
import by.base.main.model.OrderProduct;
import by.base.main.model.Product;
import by.base.main.service.OrderProductService;
import by.base.main.service.OrderService;
import by.base.main.service.ProductService;

@Component
public class CheckOrderNeeds {

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ProductService productService;
	
	/**
	 * <b>Note:</b>
	 * @param order
	 * @return
	 */
	public  String check(Order order) {
		Map<Long, Double> productHasOrder = new HashMap<Long, Double>(); // мапа с данными из ордера
		List<OrderLine> orderLines = new ArrayList<OrderLine>(order.getOrderLines());
		String codeContract = order.getMarketContractType();
		//заполняем мапу productHasOrder чтобы удобнее было считать
		for (OrderLine orderLine : orderLines) {
			 if(!productHasOrder.containsKey(orderLine.getGoodsId())) {
				 productHasOrder.put(orderLine.getGoodsId(), orderLine.getQuantityOrder());
			 }else {
				 Double summ = productHasOrder.get(orderLine.getGoodsId());
				 summ = summ + orderLine.getQuantityOrder();
				 productHasOrder.put(orderLine.getGoodsId(), summ);
			 }
			 
		}
		String result = "";
		
		for (Entry<Long, Double> entry : productHasOrder.entrySet()) {
			Product product = productService.getProductByCode(entry.getKey().intValue());
			if(product == null) {
				result = result + "Отсутствует код товар в базе данных " +entry.getKey()+";\n";
				continue;
			}
			List <OrderProduct> orderProducts = new ArrayList<OrderProduct>(product.getOrderProducts());
			
			if(orderProducts.isEmpty()) {
				result = result + "Отсутствуют потребности в базе данных по продуку: " + product.getName()+" (" +entry.getKey()+");\n";
				continue;
			}
			
			OrderProduct orderProduct = orderProducts.get(orderProducts.size()-1);
			
			if(codeContract == null) {
				if(entry.getValue() < orderProduct.getQuantity()) {
					result = result + "Заказанного товара ("+entry.getKey()+" - "+orderProduct.getNameProduct()+") в заказе " + order.getCounterparty() + " ("+order.getMarketNumber()+") меньше, чем потребности. Код контракта отсутствует \n";
				}
				
				if(entry.getValue() > orderProduct.getQuantity()) {
					result = result + "Заказанного товара ("+entry.getKey()+" - "+orderProduct.getNameProduct()+") в заказе " + order.getCounterparty() + " ("+order.getMarketNumber()+") больше, чем потребности; Код контракта отсутствует\n";
				}
			}else {
				if(entry.getValue() < orderProduct.getQuantity()) {
					result = result + "Заказанного товара ("+entry.getKey()+" - "+orderProduct.getNameProduct()+"), по контракту "+codeContract+", в заказе " + order.getCounterparty() + " ("+order.getMarketNumber()+") меньше, чем потребности; \n";
				}
				
				if(entry.getValue() > orderProduct.getQuantity()) {
					result = result + "Заказанного товара ("+entry.getKey()+" - "+orderProduct.getNameProduct()+"), по контракту "+codeContract+", в заказе " + order.getCounterparty() + " ("+order.getMarketNumber()+") больше, чем потребности; \n";
				}
			}
		}
		
		
		return result;		
	}
}
