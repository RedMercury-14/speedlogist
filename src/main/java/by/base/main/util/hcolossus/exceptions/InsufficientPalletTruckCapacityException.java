package by.base.main.util.hcolossus.exceptions;

import java.util.List;

import by.base.main.model.Shop;
import by.base.main.util.hcolossus.pojo.MyVehicle;

/**
 * Исключение, возникающее в случае, если требуемая вместимость паллет для магазинов
 * превышает доступную вместимость паллет в грузовиках.
 *
 * <p>Класс предоставляет дополнительную информацию о причинах исключения,
 * включая список магазинов, список грузовиков и коэффициент, используемый
 * при расчете вместимости. Исключение может быть вызвано при проверке достаточности
 * вместимости грузовиков для обслуживания всех магазинов.
 */
public class InsufficientPalletTruckCapacityException extends RuntimeException {
	
	private String message;
	
	private List<Shop> shopsForOptimization;
	
	private List<MyVehicle> trucks;
	
	private Double mainKoef;

	/**
	 * @param message
	 */
	public InsufficientPalletTruckCapacityException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param shopsForOptimization
	 * @param trucks
	 * @param mainKoef
	 */
	public InsufficientPalletTruckCapacityException(String message, List<Shop> shopsForOptimization,
			List<MyVehicle> trucks, Double mainKoef) {
		super(message);
		this.message = message;
		this.shopsForOptimization = shopsForOptimization;
		this.trucks = trucks;
		this.mainKoef = mainKoef;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<Shop> getShopsForOptimization() {
		return shopsForOptimization;
	}

	public void setShopsForOptimization(List<Shop> shopsForOptimization) {
		this.shopsForOptimization = shopsForOptimization;
	}

	public List<MyVehicle> getTrucks() {
		return trucks;
	}

	public void setTrucks(List<MyVehicle> trucks) {
		this.trucks = trucks;
	}

	public Double getMainKoef() {
		return mainKoef;
	}

	public void setMainKoef(Double mainKoef) {
		this.mainKoef = mainKoef;
	}

	
	
	

}
