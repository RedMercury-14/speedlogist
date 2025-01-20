package by.base.main.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import by.base.main.model.OrderProduct;

public interface OrderProductDAO {

	List<OrderProduct> getAllOrderProductList();

	Integer saveOrderProduct(OrderProduct orderProduct);
	
	/**
	 * Отдаёт OrderProduct по id
	 * @param id
	 * @return
	 */
	OrderProduct getOrderProductById(Integer id);
	
	/**
	 * Возвращает заказы поставщикам за определенную дату
	 * @param date
	 * @return
	 */
	List<OrderProduct> getOrderProductListHasDate(Date date);
	
	/**
	 * Метод принимает код продукта и по заданному периоду возвращает заказы по этому продукту
	 * <br>Метод исключает дубликаты (Set<>)
	 * @param codeProduct
	 * @param start начало выборки
	 * @param finish конец выборки
	 * @return
	 */
	@Deprecated
	List<OrderProduct> getOrderProductListHasCodeProductAndPeriod(Integer codeProduct, Date start, Date finish);
	
	/**
	 * Метод принимает лист с кодами продуктов и по заданному периоду возвращает заказы по этим продуктам (где они есть)
	 * <br>Метод исключает дубликаты (Set<>)
	 * @param codeProduct
	 * @param start
	 * @param finish
	 * @return
	 */	
	List<OrderProduct> getOrderProductListHasCodeProductGroupAndPeriod(List<Integer> codeProducts , Date start, Date finish);

	void updateOrderProduct(OrderProduct orderProduct);
	
	/**
	 * Получаем мапу где: 
	 * <br>ключ - дата, значение - мапа с кодом товара (ключ) и значением (как в методе orderProductService.getOrderProductMapHasDate(dateTarget))
	 * @param dates
	 * @return
	 */
	Map<java.sql.Date, Map<Integer, OrderProduct>> getOrderProductMapHasDateList(List<java.sql.Date> dates);
	
}
