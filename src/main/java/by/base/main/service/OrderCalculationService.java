package by.base.main.service;

import by.base.main.model.OrderCalculation;

import java.sql.Date;
import java.util.List;

public interface OrderCalculationService {

    /**
     * @param id
     * @return
     * <br>Возвращает расчёт по id</br>
     * @author Ira
     */
    OrderCalculation getOrderCalculationById(Integer id);

    /**
     * @param orderCalculation
     * @return
     * <br>Сохраняет расчёт</br>
     * @author Ira
     */
    Integer saveOrderCalculation (OrderCalculation orderCalculation);

    /**
     * @param dateFrom
     * @param dateTo
     * @return
     * <br>Возвращает лист расчётов за диапазон дат</br>
     * @author Ira
     */
    List<OrderCalculation> getOrderCalculationsForPeriod(Date dateFrom, Date dateTo);

    /**
     * @param contract
     * @param numStock
     * @param date
     * @param goodId
     * @return
     * <br>Возвращает расчёт по номеру склада и дате поставки</br>
     * @author Ira
     */
    OrderCalculation getOrderCalculatiionByContractNumStockGoodIdAndDeliveryDate(Long contract, Integer numStock, Date date, Long goodId);
}
