/**
 * 
 */
package by.base.main.util.hcolossus.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.util.CustomModel;

import by.base.main.controller.MainController;
import by.base.main.controller.ajax.MainRestController;
import by.base.main.model.Shop;
import by.base.main.service.ShopService;
import by.base.main.util.GraphHopper.RoutingMachine;

/**
 * Класс отвечающий за формирование и управление матрицы расстояний
 */
@Service
public class MatrixMachine {
	
	public static Map<String, Double> matrix = new HashMap<String, Double>(); // матрица расстояний
	public static Map<String, Long> matrixTime = new HashMap<String, Long>(); // матрица времени
	
	@Autowired
	private RoutingMachine routingMachine;
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private MainController mainController;
	
	/**
	 * Метод заполняет матрицу по входному листу магазинов и складу
	 * @param shopList
	 * @param stock
	 * @return
	 */
	public Map<String, Double> createMatrixHasList(List<Integer> shopList, Integer stock) {
		Map<Integer, Shop> allShop =  shopService.getShopMap();
		List<Integer> shopListForDIstance = new ArrayList<Integer>(shopList);
		shopListForDIstance.add(stock);
		for (Integer integer : shopListForDIstance) {
			for (int i = 0; i < shopListForDIstance.size(); i++) {				
				if(integer == shopListForDIstance.get(i)) {
					continue;
				}
				Integer integerTo = shopListForDIstance.get(i);
				double sum = 0;
				Long time = 0L;
				
//				System.out.println(integer + " --> " + integerTo);
				Shop from = allShop.get(integer);
				Shop to = allShop.get(integerTo);
				if(from == null) {
					System.err.println("MatrixMachine.createMatrixHasList: Магазин " + integer + " не найден в базе данных!");
					return null;
				}
				if(to == null) {
					System.err.println("MatrixMachine.createMatrixHasList: Магазин " + integerTo + " не найден в базе данных!");
					return null;
				}
				
				if(matrix.containsKey(from.getNumshop()+"-"+to.getNumshop())) {
					sum = matrix.get(from.getNumshop()+"-"+to.getNumshop());
				}else {
					double fromLat = Double.parseDouble(from.getLat());
			        double fromLng = Double.parseDouble(from.getLng());
			        
			        double toLat = Double.parseDouble(to.getLat());
			        double toLng = Double.parseDouble(to.getLng());
			        
			        CustomModel model = null;
					try {
						model = routingMachine.parseJSONFromClientCustomModel(null);
					} catch (ParseException e) {
						e.printStackTrace();
					}
			        System.err.println(model);
			        GHRequest req = routingMachine.GHRequestBilder(fromLat, fromLng, model, toLat, toLng);
			        GraphHopper hopper = routingMachine.getGraphHopper();
			        GHResponse rsp = hopper.route(req);
			        ResponsePath path = rsp.getBest();
			        sum = path.getDistance();
			        time = path.getTime();
			        matrix.put(from.getNumshop()+"-"+to.getNumshop(), sum);
			        matrixTime.put(from.getNumshop()+"-"+to.getNumshop(), time);
				}
		        System.out.println("MatrixMachine: "+integer + " --> " + integerTo + " --> " + sum);
			}			
		}
		return matrix;		
	}
	
	public Map<String, Double> createMatrixHasListTEXT(List<Integer> shopList, Integer stock) {
		Map<String, Double> mapresult = new HashMap<String, Double>();
		Map<Integer, Shop> allShop =  shopService.getShopMap();
		List<Integer> shopListForDIstance = new ArrayList<Integer>(shopList);
		shopListForDIstance.add(stock);
		for (Integer integer : shopListForDIstance) {
			for (int i = 0; i < shopListForDIstance.size(); i++) {				
				if(integer == shopListForDIstance.get(i)) {
					continue;
				}
				Integer integerTo = shopListForDIstance.get(i);
				double sum = 0;
				Long time = 0L;
				
//				System.out.println(integer + " --> " + integerTo);
				Shop from = allShop.get(integer);
				Shop to = allShop.get(integerTo);
				if(from == null) {
					System.err.println("MatrixMachine.createMatrixHasList: Магазин " + integer + " не найден в базе данных!");
					return null;
				}
				if(to == null) {
					System.err.println("MatrixMachine.createMatrixHasList: Магазин " + integerTo + " не найден в базе данных!");
					return null;
				}
				
				if(matrix.containsKey(from.getNumshop()+"-"+to.getNumshop())) {
					mapresult.put(from.getNumshop()+"-"+to.getNumshop(), matrix.get(from.getNumshop()+"-"+to.getNumshop()));
				}else {
					double fromLat = Double.parseDouble(from.getLat());
			        double fromLng = Double.parseDouble(from.getLng());
			        
			        double toLat = Double.parseDouble(to.getLat());
			        double toLng = Double.parseDouble(to.getLng());
			        
			        CustomModel model = null;
					try {
						model = routingMachine.parseJSONFromClientCustomModel(null);
					} catch (ParseException e) {
						e.printStackTrace();
					}
			        System.err.println(model);
			        GHRequest req = routingMachine.GHRequestBilder(fromLat, fromLng, model, toLat, toLng);
			        GraphHopper hopper = routingMachine.getGraphHopper();
			        GHResponse rsp = hopper.route(req);
			        ResponsePath path = rsp.getBest();
			        sum = path.getDistance();
			        time = path.getTime();
			        mapresult.put(from.getNumshop()+"-"+to.getNumshop(), sum);
				}
			}			
		}
		return mapresult;		
	}
	
	/**
	 * Метод общего просчёта расстояний и создания матрицы.
	 * <br>Вконце метода идёт запись в файл для последующей сериализации.
	 * <br>Если в параметрах указать число, то это будет равно колличеству итераций (для теста). Для работы подать null
	 * @return колличество элементов в матрице
	 */
	public int calculationDistance(Integer j) {
		List<Shop> allShop =  shopService.getShopList();
		int k = 0;
		for (Shop shop : allShop) {
			if(j != null && k == j) {
				break;
			}
			for (int i = 0; i < allShop.size(); i++) {
				Shop shopI = allShop.get(i);				
				if(shopI.getNumshop() == shop.getNumshop()) {
					continue;
				}
				String key = shop.getNumshop()+"-"+shopI.getNumshop();
				double sum = 0;
				Long time = 0L;
				if(!matrix.containsKey(key)) {					
					double fromLat = Double.parseDouble(shop.getLat());
			        double fromLng = Double.parseDouble(shop.getLng());
			        
			        double toLat = Double.parseDouble(shopI.getLat());
			        double toLng = Double.parseDouble(shopI.getLng());
			        
			        CustomModel model = null;
					try {
						model = routingMachine.parseJSONFromClientCustomModel(null);
					} catch (ParseException e) {
						e.printStackTrace();
					}
			        
			        GHRequest req = routingMachine.GHRequestBilder(fromLat, fromLng, model, toLat, toLng);
			        GraphHopper hopper = routingMachine.getGraphHopper();
			        GHResponse rsp = hopper.route(req);
			        ResponsePath path = rsp.getBest();
			        sum = path.getDistance();
			        time = path.getTime();
			        matrix.put(key, sum);
			        matrixTime.put(key, time);
			        if(i%100 == 0) {
						System.out.println("MatrixMachine: "+shop.getNumshop() + " --> " + shopI.getNumshop() + " --> " + sum);
					}
				}else {
					if(i%100 == 0) {
						System.out.println("MatrixMachine: "+shop.getNumshop() + " --> " + shopI.getNumshop() + " --> " + matrix.get(key));
					}
				}				
			}
			k++;
		}
		
		System.err.println("Сохранилась матрица в: "+ mainController.path + "resources/distance/matrix.ser");
		try {
			FileOutputStream fos = new FileOutputStream(mainController.path + "resources/distance/matrix.ser");
                  ObjectOutputStream oos = new ObjectOutputStream(fos);
                  oos.writeObject(this.matrix);
                  oos.close();
                  fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return matrix.size();		
	}
	
	public Map<String, Double> loadMatrixOfDistance() {
		try {
			FileInputStream fis = new FileInputStream(mainController.path + "resources/distance/matrixMain.ser");
		         ObjectInputStream ois = new ObjectInputStream(fis);
		         this.matrix = (HashMap) ois.readObject();
		         ois.close();
		         fis.close();
			}catch (FileNotFoundException e) {
				System.err.println("ОШибка в методе loadMatrixOfDistance");
			}catch (Exception e) {
				e.printStackTrace();
			}
		return matrix;		
	}
}
