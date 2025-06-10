package by.base.main.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "distance_matrix")
public class DistanceMatrix {
	
	@Column(name = "id_distance_matrix")
    private String idDistanceMatrix; // PRIMARY KEY

    @Column(name = "distance")
    private Double distance; 

    @Column(name = "time")
    private Double time;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private String id; // PRIMARY KEY
    
    @Column(name = "shop_from")
    private String shopFrom; 
    
    @Column(name = "shop_to")
    private String shopTo; 

    
    
	public String getShopFrom() {
		return shopFrom;
	}

	public void setShopFrom(String shopFrom) {
		this.shopFrom = shopFrom;
	}

	public String getShopTo() {
		return shopTo;
	}

	public void setShopTo(String shopTo) {
		this.shopTo = shopTo;
	}

	public String getIdDistanceMatrix() {
		return idDistanceMatrix;
	}

	public void setIdDistanceMatrix(String idDistanceMatrix) {
		this.idDistanceMatrix = idDistanceMatrix;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Double getTime() {
		return time;
	}

	public void setTime(Double time) {
		this.time = time;
	}
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(distance, idDistanceMatrix, time);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DistanceMatrix other = (DistanceMatrix) obj;
		return Objects.equals(distance, other.distance) && Objects.equals(idDistanceMatrix, other.idDistanceMatrix)
				&& Objects.equals(time, other.time);
	}

	@Override
	public String toString() {
		return "DistanceMatrix [idDistanceMatrix=" + idDistanceMatrix + ", distance=" + distance + ", time=" + time
				+ "]";
	}
    
    

}
