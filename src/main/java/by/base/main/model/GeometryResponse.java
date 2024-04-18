package by.base.main.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GeometryResponse {
	
	private List<double[]> coordinates;
	private String type;
	
	
	
	/**
	 * @param coordinates
	 * @param type
	 */
	public GeometryResponse(List<double[]> coordinates, String type) {
		super();
		this.coordinates = coordinates;
		this.type = type;
	}
	public List<double[]> getCoordinates() {
		return coordinates;
	}
	public void setCoordinates(List<double[]> coordinates) {
		this.coordinates = coordinates;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public int hashCode() {
		return Objects.hash(coordinates, type);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeometryResponse other = (GeometryResponse) obj;
		return Objects.equals(coordinates, other.coordinates) && Objects.equals(type, other.type);
	}
	@Override
	public String toString() {
		return "GeometryResponse [coordinates=" + coordinates + ", type=" + type + "]";
	}
	
	
}
