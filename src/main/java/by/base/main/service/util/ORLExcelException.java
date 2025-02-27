package by.base.main.service.util;

public class ORLExcelException extends RuntimeException{
	
	private String message;
	
	private Integer status;

	/**
	 * @param message
	 */
	public ORLExcelException(String message) {
		super();
		this.message = message;
	}
	
	public ORLExcelException() {
		super();
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

	@Override
	public String toString() {
		return "ORLExcelException [message=" + message + ", status=" + status + "]";
	}
	
	
	
}
