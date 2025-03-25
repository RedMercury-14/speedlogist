package by.base.main.dto;

import java.util.Objects;
/**
 * @author IRA
 */
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

	@Override
	public int hashCode() {
		return Objects.hash(idOrder, loginManager, marketNumber, pallets, status);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderCheckPalletsDto other = (OrderCheckPalletsDto) obj;
		return Objects.equals(idOrder, other.idOrder) && Objects.equals(loginManager, other.loginManager)
				&& Objects.equals(marketNumber, other.marketNumber) && Objects.equals(pallets, other.pallets)
				&& Objects.equals(status, other.status);
	}

	@Override
	public String toString() {
		return "OrderCheckPalletsDto [idOrder=" + idOrder + ", status=" + status + ", pallets=" + pallets
				+ ", marketNumber=" + marketNumber + ", loginManager=" + loginManager + "]";
	}
    
}
