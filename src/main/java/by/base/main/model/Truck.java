package by.base.main.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "truck")
public class Truck implements Serializable{

	/**
	 * @author Dima Hrushevski
	 * */
	 
	private static final long serialVersionUID = 2716750147221795304L;
	
	public Truck() {}
	
	public Truck(String numTruck, String cargoCapacity, String pallCapacity, String modelTruck, String brandTruck,
			String numTrailer, String typeTrailer) {
		super();
		this.numTruck = numTruck;
		this.cargoCapacity = cargoCapacity;
		this.pallCapacity = pallCapacity;
		this.modelTruck = modelTruck;
		this.brandTruck = brandTruck;
		this.numTrailer = numTrailer;
		this.typeTrailer = typeTrailer;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idtruck")
	private int idTruck;
	
	@Column(name = "numTruck")
	private String numTruck;
	
	@Column(name = "nameDriver")
	private String nameDriver;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "cargoCapacity")
	private String cargoCapacity;
	
	@Column(name = "pallCapacity")
	private String pallCapacity;
	
	
	@ManyToOne(fetch = FetchType.LAZY, 
				cascade = { CascadeType.ALL })
	@JoinColumn(name = "user_iduser")
	@JsonBackReference
	private User user;
	
	@OneToMany(fetch=FetchType.LAZY,
			   mappedBy="truck",
			   cascade= {CascadeType.PERSIST, CascadeType.MERGE,
						 CascadeType.DETACH, CascadeType.REFRESH})
	@JsonIgnore
	private List<Route> routes;
	
	@Column(name = "modelTruck")
	private String modelTruck;
	
	@Column(name = "brandTruck")
	private String brandTruck;
	
	@Column(name = "numTrailer")
	private String numTrailer;
	
	@Column(name = "typeTrailer")
	private String typeTrailer;
	
	@Column(name = "brandTrailer")
	private String brandTrailer;
	
	@Column(name = "ownerTruck")
	private String ownerTruck;

	/**
	 * тип сцепки: \nгрузовик\nполуприцеп\nсцепка
	 */
	@Column(name = "hitch_type")
	private String hitchType;
	
	/**
	 * тип загрузки\nзадняя\nбоковая\nзадняя + боковая\nполная растентовка\n
	 */
	@Column(name = "type_of_load")
	private String typeLoad;
	
	/**
	 * прочая инфа, такая как \nпневмоподушка\nгидроборт\nGPS навигация\nремни\nстойки
	 */
	@Column(name = "info")
	private String info;
	
	/**
	 * объем, м. куб 
	 */
	@Column(name = "volume_trailer")
	private Integer volumeTrailer;
	
	/**
	 * внутринние габариты кузова (Д/Ш/В), м
	 */
	@Column(name = "dimensions_body")
	private String dimensionsBody;
	
	/**
	 * Number of axes\n2\n3\n4+
	 */
	@Column(name = "number_axes")
	private String number_axes;
	
	/**
	 * Технический паспорт МАА	325845	РЭП ГАИ	02.02.2022\n
	 */
	@Column(name = "technical_certificate")
	private String technicalCertificate;
	
	/**
	 * проверена ли машина модератором. Если true то проверена
	 */
	@Column(name = "is_verify")
	private Boolean verify;
	
	public int getIdTruck() {
		return idTruck;
	}

	public void setIdTruck(int idTruck) {
		this.idTruck = idTruck;
	}

	public String getNumTruck() {
		return numTruck;
	}

	public void setNumTruck(String numTruck) {
		this.numTruck = numTruck;
	}

	public String getNameDriver() {
		return nameDriver;
	}

	public void setNameDriver(String nameDriver) {
		this.nameDriver = nameDriver;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCargoCapacity() {
		return cargoCapacity;
	}

	public void setCargoCapacity(String cargoCapacity) {
		this.cargoCapacity = cargoCapacity;
	}

	public String getPallCapacity() {
		return pallCapacity;
	}

	public void setPallCapacity(String pallCapacity) {
		this.pallCapacity = pallCapacity;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}	

	public List<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

	public String getModelTruck() {
		return modelTruck;
	}

	public void setModelTruck(String modelTruck) {
		this.modelTruck = modelTruck;
	}

	public String getBrandTruck() {
		return brandTruck;
	}

	public void setBrandTruck(String brandTruck) {
		this.brandTruck = brandTruck;
	}

	public String getNumTrailer() {
		return numTrailer;
	}

	public void setNumTrailer(String numTrailer) {
		this.numTrailer = numTrailer;
	}

	public String getTypeTrailer() {
		return typeTrailer;
	}

	public void setTypeTrailer(String typeTrailer) {
		this.typeTrailer = typeTrailer;
	}

	public String getBrandTrailer() {
		return brandTrailer;
	}

	public void setBrandTrailer(String brandTrailer) {
		this.brandTrailer = brandTrailer;
	}

	public String getOwnerTruck() {
		return ownerTruck;
	}

	public void setOwnerTruck(String ownerTruck) {
		this.ownerTruck = ownerTruck;
	}
	
	

	public String getHitchType() {
		return hitchType;
	}

	public void setHitchType(String hitchType) {
		this.hitchType = hitchType;
	}

	public String getTypeLoad() {
		return typeLoad;
	}

	public void setTypeLoad(String typeLoad) {
		this.typeLoad = typeLoad;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Integer getVolumeTrailer() {
		return volumeTrailer;
	}

	public void setVolumeTrailer(Integer volumeTrailer) {
		this.volumeTrailer = volumeTrailer;
	}

	public String getDimensionsBody() {
		return dimensionsBody;
	}

	public void setDimensionsBody(String dimensionsBody) {
		this.dimensionsBody = dimensionsBody;
	}

	public String getNumber_axes() {
		return number_axes;
	}

	public void setNumber_axes(String number_axes) {
		this.number_axes = number_axes;
	}

	public String getTechnicalCertificate() {
		return technicalCertificate;
	}

	public void setTechnicalCertificate(String technicalCertificate) {
		this.technicalCertificate = technicalCertificate;
	}

	public Boolean getVerify() {
		return verify;
	}

	public void setVerify(Boolean verify) {
		this.verify = verify;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idTruck);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Truck other = (Truck) obj;
		return idTruck == other.idTruck;
	}

	@Override
	public String toString() {
		return "Truck [idTruck=" + idTruck + ", numTruck=" + numTruck + ", nameDriver=" + nameDriver
				+ ", telephoneDriver=" + status + ", cargoCapacity=" + cargoCapacity + ", pallCapacity="
				+ pallCapacity + "]";
	}
	
}
