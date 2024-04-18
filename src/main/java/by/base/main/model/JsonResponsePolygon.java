package by.base.main.model;

import java.util.Map;
import java.util.Objects;

/**
 * Модель для ответа от сервера на фронт для полигонов
 * @author Dima
 *
 */
public class JsonResponsePolygon {
	
	private String type;
	private Map <String, String> properties;
	private GeometryResponse geometry;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Map<String, String> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	public GeometryResponse getGeometry() {
		return geometry;
	}
	public void setGeometry(GeometryResponse geometry) {
		this.geometry = geometry;
	}
	@Override
	public String toString() {
		return "JsonResponsePolygon [type=" + type + ", properties=" + properties + ", geometry=" + geometry + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(geometry, properties, type);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JsonResponsePolygon other = (JsonResponsePolygon) obj;
		return Objects.equals(geometry, other.geometry) && Objects.equals(properties, other.properties)
				&& Objects.equals(type, other.type);
	}
	
	

}
