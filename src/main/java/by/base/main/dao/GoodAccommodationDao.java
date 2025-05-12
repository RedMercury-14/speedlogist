package by.base.main.dao;

import java.util.List;
import java.util.Map;

import by.base.main.model.GoodAccommodation;

public interface GoodAccommodationDao {
    long save(GoodAccommodation goodAccommodation);

    void updateGoodAccommodation(GoodAccommodation goodAccommodation);

    /**
     * Выборка происходит по 20 статусам
     * @param goodId
     * @param stock
     * @return
     */
    GoodAccommodation getActualGoodAccommodationByGoodIdAndStock(Long productCode);

    GoodAccommodation getGoodAccommodationById(Long accommodationId);
    
    List<GoodAccommodation> getActualGoodAccommodationByGoodId(Long productCode);    
    /**
     * Возвращает map GoodAccommodation по листу кодов продуктов.
     * @param productCode
     * @return
     */
    Map<Long,GoodAccommodation> getActualGoodAccommodationByCodeProductList(List<Long> productCode);
}
