package by.base.main.dto;

/**
 * Другая реализация объекта MarketDataForRequestDto с добавлением поля <b>OrderBuyGroupIdArray</b>
 */
public class MarketDataArrayForRequestDto {
	
	private Object[] OrderBuyGroupIdArray;

	/**
	 * @param orderBuyGroupIdArray
	 */
	public MarketDataArrayForRequestDto(Object[] orderBuyGroupIdArray) {
		super();
		OrderBuyGroupIdArray = orderBuyGroupIdArray;
	}

	public Object[] getOrderBuyGroupIdArray() {
		return OrderBuyGroupIdArray;
	}

	public void setOrderBuyGroupIdArray(Object[] orderBuyGroupIdArray) {
		OrderBuyGroupIdArray = orderBuyGroupIdArray;
	}

	@Override
	public String toString() {
		return "MarketDataArrayForRequestDto [OrderBuyGroupIdArray=" + OrderBuyGroupIdArray + "]";
	}
	
	

}
