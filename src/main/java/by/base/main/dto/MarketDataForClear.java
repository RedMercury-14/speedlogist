package by.base.main.dto;

public class MarketDataForClear {

	private Integer IsDelete;
	
	
	public MarketDataForClear() {}
	
	/**
	 * @param IsDelete
	 */
	public MarketDataForClear(Integer IsDelete) {
		super();
		this.IsDelete = IsDelete;
	}

	public Integer getIsDelete() {
		return IsDelete;
	}

	public void setIsDelete(Integer isDelete) {
		IsDelete = isDelete;
	}

	@Override
	public String toString() {
		return "MarketDataForClear [IsDelete=" + IsDelete + "]";
	}
	
		
	
}
