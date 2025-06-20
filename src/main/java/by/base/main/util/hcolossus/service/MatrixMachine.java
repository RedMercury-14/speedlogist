/**
 * 
 */
package by.base.main.util.hcolossus.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

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
import by.base.main.model.DistanceMatrix;
import by.base.main.model.Shop;
import by.base.main.service.DistanceMatrixService;
import by.base.main.service.ShopService;
import by.base.main.util.GraphHopper.RoutingMachine;



/**
 * Класс отвечающий за формирование и управление матрицы расстояний
 */
@Service
public class MatrixMachine {
	
	public Map<String, Double> matrix = new HashMap<String, Double>(); // матрица расстояний
	public Map<String, Double> matrixTime = new HashMap<String, Double>(); // матрица времени
	
	@Autowired
	private RoutingMachine routingMachine;
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private MainController mainController;
	
	@Autowired
	private DistanceMatrixService distanceMatrixService;
	
	/**
	 * метод загружает сразу две матрицы, время и расстояния
	 * @param matrix
	 */
	public void loadMatrix(List<Map<String, Double>> matrix) {
		this.matrix = matrix.get(0);
		this.matrixTime = matrix.get(1);
	}
	
	/**
	 * Метод заполняет матрицу по входному листу магазинов и складу
	 * @param shopList
	 * @param stock
	 * @return
	 */
	public Map<String, Double> createMatrixHasList(List<Integer> shopList, Integer stock, Map<Integer, Shop> allShop) {
		List<Integer> shopListForDIstance = new ArrayList<Integer>(shopList);
		shopListForDIstance.add(stock);
		for (Integer integer : shopListForDIstance) {
			for (int i = 0; i < shopListForDIstance.size(); i++) {				
				if(integer == shopListForDIstance.get(i)) {
					continue;
				}
				Integer integerTo = shopListForDIstance.get(i);
				double sum = 0;
				double time = 0;
				
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
					System.out.println("Отсутствуют данные " + from.getNumshop()+"-"+to.getNumshop() + " !!");
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
			     // Создаем и сохраняем объект
                    DistanceMatrix dm = new DistanceMatrix();
                    dm.setIdDistanceMatrix(from.getNumshop()+"-"+to.getNumshop()); // Генерация ID
                    dm.setDistance(sum);
                    dm.setTime(time);                    
//                    distanceMatrixService.save(dm);			        
			        matrix.put(from.getNumshop()+"-"+to.getNumshop(), sum);
			        matrixTime.put(from.getNumshop()+"-"+to.getNumshop(), time);
				}
//		        System.out.println("MatrixMachine: "+integer + " --> " + integerTo + " --> " + sum);
			}			
		}
		return matrix;		
	}
	
	public Map<String, Double> createMatrixHasListTEXT(List<Integer> shopList, Integer stock, Map<Integer, Shop> allShop) {
		Map<String, Double> mapresult = new HashMap<String, Double>();
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
	public int calculationDistance(Integer j, List<Shop> allShop) {
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
				double time = 0L;
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
						System.out.println("MatrixMachine: "+shop.getNumshop() + " --> " + shopI.getNumshop() + " --> " + sum + " --> " + (k*100/allShop.size())+"%");
					}
				}else {
					if(i%100 == 0) {
						System.out.println("MatrixMachine: "+shop.getNumshop() + " --> " + shopI.getNumshop() + " --> " + matrix.get(key) + " --> " + (k*100/allShop.size())+"%");
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
	
	/*
	 *  многопоточное создание матрицы расстояний
	 */
	public int calculationDistanceNew(Integer j, int threadCount) {
	    // Проверка и создание директории
	    Path saveDir = Paths.get(mainController.path + "resources/distance/");
	    try {
	        Files.createDirectories(saveDir);
	    } catch (IOException e) {
	        System.err.println("Ошибка создания директории: " + e.getMessage());
	    }
	    loadMatrixOfDistanceAutosave();

	    // Информация о запуске
	    System.out.printf("=== ЗАПУСК РАСЧЕТА ===\nПотоков: %d\nМагазинов: %d\n", 
	        threadCount, shopService.getShopList().size());

	    // Инициализация структур
	    ConcurrentMap<String, Double> concurrentMatrix = new ConcurrentHashMap<>(matrix);
	    ConcurrentMap<String, Double> concurrentMatrixTime = new ConcurrentHashMap<>(matrixTime);
	    
	    // Счетчики прогресса
	    AtomicInteger totalProcessed = new AtomicInteger(0);
	    AtomicInteger shopCounter = new AtomicInteger(0);
	    List<Shop> allShop = shopService.getShopList();
	    int totalShops = j != null ? Math.min(j, allShop.size()) : allShop.size();
	    int totalPairs = totalShops * (allShop.size() - 1); // Общее количество пар
	    
	    // Shutdown hook
	    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
	        System.out.println("\n!!! ЭКСТРЕННОЕ СОХРАНЕНИЕ !!!");
	        forceSave(concurrentMatrix, concurrentMatrixTime);
	    }));

	    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
	    List<Future<?>> futures = new ArrayList<>();

	    // Основной цикл
	    for (int k = 0; k < allShop.size(); k++) {
	        if (j != null && k == j) break;
	        
	        final Shop shop = allShop.get(k);
	        final int currentShopNum = shopCounter.incrementAndGet();
	        
	        for (int i = 0; i < allShop.size(); i++) {
	            if (i == k) continue;
	            
	            final Shop shopI = allShop.get(i);
	            final String key = shop.getNumshop() + "-" + shopI.getNumshop();
	            
	            futures.add(executor.submit(() -> {
	                try {
	                    if (!concurrentMatrix.containsKey(key)) {
	                        double[] result = calculateDistance(shop, shopI);
	                        concurrentMatrix.put(key, result[0]);
	                        concurrentMatrixTime.put(key, (double)result[1]);
	                    }
	                    
	                    // Обновление прогресса
	                    int processed = totalProcessed.incrementAndGet();
	                    if (processed % 100 == 0) {
	                        double percentDone = (double)processed / totalPairs * 100;
	                        System.out.printf(
	                            "Прогресс: магазин %d/%d (%.1f%%) | пар %d/%d (%.1f%%) | %s -> %s | dist: %.2f\n",
	                            currentShopNum, totalShops, 
	                            (double)currentShopNum / totalShops * 100,
	                            processed, totalPairs,
	                            percentDone,
	                            shop.getNumshop(), shopI.getNumshop(),
	                            concurrentMatrix.getOrDefault(key, 0.0)
	                        );
	                    }
	                    
	                    // Автосохранение
	                    if (processed % 1000 == 0) {
	                        conditionalSave(concurrentMatrix, concurrentMatrixTime);
	                    }
	                } catch (Exception e) {
	                    System.err.printf("Ошибка в паре %s-%s: %s\n",
	                        shop.getNumshop(), shopI.getNumshop(), e.getMessage());
	                    e.printStackTrace();
	                }
	            }));
	        }
	    }

	    // Завершение
	    awaitCompletion(futures, executor);
	    forceSave(concurrentMatrix, concurrentMatrixTime);
	    
	    System.out.println("=== РАСЧЕТ ЗАВЕРШЕН ===");
	    System.out.println("Всего элементов в матрице: " + matrix.size());
	    
	    return matrix.size();
	}

	private double[] calculateDistance(Shop from, Shop to) throws Exception {
	    double fromLat = Double.parseDouble(from.getLat());
	    double fromLng = Double.parseDouble(from.getLng());
	    double toLat = Double.parseDouble(to.getLat());
	    double toLng = Double.parseDouble(to.getLng());
	    
	    CustomModel model = routingMachine.parseJSONFromClientCustomModel(null);
	    GHRequest req = routingMachine.GHRequestBilder(fromLat, fromLng, model, toLat, toLng);
	    GHResponse rsp = routingMachine.getGraphHopper().route(req);
	    ResponsePath path = rsp.getBest();
	    
	    return new double[]{path.getDistance(), path.getTime()};
	}

	private synchronized void conditionalSave(
		    ConcurrentMap<String, Double> tempMatrix,
		    ConcurrentMap<String, Double> tempMatrixTime) {
		    
		    long start = System.currentTimeMillis();
		    Path tempFile = Paths.get(mainController.path + "resources/distance/matrix.temp");
		    Path targetFile = Paths.get(mainController.path + "resources/distance/matrix.ser");
		    
		    System.out.println("\n[Автосохранение] Начато...");
		    System.out.println("Путь сохранения: " + targetFile.toAbsolutePath());
		    
		    try {
		        // Обновляем основные матрицы
		        matrix.putAll(tempMatrix);
		        matrixTime.putAll(tempMatrixTime);
		        
		        // Создаем временный файл
		        try (ObjectOutputStream oos = new ObjectOutputStream(
		            Files.newOutputStream(tempFile, StandardOpenOption.CREATE))) {
		            oos.writeObject(matrix);
		        }
		        
		        // Атомарное перемещение
		        Files.move(tempFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
		        
		        System.out.printf("[Автосохранение] Успешно завершено за %d мс\n", System.currentTimeMillis() - start);
		        System.out.println("Сохранено элементов: " + matrix.size());
		        System.out.println("Файл: " + targetFile.toAbsolutePath());
		    } catch (Exception e) {
		        System.err.println("[Автосохранение] Ошибка:");
		        System.err.println("Путь: " + targetFile.toAbsolutePath());
		        e.printStackTrace();
		        
		        // Попытка удалить временный файл при ошибке
		        try {
		            if (Files.exists(tempFile)) {
		                Files.delete(tempFile);
		            }
		        } catch (IOException ex) {
		            System.err.println("Не удалось удалить временный файл:");
		            ex.printStackTrace();
		        }
		    }
		}

	private synchronized void forceSave(
	    ConcurrentMap<String, Double> tempMatrix,
	    ConcurrentMap<String, Double> tempMatrixTime) {
	    
	    System.out.println("Инициировано принудительное сохранение...");
	    conditionalSave(tempMatrix, tempMatrixTime);
	}

	private void awaitCompletion(List<Future<?>> futures, ExecutorService executor) {
	    try {
	        for (Future<?> future : futures) {
	            try {
	                future.get();
	            } catch (ExecutionException e) {
	                System.err.println("Ошибка выполнения: " + e.getCause().getMessage());
	            }
	        }
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	    } finally {
	        executor.shutdownNow();
	    }
	}
	
	@Deprecated
	public Map<String, Double> loadMatrixOfDistance() {
		this.matrix = distanceMatrixService.getDistanceMatrix();
		return matrix;		
	}

	
	public Map<String, Double> loadMatrixOfDistanceV2() {
		this.matrix = distanceMatrixService.getDistanceMatrix();
		return matrix;		
	}
	
	public Map<String, Double> loadMatrixOfDistanceAutosave() {
		try {
			FileInputStream fis = new FileInputStream(mainController.path + "resources/distance/matrix.ser");
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
	
	
	/*
	 *новый метод который записывает сразу в БД 
	 */
	
	public int calculationDistanceToDB(Integer j, int threadCount) {
	    // Проверка и создание директории для логов (если нужно)
	    Path saveDir = Paths.get(mainController.path + "resources/distance/");
	    try {
	        Files.createDirectories(saveDir);
	    } catch (IOException e) {
	        System.err.println("Ошибка создания директории для логов: " + e.getMessage());
	    }

	    // Информация о запуске
	    System.out.printf("=== ЗАПУСК РАСЧЕТА В БД ===\nПотоков: %d\nМагазинов: %d\n", 
	        threadCount, shopService.getShopList().size());

	    // Счетчики прогресса
	    AtomicInteger totalProcessed = new AtomicInteger(0);
	    AtomicInteger newRecords = new AtomicInteger(0);
	    AtomicInteger existingRecords = new AtomicInteger(0);
	    AtomicInteger shopCounter = new AtomicInteger(0);
	    
	    List<Shop> allShop = shopService.getShopList();
	    int totalShops = j != null ? Math.min(j, allShop.size()) : allShop.size();
	    int totalPairs = totalShops * (allShop.size() - 1);

	    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
	    List<Future<?>> futures = new ArrayList<>();

	    // Основной цикл
	    for (int k = 0; k < allShop.size(); k++) {
	        if (j != null && k == j) break;
	        
	        final Shop shop = allShop.get(k);
	        final int currentShopNum = shopCounter.incrementAndGet();
	        
	        for (int i = 0; i < allShop.size(); i++) {
	            if (i == k) continue;
	            
	            final Shop shopI = allShop.get(i);
	            final String key = shop.getNumshop() + "-" + shopI.getNumshop();
	            
	            futures.add(executor.submit(() -> {
	                try {
	                    // Проверяем существование записи в БД
	                    if (!distanceMatrixService.isExist(key)) {
	                        // Вычисляем расстояние и время
	                        double[] result = calculateDistanceV2(shop, shopI);
	                        double distance = result[0];
	                        double time = result[1];
	                        
	                        // Создаем и сохраняем объект
	                        DistanceMatrix dm = new DistanceMatrix();
	                        dm.setIdDistanceMatrix(key); // Генерация ID
	                        dm.setDistance(distance);
	                        dm.setTime(time);
	                        dm.setShopFrom(shop.getNumshop()+"");
	                        dm.setShopTo(shopI.getNumshop()+"");
	                        
	                        distanceMatrixService.save(dm);
	                        newRecords.incrementAndGet();
	                    } else {
	                        existingRecords.incrementAndGet();
	                    }
	                    
	                    // Логирование прогресса
	                    int processed = totalProcessed.incrementAndGet();
	                    if (processed % 100 == 0) {
	                        double percentDone = (double)processed / totalPairs * 100;
	                        System.out.printf(
	                            "Прогресс: магазин %d/%d (%.1f%%) | пар %d/%d (%.1f%%) | Новые: %d | Существующие: %d | %s -> %s\n",
	                            currentShopNum, totalShops, 
	                            (double)currentShopNum / totalShops * 100,
	                            processed, totalPairs,
	                            percentDone,
	                            newRecords.get(),
	                            existingRecords.get(),
	                            shop.getNumshop(), shopI.getNumshop()
	                        );
	                    }
	                } catch (Exception e) {
	                    System.err.printf("Ошибка в паре %s-%s: %s\n",
	                        shop.getNumshop(), shopI.getNumshop(), e.getMessage());
	                    e.printStackTrace();
	                }
	            }));
	        }
	    }

	    // Завершение
	    awaitCompletionV2(futures, executor);
	    
	    System.out.println("=== РАСЧЕТ ЗАВЕРШЕН ===");
	    System.out.printf("Итого: новых записей - %d, существующих - %d\n", 
	        newRecords.get(), existingRecords.get());
	    
	    return newRecords.get();
	}

	private double[] calculateDistanceV2(Shop from, Shop to) throws Exception {
	    double fromLat = Double.parseDouble(from.getLat());
	    double fromLng = Double.parseDouble(from.getLng());
	    double toLat = Double.parseDouble(to.getLat());
	    double toLng = Double.parseDouble(to.getLng());
	    
	    CustomModel model = routingMachine.parseJSONFromClientCustomModel(null);
	    GHRequest req = routingMachine.GHRequestBilder(fromLat, fromLng, model, toLat, toLng);
	    GHResponse rsp = routingMachine.getGraphHopper().route(req);
	    ResponsePath path = rsp.getBest();
	    
	    return new double[]{path.getDistance(), path.getTime()};
	}

	private void awaitCompletionV2(List<Future<?>> futures, ExecutorService executor) {
	    try {
	        for (Future<?> future : futures) {
	            try {
	                future.get();
	            } catch (ExecutionException e) {
	                System.err.println("Ошибка выполнения: " + e.getCause().getMessage());
	            }
	        }
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	    } finally {
	        executor.shutdownNow();
	    }
	}
	
}
