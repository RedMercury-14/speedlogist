package by.base.main.service.yardService.yardServiceImpl;

import by.base.main.dao.yardDao.ShopShipmentDAO;
import by.base.main.model.yard.ShopShipment;
import by.base.main.service.yardService.ShopShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class ShopShipmentServiceImpl implements ShopShipmentService {

    @Autowired
    private ShopShipmentDAO shopShipmentDAO;

//    @Transactional
    @Override
    public Long saveShopShipment(ShopShipment shopShipment) {
        return shopShipmentDAO.saveShopShipment(shopShipment);
    }
}
