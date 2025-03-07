package by.base.main.dao.impl;

import java.sql.Date;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dto.CounterpartyDTO;

import by.base.main.dao.ScheduleDAO;
import by.base.main.dto.ScheduleCountOrderDTO;
import by.base.main.model.Schedule;

@Repository
public class ScheduleDAOImpl implements ScheduleDAO{

	@Autowired
	private SessionFactory sessionFactory;
	
	private static final String queryGetObjByIdOrder = "from Schedule where idSchedule=:idSchedule";
	
	@Override
	public Schedule getScheduleById(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryGetObjByIdOrder, Schedule.class);
		theObject.setParameter("idSchedule", id);
		List<Schedule> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}
		Schedule object = trucks.stream().findFirst().get();
		return object;
	}

	
	private static final String queryGetListRC = "from Schedule where type='РЦ'";
	
	@Override
	public List<Schedule> getSchedulesListRC() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theRole = currentSession.createQuery(queryGetListRC, Schedule.class);
		List <Schedule> roles = theRole.getResultList();
		return roles;
	}
	
	private static final String queryGetListTO = "from Schedule where type='ТО' AND status = 20";
	
	@Override
	@Deprecated
	public List<Schedule> getSchedulesListTO() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theRole = currentSession.createQuery(queryGetListTO, Schedule.class);
		List <Schedule> roles = theRole.getResultList();
		return roles;
	}
	
	private static final String queryGetListTOAll = "from Schedule where type='ТО'";
	
	@Override
	public List<Schedule> getSchedulesListTOAll() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theRole = currentSession.createQuery(queryGetListTOAll, Schedule.class);
		List <Schedule> roles = theRole.getResultList();
		return roles;
	}

	private static final String queryGetObjByNumContract = "from Schedule where counterpartyContractCode=:counterpartyContractCode";
	
	@Override
	public Schedule getScheduleByNumContract(Long num) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryGetObjByNumContract, Schedule.class);
		theObject.setParameter("counterpartyContractCode", num);
		List<Schedule> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}
		Schedule object = trucks.stream().findFirst().get();
		return object;
		
		//испытать этот метод!
//		Session currentSession = sessionFactory.getCurrentSession();
//	    Query<Schedule> theObject = currentSession.createQuery(queryGetObjByNumContract, Schedule.class);
//	    theObject.setParameter("counterpartyContractCode", num);
//	    
//	    // Используем uniqueResultOptional
//	    return theObject.uniqueResultOptional().orElse(null);
	}

	
	@Override
	public Integer saveOrder(Schedule schedule) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(schedule);
		return Integer.parseInt(currentSession.getIdentifier(schedule).toString());
	}

	
	@Override
	public void updateOrder(Schedule schedule) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.update(schedule);
	}

	private static final String queryDeleteById = "delete from Schedule where idSchedule=:idSchedule";
	
	@Override
	public void deleteOrderById(Integer id) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query theQuery = 
				currentSession.createQuery(queryDeleteById);
		theQuery.setParameter("idSchedule", id);
		theQuery.executeUpdate();

	}

	private static final String queryGetSchedulesByStock = "from Schedule where numStock=:numStock";
	
	@Override
	public List<Schedule> getSchedulesByStock(Integer numStock) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryGetSchedulesByStock, Schedule.class);
		theObject.setParameter("numStock", numStock);
		List<Schedule> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}
		return trucks;
	}


	
	@Override
	public List<Schedule> getSchedulesByDateOrder(Date date, Integer numStock) {
		String dayName = DayOfWeek.from(date.toLocalDate()).getDisplayName(java.time.format.TextStyle.FULL, new Locale("en")).toLowerCase();		
		String queryGetSchedulesByDateOrder = "from Schedule where status=20 AND numStock=:numStock AND "+dayName+" LIKE '%з%'";
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryGetSchedulesByDateOrder, Schedule.class);
		theObject.setParameter("numStock", numStock);
		List<Schedule> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}
		return trucks;
	}

	private static final String queryGetListTOContract = "from Schedule where type='ТО' AND counterpartyContractCode=:counterpartyContractCode";
	
	@Override
	@Deprecated
	public List<Schedule> getSchedulesListTOContract(String contractCode) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theRole = currentSession.createQuery(queryGetListTOContract, Schedule.class);
		theRole.setParameter("counterpartyContractCode", Long.parseLong(contractCode.trim()));
		List <Schedule> roles = theRole.getResultList();
		return roles;
	}

	
	@Override
	public List<Schedule> getSchedulesListTOСounterparty(String counterpartyName) {
		final String queryGetListTOСounterparty = "from Schedule where type='ТО' AND name LIKE '%"+ counterpartyName + "%'";
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theRole = currentSession.createQuery(queryGetListTOСounterparty, Schedule.class);
		List <Schedule> roles = theRole.getResultList();
		return roles;
	}

	private static final String queryGetSchedulesByTOType = "from Schedule where toType=:toType AND status=20";
	
	@Override
	public List<Schedule> getSchedulesByTOType(String toType) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryGetSchedulesByTOType, Schedule.class);
		theObject.setParameter("toType", toType.trim());
		List<Schedule> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}
		return trucks;
	}

	private static final String queryGetObjByNumContractAndStock = "from Schedule where counterpartyContractCode=:counterpartyContractCode AND numStock=:numStock";
	
	@Override
	@Deprecated
	public Schedule getScheduleByNumContractAndNUmStock(Long num, Integer numStock) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryGetObjByNumContractAndStock, Schedule.class);
		theObject.setParameter("counterpartyContractCode", num);
		theObject.setParameter("numStock", numStock);
		List<Schedule> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}
		Schedule object = trucks.stream().findFirst().get();
		return object;
	}
	

	private static final String counterpartyConstruct = "SELECT new com.dto.CounterpartyDTO(" +
	        "s.counterpartyCode, " +
	        "MIN(s.name)) "; // Используем MIN для name
	
	private static final String queryGetcounterpartyListRC = counterpartyConstruct +
            "FROM Schedule s " +
            "WHERE s.type ='РЦ' AND s.counterpartyCode IS NOT NULL " +
            "GROUP BY s.counterpartyCode";	
	
	@Override
	public List<CounterpartyDTO> getcounterpartyListRC() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<CounterpartyDTO> theRole = currentSession.createQuery(queryGetcounterpartyListRC, CounterpartyDTO.class);
		List <CounterpartyDTO> roles = theRole.getResultList();
		/*
		 * из за этого цикла метод ужасно медленный
		 */
		for (CounterpartyDTO dto : roles) {
		    List<Long> contractCodes = currentSession.createQuery(
		        "SELECT c.counterpartyContractCode FROM Schedule c WHERE c.type ='РЦ' AND c.counterpartyCode = :code", Long.class) //тут обнаружена проблема. Добавил c.type ='РЦ' т.е. подтягивало коды из графиков поставок на ТО
		        .setParameter("code", dto.getCounterpartyCode())
		        .getResultList();
		    
		    dto.setCounterpartyContractCode(contractCodes); // Устанавливаем список в DTO
		}
		return roles;
	}
	
	@Override	
	public List<CounterpartyDTO> getСounterpartyListRCNameOnly() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<CounterpartyDTO> theRole = currentSession.createQuery(queryGetcounterpartyListRC, CounterpartyDTO.class);
		List <CounterpartyDTO> roles = theRole.getResultList();
		return roles;
	}

	private static final String queryGetcounterpartyListTO = counterpartyConstruct +
            "FROM Schedule s " +
            "WHERE s.type ='ТО' AND s.counterpartyCode IS NOT NULL " +
            "GROUP BY s.counterpartyCode";
	
	@Override
	public List<CounterpartyDTO> getcounterpartyListTO() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<CounterpartyDTO> theRole = currentSession.createQuery(queryGetcounterpartyListTO, CounterpartyDTO.class);
		List <CounterpartyDTO> roles = theRole.getResultList();
//		for (CounterpartyDTO dto : roles) {
//		    List<Long> contractCodes = currentSession.createQuery(
//		        "SELECT c.counterpartyContractCode FROM Schedule c WHERE c.counterpartyCode = :code", Long.class)
//		        .setParameter("code", dto.getCounterpartyCode())
//		        .getResultList();
//		    
//		    dto.setCounterpartyContractCode(contractCodes); // Устанавливаем список в DTO
//		}
		return roles;
	}

	private static final String queryGetObjByNumContractAndNumStock = "from Schedule where counterpartyContractCode=:counterpartyContractCode AND numStock=:numStock";
	
	@Override
	public Schedule getScheduleByNumContractAndNumStock(Long num, Integer shock) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryGetObjByNumContractAndNumStock, Schedule.class);
		theObject.setParameter("counterpartyContractCode", num);
		theObject.setParameter("numStock", shock);
		List<Schedule> trucks = theObject.getResultList();
		if(trucks.isEmpty()) {
			return null;
		}
		Schedule object = trucks.stream().findFirst().get();
		return object;
	}

	
	private static final String counterpartyConstruct2 = "SELECT new com.dto.CounterpartyDTO(" +
			"MIN(s.counterpartyCode), " +  // Используем MIN для counterpartyCode
	        "MIN(s.name),"+ // Используем MIN для name
	        "s.counterpartyContractCode) ";
	private static final String queryGetUnicCodeContractTO = counterpartyConstruct2 + "FROM Schedule s " +
            "WHERE s.type ='ТО' AND s.counterpartyContractCode IS NOT NULL " +
            "GROUP BY s.counterpartyContractCode";
	
	@Override
	public List<CounterpartyDTO> getUnicCodeContractTO() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<CounterpartyDTO> theRole = currentSession.createQuery(queryGetUnicCodeContractTO, CounterpartyDTO.class);
		List <CounterpartyDTO> roles = theRole.getResultList();
		return roles;
	}

	private static final String queryUpdateScheduleBycounterpartyCodeHascodeNameOfQuantumCounterparty = "UPDATE Schedule s SET s.codeNameOfQuantumCounterparty = :newCodeName WHERE s.counterpartyCode = :counterpartyCode";
	
	@Override
	public int updateScheduleBycounterpartyCodeHascodeNameOfQuantumCounterparty(Long counterpartyCode,
			String codeNameOfQuantumCounterparty) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query query = currentSession.createQuery(queryUpdateScheduleBycounterpartyCodeHascodeNameOfQuantumCounterparty);
		query.setParameter("counterpartyCode", counterpartyCode);
		query.setParameter("newCodeName", codeNameOfQuantumCounterparty);
		int result = query.executeUpdate();
		return result;
	}


	/**
	 * @author Ira
	 * <br>Возвращает список всех графиков на ТО по номеру контракта - и временных, и постоянных</br>
	 * @param num
	 * @return
	 */
	private static final String queryGetTemporaryObjByNumContract = "from Schedule s where type = 'ТО' AND counterpartyContractCode=:counterpartyContractCode AND ((startDateTemp IS NOT NULL AND endDateTemp IS NOT NULL AND CURRENT_DATE BETWEEN startDateTemp AND endDateTemp) or (startDateTemp IS NULL AND endDateTemp IS NULL))";

	
	@Override
	public List<Schedule> getSchedulesListTOContractWithTemp(Long num) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryGetTemporaryObjByNumContract, Schedule.class);
		theObject.setParameter("counterpartyContractCode", num);

		List<Schedule> schedules = theObject.getResultList();

		return schedules;
	}

	/**
	 * @author Ira
	 * <br>Возвращает список всех графиков на ТО - и временных, и постоянных</br>
	 * @return
	 *
	 */
	private static final String queryGetListTOWithTemp = "from Schedule where type='ТО' AND status = 20 " +
			"AND ((startDateTemp IS NOT NULL AND endDateTemp IS NOT NULL AND CURRENT_DATE BETWEEN startDateTemp AND endDateTemp) or (startDateTemp IS NULL AND endDateTemp IS NULL))";
	//private static final String queryGetListTOWithTemp = "from Schedule where type='ТО'";

	
	@Override
	public List<Schedule> getSchedulesListTOWithTemp() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> schedule = currentSession.createQuery(queryGetListTOWithTemp, Schedule.class);
		List <Schedule> schedules = schedule.getResultList();

        return schedule.getResultList();
	}

	/**
	 * @author Ira
	 * <br>Возвращает список графиков на ТО по номеру контракта и номеру ТО - и временных, и постоянных</br>
	 * @return
	 */
	private static final String queryGetObjByNumContractAndNumStockWithTemp = "from Schedule where counterpartyContractCode=:counterpartyContractCode AND numStock=:numStock";
	
	@Override
	public List<Schedule> getScheduleByNumContractAndNUmStockWithTemp(Long num, Integer numStock) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryGetObjByNumContractAndNumStockWithTemp, Schedule.class);
		theObject.setParameter("counterpartyContractCode", num);
		theObject.setParameter("numStock", numStock);
		List<Schedule> schedules = theObject.getResultList();
		return schedules;
	}


	/**
	 * @author Ira
	 * <br>Возвращает список графиков по типу ТО - и временных, и постоянных</br>
	 * @param toType
	 * @return
	 */
	private static final String queryGetSchedulesByTOTypeWithTemp = "from Schedule where toType=:toType AND status=20 AND ((startDateTemp IS NOT NULL AND endDateTemp IS NOT NULL AND CURRENT_DATE BETWEEN startDateTemp AND endDateTemp) or (startDateTemp IS NULL AND endDateTemp IS NULL))";
	
	@Override
	public List<Schedule> getSchedulesByTOTypeWithTemp(String toType) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryGetSchedulesByTOTypeWithTemp, Schedule.class);
		theObject.setParameter("toType", toType.trim());
		List<Schedule> schedules = theObject.getResultList();

		if(schedules.isEmpty()) {
			return null;
		}
		return schedules;
	}

	private static final String queryGetSchedulesListTOContractOnlyTemp = "from Schedule "
			+ "where counterpartyContractCode=:counterpartyContractCode "
			+ "AND status=20 "
			+ "AND startDateTemp IS NOT NULL AND endDateTemp IS NOT NULL";
	
	@Override
	public List<Schedule> getSchedulesListTOContractOnlyTemp(Long num) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryGetSchedulesListTOContractOnlyTemp, Schedule.class);
		theObject.setParameter("counterpartyContractCode", num);
		Set<Schedule> schedules = new HashSet<Schedule>(theObject.getResultList());
		for (Schedule schedule : schedules) {
			if(schedule.getStartDateTemp() == null || schedule.getEndDateTemp() == null) {
				throw new DTOException("Ошибка запроса. Вернулся постоянный график");
			}
		}
		return new ArrayList<Schedule>(schedules);
	}

	private static final String queryAllGetObjByNumContractAndNumStock = "from Schedule where counterpartyContractCode=:counterpartyContractCode AND numStock=:numStock";
	
	@Override
	public List<Schedule> getAllSchedulesByNumContractAndNumStock(Long num, Integer shock) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theObject = currentSession.createQuery(queryAllGetObjByNumContractAndNumStock, Schedule.class);
		theObject.setParameter("counterpartyContractCode", num);
		theObject.setParameter("numStock", shock);
		List<Schedule> schedules = theObject.getResultList();

		return schedules;
	}
	
	
	@Override
	public ScheduleCountOrderDTO getCountScheduleOrderHasWeek() {
	    String sqlQuery = "SELECT " +
	            "COALESCE(SUM(CASE WHEN monday LIKE ? AND type = 'РЦ' THEN 1 ELSE 0 END), 0), " +
	            "COALESCE(SUM(CASE WHEN tuesday LIKE ? AND type = 'РЦ' THEN 1 ELSE 0 END), 0), " +
	            "COALESCE(SUM(CASE WHEN wednesday LIKE ? AND type = 'РЦ' THEN 1 ELSE 0 END), 0), " +
	            "COALESCE(SUM(CASE WHEN thursday LIKE ? AND type = 'РЦ' THEN 1 ELSE 0 END), 0), " +
	            "COALESCE(SUM(CASE WHEN friday LIKE ? AND type = 'РЦ' THEN 1 ELSE 0 END), 0), " +
	            "COALESCE(SUM(CASE WHEN saturday LIKE ? AND type = 'РЦ' THEN 1 ELSE 0 END), 0), " +
	            "COALESCE(SUM(CASE WHEN sunday LIKE ? AND type = 'РЦ' THEN 1 ELSE 0 END), 0) " +
	            "FROM schedule";

	    Session currentSession = sessionFactory.getCurrentSession();
	    Query query = currentSession.createNativeQuery(sqlQuery);

	    // Устанавливаем параметры для LIKE
	    for (int i = 1; i <= 7; i++) {
	        query.setParameter(i, "%з%");
	    }

	    Object[] result = (Object[]) query.getSingleResult();

	    return new ScheduleCountOrderDTO(
	            ((Number) result[0]).longValue(),
	            ((Number) result[1]).longValue(),
	            ((Number) result[2]).longValue(),
	            ((Number) result[3]).longValue(),
	            ((Number) result[4]).longValue(),
	            ((Number) result[5]).longValue(),
	            ((Number) result[6]).longValue()
	    );
	}


	@Override
	public ScheduleCountOrderDTO getCountScheduleDeliveryHasWeek() {
		String sqlQuery = "SELECT " +
		          "COALESCE(SUM(CASE WHEN monday REGEXP ? AND type = 'РЦ' THEN 1 ELSE 0 END), 0), " +
		          "COALESCE(SUM(CASE WHEN tuesday REGEXP ? AND type = 'РЦ' THEN 1 ELSE 0 END), 0), " +
		          "COALESCE(SUM(CASE WHEN wednesday REGEXP ? AND type = 'РЦ' THEN 1 ELSE 0 END), 0), " +
		          "COALESCE(SUM(CASE WHEN thursday REGEXP ? AND type = 'РЦ' THEN 1 ELSE 0 END), 0), " +
		          "COALESCE(SUM(CASE WHEN friday REGEXP ? AND type = 'РЦ' THEN 1 ELSE 0 END), 0), " +
		          "COALESCE(SUM(CASE WHEN saturday REGEXP ? AND type = 'РЦ' THEN 1 ELSE 0 END), 0), " +
		          "COALESCE(SUM(CASE WHEN sunday REGEXP ? AND type = 'РЦ' THEN 1 ELSE 0 END), 0) " +
		          "FROM schedule";

		    Session currentSession = sessionFactory.getCurrentSession();
		    Query query = currentSession.createNativeQuery(sqlQuery);

		    // Устанавливаем параметры для REGEXP
		    for (int i = 1; i <= 7; i++) {
		       query.setParameter(i, ".*понедельник|вторник|среда|четверг|пятница|суббота|воскресенье.*");
		    }

		    Object[] result = (Object[]) query.getSingleResult();

		    return new ScheduleCountOrderDTO(
		          ((Number) result[0]).longValue(),
		          ((Number) result[1]).longValue(),
		          ((Number) result[2]).longValue(),
		          ((Number) result[3]).longValue(),
		          ((Number) result[4]).longValue(),
		          ((Number) result[5]).longValue(),
		          ((Number) result[6]).longValue()
		    );
	}



}
