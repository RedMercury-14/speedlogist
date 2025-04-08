package by.base.main.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.PriceProtocolDAO;
import by.base.main.model.PriceProtocol;
import by.base.main.service.PriceProtocolService;

@Service
public class PriceProtocolServiceImpl implements PriceProtocolService{
	
	@Autowired
    private PriceProtocolDAO priceProtocolDAO;

    @Override
    @Transactional
    public List<PriceProtocol> getAll() {
        return priceProtocolDAO.getAll();
    }

    @Override
    @Transactional
    public PriceProtocol getById(int idPriceProtocol) {
        return priceProtocolDAO.getById(idPriceProtocol);
    }

    @Override
    @Transactional
    public PriceProtocol getByProductCode(String productCode) {
        return priceProtocolDAO.getByProductCode(productCode);
    }

    @Override
    @Transactional
    public PriceProtocol getByBarcode(String barcode) {
        return priceProtocolDAO.getByBarcode(barcode);
    }

    @Override
    @Transactional
    public List<PriceProtocol> getByContractNumber(String contractNumber) {
        return priceProtocolDAO.getByContractNumber(contractNumber);
    }

    @Override
    @Transactional
    public int save(PriceProtocol priceProtocol) {
        return priceProtocolDAO.save(priceProtocol);
    }

    @Override
    @Transactional
    public void update(PriceProtocol priceProtocol) {
        priceProtocolDAO.update(priceProtocol);
    }

}
