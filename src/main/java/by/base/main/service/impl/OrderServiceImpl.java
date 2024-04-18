package by.base.main.service.impl;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.OrderDAO;
import by.base.main.model.Order;
import by.base.main.model.Route;
import by.base.main.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private OrderDAO orderDAO;

	@Override
	public Order getOrderById(Integer id) {
		return orderDAO.getOrderById(id);
	}

	@Override
	public List<Order> getOrderByDateCreate(Date date) {
		return orderDAO.getOrderByDateCreate(date);
	}

	@Override
	public List<Order> getOrderByDateDelivery(Date date) {
		return orderDAO.getOrderByDateDelivery(date);
	}

	@Override
	public Order getOrderByRoute(Route route) {
		return orderDAO.getOrderByRoute(route);
	}

	@Override
	public List<Order> getOrderByPeriodDelivery(Date dateStart, Date dateEnd) {
		return orderDAO.getOrderByPeriodDelivery(dateStart, dateEnd);
	}

	@Override
	public List<Order> getOrderByPeriodCreate(Date dateStart, Date dateEnd) {
		return orderDAO.getOrderByPeriodCreate(dateStart, dateEnd);
	}

	@Override
	public Integer saveOrder(Order order) {
		return orderDAO.saveOrder(order);
	}

	@Override
	public void updateOrder(Order order) {
		orderDAO.updateOrder(order);
	}

	@Override
	public int updateOrderFromStatus(Order order) {
		return orderDAO.updateOrderFromStatus(order);
	}

	@Override
	public List<Order> getOrderByPeriodCreateAndCounterparty(Date dateStart, Date dateEnd, String counterparty) {
		return orderDAO.getOrderByPeriodCreateAndCounterparty(dateStart, dateEnd, counterparty);
	}

	@Override
	public Order getOrderByIdRoute(Integer idRoute) {
		return orderDAO.getOrderByIdRoute(idRoute);
	}

	@Override
	public void deleteOrderById(Integer id) {
		orderDAO.deleteOrderById(id);
		
	}

	@Override
	public boolean checkOrderHasMarketCode(String code) {
		// TODO Auto-generated method stub
		return orderDAO.checkOrderHasMarketCode(code);
	}

	@Override
	public String saveOrderFromExcel(Order order) {
		Order oldOrder = null;
		oldOrder = orderDAO.getOrderHasMarketCode(order.getMarketNumber());
		if(oldOrder == null) {
			orderDAO.saveOrder(order);
			return "Создан новый заказ с номером " + order.getMarketNumber();
		}
		
		if(oldOrder!=null) {
			switch (oldOrder.getStatus()) {
			case 5:
				oldOrder.setDateDelivery(order.getDateDelivery());
				oldOrder.setCounterparty(order.getCounterparty());
				oldOrder.setNumStockDelivery(order.getNumStockDelivery());
				oldOrder.setCargo(order.getCargo());
				oldOrder.setDateCreate(order.getDateCreate());
				oldOrder.setPall(order.getPall());
				oldOrder.setChangeStatus(order.getChangeStatus());
				oldOrder.setTimeUnload(order.getTimeUnload());
				oldOrder.setSku(order.getSku());
				oldOrder.setDateCreateMarket(order.getDateCreateMarket());
				oldOrder.setMixPall(order.getMixPall());
				oldOrder.setMonoPall(order.getMonoPall());
				orderDAO.updateOrder(oldOrder);
				return "Обновлён заказ " +  order.getMarketNumber();
			case 10:
				return "Заказ " +  order.getMarketNumber() + " отменен, но виртуальный не создан";
			case 6:
				return "Заказ " +  order.getMarketNumber() + " поставлен в слоты , но не создан";
			case 7:
				return "Заказ " +  order.getMarketNumber() + " поставлен в слоты как доставка поставщиком";
			case 100:
				return "Заказ " +  order.getMarketNumber() + " поставлен в слоты как доставка поставщиком";
			default:
				return "Заказ " +  order.getMarketNumber() + " уже создан";
			}
		}
		return "Неизвестный статус OrderServiceImpl";
		
	}

	@Override
	public String updateOrderForSlots(Order order) {
		Timestamp dateTimeStart = order.getTimeDelivery(); // это верхняя граница проверки (время начала выгрузки)		
		Timestamp dateTimeFinish = getTimestampEndUnload(order);// это нижняя граница времени (время окончания выгрузки)
		
		//далее проверяем: берем слот (той же рампы) к верхней границе и раньше, высчитываем для него время начала и время окончания и проверяем, входит ли верзняя граница в этот диапазон
		Order before = orderDAO.getOrderBeforeTimeDeliveryHasStockAndRamp(order);
		Order after = orderDAO.getOrderAfterTimeDeliveryHasStockAndRamp(order);
		
		boolean targetStart = false;
		boolean targetEnd = false;
		
		if(before != null) {
			targetStart = dateTimeStart.toLocalDateTime().isBefore(getTimestampEndUnload(before).toLocalDateTime()); // ходит ли начало выгрузки в бллижайшую выгрузку (верхнее пересечение)
		}
		if(after != null) {
			targetEnd = dateTimeFinish.toLocalDateTime().isAfter(after.getTimeDelivery().toLocalDateTime()); // пересекается ли окончание выгрузки со следующей выгрузкой (нижнее пересечение)
		}		
		if(!targetStart && !targetEnd) {
			orderDAO.updateOrder(order);
			return null;
		}else {
			return "Обнаружено пересечение слотов!";
		}		
	}
	
	/**
	 * Метод возвращает дау и время окончания выгрузки
	 * @param order
	 * @return
	 */
	private Timestamp getTimestampEndUnload (Order order) {
		Timestamp dateTimeStart = order.getTimeDelivery(); // это верхняя граница проверки (время начала выгрузки)
		
		LocalDateTime localDateTimeFinish = LocalDateTime.of(dateTimeStart.toLocalDateTime().toLocalDate(), dateTimeStart.toLocalDateTime().toLocalTime()); 
		localDateTimeFinish = localDateTimeFinish.plusHours(order.getTimeUnload().toLocalTime().getHour());
		localDateTimeFinish = localDateTimeFinish.plusMinutes(order.getTimeUnload().toLocalTime().getMinute());
		
		return Timestamp.valueOf(localDateTimeFinish);
	}

	@Override
	public Order getOrderByMarketNumber(String number) {
		Order order = orderDAO.getOrderByMarketNumber(number);
		return order;
	}

	@Override
	public List<Order> getOrderByPeriodCreateMarket(Date dateStart, Date dateEnd) {
		return orderDAO.getOrderByPeriodCreateMarket(dateStart, dateEnd);
	}

	@Override
	public List<Order> getOrderByTimeDelivery(Date dateStart, Date dateEnd) {
		return orderDAO.getOrderByTimeDelivery(dateStart, dateEnd);
	}
}
