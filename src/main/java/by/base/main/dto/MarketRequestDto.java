package by.base.main.dto;

/**
 * общий объект запроса к маркету
 */
public class MarketRequestDto {
	private String CRC = ""; // для всех как в инструкции
	private MarketPacketDto Packet;
	
	
	
	/**
	 * 
	 */
	public MarketRequestDto() {
		super();
	}
	/**
	 * @param cRC
	 * @param packet
	 */
	public MarketRequestDto(String cRC, MarketPacketDto packet) {
		super();
		CRC = cRC;
		Packet = packet;
	}
	public String getCRC() {
		return CRC;
	}
	public void setCRC(String cRC) {
		CRC = cRC;
	}
	public MarketPacketDto getPacket() {
		return Packet;
	}
	public void setPacket(MarketPacketDto packet) {
		Packet = packet;
	}
	@Override
	public String toString() {
		return "MarketRequestDto [CRC=" + CRC + ", Packet=" + Packet + "]";
	}
	
	
}
