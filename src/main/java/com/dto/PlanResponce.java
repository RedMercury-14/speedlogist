package com.dto;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import by.base.main.model.Schedule;

/**
 * Класс реализующий ответ со статусом для метода ReaderSchedulePlan.process
 * <br/>Если статус 0 - то запрещено действие
 * <br/>Если статус 200 - то разрешено действие
 */
public class PlanResponce {

	private Integer status;
	private String message;
	private List <Date> dates = new ArrayList<Date>();
	private Schedule schedule;
	
	/**
	 * @param status
	 * @param message
	 */
	public PlanResponce(Integer status, String message) {
		super();
		this.status = status;
		this.message = message;
		
	}
	
	/**
	 * @param status
	 * @param message
	 * @param dates
	 * @param schedule
	 */
	public PlanResponce(Integer status, String message, List<Date> dates, Schedule schedule) {
		super();
		this.status = status;
		this.message = message;
		this.dates = dates;
		this.schedule = schedule;
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

	public List<Date> getDates() {
		return dates;
	}

	public void setDates(List<Date> dates) {
		this.dates = dates;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	@Override
	public String toString() {
		return "PlanResponce [status=" + status + ", message=" + message + ", dates=" + dates + ", schedule=" + schedule
				+ "]";
	}
	
	
	
}
