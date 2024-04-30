package by.base.main.dto;

public class MarketPacketDto {

	private String JWT;
	private String MethodName;
	private String ServiceNumber;
	private MarketDataDto Data;
	
	
	
	
	/**
	 * 
	 */
	public MarketPacketDto() {
		super();
	}
	/**
	 * @param jWT
	 * @param methodName
	 * @param serviceNumber
	 * @param data
	 */
	public MarketPacketDto(String jWT, String methodName, String serviceNumber, MarketDataDto data) {
		super();
		JWT = jWT;
		MethodName = methodName;
		ServiceNumber = serviceNumber;
		Data = data;
	}
	public String getJWT() {
		return JWT;
	}
	public void setJWT(String jWT) {
		JWT = jWT;
	}
	public String getMethodName() {
		return MethodName;
	}
	public void setMethodName(String methodName) {
		MethodName = methodName;
	}
	public String getServiceNumber() {
		return ServiceNumber;
	}
	public void setServiceNumber(String serviceNumber) {
		ServiceNumber = serviceNumber;
	}
	public MarketDataDto getData() {
		return Data;
	}
	public void setData(MarketDataDto data) {
		Data = data;
	}
	@Override
	public String toString() {
		return "MarketPacketDto [JWT=" + JWT + ", MethodName=" + MethodName + ", ServiceNumber=" + ServiceNumber
				+ ", Data=" + Data + "]";
	}
	
	
	
}
