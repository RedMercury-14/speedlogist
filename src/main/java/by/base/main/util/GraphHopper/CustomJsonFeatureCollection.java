package by.base.main.util.GraphHopper;

import java.util.ArrayList;
import java.util.List;

import com.graphhopper.util.JsonFeature;
import com.graphhopper.util.JsonFeatureCollection;

public class CustomJsonFeatureCollection extends JsonFeatureCollection{
	String type = "FeatureCollection";
    List<JsonFeature> features = new ArrayList<>();

    public String getType() {
        return type;
    }

    public List<JsonFeature> getFeatures() {
        return features;
    }
    

    public void setFeatures(List<JsonFeature> features) {
		this.features = features;
	}

	@Override
    public String toString() {
        return features.toString();
    }
}
