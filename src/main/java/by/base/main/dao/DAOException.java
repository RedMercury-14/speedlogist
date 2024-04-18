package by.base.main.dao;

public class DAOException extends Throwable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6595219381751468974L;

	public DAOException(String message, Throwable cause) {
		super(message, cause);
		// add logger
	}

	public DAOException(Throwable cause) {
		super(cause);
		// add logger
	}
	
	

}
