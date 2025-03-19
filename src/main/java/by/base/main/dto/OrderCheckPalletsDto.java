package by.base.main.dto;

public class OrderCheckPalletsDto {
    Integer idOrder;
    Integer status;
    Integer pallets;
    String marketNumber;
    String loginManager;

    public Integer getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Integer idOrder) {
        this.idOrder = idOrder;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPallets() {
        return pallets;
    }

    public void setPallets(Integer pallets) {
        this.pallets = pallets;
    }

    public String getMarketNumber() {
        return marketNumber;
    }

    public void setMarketNumber(String marketNumber) {
        this.marketNumber = marketNumber;
    }

    public String getLoginManager() {
        return loginManager;
    }

    public void setLoginManager(String loginManager) {
        this.loginManager = loginManager;
    }
}
