package by.base.main.util.hcolossus.pojo;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Класс реализующий транспортное средство для проекта колос.
 * важен тип, перевозимое кол-во паллет и перевозимый вес.
 * 
 */
public class Vehicle{
	
	private int id;
	private String name;
	private String type;
	private Integer pall;	//ко-во паллет
	private Integer weigth;	//вес в кг
	private double volume;	//объём метрах куб
	private double height;	//высота метрах
	private double width;	//ширина метрах 
	private double length;	//длинна в метрах
	private boolean isFull;	//загрпужена ли полностью машина
	
	//реализации специальных полей
	private Integer targetPall;	//ко-во паллет загруженных на текущий момент времени
	private Integer targetWeigth;	//вес загруженный на текущий момент времени
	
	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param pall
	 */
	public Vehicle(int id, String name, String type, Integer pall) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.pall = pall;
		this.isFull = false;
		this.targetPall = 0;
	}
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param type
	 * @param pall
	 * @param weigth
	 */
	public Vehicle(int id, String name, String type, Integer pall, Integer weigth) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.pall = pall;
		this.weigth = weigth;
		this.isFull = false;
		this.targetPall = 0;
	}

	public Vehicle() {
		super();
		this.isFull = false;
		this.targetPall = 0;
	}
	
	
	/**
	 * Возвращает вес в кг загруженный в машину
	 * @return
	 */
	public Integer getTargetWeigth() {
		return targetWeigth;
	}

	/**
	 * Задаёт вес в кг, загруженный в машину
	 * @param targetWeigth
	 */
	public void setTargetWeigth(Integer targetWeigth) {
		this.targetWeigth = targetWeigth;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * возвращает паллетовместимость машины
	 * @return
	 */
	public Integer getPall() {
		return pall;
	}

	/**
	 * паллетовместимость машины
	 * @param pall
	 */
	public void setPall(Integer pall) {
		this.pall = pall;
	}

	/**
	 * Возвращает грузоподъемность авто
	 * @return
	 */
	public Integer getWeigth() {
		return weigth;
	}

	/**
	 * задаёт грузоподъемность авто
	 * @param weigth
	 */
	public void setWeigth(Integer weigth) {
		this.weigth = weigth;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public boolean isFull() {
		if(this.pall == this.targetPall || this.pall.equals(this.targetPall)) {
			return true;
		}else {
			return false;
		}
		
	}


	/**
	 * возвращет кол-во загруженных паллет в машине
	 * @return
	 */
	public Integer getTargetPall() {
		return targetPall;
	}

	/**
	 * ко-во паллет загруженных на текущий момент времени
	 * @param targetPall
	 */
	public void setTargetPall(Integer targetPall) {
		this.targetPall = targetPall;
	}
	
	/**
	 * Возвращает свободное место
	 * @return
	 */
	public Integer getFreePall() {
		return (int) (pall - targetPall) < 0 ? -1 : (pall - targetPall) ;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, pall, weigth);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vehicle other = (Vehicle) obj;
		return id == other.id && Double.doubleToLongBits(pall) == Double.doubleToLongBits(other.pall)
				&& Double.doubleToLongBits(weigth) == Double.doubleToLongBits(other.weigth);
	}
	
	/**
	 * Аналогичен методу clone только возвращает Vehicle с id отрицательным значением
	 * @return
	 */
	@JsonIgnore
	public Vehicle getVirtualVehicle() {
		Vehicle vehicle = new Vehicle();
		vehicle.setId(this.id*(-1));
		vehicle.setName(this.name);
		vehicle.setType(this.type);
		vehicle.setPall(this.pall);
		vehicle.setWeigth(this.weigth);
		vehicle.setVolume(this.volume);
		vehicle.setHeight(this.height);
		vehicle.setWidth(this.width);
		vehicle.setLength(this.length);
		vehicle.setTargetPall(this.targetPall);		
		return vehicle;
		
	}

	
	public String toString() {
		if(this.pall == this.targetPall || this.pall.equals(this.targetPall)) {
			this.isFull = true;
		}else {
			this.isFull = false;
		}
		return "Vehicle [id=" + id + ", name=" + name + ", type=" + type + ", pall=" + pall + ", weigth=" + weigth
				+ ", volume=" + volume + ", height=" + height + ", width=" + width + ", length=" + length + ", isFull="
				+ isFull + ", targetPall=" + targetPall + ", targetWeigth(загружено кг)="+targetWeigth+"]";
	}
	
}
