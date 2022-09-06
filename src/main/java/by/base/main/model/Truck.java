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
	
	
	@ManyToOne(fetch = FetchType.EAGER, 
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
