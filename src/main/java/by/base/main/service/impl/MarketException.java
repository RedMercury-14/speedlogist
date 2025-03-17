package by.base.main.service.impl;

public class MarketException extends RuntimeException{
	
	/**
	 * Сообщение, которое пришло от маркета
	 */
	private String message;
	
	private String status;
	
	private String responseCode;
	
	private String description;
	
	private String request;
	
	/**
	 * @param message
	 */
	public MarketException(String message) {
		super();
		this.message = message;
	}

	/**
	 * @param message
	 * @param status
	 */
	public MarketException(String message, String status) {
		super();
		this.message = message;
		this.status = status;
	}

	/**
	 * @param message
	 * @param responseCode
	 * @param description
	 */
	public MarketException(String message, String responseCode, String description) {
		super();
		this.message = message;
		this.responseCode = responseCode;
		this.description = description;
	}
	
	

	/**
	 * @param message
	 * @param status
	 * @param responseCode
	 * @param description
	 */
	public MarketException(String message, String status, String responseCode, String description) {
		super();
		this.message = message;
		this.status = status;
		this.responseCode = responseCode;
		this.description = description;
	}
	
	

	/**
	 * @param message
	 * @param status
	 * @param responseCode
	 * @param description
	 * @param request
	 */
	public MarketException(String message, String status, String responseCode, String description, String request) {
		super();
		this.message = message;
		this.status = status;
		this.responseCode = responseCode;
		this.description = description;
		this.request = request;
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return message;
	}

	@Override
	public void printStackTrace() {
		// TODO Auto-generated method stub
		super.printStackTrace();
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		// TODO Auto-generated method stub
		return super.getStackTrace();
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	@Override
	public String toString() {
		return "{" +
                "\"message\":\"" + message + "\"," +
                "\"status\":\"" + status + "\"," +
                "\"responseCode\":\"" + responseCode + "\"," +
                "\"description\":\"" + description + "\"," +
                "\"request\":\"" + request + "\"}";
	}
}
