package by.base.main.service;

import by.base.main.model.CarrierBid;
import by.base.main.model.User;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public interface CarrierBidService {

    Long save(CarrierBid carrierBid);

    void delete(CarrierBid carrierBid);

    List<CarrierBid> getCarrierBidsByDate(Date dateStart, Date dateFinish);

    List<CarrierBid> getCarrierBidsByRouteId(Integer routeId);

    CarrierBid getCarrierBidByRouteAndUser(Integer routeId, User user);
}
