package com.dto;

/**
 * Класс реализующий ответ со статусом для метода ReaderSchedulePlan.process
 * <br/>Если статус 0 - то запрещено действие
 * <br/>Если статус 200 - то разрешено действие
 */
public class PlanResponce {

	private Integer status;
	private String message;
	/**
	 * @param status
	 * @param message
	 */
	public PlanResponce(Integer status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
	
	public PlanResponce() {
		super();
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "PlanResponce [status=" + status + ", message=" + message + "]";
	}
	
	
}
