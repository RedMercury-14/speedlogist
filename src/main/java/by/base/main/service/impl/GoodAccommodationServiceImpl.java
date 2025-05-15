package by.base.main.service.impl;

import by.base.main.dao.GoodAccommodationDao;
import by.base.main.model.GoodAccommodation;
import by.base.main.service.GoodAccommodationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

@Service
public class GoodAccommodationServiceImpl implements GoodAccommodationService {

    @Autowired
    private GoodAccommodationDao goodAccommodationDao;

    @Transactional
    @Override
    public long save(GoodAccommodation goodAccommodation) {
        return goodAccommodationDao.save(goodAccommodation);
    }

    @Transactional
    @Override
    public GoodAccommodation getActualGoodAccommodationByGoodIdAndStock(Long goodId) {
        return goodAccommodationDao.getActualGoodAccommodationByGoodIdAndStock(goodId);
    }

    @Transactional
    @Override
    public GoodAccommodation getGoodAccommodationById(Long accommodationId) {
        return goodAccommodationDao.getGoodAccommodationById(accommodationId);
    }

    @Transactional
    @Override
    public void update(GoodAccommodation goodAccommodation) {
        goodAccommodationDao.updateGoodAccommodation(goodAccommodation);
    }

    @Transactional
	@Override
	public List<GoodAccommodation> getActualGoodAccommodationByGoodId(Long productCode) {
		return goodAccommodationDao.getActualGoodAccommodationByGoodId(productCode);
	}

    @Transactional
	@Override
	public Map<Long, GoodAccommodation> getActualGoodAccommodationByCodeProductList(List<Long> productCode) {
		return goodAccommodationDao.getActualGoodAccommodationByCodeProductList(productCode);
	}

    @Transactional
	@Override
	public List<GoodAccommodation> getAll() {
		return goodAccommodationDao.getAll();
	}

}
