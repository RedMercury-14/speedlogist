package by.base.main.service;

import by.base.main.model.CarrierBid;
import by.base.main.model.Route;
import by.base.main.model.User;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public interface CarrierBidService {

    /**
     * Метод для сохранения ставки
     * @param carrierBid
     * @author Ira
     */
    Long save(CarrierBid carrierBid);

    /**
     * Метод для обновления ставки
     * @param carrierBid
     * @author Ira
     */
    void update(CarrierBid carrierBid);

    /**
     * Метод для удаления ставки
     * @param carrierBid
     * @author Ira
     */
    void delete(CarrierBid carrierBid);

    /**
     * Метод для получения ставки по id
     * @author Ira
     */
    CarrierBid getById(Long bidId);

    /**
     * Метод для получения списка ставок за указанный промежуток времени
     * @author Ira
     */
    List<CarrierBid> getCarrierBidsByDate(Date dateStart, Date dateFinish);

    /**
     * Метод для получения списка ставок для определённого маршрута
     * @author Ira
     */
    List<CarrierBid> getCarrierBidsByRouteId(Integer routeId);

    /**
     * Метод для получения ставки определённого перевозчика для определённого маршрута
     * @author Ira
     */
    CarrierBid getCarrierBidByRouteAndUser(Integer routeId, User user);

    /**
     * Метод для удаления неактуальных для маршрута ставок
     * @author Ira
     */
    void deleteIrrelevantBidsForRoute(Route route);
    
    /**
     * Метод для получения списка действующих ставок для определённого маршрута
     * @author Ira
     */
    List<CarrierBid> getActualCarrierBidsByRouteId(Integer routeId);
}
