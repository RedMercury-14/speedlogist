package by.base.main.dto;

/**
 * @author DIma
 * Возвращает кол-во графиков поставок по дням недели, где в этот день недели стоит з (т.е. заказ)
 */
public class ScheduleCountOrderDTO {
	
	private Long monday;
    private Long tuesday;
    private Long wednesday;
    private Long thursday;
    private Long friday;
    private Long saturday;
    private Long sunday;

    public ScheduleCountOrderDTO(Long monday, Long tuesday, Long wednesday, Long thursday, 
                            Long friday, Long saturday, Long sunday) {
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }

    // Геттеры и сеттеры
    public Long getMonday() { return monday; }
    public void setMonday(Long monday) { this.monday = monday; }

    public Long getTuesday() { return tuesday; }
    public void setTuesday(Long tuesday) { this.tuesday = tuesday; }

    public Long getWednesday() { return wednesday; }
    public void setWednesday(Long wednesday) { this.wednesday = wednesday; }

    public Long getThursday() { return thursday; }
    public void setThursday(Long thursday) { this.thursday = thursday; }

    public Long getFriday() { return friday; }
    public void setFriday(Long friday) { this.friday = friday; }

    public Long getSaturday() { return saturday; }
    public void setSaturday(Long saturday) { this.saturday = saturday; }

    public Long getSunday() { return sunday; }
    public void setSunday(Long sunday) { this.sunday = sunday; }

	@Override
	public String toString() {
		return "ScheduleCountDTO [monday=" + monday + ", tuesday=" + tuesday + ", wednesday=" + wednesday
				+ ", thursday=" + thursday + ", friday=" + friday + ", saturday=" + saturday + ", sunday=" + sunday
				+ "]";
	}
    
    

}
