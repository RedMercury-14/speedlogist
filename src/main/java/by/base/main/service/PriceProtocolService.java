package by.base.main.service;

import java.util.List;

import by.base.main.model.PriceProtocol;

public interface PriceProtocolService {
	
    List<PriceProtocol> getAll();

    PriceProtocol getById(int idPriceProtocol);

    PriceProtocol getByProductCode(String productCode);

    PriceProtocol getByBarcode(String barcode);

    List<PriceProtocol> getByContractNumber(String contractNumber);

    int save(PriceProtocol priceProtocol);

    void update(PriceProtocol priceProtocol);

}
