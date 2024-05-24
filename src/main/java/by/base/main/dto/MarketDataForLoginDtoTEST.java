package by.base.main.dto;

public class MarketDataForLoginDtoTEST {
	private String LoginName;
	private String Password;
	private int LoginNameTypeId; // тип пользователя (1 – юридическое лицо).
	
	
	
	/**
	 * 
	 */
	public MarketDataForLoginDtoTEST() {
		super();
	}
	/**
	 * тестовая сущьность, разница в том что LoginNameTypeId - это int
	 * @param loginName
	 * @param password
	 * @param loginNameTypeId тип пользователя (1 – юридическое лицо).
	 */
	public MarketDataForLoginDtoTEST(String loginName, String password, int loginNameTypeId) {
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
	 * тип пользователя (101 – юридическое лицо).
	 * @return
	 */
	public int getLoginNameTypeId() {
		return LoginNameTypeId;
	}
	/**
	 * тип пользователя (101 – юридическое лицо).
	 * @param loginNameTypeId
	 */
	public void setLoginNameTypeId(int loginNameTypeId) {
		LoginNameTypeId = loginNameTypeId;
	}
	@Override
	public String toString() {
		return "MarketDataDto [LoginName=" + LoginName + ", Password=" + Password + ", LoginNameTypeId="
				+ LoginNameTypeId + "]";
	}
	
	
}
