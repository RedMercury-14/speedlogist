package by.base.main.util.hcolossus.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import by.base.main.util.hcolossus.pojo.Vehicle;

/**
 * Класс подготовки и обработки машин
 */
@Service
public class VehicleMachine {

//	private Comparator<Vehicle> vehicleComparator = (o1, o2) -> (o2.getPall() - o1.getPall()); // сортирует от большей
																								// потребности к меньшей
	private Comparator<Vehicle> vehicleComparator = (o1, o2) -> Double.compare(o2.getPall(), o1.getPall());


	/**
	 * первая версия подготовки машин и сортировки
	 * <br>принимает машины из стринги 
	 * @param jsonMainObject - главный объект json пришедший от фронта
	 * @return
	 * @throws ParseException 
	 */
	public List<Vehicle> prepareVehicleList(JSONObject jsonMainObject) throws ParseException {
		JSONParser parser = new JSONParser();
		List<Vehicle> result = new ArrayList<Vehicle>();
		// создаём машины
		// фуры
		JSONObject big = (JSONObject) parser.parse(jsonMainObject.get("big").toString());
		int nuOfBig = Integer.parseInt(big.get("count").toString());
		double capacityBig = Integer.parseInt(big.get("tonnage").toString()); //это паллеты
		double maxDurationBig = Double.parseDouble(big.get("maxMileage").toString()); // это тоннаж
		int id = 1;
		for (int i = 0; i < nuOfBig; i++) {
			Vehicle vehicle = new Vehicle(id, "фура", "фура", capacityBig);
			result.add(vehicle);
			id++;
		}
		// средние
		JSONObject middle = (JSONObject) parser.parse(jsonMainObject.get("middle").toString());
		int nuOfVehiclesMiddle = Integer.parseInt(middle.get("count").toString());
		double capacityMiddle = Integer.parseInt(middle.get("tonnage").toString());
		double maxDurationMiddle = Double.parseDouble(middle.get("maxMileage").toString());
		for (int i = 0; i < nuOfVehiclesMiddle; i++) {
			Vehicle vehicle = new Vehicle(id, "средняя", "средняя", capacityMiddle);
			result.add(vehicle);
			id++;
		}
		// маленькие
		JSONObject little = (JSONObject) parser.parse(jsonMainObject.get("little").toString());
		int nuOfVehiclesLittle = Integer.parseInt(little.get("count").toString());
		double capacityLittle = Integer.parseInt(little.get("tonnage").toString());
		double maxDurationLittle = Double.parseDouble(little.get("maxMileage").toString());
		for (int i = 0; i < nuOfVehiclesLittle; i++) {
			Vehicle vehicle = new Vehicle(id, "маленькая", "маленькая", capacityLittle);
			result.add(vehicle);
			id++;
		}
		return result;
	}
	
	/**
	 * вторая версия подготовки машин и сортировки
	 * <br>принимает машины из стринги пчём любое колличество
	 * @param jsonMainObject - главный объект json пришедший от фронта
	 * @return
	 * @throws ParseException
	 */
	public List<Vehicle> prepareVehicleListVersion2(JSONObject jsonMainObject) throws ParseException {
		JSONParser parser = new JSONParser();
		List<Vehicle> result = new ArrayList<Vehicle>();
		JSONArray carsArrayJSON = (JSONArray) jsonMainObject.get("cars");
		int id = 1;
		for (Object object : carsArrayJSON) {
			JSONObject jsonCarObject = (JSONObject) parser.parse(object.toString());
			String nameCar = jsonCarObject.get("carName").toString();
			int numOfCars = Integer.parseInt(jsonCarObject.get("carCount").toString());
			double pallOfCars = Integer.parseInt(jsonCarObject.get("maxPall").toString()); //это паллеты
			int weightOfCars = Integer.parseInt(jsonCarObject.get("maxTonnage").toString()); //это вес
			boolean isTwiceRound = Boolean.parseBoolean(jsonCarObject.get("secondRound").toString());
			for (int i = 0; i < numOfCars; i++) {
				Vehicle vehicle = new Vehicle(id, nameCar, nameCar, pallOfCars, weightOfCars);
				vehicle.setTwiceRound(isTwiceRound);
				result.add(vehicle);
				id++;
				if(vehicle.isTwiceRound()) {
					Vehicle cloneTruck = vehicle.cloneForSecondRound();
					cloneTruck.setClone(true);
					cloneTruck.setTwiceRound(false);
					cloneTruck.setId(cloneTruck.getId()*(-1));
					result.add(cloneTruck);	
				}
				
			}
		}
		return result;
	}

}
