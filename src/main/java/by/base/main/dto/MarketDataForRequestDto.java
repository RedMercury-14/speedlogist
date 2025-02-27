package by.base.main.dto;

public class MarketDataForRequestDto {
	private String OrderBuyGroupId;
		
	/**
	 * 
	 */
	public MarketDataForRequestDto() {
		super();
	}

	/**
	 * @param orderBuyGroupId
	 */
	public MarketDataForRequestDto(String orderBuyGroupId) {
		super();
		OrderBuyGroupId = orderBuyGroupId;
	}
	
	public String getOrderBuyGroupId() {
		return OrderBuyGroupId;
	}

	public void setOrderBuyGroupId(String orderBuyGroupId) {
		OrderBuyGroupId = orderBuyGroupId;
	}

	@Override
	public String toString() {
		return "MarketDataForRequestDto [OrderBuyGroupId=" + OrderBuyGroupId + "]";
	}
	
	
	
	
}
