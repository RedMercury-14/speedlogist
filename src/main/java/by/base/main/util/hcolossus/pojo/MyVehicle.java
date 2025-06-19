package by.base.main.util.hcolossus.pojo;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * Класс реализующий транспортное средство для проекта колос.
 * важен тип, перевозимое кол-во паллет и перевозимый вес.
 * 
 */
public class MyVehicle{
	
	private int id;
	private String name;
	private String type;
	private Double pall;	//ко-во паллет
	private Integer weigth;	//вес в кг
	private double volume;	//объём метрах куб
	private double height;	//высота метрах
	private double width;	//ширина метрах 
	private double length;	//длинна в метрах
	private boolean isFull;	//загрпужена ли полностью машина
	private boolean isTwiceRound;	//можно ли отправлять на второй круг. По умолчанию false
	private boolean isClone;	//является ли эта машина клоном для второго круга. По умолчанию false
	
	//реализации специальных полей
	private Double targetPall;	//ко-во паллет загруженных на текущий момент времени
	private Integer targetWeigth;	//вес загруженный на текущий момент времени
	
	/**
	 * @param id
	 * @param name
	 * @param type
	 * @param pall
	 */
	public MyVehicle(int id, String name, String type, Double pall) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.pall = pall;
		this.isFull = false;
		this.isTwiceRound = false;
		this.isClone = false;
		this.targetPall = 0.0;
	}
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param type
	 * @param pall
	 * @param weigth
	 */
	public MyVehicle(int id, String name, String type, Double pall, Integer weigth) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.pall = pall;
		this.weigth = weigth;
		this.isFull = false;
		this.isTwiceRound = false;
		this.isClone = false;
		this.targetPall = 0.0;
	}

	public MyVehicle() {
		super();
		this.isFull = false;
		this.isTwiceRound = false;
		this.isClone = false;
		this.targetPall = 0.0;
	}
	
	
	
	public boolean isClone() {
		return isClone;
	}

	public void setClone(boolean isClone) {
		this.isClone = isClone;
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
	public Double getPall() {
		return pall;
	}

	/**
	 * паллетовместимость машины
	 * @param pall
	 */
	public void setPall(Double pall) {
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
		if(this.pall == this.targetPall || this.pall.equals(this.targetPall) || this.weigth == this.targetWeigth || this.weigth.equals(this.targetWeigth)) {
			return true;
		}else {
			return false;
		}
		
	}


	public boolean isTwiceRound() {
		return isTwiceRound;
	}

	public void setTwiceRound(boolean isTwiceRound) {
		this.isTwiceRound = isTwiceRound;
	}

	/**
	 * возвращет кол-во загруженных паллет в машине
	 * @return
	 */
	public Double getTargetPall() {
		return targetPall;
	}

	/**
	 * ко-во паллет загруженных на текущий момент времени
	 * @param targetPall
	 */
	public void setTargetPall(Double targetPall) {
		this.targetPall = targetPall;
	}
	
	/**
	 * Возвращает свободное место
	 * @return
	 */
	public Double getFreePall() {
		return (pall - targetPall) < 0 ? -1 : (pall - targetPall) ;
	}
	
	/**
	 * Возвращает свободное вес в машине
	 * @return
	 */
	public Integer getFreeWeigth() {
		if(targetWeigth != null) {
			return (int) (weigth - targetWeigth) < 0 ? -1 : (weigth - targetWeigth) ;
		}else {
			return weigth;
		}
		
	}

//	@Override
//	public int hashCode() {
//		return Objects.hash(id, pall, weigth);
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Vehicle other = (Vehicle) obj;
//		return id == other.id && Double.doubleToLongBits(pall) == Double.doubleToLongBits(other.pall)
//				&& Double.doubleToLongBits(weigth) == Double.doubleToLongBits(other.weigth);
//	}
	
	
	
	/**
	 * Аналогичен методу clone только возвращает Vehicle с id отрицательным значением
	 * @return
	 */
	@JsonIgnore
	public MyVehicle getVirtualVehicle() {
		MyVehicle vehicle = new MyVehicle();
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
		vehicle.setTwiceRound(this.isTwiceRound);
		vehicle.setClone(this.isClone);
		return vehicle;
		
	}
	
	/**
	 * Метод клонирует машину 
	 * @return
	 */
	@JsonIgnore
	public MyVehicle cloneForSecondRound() {
	    MyVehicle clonedVehicle = new MyVehicle();
	    
	    clonedVehicle.setId(this.getId());
	    clonedVehicle.setName(this.getName());
	    clonedVehicle.setType(this.getType());
	    clonedVehicle.setPall(this.getPall());
	    clonedVehicle.setWeigth(this.getWeigth());
	    clonedVehicle.setVolume(this.getVolume());
	    clonedVehicle.setHeight(this.getHeight());
	    clonedVehicle.setWidth(this.getWidth());
	    clonedVehicle.setLength(this.getLength());
	    clonedVehicle.setTwiceRound(this.isTwiceRound());
	    
	    // Устанавливаем, что это клон
	    clonedVehicle.setClone(true);

	    return clonedVehicle;
	}


	
	@Override
	public int hashCode() {
		return Objects.hash(id, isClone, pall, weigth);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyVehicle other = (MyVehicle) obj;
		return id == other.id && isClone == other.isClone && Objects.equals(pall, other.pall)
				&& Objects.equals(weigth, other.weigth);
	}

	public String toString() {
		if(this.pall == this.targetPall || this.pall.equals(this.targetPall)) {
			this.isFull = true;
		}else {
			this.isFull = false;
		}
		return "Vehicle [id=" + id + ", name=" + name + ", type=" + type + ", pall=" + pall + ", weigth=" + weigth
				+ ", volume=" + volume + ", height=" + height + ", width=" + width + ", length=" + length + ", isFull="
				+ isFull + ", targetPall=" + targetPall + ", targetWeigth(загружено кг)="+targetWeigth + ", isClone=" + isClone + ", isTwiceRound=" + isTwiceRound +"]";
	}
	
}
