package by.base.main.util.GraphHopper;

import static com.graphhopper.json.Statement.If;
import static com.graphhopper.json.Statement.Op.LIMIT;
import static com.graphhopper.json.Statement.Op.MULTIPLY;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.graphhopper.GHRequest;
import com.graphhopper.GraphHopper;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.LMProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.routing.ev.EncodedValue;
import com.graphhopper.routing.ev.EncodedValueFactory;
import com.graphhopper.util.CustomModel;
import com.graphhopper.util.JsonFeature;
import com.graphhopper.util.JsonFeatureCollection;
import com.graphhopper.util.PMap;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.shapes.GHPoint;

import by.base.main.controller.MainController;
import by.base.main.model.Shop;
import by.base.main.service.ShopService;

@Service
public class RoutingMachine {
	
	private GraphHopper graphHopper; 
	public static String roadClassPRIMARY = "0.9";
	public static String roadClassSECONDARY = "0.8";
	public static String roadClassTERTIARY = "0.8";
	public static String roadClassRESIDENTIAL = "0.6";
	public static String roadClassUNCLASSIFIED = "0.2";
	public static String roadEnvironmentFERRY = "0.01";
	public static String maxAxleLoad = "6";
	public static String maxAxleLoadCoeff = "0.2";
	public static String surfaceMISSING = "0.3";
	public static String surfaceGRAVEL = "0.3";
	public static String surfaceCOMPACTED = "0.3";
	public static String surfaceASPHALT = "0.9";
	public static String distanceInfluence = "10";
	public static String roadClassMOTORWAYTOLL = "1";
	
	public static String classLog;
	
	public Map<String, CustomJsonFeature> polygons = new HashMap<String, CustomJsonFeature>(); // кеш полигонов <Название, сам полигон>
	
	@Autowired
	ShopService shopService;
	
	@Autowired 
	MainController mainController;

	public RoutingMachine() {
		super();
//		ini();
	}
	
	/**
	 * Вводный метод запуска машины.
	 * <br>Проверяет создана ли карта. Если создана, то читает её
	 * <br>Если карты нет, то создаёт её.
	 * <br>При изминении параметров нужно пересоздавать карту!
	 * @throws IOException 
	 */
	public void ini (HttpServletRequest request){
		System.out.println("RoutingMachine init");
		String appPath = request.getServletContext().getRealPath("");
		String path;
		String pathTemp;
		if(new File("D:/map/belarus-latest.osm.pbf").exists()) {
			path = "D:/map/belarus-latest.osm.pbf";
			pathTemp = "D:/map/temp";
		}else {
			path = appPath + "map/belarus-latest.osm.pbf";
			pathTemp = appPath + "map/temp/";
		}		
		System.out.println("Path GH map= " + path);
		System.out.println("Path GH temp= " + pathTemp);
		System.out.println("===================="+Runtime.getRuntime().maxMemory()+"====================");
		GraphHopper graphHopper = new GraphHopper();
		graphHopper.setOSMFile(path);
		graphHopper.setStoreOnFlush(true);
		graphHopper.setGraphHopperLocation(pathTemp);
		CustomModel serverSideCustomModel = new CustomModel(); //test
		graphHopper.setProfiles(new Profile("car_custom").setCustomModel(serverSideCustomModel).setVehicle("car"));
		graphHopper.getLMPreparationHandler().setLMProfiles(new LMProfile("car_custom"));
//		graphHopper.getCHPreparationHandler().setCHProfiles(new CHProfile("car_custom"));
		EncodedValueFactory encodedValueFactory = graphHopper.getEncodedValueFactory();
		EncodedValue value = encodedValueFactory.create("surface", new PMap());
		EncodedValue value2 = encodedValueFactory.create("smoothness", new PMap());
		EncodedValue value3 = encodedValueFactory.create("max_axle_load", new PMap());
		EncodedValue value4 = encodedValueFactory.create("toll", new PMap());
		EncodedValue value5 = encodedValueFactory.create("max_width", new PMap());
		EncodedValue value6 = encodedValueFactory.create("max_weight", new PMap());
		EncodedValue value7 = encodedValueFactory.create("max_height", new PMap());
		EncodedValue value8 = encodedValueFactory.create("hazmat_tunnel", new PMap());
		EncodedValue value9 = encodedValueFactory.create("hazmat", new PMap());
		EncodedValue value10 = encodedValueFactory.create("hazmat_water", new PMap());
		EncodedValue value11 = encodedValueFactory.create("max_length", new PMap());
		EncodedValue value12 = encodedValueFactory.create("hgv", new PMap());
		graphHopper.setEncodedValuesString(value.toString());
		graphHopper.setEncodedValuesString(value.toString()+","+value2.toString()+","+value3.toString()+","+value4+","+value5+","+value6+","+value7+","+value8+","+value9+","+value10+","+value11+","+value12);
		GraphHopper hopper = graphHopper.importOrLoad();
//		hopper.getEncodingManager().getEncodedValues().forEach(e->System.out.println(e));
//		System.err.println(hopper.getEncodedValuesString());
		this.graphHopper = hopper;
		System.out.println("Загрузка полигонов");
		deSerializablePolygons();
		System.out.println("Загружено " + polygons.size() + " полигонов");
		System.out.println("====================END====================");
	}

	public GraphHopper getGraphHopper() {
		return graphHopper;
	}

	public void setGraphHopper(GraphHopper graphHopper) {
		this.graphHopper = graphHopper;
	}
	
	/**
	 * Сохранение нового полигона в кеш и файл
	 */
	public void savePolygon(CustomJsonFeature jsonFeature) {
		polygons.put(jsonFeature.getId(), jsonFeature);
		serializablePolygons();
	}	
	
	/**
	 * Удаление полигона из кеша и файла
	 * @param jsonFeature
	 */
	public JsonFeature deletePolygon(JsonFeature jsonFeature) {
		JsonFeature result = polygons.remove(jsonFeature.getId());
		serializablePolygons();
		return result;
	}
	
	
	/**
	 * Удаление полигона из кеша и файла
	 * @param id
	 */
	public JsonFeature deletePolygon(String id) {
		JsonFeature result = polygons.remove(id);
		serializablePolygons();
		return result;
	}
	
	public void serializablePolygons() {
		System.err.println(mainController.path + "resources/others/");
		try {
			FileOutputStream fos = new FileOutputStream(mainController.path + "resources/others/polygons.ser");
                  ObjectOutputStream oos = new ObjectOutputStream(fos);
                  oos.writeObject(this.polygons);
                  oos.close();
                  fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deSerializablePolygons() {
		try {
			FileInputStream fis = new FileInputStream(mainController.path + "resources/others/polygons.ser");
		         ObjectInputStream ois = new ObjectInputStream(fis);
		         this.polygons = (HashMap) ois.readObject();
		         ois.close();
		         fis.close();
			}catch (FileNotFoundException e) {
				System.err.println("polygons.ser не найден!");
			}catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	
	/**
	 * Метод преобразуете Map с листами координат в мапу с запросами, 
	 * <br>где ключ является подярковым номером маршрута согласно excel
	 * @param pointsMap
	 * @return
	 * @throws ParseException
	 */
	public Map<Long,List<GHRequest>> generateGHRequestHasMap(Map<Long,List<Double[]>> pointsMap) throws ParseException {
		CustomModel model = parseJSONFromClientCustomModel(null);
		Map<Long,List<GHRequest>> requestMap = new HashMap<Long, List<GHRequest>>();
		pointsMap.forEach((k,v) ->{
			List<Double[]> coordinates = v;
			List<GHRequest> ghRequests = new ArrayList<GHRequest>();
			for (int j = 0; j < coordinates.size()-1; j++) {
				GHRequest request = GHRequestBilder(coordinates.get(j)[0], coordinates.get(j)[1], model, coordinates.get(j+1)[0], coordinates.get(j+1)[1]);
				ghRequests.add(request);
			}
			requestMap.put(k, ghRequests);
		});
		return requestMap;		
	}
	
	/**
	 * Метод принимает запрос json и отдаёт массив GHRequest от точке к точке, разделяя общий маршрут на отдельные участки. 
	 * <b>Новая версия. от 11.07.2024</b> 
	 * <br><b>Модель уже установлена в GHRequest</b> 
	 * <br><b>В зависимости от того, какая последняя точка, меняется модель: если это склад 1700, 1100, 100, 1200 - то машина едет по смягченным ограничениям</b> 
	 * <br> Колличество точек не ограничено.
	 * @param JSONstr
	 * @return
	 * @throws ParseException
	 */
	public List<GHRequest> parseJSONFromClientRequestSplitV2(String JSONstr) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(JSONstr);
		List<Double[]> coordinates = new ArrayList<Double[]>();
		Double[] startPoint = getPointsDouble(jsonMainObject.get("startPoint").toString());
		coordinates.add(startPoint);
		JSONArray lang = (JSONArray) jsonMainObject.get("points");

		for (Object str : lang) {
			coordinates.add(getPointsDouble(str.toString()));
		}
		List<GHRequest> ghRequests = new ArrayList<GHRequest>();
		CustomModel model = parseJSONFromClientCustomModel(JSONstr);		
		CustomModel specoalModel = getSpecialModel();
//		System.out.println(model);
		List<Shop> shops = getShopAsPointNumbers(JSONstr);	
		for (int i = 0; i < coordinates.size()-1; i++) {
			Shop shopNext = null;
			GHRequest request;
			if(i != coordinates.size()-1) shopNext = shops.get(i+1);
			if(shopNext != null) {
				if(shopNext.getNumshop() == 1700 || shopNext.getNumshop() == 1100 || shopNext.getNumshop() == 1200 || shopNext.getNumshop() == 100 || shopNext.getNumshop() == 1250) {
					request = GHRequestBilder(coordinates.get(i)[0], coordinates.get(i)[1], specoalModel, coordinates.get(i+1)[0], coordinates.get(i+1)[1]);
				}else {
					request = GHRequestBilder(coordinates.get(i)[0], coordinates.get(i)[1], model, coordinates.get(i+1)[0], coordinates.get(i+1)[1]);
				}
			}else {
				request = GHRequestBilder(coordinates.get(i)[0], coordinates.get(i)[1], model, coordinates.get(i+1)[0], coordinates.get(i+1)[1]);
			}			
			ghRequests.add(request);
		}		
		return ghRequests;		
	}
	
	/**
	 * Метод принимает запрос json и отдаёт массив GHRequest от точке к точке, разделяя общий маршрут на отдельные участки. 
	 * <br>Постотянные ограничения в любое направление движения
	 * <b>Модель уже установлена в GHRequest</b> 
	 * <br> Колличество точек не ограничено.
	 * @param JSONstr
	 * @return
	 * @throws ParseException
	 */
	public List<GHRequest> parseJSONFromClientRequestSplit(String JSONstr) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(JSONstr);
		List<Double[]> coordinates = new ArrayList<Double[]>();
		Double[] startPoint = getPointsDouble(jsonMainObject.get("startPoint").toString());
		coordinates.add(startPoint);
		JSONArray lang = (JSONArray) jsonMainObject.get("points");

		for (Object str : lang) {
			coordinates.add(getPointsDouble(str.toString()));
		}
		List<GHRequest> ghRequests = new ArrayList<GHRequest>();
		CustomModel model = parseJSONFromClientCustomModel(JSONstr);		
//		System.out.println(model);			
		for (int i = 0; i < coordinates.size()-1; i++) {
			GHRequest request = GHRequestBilder(coordinates.get(i)[0], coordinates.get(i)[1], model, coordinates.get(i+1)[0], coordinates.get(i+1)[1]);
			ghRequests.add(request);
		}		
		return ghRequests;		
	}
	
	/**
	 * Метод принимает лист магазинов, и строит по ним лист запросов List<GHRequest>
	 * @param way
	 * @return
	 * @throws ParseException 
	 */
	public List<GHRequest> createrListGHRequest(List<Shop> way) throws ParseException{
		List<Double[]> coordinates = new ArrayList<Double[]>();
		Double[] startPoint = getPointsDouble(way.get(0).getNumshop()+"");
		coordinates.add(startPoint);
		for (int i = 1; i < way.size()-1; i++) {
			Shop shop = way.get(i);
			coordinates.add(getPointsDouble(shop.getNumshop()+""));
		}
		List<GHRequest> ghRequests = new ArrayList<GHRequest>();
		CustomModel model = parseJSONFromClientCustomModel(null);
		for (int i = 0; i < coordinates.size()-1; i++) {
			GHRequest request = GHRequestBilder(coordinates.get(i)[0], coordinates.get(i)[1], model, coordinates.get(i+1)[0], coordinates.get(i+1)[1]);
			ghRequests.add(request);
		}	
		//от последней точки до склада
		GHRequest lastPoint = GHRequestBilder(coordinates.get(coordinates.size()-1)[0], coordinates.get(coordinates.size()-1)[1], model, startPoint[0], startPoint[1]);
		ghRequests.add(lastPoint);
		return ghRequests;
		
	}
	
	/**
	 * Метод создания GHRequest.
	 * @param latFrom
	 * @param lonFrom
	 * @param model
	 * @param latTo
	 * @param lonTo
	 * @return
	 */
	public GHRequest GHRequestBilder (double latFrom, double lonFrom, CustomModel model, double latTo, double lonTo) {
		GHRequest request= new GHRequest().addPoint(new GHPoint(latFrom, lonFrom))
				.addPoint(new GHPoint(latTo, lonTo))
//				.setAlgorithm(Parameters.Algorithms.ALT_ROUTE)
//				.putHint(Parameters.Algorithms.AltRoute.MAX_WEIGHT, 10)
//				.putHint(Parameters.Algorithms.AltRoute.MAX_PATHS, 10)
//				.putHint(Parameters.Algorithms.AltRoute.MAX_SHARE, 10)
//				.setAlgorithm(Parameters.Algorithms.DIJKSTRA)
//				.putHint(Parameters.CH.DISABLE, true)
				.setProfile("car_custom")
				.setCustomModel(model);
		return request;
		
	}
	
	/**
	 * Метод принимает запрос json и отдаёт готовый GHRequest. Максимум 16 точек
	 * <br> Используется для ручного ввода точек
	 * @param JSONstr
	 * @return
	 * @throws ParseException
	 */
	public GHRequest parseJSONFromClientRequest(String JSONstr) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(JSONstr);
		List<Double[]> coordinates = new ArrayList<Double[]>();
		Double[] startPoint = getPointsDouble(jsonMainObject.get("startPoint").toString());
		coordinates.add(startPoint);
		JSONArray lang = (JSONArray) jsonMainObject.get("points");

		for (Object str : lang) {
			coordinates.add(getPointsDouble(str.toString()));
		}

		int key = coordinates.size();
		switch (key) {
		case 2:
			return new GHRequest().addPoint(new GHPoint(coordinates.get(key - 2)[0], coordinates.get(key - 2)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 1)[0], coordinates.get(key - 1)[1]))
					.setProfile("car_custom");
		case 3:
			return new GHRequest().addPoint(new GHPoint(coordinates.get(key - 3)[0], coordinates.get(key - 3)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 2)[0], coordinates.get(key - 2)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 1)[0], coordinates.get(key - 1)[1]))
					.setProfile("car_custom");
		case 4:
			return new GHRequest().addPoint(new GHPoint(coordinates.get(key - 4)[0], coordinates.get(key - 4)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 3)[0], coordinates.get(key - 3)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 2)[0], coordinates.get(key - 2)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 1)[0], coordinates.get(key - 1)[1]))
					.setProfile("car_custom");
		case 5:
			return new GHRequest().addPoint(new GHPoint(coordinates.get(key - 5)[0], coordinates.get(key - 5)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 4)[0], coordinates.get(key - 4)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 3)[0], coordinates.get(key - 3)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 2)[0], coordinates.get(key - 2)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 1)[0], coordinates.get(key - 1)[1]))
					.setProfile("car_custom");
		case 6:
			return new GHRequest().addPoint(new GHPoint(coordinates.get(key - 6)[0], coordinates.get(key - 6)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 5)[0], coordinates.get(key - 5)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 4)[0], coordinates.get(key - 4)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 3)[0], coordinates.get(key - 3)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 2)[0], coordinates.get(key - 2)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 1)[0], coordinates.get(key - 1)[1]))
					.setProfile("car_custom");
		case 7:
			return new GHRequest().addPoint(new GHPoint(coordinates.get(key - 7)[0], coordinates.get(key - 7)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 6)[0], coordinates.get(key - 6)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 5)[0], coordinates.get(key - 5)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 4)[0], coordinates.get(key - 4)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 3)[0], coordinates.get(key - 3)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 2)[0], coordinates.get(key - 2)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 1)[0], coordinates.get(key - 1)[1]))
					.setProfile("car_custom");
		case 8:
			return new GHRequest().addPoint(new GHPoint(coordinates.get(key - 8)[0], coordinates.get(key - 8)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 7)[0], coordinates.get(key - 7)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 6)[0], coordinates.get(key - 6)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 5)[0], coordinates.get(key - 5)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 4)[0], coordinates.get(key - 4)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 3)[0], coordinates.get(key - 3)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 2)[0], coordinates.get(key - 2)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 1)[0], coordinates.get(key - 1)[1]))
					.setProfile("car_custom");
		case 9:
			return new GHRequest().addPoint(new GHPoint(coordinates.get(key - 9)[0], coordinates.get(key - 9)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 8)[0], coordinates.get(key - 8)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 7)[0], coordinates.get(key - 7)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 6)[0], coordinates.get(key - 6)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 5)[0], coordinates.get(key - 5)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 4)[0], coordinates.get(key - 4)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 3)[0], coordinates.get(key - 3)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 2)[0], coordinates.get(key - 2)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 1)[0], coordinates.get(key - 1)[1]))
					.setProfile("car_custom");
		case 10:
			return new GHRequest().addPoint(new GHPoint(coordinates.get(key - 10)[0], coordinates.get(key - 10)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 9)[0], coordinates.get(key - 9)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 8)[0], coordinates.get(key - 8)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 7)[0], coordinates.get(key - 7)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 6)[0], coordinates.get(key - 6)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 5)[0], coordinates.get(key - 5)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 4)[0], coordinates.get(key - 4)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 3)[0], coordinates.get(key - 3)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 2)[0], coordinates.get(key - 2)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 1)[0], coordinates.get(key - 1)[1]))
					.setProfile("car_custom");
		case 11:
			return new GHRequest().addPoint(new GHPoint(coordinates.get(key - 11)[0], coordinates.get(key - 11)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 10)[0], coordinates.get(key - 10)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 9)[0], coordinates.get(key - 9)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 8)[0], coordinates.get(key - 8)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 7)[0], coordinates.get(key - 7)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 6)[0], coordinates.get(key - 6)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 5)[0], coordinates.get(key - 5)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 4)[0], coordinates.get(key - 4)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 3)[0], coordinates.get(key - 3)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 2)[0], coordinates.get(key - 2)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 1)[0], coordinates.get(key - 1)[1]))
					.setProfile("car_custom");
		case 12:
			return new GHRequest().addPoint(new GHPoint(coordinates.get(key - 12)[0], coordinates.get(key - 12)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 11)[0], coordinates.get(key - 11)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 10)[0], coordinates.get(key - 10)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 9)[0], coordinates.get(key - 9)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 8)[0], coordinates.get(key - 8)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 7)[0], coordinates.get(key - 7)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 6)[0], coordinates.get(key - 6)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 5)[0], coordinates.get(key - 5)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 4)[0], coordinates.get(key - 4)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 3)[0], coordinates.get(key - 3)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 2)[0], coordinates.get(key - 2)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 1)[0], coordinates.get(key - 1)[1]))
					.setProfile("car_custom");
		case 13:
			return new GHRequest().addPoint(new GHPoint(coordinates.get(key - 13)[0], coordinates.get(key - 13)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 12)[0], coordinates.get(key - 12)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 11)[0], coordinates.get(key - 11)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 10)[0], coordinates.get(key - 10)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 9)[0], coordinates.get(key - 9)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 8)[0], coordinates.get(key - 8)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 7)[0], coordinates.get(key - 7)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 6)[0], coordinates.get(key - 6)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 5)[0], coordinates.get(key - 5)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 4)[0], coordinates.get(key - 4)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 3)[0], coordinates.get(key - 3)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 2)[0], coordinates.get(key - 2)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 1)[0], coordinates.get(key - 1)[1]))
					.setProfile("car_custom");
		case 14:
			return new GHRequest().addPoint(new GHPoint(coordinates.get(key - 14)[0], coordinates.get(key - 14)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 13)[0], coordinates.get(key - 13)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 12)[0], coordinates.get(key - 12)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 11)[0], coordinates.get(key - 11)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 10)[0], coordinates.get(key - 10)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 9)[0], coordinates.get(key - 9)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 8)[0], coordinates.get(key - 8)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 7)[0], coordinates.get(key - 7)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 6)[0], coordinates.get(key - 6)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 5)[0], coordinates.get(key - 5)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 4)[0], coordinates.get(key - 4)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 3)[0], coordinates.get(key - 3)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 2)[0], coordinates.get(key - 2)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 1)[0], coordinates.get(key - 1)[1]))
					.setProfile("car_custom");
		case 15:
			return new GHRequest().addPoint(new GHPoint(coordinates.get(key - 15)[0], coordinates.get(key - 15)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 14)[0], coordinates.get(key - 14)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 13)[0], coordinates.get(key - 13)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 12)[0], coordinates.get(key - 12)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 11)[0], coordinates.get(key - 11)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 10)[0], coordinates.get(key - 10)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 9)[0], coordinates.get(key - 9)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 8)[0], coordinates.get(key - 8)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 7)[0], coordinates.get(key - 7)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 6)[0], coordinates.get(key - 6)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 5)[0], coordinates.get(key - 5)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 4)[0], coordinates.get(key - 4)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 3)[0], coordinates.get(key - 3)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 2)[0], coordinates.get(key - 2)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 1)[0], coordinates.get(key - 1)[1]))
					.setProfile("car_custom");
		case 16:
			return new GHRequest().addPoint(new GHPoint(coordinates.get(key - 16)[0], coordinates.get(key - 16)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 15)[0], coordinates.get(key - 15)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 14)[0], coordinates.get(key - 14)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 13)[0], coordinates.get(key - 13)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 12)[0], coordinates.get(key - 12)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 11)[0], coordinates.get(key - 11)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 10)[0], coordinates.get(key - 10)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 9)[0], coordinates.get(key - 9)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 8)[0], coordinates.get(key - 8)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 7)[0], coordinates.get(key - 7)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 6)[0], coordinates.get(key - 6)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 5)[0], coordinates.get(key - 5)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 4)[0], coordinates.get(key - 4)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 3)[0], coordinates.get(key - 3)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 2)[0], coordinates.get(key - 2)[1]))
					.addPoint(new GHPoint(coordinates.get(key - 1)[0], coordinates.get(key - 1)[1]))
					.setProfile("car_custom");

		default:

			return null;
		}

	}
	
	/**
	 * Основной метод создания CustomModel
	 * <br>Если параметры не приходят, то параметры берутся из дефолтных
	 * <br>Если параметры приходят - вставляются те, что пришли.
	 * <br>Если в метод приходит null, то параметры берутся дефолтными
	 * @param json
	 * @return
	 * @throws ParseException
	 */
	public CustomModel parseJSONFromClientCustomModel(String json) throws ParseException {
		
		//тестовая область
//		List<JsonFeature> features = new ArrayList<JsonFeature>();
//		Coordinate[] area1 = new Coordinate[]{	//Могилёв	        
//		        new Coordinate(30.223011, 53.939705),
//		        new Coordinate(30.452461, 53.947156),
//		        new Coordinate(30.427302, 53.805030),
//		        new Coordinate(30.195336, 53.836589),
//		        new Coordinate(30.223011, 53.939705),
//		};
//		Coordinate[] area2 = new Coordinate[]{	//Гомель	        
//		        new Coordinate(30.898161, 52.506210),
//		        new Coordinate(31.133551, 52.516118),
//		        new Coordinate(31.153585, 52.335329),
//		        new Coordinate(30.846400, 52.365415),
//		        new Coordinate(30.898161, 52.506210),
//		};
//		features.add(new JsonFeature("area1",
//                "Feature",
//                null,
//                new GeometryFactory().createPolygon(area1),
//                new HashMap<>()));
//		features.add(new JsonFeature("area2",
//                "Feature",
//                null,
//                new GeometryFactory().createPolygon(area2),
//                new HashMap<>()));
//		CustomJsonFeatureCollection customJsonFeatureCollection = new CustomJsonFeatureCollection();
//		customJsonFeatureCollection.setFeatures(features);
		
		List<JsonFeature> features = new ArrayList<JsonFeature>();
		polygons.forEach((k,v) -> features.add(v.toJsonFeature()));
		CustomJsonFeatureCollection customJsonFeatureCollection = new CustomJsonFeatureCollection();
		customJsonFeatureCollection.setFeatures(features);
		
		if(json != null) {
			JSONParser parser = new JSONParser();
			JSONObject jsonMainObject = (JSONObject) parser.parse(json);
			String parameterJSON = jsonMainObject.get("parameters").toString();
			JSONObject jsonParameterObject = (JSONObject) parser.parse(parameterJSON);

			CustomModel model = new CustomModel();
			model.addToPriority(If("road_class == PRIMARY", MULTIPLY,
					jsonParameterObject.get("roadClassPRIMARY") != null
							? jsonParameterObject.get("roadClassPRIMARY").toString()
							: roadClassPRIMARY));
			model.addToPriority(If("road_class == TERTIARY", MULTIPLY,
					jsonParameterObject.get("roadClassTERTIARY") != null
							? jsonParameterObject.get("roadClassTERTIARY").toString()
							: roadClassTERTIARY));
			model.addToPriority(If("road_environment == FERRY", MULTIPLY,
					jsonParameterObject.get("roadEnvironmentFERRY") != null
							? jsonParameterObject.get("roadEnvironmentFERRY").toString()
							: roadEnvironmentFERRY));
			model.addToPriority(If("road_class == RESIDENTIAL", MULTIPLY,
					jsonParameterObject.get("roadClassRESIDENTIAL") != null
							? jsonParameterObject.get("roadClassRESIDENTIAL").toString()
							: roadClassRESIDENTIAL));
			model.addToPriority(If("road_class == SECONDARY", MULTIPLY,
					jsonParameterObject.get("roadClassSECONDARY") != null
							? jsonParameterObject.get("roadClassSECONDARY").toString()
							: roadClassSECONDARY));
			model.addToPriority(If("surface == MISSING", MULTIPLY,
					jsonParameterObject.get("surfaceMISSING") != null ? jsonParameterObject.get("surfaceMISSING").toString()
							: surfaceMISSING));
			model.addToPriority(If("surface == GRAVEL", MULTIPLY, jsonParameterObject.get("surfaceGRAVEL") != null ? jsonParameterObject.get("surfaceGRAVEL").toString() : surfaceGRAVEL));
			
			model.addToPriority(If("surface == COMPACTED", MULTIPLY,
					jsonParameterObject.get("surfaceCOMPACTED") != null
							? jsonParameterObject.get("surfaceCOMPACTED").toString()
							: surfaceCOMPACTED));
			model.addToPriority(If("surface == ASPHALT", MULTIPLY,
					jsonParameterObject.get("surfaceASPHALT") != null ? jsonParameterObject.get("surfaceASPHALT").toString()
							: surfaceASPHALT));		
			if (jsonParameterObject.get("maxAxleLoad") != null) {
				model.addToPriority(If("max_axle_load < " + jsonParameterObject.get("maxAxleLoad").toString(), MULTIPLY,
						jsonParameterObject.get("maxAxleLoadCoeff").toString()));
			} else {
				model.addToPriority(If("max_axle_load < " + maxAxleLoad, MULTIPLY, maxAxleLoadCoeff));
			}
			if (jsonParameterObject.get("distanceInfluence") != null) {
				model.setDistanceInfluence(Double.parseDouble(jsonParameterObject.get("distanceInfluence").toString()));
			}else {
				model.setDistanceInfluence(Double.parseDouble(distanceInfluence));
			}
					
			model.addToPriority(If("road_class == UNCLASSIFIED", MULTIPLY, jsonParameterObject.get("roadClassUNCLASSIFIED") != null ? jsonParameterObject.get("roadClassUNCLASSIFIED").toString() : roadClassUNCLASSIFIED));		
			model.addToPriority(If("road_class == MOTORWAY && toll == ALL", MULTIPLY, jsonParameterObject.get("roadClassMOTORWAYTOLL") != null ? jsonParameterObject.get("roadClassMOTORWAYTOLL").toString() : roadClassMOTORWAYTOLL)); //тут платные дороги
			model.addToSpeed(If("true", LIMIT, "90"));
//			model.addToPriority(If("true", LIMIT, "100"));
			
			//для грузовиков:
//			model.addToPriority(If("hazmat == NO || hazmat_water == NO", MULTIPLY, "0.01"));		
//			model.addToPriority(If("hazmat_tunnel == D || hazmat_tunnel == E", MULTIPLY, "0.01"));
//			model.addToPriority(If("max_width < 3 || max_weight < 3.5 || max_height < 4", MULTIPLY, "0.01"));
//			model.addToPriority(If("max_width < 3 || max_height < 3.9", MULTIPLY, "0.01"));
			
			//загружаем полигоны			
			model.setAreas(customJsonFeatureCollection);
			
			for (Map.Entry<String, CustomJsonFeature> jsonFeatureEntry: polygons.entrySet()) {
				
				switch (jsonFeatureEntry.getValue().getAction()) {
				case "trafficBan": //запрет
					model.addToPriority(If("in_"+jsonFeatureEntry.getValue().getId(), MULTIPLY, "0.01"));
					break;
					
				case "trafficSpecialBan": //запрет, который могут игноррировать, если машина едет пустая
					model.addToPriority(If("in_"+jsonFeatureEntry.getValue().getId(), MULTIPLY, "0.01"));
					break;
					
				case "trafficRestrictions": //ограничение
					model.addToPriority(If("in_"+jsonFeatureEntry.getValue().getId(), MULTIPLY, "0.5"));
					break;

				default:
					break;
				}
			}
			return model;
		}else {
			CustomModel model = new CustomModel();
			model.addToPriority(If("road_class == PRIMARY", MULTIPLY, roadClassPRIMARY));
			model.addToPriority(If("road_class == TERTIARY", MULTIPLY, roadClassTERTIARY));
			model.addToPriority(If("road_environment == FERRY", MULTIPLY, roadEnvironmentFERRY));
			model.addToPriority(If("road_class == RESIDENTIAL", MULTIPLY, roadClassRESIDENTIAL));
			model.addToPriority(If("road_class == SECONDARY", MULTIPLY, roadClassSECONDARY));
			model.addToPriority(If("surface == MISSING", MULTIPLY, surfaceMISSING));
			model.addToPriority(If("surface == GRAVEL", MULTIPLY, surfaceGRAVEL));			
			model.addToPriority(If("surface == COMPACTED", MULTIPLY, surfaceCOMPACTED));
			model.addToPriority(If("surface == ASPHALT", MULTIPLY, surfaceASPHALT));		
			model.addToPriority(If("max_axle_load < " + maxAxleLoad, MULTIPLY, maxAxleLoadCoeff));
			model.setDistanceInfluence(Double.parseDouble(distanceInfluence));
			model.addToPriority(If("road_class == UNCLASSIFIED", MULTIPLY, roadClassUNCLASSIFIED));		
			model.addToPriority(If("road_class == MOTORWAY && toll == ALL", MULTIPLY, roadClassMOTORWAYTOLL));
			
			model.addToSpeed(If("true", LIMIT, "90"));
//			model.addToPriority(If("true", LIMIT, "100"));
			
			//для грузовиков:
//			model.addToPriority(If("hazmat == NO || hazmat_water == NO", MULTIPLY, "0.01"));		
//			model.addToPriority(If("hazmat_tunnel == D || hazmat_tunnel == E", MULTIPLY, "0.01"));
//			model.addToPriority(If("max_width < 3 || max_weight < 3.5 || max_height < 4", MULTIPLY, "0.01"));
//			model.addToPriority(If("max_width < 3 || max_height < 3.9", MULTIPLY, "0.01"));
			
			//загружаем полигоны			
			model.setAreas(customJsonFeatureCollection);
			
			//задаём приоритеты для области
			for (Map.Entry<String, CustomJsonFeature> jsonFeatureEntry: polygons.entrySet()) {
				
				switch (jsonFeatureEntry.getValue().getAction()) {
				case "trafficBan": //запрет
					model.addToPriority(If("in_"+jsonFeatureEntry.getValue().getId(), MULTIPLY, "0.01"));
					break;
					
				case "trafficSpecialBan": //запрет, который могут игноррировать, если машина едет пустая
					model.addToPriority(If("in_"+jsonFeatureEntry.getValue().getId(), MULTIPLY, "0.01"));
					break;
					
				case "trafficRestrictions": //ограничение
					model.addToPriority(If("in_"+jsonFeatureEntry.getValue().getId(), MULTIPLY, "0.5"));
					break;

				default:
					break;
				}
			}
			return model;
		}
		//
	}
	
	/**
	 * Метод отдаёт специальную модель без ограничений
	 * <br>Но с ограничениями транзита
	 * @param json
	 * @return
	 * @throws ParseException
	 */
	public CustomModel getSpecialModel() throws ParseException {
		List<JsonFeature> features = new ArrayList<JsonFeature>();
		polygons.forEach((k,v) -> features.add(v.toJsonFeature()));
		CustomJsonFeatureCollection customJsonFeatureCollection = new CustomJsonFeatureCollection();
		customJsonFeatureCollection.setFeatures(features);
		CustomModel model = new CustomModel();
		model.addToPriority(If("road_class == PRIMARY", MULTIPLY, roadClassPRIMARY));
		model.addToPriority(If("road_class == TERTIARY", MULTIPLY, roadClassTERTIARY));
		model.addToPriority(If("road_environment == FERRY", MULTIPLY, roadEnvironmentFERRY));
		model.addToPriority(If("road_class == RESIDENTIAL", MULTIPLY, roadClassRESIDENTIAL));
		model.addToPriority(If("road_class == SECONDARY", MULTIPLY, roadClassSECONDARY));
		model.addToPriority(If("surface == MISSING", MULTIPLY, surfaceMISSING));
		model.addToPriority(If("surface == GRAVEL", MULTIPLY, surfaceGRAVEL));			
		model.addToPriority(If("surface == COMPACTED", MULTIPLY, surfaceCOMPACTED));
		model.addToPriority(If("surface == ASPHALT", MULTIPLY, surfaceASPHALT));		
		model.addToPriority(If("max_axle_load < " + "1", MULTIPLY, maxAxleLoadCoeff));
		model.setDistanceInfluence(Double.parseDouble(distanceInfluence));
		model.addToPriority(If("road_class == UNCLASSIFIED", MULTIPLY, roadClassUNCLASSIFIED));		
		model.addToPriority(If("road_class == MOTORWAY && toll == ALL", MULTIPLY, roadClassMOTORWAYTOLL));
		
		model.addToSpeed(If("true", LIMIT, "90"));
//		model.addToPriority(If("true", LIMIT, "100"));
		
		//для грузовиков:
//		model.addToPriority(If("hazmat == NO || hazmat_water == NO", MULTIPLY, "0.01"));		
//		model.addToPriority(If("hazmat_tunnel == D || hazmat_tunnel == E", MULTIPLY, "0.01"));
//		model.addToPriority(If("max_width < 3 || max_weight < 3.5 || max_height < 4", MULTIPLY, "0.01"));
//		model.addToPriority(If("max_width < 3 || max_height < 3.9", MULTIPLY, "0.01"));
		
		//загружаем полигоны			
		model.setAreas(customJsonFeatureCollection);
		
		//задаём приоритеты для области
		for (Map.Entry<String, CustomJsonFeature> jsonFeatureEntry: polygons.entrySet()) {
			
			switch (jsonFeatureEntry.getValue().getAction()) {
			case "trafficBan": //запрет
				model.addToPriority(If("in_"+jsonFeatureEntry.getValue().getId(), MULTIPLY, "0.01"));
				break;
				
			case "trafficSpecialBan": //запрет, который могут игноррировать, если машина едет пустая
//				model.addToPriority(If("in_"+jsonFeatureEntry.getValue().getId(), MULTIPLY, "0.01"));
				System.err.println("Игноррируем этот полигон");
				break;
				
			case "trafficRestrictions": //ограничение
				model.addToPriority(If("in_"+jsonFeatureEntry.getValue().getId(), MULTIPLY, "0.5"));				
				break;

			default:
				break;
			}
		}
		return model;
	}
	
	/**
	 * возвращает массив с отдельными точками с типом Double
	 * <br> Внутренний метод
	 * @param points
	 * @return
	 */
	private Double[] getPointsDouble(String points) {
		if(points.contains(",")) {
			String[] mass = points.split(",");
			Double[] result = new Double[mass.length];
			for (int i = 0; i < mass.length; i++) {
				result[i] = Double.parseDouble(mass[i]);
			}
			return result;
		}else {
			Shop shop = shopService.getShopByNum(Integer.parseInt(points));
			
			String[] mass = new String[] {shop.getLat(), shop.getLng()};
			Double[] result = new Double[mass.length];
			for (int i = 0; i < mass.length; i++) {
				result[i] = Double.parseDouble(mass[i]);
			}
			return result;
		}
		
	}
	
	/**
	 * Возвращает лист магазинов преобразованный из JSON формата 
	 * <br> Метод используется для единичных запросов
	 * @param JSONstr
	 * @return
	 * @throws ParseException
	 */
	public List<Shop[]> getShopAsPoint(String JSONstr) throws ParseException {
		
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(JSONstr);
		List <Shop> shops = new ArrayList<Shop>();
		Shop mainStartShop = shopService.getShopByNum(Integer.parseInt(jsonMainObject.get("startPoint").toString()));
		shops.add(mainStartShop);
		JSONArray lang = (JSONArray) jsonMainObject.get("points");
		for (Object str : lang) {
			shops.add(shopService.getShopByNum(Integer.parseInt(str.toString())));
		}
		List<Shop[]> result = new ArrayList<Shop[]>();
		for (int i = 0; i < shops.size()-1; i++) {
			result.add(new Shop[] {shops.get(i), shops.get(i+1)});
		}
		return result;
	}
	
	/**
	 * Возвращает лист с номерами магазинов преобразованный из JSON формата 
	 * @param JSONstr
	 * @return
	 * @throws ParseException
	 */
	public List<Shop> getShopAsPointNumbers(String JSONstr) throws ParseException {
		
		JSONParser parser = new JSONParser();
		JSONObject jsonMainObject = (JSONObject) parser.parse(JSONstr);
		List <Shop> shops = new ArrayList<Shop>();
		Shop mainStartShop = shopService.getShopByNum(Integer.parseInt(jsonMainObject.get("startPoint").toString()));
		shops.add(mainStartShop);
		JSONArray lang = (JSONArray) jsonMainObject.get("points");
		for (Object str : lang) {
			shops.add(shopService.getShopByNum(Integer.parseInt(str.toString())));
		}
		return shops;
	}
	

	/**
	 * Возвращает лист магазинов, от точки к точке, для отогбражения на карте, пример: 1700->582->869->775->1700
	 * <br>1700->582
	 * <br>582->869
	 * <br>869->775
	 * <br>775->1700
	 * @param way
	 * @return
	 * @throws ParseException
	 */
	public List<Shop[]> getShopAsWay(List<Shop> way) throws ParseException {
		List<Shop[]> result = new ArrayList<Shop[]>();
		for (int i = 0; i < way.size()-1; i++) {
			result.add(new Shop[] {way.get(i), way.get(i+1)});
		}
		return result;		
	}

	/**
	 * ОТдаёт кеш с полигонами
	 * @return
	 */
	public Map<String, CustomJsonFeature> getPolygons() {
		return polygons;
	}
	
	

}
