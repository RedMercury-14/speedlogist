package by.base.main.dto;

public class OrderBuyDTO {

	private Long GoodsId; // Код товара
	private String GoodsName; // Наименование товара
	private String GoodsGroupName; // Полное наименование товарной группы
	private String Barcode; //  Штрих-код товара
	private String QuantityInPack; // Кол-во в упаковке
	private String QuantityInPallet; // Кол-во в паллете
	private String QuantityOrder; // Кол-во заказано
	public Long getGoodsId() {
		return GoodsId;
	}
	public void setGoodsId(Long goodsId) {
		GoodsId = goodsId;
	}
	public String getGoodsName() {
		return GoodsName;
	}
	public void setGoodsName(String goodsName) {
		GoodsName = goodsName;
	}
	public String getGoodsGroupName() {
		return GoodsGroupName;
	}
	public void setGoodsGroupName(String goodsGroupName) {
		GoodsGroupName = goodsGroupName;
	}
	public String getBarcode() {
		return Barcode;
	}
	public void setBarcode(String barcode) {
		Barcode = barcode;
	}
	public String getQuantityInPack() {
		return QuantityInPack;
	}
	public void setQuantityInPack(String quantityInPack) {
		QuantityInPack = quantityInPack;
	}
	public String getQuantityInPallet() {
		return QuantityInPallet;
	}
	public void setQuantityInPallet(String quantityInPallet) {
		QuantityInPallet = quantityInPallet;
	}
	public String getQuantityOrder() {
		return QuantityOrder;
	}
	public void setQuantityOrder(String quantityOrder) {
		QuantityOrder = quantityOrder;
	}
	@Override
	public String toString() {
		return "OrderBuyDTO [GoodsId=" + GoodsId + ", GoodsName=" + GoodsName + ", GoodsGroupName=" + GoodsGroupName
				+ ", Barcode=" + Barcode + ", QuantityInPack=" + QuantityInPack + ", QuantityInPallet="
				+ QuantityInPallet + ", QuantityOrder=" + QuantityOrder + "]";
	}
	
	
}
