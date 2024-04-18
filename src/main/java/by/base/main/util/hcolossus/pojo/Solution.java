/**
 * 
 */
package by.base.main.util.hcolossus.pojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;

import by.base.main.model.MapResponse;
import by.base.main.model.Shop;
import by.base.main.util.hcolossus.service.MatrixMachine;

/**
 * Класс который будет собирать все маршруты по статусам и провожить к логическому завершению все маршруты
 */
public class Solution {
	
	/**
	 * Чистовой маршрут
	 */
	private List<VehicleWay> whiteWay;
	
	/**
	 * Недогруженный маршрут
	 */
	private List<VehicleWay> inProcessWay;
	
	/**
	 * Пустые машины
	 */
	private List<Vehicle> emptyTrucks;
	
	/**
	 * Проблемные магазины (которые подзодят к нескольким маршрутам, но с отрицательной логичностью)
	 */
	private List <Shop> problemShop;
	
	/**
	 * Магазины на которые не хватило авто
	 */
	private List <Shop> emptyShop;
	
	private String stackTrace;
	
	/**
	 * Маршруты сформированные GH где ключ - это id самого маршрута
	 */
	private Map<String, List<MapResponse>> mapResponses;
	
	/**
	 * суммарный пробег по каждому маршруту, где ключ - это id самого маршрута
	 */
	private Map<String, Double> totalRunList;
	
	private Double totalRunKM;
	
	private Double koef;

	public List<VehicleWay> getWhiteWay() {
		return whiteWay;
	}

	public void setWhiteWay(List<VehicleWay> whiteWay) {
		this.whiteWay = whiteWay;
	}

	public Double getKoef() {
		return koef;
	}

	public void setKoef(Double koef) {
		this.koef = koef;
	}

	public List<VehicleWay> getInProcessWay() {
		return inProcessWay;
	}

	public void setInProcessWay(List<VehicleWay> inProcessWay) {
		this.inProcessWay = inProcessWay;
	}

	public List<Vehicle> getEmptyTrucks() {
		return emptyTrucks;
	}

	public void setEmptyTrucks(List<Vehicle> emptyTrucks) {
		this.emptyTrucks = emptyTrucks;
	}

	public List<Shop> getProblemShop() {
		return problemShop;
	}

	public void setProblemShop(List<Shop> problemShop) {
		this.problemShop = problemShop;
	}

	public List<Shop> getEmptyShop() {
		return emptyShop;
	}

	public void setEmptyShop(List<Shop> emptyShop) {
		this.emptyShop = emptyShop;
	}

	public Map<String, List<MapResponse>> getMapResponses() {
		return mapResponses;
	}

	public void setMapResponses(Map<String, List<MapResponse>> mapResponses) {
		this.mapResponses = mapResponses;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public Map<String, Double> getTotalRunList() {
		if(totalRunList != null) {
			return totalRunList;
		}
		Map<String, Double> result = new HashMap<String, Double>();
		if(!mapResponses.isEmpty()) {
			for (Map.Entry<String, List<MapResponse>> entry : mapResponses.entrySet()) {
				String idRoute = entry.getKey();
				Double totalRun = 0.0;
				for (MapResponse r : entry.getValue()) {
					totalRun = totalRun + r.getDistance();
				}
				result.put(idRoute, totalRun);
			}			
		}
		this.totalRunList = result;
		return result;
	}
	
	/**
	 * @return отдаёт сумарное расстояние по текущему решения получая напрямую из GH
	 */
	public Double getTotalRunSolutionGH() {
		Map<String, Double> result = new HashMap<String, Double>();
		Double totalRunFinal = 0.0;
		if(!mapResponses.isEmpty()) {
			for (Map.Entry<String, List<MapResponse>> entry : mapResponses.entrySet()) {
				String idRoute = entry.getKey();
				Double totalRun = 0.0;
				for (MapResponse r : entry.getValue()) {
					totalRun = totalRun + r.getDistance();
					totalRunFinal = totalRunFinal + r.getDistance();
				}
				result.put(idRoute, totalRun);
			}			
		}
		this.totalRunList = result;	
		this.totalRunKM = totalRunFinal;
		return totalRunFinal;		
	}
	
	public Double getTotalRunKM() {
		return totalRunKM;
	}

	public void setTotalRunKM(Double totalRunKM) {
		this.totalRunKM = totalRunKM;
	}

	@Override
	public String toString() {
		String textWay = "";
		for (VehicleWay w : whiteWay) {
			textWay = textWay + w.toText();
		}
		
		String emptyTrucksStr = "";
		for (Vehicle truck : emptyTrucks) {
			emptyTrucksStr = emptyTrucksStr + truck.toString() + "\n";
		}
		
		String emptyShopStr = "";
		for (Shop shop : emptyShop) {
			emptyShopStr = emptyShopStr + shop.toString();
		}
		
		return "Solution [Распределенные маршруты: \n" + textWay + "Незагруженные машины :\n" + emptyTrucksStr+"\n"
				+"Нераспредилённые магазины :" + emptyShopStr + "Суммарный пробег : " + getTotalRunKM() + "]";
	}
	
	

}
