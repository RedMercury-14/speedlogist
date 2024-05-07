package by.base.main.dto;

public class MarketErrorDto {

	private String Error;
	private String ErrorDescription;
	
	
	public MarketErrorDto() {}
	
	/**
	 * @param error
	 * @param errorDescription
	 */
	public MarketErrorDto(String error, String errorDescription) {
		super();
		this.Error = error;
		this.ErrorDescription = errorDescription;
	}
	public String getError() {
		return Error;
	}
	public void setError(String error) {
		this.Error = error;
	}
	public String getErrorDescription() {
		return ErrorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.ErrorDescription = errorDescription;
	}

	@Override
	public String toString() {
		return "MarketErrorDto [Error=" + Error + ", ErrorDescription=" + ErrorDescription + "]";
	}
	
	
}
