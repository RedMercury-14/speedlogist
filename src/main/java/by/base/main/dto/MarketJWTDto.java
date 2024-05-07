package by.base.main.dto;

public class MarketJWTDto {
	
	private String JWT;
	
	

	/**
	 * 
	 */
	public MarketJWTDto() {
		super();
	}

	/**
	 * @param jWT
	 */
	public MarketJWTDto(String jWT) {
		super();
		JWT = jWT;
	}

	public String getJWT() {
		return JWT;
	}

	public void setJWT(String jWT) {
		JWT = jWT;
	}

	@Override
	public String toString() {
		return "MarketJWTDto [JWT=" + JWT + "]";
	}
	
	

}
