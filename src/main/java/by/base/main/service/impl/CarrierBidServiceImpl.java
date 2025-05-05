package by.base.main.service.impl;

import by.base.main.dao.CarrierBidDao;
import by.base.main.model.CarrierBid;
import by.base.main.model.User;
import by.base.main.service.CarrierBidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;

@Service
public class CarrierBidServiceImpl implements CarrierBidService {


    @Autowired
    private CarrierBidDao carrierBidDao;

    @Transactional
    @Override
    public Long save(CarrierBid carrierBid) {
        return carrierBidDao.save(carrierBid);
    }

    @Transactional
    @Override
    public void update(CarrierBid carrierBid) {
        carrierBidDao.update(carrierBid);
    }

    @Transactional
    @Override
    public void delete(CarrierBid carrierBid) {
        carrierBidDao.delete(carrierBid);
    }

    @Transactional
    @Override
    public List<CarrierBid> getCarrierBidsByDate(Date dateStart, Date dateFinish){
        return carrierBidDao.getCarrierBidsByDate(dateStart, dateFinish);
    }

    @Transactional
    @Override
    public List<CarrierBid> getCarrierBidsByRouteId(Integer routeId) {
        return carrierBidDao.getCarrierBidsByRouteId(routeId);
    }

    @Transactional
    @Override
    public CarrierBid getCarrierBidByRouteAndUser(Integer routeId, User user) {
        return carrierBidDao.getCarrierBidByRouteAndUser(routeId, user);
    }

    @Transactional
    @Override
    public CarrierBid getById(Long bidId){
        return carrierBidDao.getById(bidId);
    }
}
