package by.base.main.service;

import java.util.Map;

import org.json.simple.parser.ParseException;
import by.base.main.model.Order;

public interface MarketAPI {
	
	/**
	 * ВОзвращает мапу оредров из маркета.
	 * Принимает список заказов (напр. 23516507,23792375)
	 * @param idMarket
	 * @return
	 * @throws ParseException
	 */
	public Map<String, Order> getMarketOrders(String idMarket) throws ParseException;

}
