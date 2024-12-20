package by.base.main.model;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "schedule")
public class Schedule{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idschedule")
    private Integer idSchedule;

    @Column(name = "counterparty_code")
    private Long counterpartyCode;

    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "counterparty_contract_code")
    private Long counterpartyContractCode;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "monday")
    private String monday;

    @Column(name = "tuesday")
    private String tuesday;

    @Column(name = "wednesday")
    private String wednesday;

    @Column(name = "thursday")
    private String thursday;

    @Column(name = "friday")
    private String friday;

    @Column(name = "saturday")
    private String saturday;

    @Column(name = "sunday")
    private String sunday;

    @Column(name = "supplies")
    private Integer supplies;

    @Column(name = "tz")
    private String tz;

    @Column(name = "tp")
    private String tp;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "multiple_of_pallet")
    private Boolean multipleOfPallet;

    @Column(name = "multiple_of_truck")
    private Boolean multipleOfTruck;

    @Column(name = "num_stock")
    private Integer numStock;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "runoff_calculation")
    private Integer runoffCalculation;

    @Column(name = "date_last_calculation")
    private Date dateLastCalculation;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "history")
    private String history;
    
    @Column(name = "date_last_changing")
    private Date dateLastChanging;
    
    @Column(name = "is_not_calc")
    private Boolean isNotCalc;
    
    /*
     * Столбец сегодня на сегодня
     */
    @Column(name = "day_to_day")
    private Boolean isDayToDay;
    
    /*
     * тип графика: на РЦ или на ТО
     */
    @Column(name = "type")
    private String type;

	@Column(name = "start_date_temp")
	private Date startDateTemp;

	@Column(name = "end_date_temp")
	private Date endDateTemp;
	
	@Column(name = "date_load_excel")
	private Timestamp dateLoadExcel;

	public Date getStartDateTemp() {
		return startDateTemp;
	}

	public void setStartDateTemp(Date startDateTemp) {
		this.startDateTemp = startDateTemp;
	}

	public Date getEndDateTemp() {
		return endDateTemp;
	}

	public void setEndDateTemp(Date endDateTemp) {
		this.endDateTemp = endDateTemp;
	}

	/*
     * График формирования заказа  четная неделя ставим метка  --ч-- , нечетная --- н ---
     */
    @Column(name = "order_formation_schedule")
    private String orderFormationSchedule;
    
    /*
     * График отгрузки заказа  четная неделя ставим метка  --ч-- , нечетная --- н ---
     */
    @Column(name = "order_shipment_schedule")
    private String orderShipmentSchedule;
    
    /*
     * тип для графиков поставок на ТО : холодный или хухой
     */
    @Column(name = "to_type")
    private String toType;
    
    @Column(name = "name_stock")
    private String nameStock;
    
    /**
     * кратность машины
     */
    @Column(name = "machine_multiplicity")
    private Integer machineMultiplicity;
    
    /**
     * связь поставки в одной машины
     */
    @Column(name = "connection_supply")
    private Integer connectionSupply;
    
    /**
     * кодовое ИМЯ КОНТРАГЕНТА
     */
    @Column(name = "code_name_of_quantum_counterparty")
    private String codeNameOfQuantumCounterparty;
    
    /**
     * Квант
     */
    @Column(name = "quantum")
    private Double quantum;
    
    /**
     * измерения КВАНТА
     */
    @Column(name = "quantum_measurements")
    private String quantumMeasurements;
    
    
    
    @JsonIgnore
    @Transient
    private List<String> days;

    
    /**
	 * 
	 */
	public Schedule() {
		super();
	}


	/**
     * кодовое ИМЯ КОНТРАГЕНТА
     */
	public String getCodeNameOfQuantumCounterparty() {
		return codeNameOfQuantumCounterparty;
	}


	/**
     * кодовое ИМЯ КОНТРАГЕНТА
     */
	public void setCodeNameOfQuantumCounterparty(String codeNameOfQuantumCounterparty) {
		this.codeNameOfQuantumCounterparty = codeNameOfQuantumCounterparty;
	}


	/**
	 * Квант
	 * @return
	 */
	public Double getQuantum() {
		return quantum;
	}

	/**
	 * Квант
	 * @return
	 */
	public void setQuantum(Double quantum) {
		this.quantum = quantum;
	}


	/**
	 * измерения КВАНТА
	 * @return
	 */
	public String getQuantumMeasurements() {
		return quantumMeasurements;
	}


	/**
	 * измерения КВАНТА
	 * @return
	 */
	public void setQuantumMeasurements(String quantumMeasurements) {
		this.quantumMeasurements = quantumMeasurements;
	}



	public Integer getIdSchedule() {
        return idSchedule;
    }
    
    

    public Date getDateLastChanging() {
		return dateLastChanging;
	}



	public String getToType() {
		return toType;
	}



	public void setToType(String toType) {
		this.toType = toType;
	}



	public String getNameStock() {
		return nameStock;
	}



	public void setNameStock(String nameStock) {
		this.nameStock = nameStock;
	}



	public void setDateLastChanging(Date dateLastChanging) {
		this.dateLastChanging = dateLastChanging;
	}



	public Boolean getIsDayToDay() {
		return isDayToDay;
	}



	public void setIsDayToDay(Boolean isDayToDay) {
		this.isDayToDay = isDayToDay;
	}


	/**
	 * тип графика: на РЦ или на ТО
	 * @return
	 */
	public String getType() {
		return type;
	}


	/**
	 * тип графика: на РЦ или на ТО
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}


	/**
     * График формирования заказа  четная неделя ставим метка  --ч-- , нечетная --- н ---
     */
	public String getOrderFormationSchedule() {
		return orderFormationSchedule;
	}


	/**
     * График формирования заказа  четная неделя ставим метка  --ч-- , нечетная --- н ---
     */
	public void setOrderFormationSchedule(String orderFormationSchedule) {
		this.orderFormationSchedule = orderFormationSchedule;
	}


	/**
     * График отгрузки заказа  четная неделя ставим метка  --ч-- , нечетная --- н ---
     */
	public String getOrderShipmentSchedule() {
		return orderShipmentSchedule;
	}


	/**
     * График отгрузки заказа  четная неделя ставим метка  --ч-- , нечетная --- н ---
     */
	public void setOrderShipmentSchedule(String orderShipmentSchedule) {
		this.orderShipmentSchedule = orderShipmentSchedule;
	}



	public Integer getStatus() {
		return status;
	}



	public Boolean getIsNotCalc() {
		return isNotCalc;
	}



	public void setIsNotCalc(Boolean isNotCalc) {
		this.isNotCalc = isNotCalc;
	}



	public void setStatus(Integer status) {
		this.status = status;
	}



	public String getHistory() {
		return history;
	}



	public void setHistory(String history) {
		this.history = history;
	}

	/**
	 * кратность машины
	 * @return
	 */
	public Integer getMachineMultiplicity() {
		return machineMultiplicity;
	}


	/**
	 * кратность машины
	 * @param machineMultiplicity
	 */
	public void setMachineMultiplicity(Integer machineMultiplicity) {
		this.machineMultiplicity = machineMultiplicity;
	}

	/**
	 * связь поставки в одной машины
	 * @return
	 */
	public Integer getConnectionSupply() {
		return connectionSupply;
	}

	/**
	 * связь поставки в одной машины
	 * @param connectionSupply
	 */
	public void setConnectionSupply(Integer connectionSupply) {
		this.connectionSupply = connectionSupply;
	}


	/**
	 * Дата и время прогрузки из файла ексель
	 * @return
	 */
	public Timestamp getDateLoadExcel() {
		return dateLoadExcel;
	}

	/**
	 * Дата и время прогрузки из файла ексель
	 * @return
	 */
	public void setDateLoadExcel(Timestamp dateLoadExcel) {
		this.dateLoadExcel = dateLoadExcel;
	}

	/**
     * Метод отдаёт лист с днями, которые не равны null
     * 
     * @return
     */
    public List<String> getDays() {
    	days = new ArrayList<String>();
    	if(monday != null) {
    		days.add("MONDAY");
    	}
    	
    	if(tuesday != null) {
    		days.add("TUESDAY");
    	}
    	
    	if(wednesday != null) {
    		days.add("WEDNESDAY");
    	}
    	
    	if(thursday != null) {
    		days.add("THURSDAY");
    	}
    	
    	if(friday != null) {
    		days.add("FRIDAY");
    	}
    	
    	if(saturday != null) {
    		days.add("SATURDAY");
    	}
    	
    	if(sunday != null) {
    		days.add("SUNDAY");
    	}
    	
		return days;
	}
    
    /**
     * Отдаёт мапу с днями, где ключи это день (в аппер кейсе) а значение - хначение этого дня
     * @return
     */
    public Map<String, String> getDaysMap() {
    	Map<String, String> map = new HashMap<String, String>();
    	if(monday != null) {
    		map.put("MONDAY", monday);
    	}
    	
    	if(tuesday != null) {
    		map.put("TUESDAY", tuesday);
    	}
    	
    	if(wednesday != null) {
    		map.put("WEDNESDAY", wednesday);
    	}
    	
    	if(thursday != null) {
    		map.put("THURSDAY", thursday);
    	}
    	
    	if(friday != null) {
    		map.put("FRIDAY", friday);
    	}
    	
    	if(saturday != null) {
    		map.put("SATURDAY", saturday);
    	}
    	
    	if(sunday != null) {
    		map.put("SUNDAY", sunday);
    	}
    	
		return map;
	}

	public Integer getRunoffCalculation() {
		return runoffCalculation;
	}

	public void setRunoffCalculation(Integer runoffCalculation) {
		this.runoffCalculation = runoffCalculation;
	}

	public void setIdSchedule(Integer idSchedule) {
        this.idSchedule = idSchedule;
    }

    public Long getCounterpartyCode() {
        return counterpartyCode;
    }

    public void setCounterpartyCode(Long counterpartyCode) {
        this.counterpartyCode = counterpartyCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCounterpartyContractCode() {
        return counterpartyContractCode;
    }

    public void setCounterpartyContractCode(Long counterpartyContractCode) {
        this.counterpartyContractCode = counterpartyContractCode;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public String getSaturday() {
        return saturday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    public String getSunday() {
        return sunday;
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
    }

    public Integer getSupplies() {
        return supplies;
    }

    public void setSupplies(Integer supplies) {
        this.supplies = supplies;
    }

    public String getTz() {
        return tz;
    }

    public void setTz(String tz) {
        this.tz = tz;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getMultipleOfPallet() {
        return multipleOfPallet;
    }

    public void setMultipleOfPallet(Boolean multipleOfPallet) {
        this.multipleOfPallet = multipleOfPallet;
    }

    public Boolean getMultipleOfTruck() {
        return multipleOfTruck;
    }

    public void setMultipleOfTruck(Boolean multipleOfTruck) {
        this.multipleOfTruck = multipleOfTruck;
    }

    public Integer getNumStock() {
        return numStock;
    }

    public void setNumStock(Integer numStock) {
        this.numStock = numStock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateLastCalculation() {
        return dateLastCalculation;
    }

    public void setDateLastCalculation(Date dateLastCalculation) {
        this.dateLastCalculation = dateLastCalculation;
    }

	@Override
	public int hashCode() {
		return Objects.hash(idSchedule);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Schedule other = (Schedule) obj;
		return Objects.equals(idSchedule, other.idSchedule);
	}



	@Override
	public String toString() {
		return "Schedule [idSchedule=" + idSchedule + ", counterpartyCode=" + counterpartyCode + ", name=" + name
				+ ", counterpartyContractCode=" + counterpartyContractCode + ", note=" + note + ", monday=" + monday
				+ ", tuesday=" + tuesday + ", wednesday=" + wednesday + ", thursday=" + thursday + ", friday=" + friday
				+ ", saturday=" + saturday + ", sunday=" + sunday + ", supplies=" + supplies + ", tz=" + tz + ", tp="
				+ tp + ", comment=" + comment + ", multipleOfPallet=" + multipleOfPallet + ", multipleOfTruck="
				+ multipleOfTruck + ", numStock=" + numStock + ", description=" + description + ", runoffCalculation="
				+ runoffCalculation + ", dateLastCalculation=" + dateLastCalculation + ", STATUS=" + status
				+ ", history=" + history + ", dateLastChanging=" + dateLastChanging + ", days=" + days + "]";
	}

	
    
    
}
