package by.base.main.dto;

import java.sql.Date;

/**
 * <br>Класс для передачи на фронт информации о поставках за определённый период</br>
 * @author Ira
 */
public class AmountOfPalletsDto {

    private long counterpartyCode;

    private String counterpartyName;

    private long counterpartyContractCode;

    private int amountOfPallets;

    private int numStock;

    private Date deliveryDate;

    public long getCounterpartyCode() {
        return counterpartyCode;
    }

    public void setCounterpartyCode(long counterpartyCode) {
        this.counterpartyCode = counterpartyCode;
    }

    public long getCounterpartyContractCode() {
        return counterpartyContractCode;
    }

    public void setCounterpartyContractCode(long counterpartyContractCode) {
        this.counterpartyContractCode = counterpartyContractCode;
    }

    public int getAmountOfPallets() {
        return amountOfPallets;
    }

    public void setAmountOfPallets(int amountOfPallets) {
        this.amountOfPallets = amountOfPallets;
    }

    public int getNumStock() {
        return numStock;
    }

    public void setNumStock(int numStock) {
        this.numStock = numStock;
    }

    public String getCounterpartyName() {
        return counterpartyName;
    }

    public void setCounterpartyName(String counterpartyName) {
        this.counterpartyName = counterpartyName;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
