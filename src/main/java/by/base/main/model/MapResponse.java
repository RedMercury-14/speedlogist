package by.base.main.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.graphhopper.util.details.PathDetail;

public class MapResponse {
	
	private List<Map<String, Object>> Instruction;
	
	private List<Double[]> points;
	
	private List<Double[]> waypoints;
	
	private Double distance;
	
	private long time;
	
	private Shop startShop;
	
	private Shop endShop;
	
	public MapResponse() {}
	

	/**
	 * @param instruction
	 * @param points
	 * @param waypoints
	 * @param distance
	 * @param time
	 */
	public MapResponse(List<Map<String, Object>> instruction, List<Double[]> points, List<Double[]> waypoints,
			Double distance, long time) {
		super();
		Instruction = instruction;
		this.points = points;
		this.waypoints = waypoints;
		this.distance = distance;
		this.time = time;
	}
	
	

	/**
	 * @param points
	 * @param distance
	 * @param time
	 * @param startShop
	 * @param endShop
	 */
	public MapResponse(List<Double[]> points, Double distance, long time, Shop startShop, Shop endShop) {
		super();
		this.points = points;
		this.distance = distance;
		this.time = time;
		this.startShop = startShop;
		this.endShop = endShop;
	}



	public List<Map<String, Object>> getInstruction() {
		return Instruction;
	}

	public void setInstruction(List<Map<String, Object>> instruction) {
		Instruction = instruction;
	}

	public List<Double[]> getPoints() {
		return points;
	}

	public void setPoints(List<Double[]> points) {
		this.points = points;
	}

	public List<Double[]> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(List<Double[]> waypoints) {
		this.waypoints = waypoints;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Shop getStartShop() {
		return startShop;
	}

	public void setStartShop(Shop startShop) {
		this.startShop = startShop;
	}

	public Shop getEndShop() {
		return endShop;
	}

	public void setEndShop(Shop endShop) {
		this.endShop = endShop;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Instruction, distance, points, time, waypoints);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapResponse other = (MapResponse) obj;
		return Objects.equals(Instruction, other.Instruction) && Objects.equals(distance, other.distance)
				&& Objects.equals(points, other.points) && time == other.time
				&& Objects.equals(waypoints, other.waypoints);
	}

	@Override
	public String toString() {
		return "MapResponse [Instruction=" + Instruction + ", points=" + points + ", waypoints=" + waypoints
				+ ", distance=" + distance + ", time=" + time + ", startShop=" + startShop + ", endShop=" + endShop
				+ "]";
	}

	
	
}
