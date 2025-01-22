package by.base.main.service.impl;

import by.base.main.dao.OrderCalculationDAO;
import by.base.main.model.OrderCalculation;
import by.base.main.service.OrderCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;


@Service
public class OrderCalculationServiceImpl implements OrderCalculationService {

    @Autowired
    private OrderCalculationDAO orderCalculationDAO;

    @Override
    @Transactional
    public OrderCalculation getOrderCalculationById(Integer id) {
        return orderCalculationDAO.getOrderCalculationById(id);
    }

    @Override
    @Transactional
    public Integer saveOrderCalculation(OrderCalculation orderCalculation) {
        return orderCalculationDAO.saveOrderCalculation(orderCalculation);
    }

    @Override
    @Transactional
    public List<OrderCalculation> getOrderCalculationsForPeriod(Date dateFrom, Date dateTo) {
        return orderCalculationDAO.getOrderCalculationsForPeriod(dateFrom, dateTo);
    }

    @Override
    @Transactional
    public OrderCalculation getOrderCalculatiionByContractNumStockGoodIdAndDeliveryDate(Long contract, Integer numStock, Date date, Long goodId) {
        return orderCalculationDAO.getOrderCalculatiionByContractNumStockAndDeliveryDate(contract, numStock, date, goodId);
    }


}
