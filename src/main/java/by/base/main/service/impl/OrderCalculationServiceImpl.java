package by.base.main.service.impl;

import by.base.main.dao.OrderCalculationDAO;
import by.base.main.model.OrderCalculation;
import by.base.main.model.Schedule;
import by.base.main.service.OrderCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * @param orderCalculation
     * @param schedule
     * Считает логистическое плечо для order calculation по графику либо по истории
     * @author Ira
     */
    @Override
    public int gelLogShoulder(OrderCalculation orderCalculation, Schedule schedule) {
        int logShoulder = 0;
        Date supplyDate = orderCalculation.getDeliveryDate();
        LocalDate date = supplyDate.toLocalDate();
        DayOfWeek supplyDay = date.getDayOfWeek();

        String forSearch = switch (supplyDay) {
            case MONDAY -> schedule.getMonday();
            case TUESDAY -> schedule.getTuesday();
            case WEDNESDAY -> schedule.getWednesday();
            case THURSDAY -> schedule.getThursday();
            case FRIDAY -> schedule.getFriday();
            case SATURDAY -> schedule.getSaturday();
            case SUNDAY -> schedule.getSunday();
        };

        String result = null;
        int orderDay = 0;

        if (forSearch != null && forSearch.contains("понедельник")) {
            result = schedule.getMonday();
            orderDay = 1;
        }
        if (forSearch != null && forSearch.contains("вторник")) {
            result = schedule.getTuesday();
            orderDay = 2;
        }
        if (forSearch != null && forSearch.contains("среда")) {
            result = schedule.getWednesday();
            orderDay = 3;
        }
        if (forSearch != null && forSearch.contains("четверг")) {
            result = schedule.getThursday();
            orderDay = 4;
        }
        if (forSearch != null && forSearch.contains("пятница")) {
            result = schedule.getFriday();
            orderDay = 5;
        }
        if (forSearch != null && forSearch.contains("суббота")) {
            result = schedule.getSaturday();
            orderDay = 6;
        }
        if (forSearch != null && forSearch.contains("воскресенье")) {
            result = schedule.getSunday();
            orderDay = 7;
        }

        //подсчёт плеча на основании графика
        if (forSearch != null){
            int week = 0;

            if (forSearch.contains("н10")) {
                week = 10 * 7;
            } else {
                for (int x = 1; x <= 9; x++) {
                    String weekStr = "н" + x;
                    int weeks;

                    if (forSearch.contains(weekStr)) {
                        weeks = x;
                        week = weeks * 7;
                        break;
                    }
                }
            }

            if (orderDay > supplyDay.getValue()) {
                week -= 7;
            }

            logShoulder += week;
            int diff = (supplyDay.getValue() - orderDay + 7) % 7 + 1;
            logShoulder += diff ;
            //подсчёт плеча на основании истории, если не удалось посчитать по графику
        } else {
            String history = orderCalculation.getHistory();
            Pattern pattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");
            LocalDate orderLocalDate = null;
            Matcher matcher = pattern.matcher(history);

            if (matcher.find()) {  // Проверяем, есть ли совпадение
                String dateString = matcher.group(1);
                orderLocalDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
            } else {
                return 0;
            }

            logShoulder = (int) ChronoUnit.DAYS.between(orderLocalDate, date) + 1;
        }
        return logShoulder;
    }


}
