package by.base.main.service;

public class ServiceException extends Throwable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4161394735382013542L;

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
		// add logger
	}

	public ServiceException(String message) {
		super(message);
		// add logger
	}

	public ServiceException(Throwable cause) {
		super(cause);
		// add logger
	}
	
	

}
