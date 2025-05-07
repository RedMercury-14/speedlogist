package by.base.main.dao;

import by.base.main.model.CarrierBid;
import by.base.main.model.User;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public interface CarrierBidDao {
    Long save(CarrierBid carrierBid);

    void delete(CarrierBid carrierBid);

    void update(CarrierBid carrierBid);

    CarrierBid getById(Long bidId);

    List<CarrierBid> getCarrierBidsByDate(Date dateStart, Date dateFinish);

    List<CarrierBid> getCarrierBidsByRouteId(Integer routeId);

    CarrierBid getCarrierBidByRouteAndUser(Integer routeId, User user);
}
