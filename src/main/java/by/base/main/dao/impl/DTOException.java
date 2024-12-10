package by.base.main.dao.impl;

public class DTOException extends RuntimeException{

	private String message;
	
	private Integer status;
	
	public DTOException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public DTOException(String message) {
		super();
		this.message = message;
	}
	
	

	/**
	 * @param message
	 * @param status
	 */
	public DTOException(String message, Integer status) {
		super();
		this.message = message;
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Override
	public void printStackTrace() {
		// TODO Auto-generated method stub
		super.printStackTrace();
	}
	
}
