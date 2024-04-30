package by.base.main.dto;

public class MarketDataDto {
	private String LoginName;
	private String Password;
	private String LoginNameTypeId; // тип пользователя (1 – юридическое лицо).
	
	
	
	/**
	 * 
	 */
	public MarketDataDto() {
		super();
	}
	/**
	 * @param loginName
	 * @param password
	 * @param loginNameTypeId тип пользователя (1 – юридическое лицо).
	 */
	public MarketDataDto(String loginName, String password, String loginNameTypeId) {
		super();
		LoginName = loginName;
		Password = password;
		LoginNameTypeId = loginNameTypeId;
	}
	public String getLoginName() {
		return LoginName;
	}
	public void setLoginName(String loginName) {
		LoginName = loginName;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	
	/**
	 * тип пользователя (1 – юридическое лицо).
	 * @return
	 */
	public String getLoginNameTypeId() {
		return LoginNameTypeId;
	}
	/**
	 * тип пользователя (1 – юридическое лицо).
	 * @param loginNameTypeId
	 */
	public void setLoginNameTypeId(String loginNameTypeId) {
		LoginNameTypeId = loginNameTypeId;
	}
	@Override
	public String toString() {
		return "MarketDataDto [LoginName=" + LoginName + ", Password=" + Password + ", LoginNameTypeId="
				+ LoginNameTypeId + "]";
	}
	
	
}
