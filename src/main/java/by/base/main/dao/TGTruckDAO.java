package by.base.main.dao;

import java.sql.Date;
import java.util.List;

import by.base.main.model.TGTruck;
import by.base.main.model.TGUser;

public interface TGTruckDAO {
	
	List<TGTruck> getTGTruckList();
	
	/**
	 * Отдаёт машины начная с сегодняшней даты и дальше
	 * @return
	 */
	List<TGTruck> getActualTGTruckList();

	Integer saveOrUpdateTGTruck(TGTruck tgTruck);
	
	List<TGTruck> getTGTruckByChatIdUser(long chatId);
	
	TGTruck getTGTruckByChatNumTruck(String numTruck, TGUser tgUser);
	
	/**
	 * Отдаёт TGTruck по номеру авто и Date
	 * (Date = date_order_truck_optimization)
	 * <b>Это строгий метод, который специально обращается к дате которая приходит!</b>
	 * @param numTruck
	 * @return
	 */
	TGTruck getTGTruckByChatNumTruckStrict(String numTruck, Date date, TGUser tgUser);
	
	TGTruck getTGTruckByChatId(Integer id);
	
	void deleteTGTruckByNumTruck(String numTruck, TGUser tgUser);
	
	void deleteTGTruckByNumTruck(String numTruck, Date date);
	
	boolean checkListName(String name, Date date);
	
	/**
	 * озвращает список машин по id юзера и периоду
	 * @param idUser
	 * @param dateStart
	 * @param dateFinish
	 * @return
	 */
	List<TGTruck> getTGTruckByidUserPeriod(Integer idUser, Date dateStart, Date dateFinish);
}
