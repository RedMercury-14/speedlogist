package by.base.main.util.hcolossus.pojo;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import by.base.main.model.MapResponse;
import by.base.main.model.Shop;

/**
 * реализация общего маршрута машины
 */
public class VehicleWay {
	
	private String id;
	

	private List<Shop> way; // маршрут
	
	/**
	 * перепробег
	 * По сути реультат проверки на логичность, т.е. 
	 * <br>если overrun > 0, то этот маршршут не логичен, т.к. имеет перепробег
	 * <br><b>справочное значение<b>
	 */
	private Double overrun; 
	
	/**
	 * магазины из-за которых перепробег стал положительным
	 */
	private List<Shop> problemShops;
	
	private String type;
	
	/**
	 * 10 - нуждается в проверке на логичность
	 * <br>15 - идеальный недогруженный маршрут
	 * 20 - черновой нелогичный маршрут (но загруженный полностью)
	 * 30 - черновой логичный маршрут (но недогруженный)
	 * <br>35 - черновой логичный маршрут <b>(догруженный)<b>
	 * 40 - оптимизированный маршут согласно расстояниям
	 * 100 - чистовой маршрут
	 */
	private int status;
	
	private Vehicle vehicle;

	private Integer freePallInVehicle = null;
	
	private Integer freeWeigthInVehicle = null;
	
	private Double totalRun;
	
	private Integer summPall;
	
	/**
	 * Есть ли ограничения на маршруте
	 */
	private Boolean isRestriction;
	

	/**
	 * 10 - нуждается в проверке на логичность
	 * <br>15 - идеальный недогруженный маршрут
	 * <br>20 - черновой нелогичный маршрут (но загруженный полностью)
	 * <br>30 - черновой логичный маршрут (но недогруженный)
	 * <br>35 - черновой логичный маршрут <b>(догруженный)<b>
	 * <br>40 - оптимизированный маршут согласно расстояниям
	 * <br>100 - чистовой маршрут
	 * @param way
	 * @param overrun
	 * @param status
	 * @param vehicle
	 */
	public VehicleWay(List<Shop> way, Double overrun, int status, Vehicle vehicle) {
		super();
		if(way == null || way.isEmpty()) {
			System.err.println("Объект VehicleWay не может быть создан без порядка прохождения точек");
		}else {
			this.way = way;
			this.overrun = overrun;
			this.status = status;
			this.vehicle = vehicle;
			this.id = Objects.hash(vehicle, way)+"";
		}		
	}
	
	/**
	 * 10 - нуждается в проверке на логичность
	 * <br>15 - идеальный недогруженный маршрут
	 * <br>20 - черновой нелогичный маршрут (но загруженный полностью)
	 * <br>30 - черновой логичный маршрут (но недогруженный)
	 * <br>35 - черновой логичный маршрут <b>(догруженный)<b>
	 * <br>40 - оптимизированный маршут согласно расстояниям
	 * <br>100 - чистовой маршрут
	 * @param id
	 * @param way
	 * @param overrun
	 * @param status
	 * @param vehicle
	 */
	public VehicleWay(String id, List<Shop> way, Double overrun, int status, Vehicle vehicle) {
		super();
		if(way == null || way.isEmpty()) {
			System.err.println("Объект VehicleWay не может быть создан без порядка прохождения точек");
		}else {
			this.way = way;
			this.overrun = overrun;
			this.status = status;
			this.vehicle = vehicle;
			this.id = id;
		}		
	}
	
	/**
	 * 10 - нуждается в проверке на логичность
	 * <br>15 - идеальный недогруженный маршрут
	 * <br>20 - черновой нелогичный маршрут (но загруженный полностью)
	 * <br>30 - черновой логичный маршрут (но недогруженный)
	 * <br>35 - черновой логичный маршрут <b>(догруженный)<b>
	 * <br>40 - оптимизированный маршут согласно расстояниям
	 * <br>100 - чистовой маршрут
	 * @param way
	 * @param overrun
	 * @param status
	 * @param vehicle
	 */
	public VehicleWay(List<Shop> way, Double overrun, int status, Vehicle vehicle, Boolean isRestriction) {
		super();
		if(way == null || way.isEmpty()) {
			System.err.println("Объект VehicleWay не может быть создан без порядка прохождения точек");
		}else {
			this.way = way;
			this.overrun = overrun;
			this.status = status;
			this.vehicle = vehicle;
			this.id = Objects.hash(vehicle, way)+"";
			this.isRestriction = isRestriction;
		}		
	}
	
	public VehicleWay(String id) {
		this.id = id;
	};

	public List<Shop> getWay() {
		return way;
	}

	public void setWay(List<Shop> way) {
		this.way = way;
	}

	/**
	 * перепробег (логичность)
	 * @return
	 */
	public Double getOverrun() {
		return overrun;
	}

	/**
	 * перепробег (логичность)
	 * @return
	 */
	public void setOverrun(Double overrun) {
		this.overrun = overrun;
	}

	
	public List<Shop> getProblemShops() {
		return problemShops;
	}

	public void setProblemShops(List<Shop> problemShops) {
		this.problemShops = problemShops;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}
	
	public Integer getSummPall() {
		return summPall;
	}

	public void setSummPall(Integer summPall) {
		this.summPall = summPall;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	/**
	 * ВОзвращает свободное место в машине на этом маршруте
	 * @return
	 */
	public Integer getFreePallInVehicle() {
		if(freePallInVehicle == null) {
			return vehicle.getFreePall();
		}else {			
			return freePallInVehicle;
		}
	}
	
	/**
	 * ВОзвращает свободный вес в машине на этом маршруте
	 * @return
	 */
	public Integer getFreeWeigthInVehicle() {
		if(freeWeigthInVehicle == null) {
			return vehicle.getFreeWeigth();
		}else {			
			return freeWeigthInVehicle;
		}
	}
	
	/**
	 * Задаёт свободный вес в машине на этом маршруте
	 * @param freePallInVehicle
	 */
	public void setFreeWeigthInVehicle(Integer freeWeigthInVehicle) {
		this.freeWeigthInVehicle = freeWeigthInVehicle;
	}
	

	/**
	 * Задаёт свободное место в машине на этом маршруте
	 * @param freePallInVehicle
	 */
	public void setFreePallInVehicle(Integer freePallInVehicle) {
		this.freePallInVehicle = freePallInVehicle;
	}
	
	/**
	 * @return Возвращает суммарный пробег маршрута
	 */
	public Double getTotalRun() {
		return totalRun;
	}

	/**
	 * Задаёт суммарный пробег маршрута
	 * @param totalRun
	 */
	public void setTotalRun(Double totalRun) {
		this.totalRun = totalRun;
	}
	
	

	public Boolean getIsRestriction() {
		return isRestriction;
	}

	public void setIsRestriction(Boolean isRestriction) {
		this.isRestriction = isRestriction;
	}

	@Override
	public int hashCode() {
		return Objects.hash(vehicle, way);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VehicleWay other = (VehicleWay) obj;
		return Objects.equals(vehicle, other.vehicle) && Objects.equals(way, other.way);
	}

	@Override
	public String toString() {
		String wayStr = "";
		for (Shop shop : way) {
			wayStr = wayStr + shop.getNumshop()+"->";
		}
		String problemShopsStr = "";
		if(problemShops == null) {
			problemShopsStr = "-";
		}else {
			for (Shop shop : problemShops) {
				problemShopsStr = problemShopsStr+shop.getNumshop()+"-";
			}
		}
		
		return "VehicleWay [id=" + id + ", way=" + wayStr + ", overrun=" + overrun + ", problemShops=" + problemShopsStr
				+ ", type=" + type + ", status=" + status + ", vehicle=" + vehicle + "]";
	}
	
	public String toText() {
		String wayStr = "Маршрут №" + id + "\n";
		for (Shop shop : way) {
			wayStr = wayStr + shop.getNumshop()+"("+shop.getNeedPall()+")"+"\n";
		}
		String truck = "";
		truck = "На маршрут назначена машина : Тип: "+vehicle.getType() + " с паллетовместимостью: " + vehicle.getPall() + "; Всего загружено в машину : " + vehicle.getTargetPall() + " паллет. \n"
				+"===================================================\n";
		return wayStr+truck;
	}
	
	private int getRandomNumber(int min, int max) {
	    return (int) ((Math.random() * (max - min)) + min);
	}


	

}
