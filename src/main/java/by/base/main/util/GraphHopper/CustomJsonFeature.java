package by.base.main.util.GraphHopper;

import java.io.Serializable;
import java.util.Map;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import com.graphhopper.util.JsonFeature;

public class CustomJsonFeature extends JsonFeature implements Serializable{
	
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 8581784931561061993L;
	private String action;
    private String id;
    private String type = "Feature";
    private Envelope bbox;
    private Geometry geometry;
    private Map<String, Object> properties;
    
    
    
	/**
	 * 
	 */
	public CustomJsonFeature() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * @param action
	 * @param id
	 * @param type
	 * @param bbox
	 * @param geometry
	 * @param properties
	 */
	public CustomJsonFeature(String action, String id, String type, Envelope bbox, Geometry geometry,
			Map<String, Object> properties) {
		super();
		this.action = action;
		this.id = id;
		this.type = type;
		this.bbox = bbox;
		this.geometry = geometry;
		this.properties = properties;
	}
	@Override
	public String toString() {
		return "CustomJsonFeature [action=" + action + ", id=" + id + ", type=" + type + ", bbox=" + bbox
				+ ", geometry=" + geometry + ", properties=" + properties + "]";
	}


	
	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public Envelope getBbox() {
		return bbox;
	}


	public void setBbox(Envelope bbox) {
		this.bbox = bbox;
	}


	public Geometry getGeometry() {
		return geometry;
	}


	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}


	public Map<String, Object> getProperties() {
		return properties;
	}


	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}


	public String getType() {
		return type;
	}

	public JsonFeature toJsonFeature() {
		JsonFeature result = new JsonFeature(id, type, bbox, geometry, properties);
		return result;
	}
	

}
