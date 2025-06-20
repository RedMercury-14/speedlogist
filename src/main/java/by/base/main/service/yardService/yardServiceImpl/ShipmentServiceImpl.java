package by.base.main.service.yardService.yardServiceImpl;

import by.base.main.dao.yardDao.ShipmentDAO;
import by.base.main.model.yard.Shipment;
import by.base.main.service.yardService.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShipmentServiceImpl implements ShipmentService {

    @Autowired
    private ShipmentDAO shipmentDAO;

//    @Transactional(transactionManager = "myTransactionManagerYard")
    @Override
    public Long saveShipment(Shipment shipment) {
        return shipmentDAO.saveShipment(shipment);
    }
}
