package by.base.main.dao.impl;

import java.sql.Date;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dto.CounterpartyDTO;

import by.base.main.dao.ScheduleDAO;
import by.base.main.model.Schedule;

@Repository
public class ScheduleDAOImpl implements ScheduleDAO{

	@Autowired
	private SessionFactory sessionFactory;
	
	private static final String queryGetObjByIdOrder = "from Schedule where idSchedule=:idSchedule";
	@Transactional
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
	@Transactional
	@Override
	public List<Schedule> getSchedulesListRC() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theRole = currentSession.createQuery(queryGetListRC, Schedule.class);
		List <Schedule> roles = theRole.getResultList();
		return roles;
	}
	
	private static final String queryGetListTO = "from Schedule where type='ТО'";
	@Transactional
	@Override
	@Deprecated
	public List<Schedule> getSchedulesListTO() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theRole = currentSession.createQuery(queryGetListTO, Schedule.class);
		List <Schedule> roles = theRole.getResultList();
		return roles;
	}

	private static final String queryGetObjByNumContract = "from Schedule where counterpartyContractCode=:counterpartyContractCode";
	@Transactional
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
		
		//испытать этот метод!
//		Session currentSession = sessionFactory.getCurrentSession();
//	    Query<Schedule> theObject = currentSession.createQuery(queryGetObjByNumContract, Schedule.class);
//	    theObject.setParameter("counterpartyContractCode", num);
//	    
//	    // Используем uniqueResultOptional
//	    return theObject.uniqueResultOptional().orElse(null);
		return object;
	}

	@Transactional
	@Override
	public Integer saveOrder(Schedule schedule) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.save(schedule);
		return Integer.parseInt(currentSession.getIdentifier(schedule).toString());
	}

	@Transactional
	@Override
	public void updateOrder(Schedule schedule) {
		Session currentSession = sessionFactory.getCurrentSession();
		currentSession.update(schedule);
	}

	@Transactional
	@Override
	public void deleteOrderById(Integer id) {
		System.err.println("В разработке");
	}

	private static final String queryGetSchedulesByStock = "from Schedule where numStock=:numStock";
	@Transactional
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


	@Transactional
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
	@Transactional
	@Override
	@Deprecated
	public List<Schedule> getSchedulesListTOContract(String contractCode) {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theRole = currentSession.createQuery(queryGetListTOContract, Schedule.class);
		theRole.setParameter("counterpartyContractCode", Long.parseLong(contractCode.trim()));
		List <Schedule> roles = theRole.getResultList();
		return roles;
	}

	@Transactional
	@Override
	public List<Schedule> getSchedulesListTOСounterparty(String counterpartyName) {
		final String queryGetListTOСounterparty = "from Schedule where type='ТО' AND name LIKE '%"+ counterpartyName + "%'";
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> theRole = currentSession.createQuery(queryGetListTOСounterparty, Schedule.class);
		List <Schedule> roles = theRole.getResultList();
		return roles;
	}

	private static final String queryGetSchedulesByTOType = "from Schedule where toType=:toType";
	@Transactional
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
	@Transactional
	@Override
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
	@Transactional
	@Override
	public List<CounterpartyDTO> getcounterpartyListRC() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<CounterpartyDTO> theRole = currentSession.createQuery(queryGetcounterpartyListRC, CounterpartyDTO.class);
		List <CounterpartyDTO> roles = theRole.getResultList();
		for (CounterpartyDTO dto : roles) {
		    List<Long> contractCodes = currentSession.createQuery(
		        "SELECT c.counterpartyContractCode FROM Schedule c WHERE c.counterpartyCode = :code", Long.class)
		        .setParameter("code", dto.getCounterpartyCode())
		        .getResultList();
		    
		    dto.setCounterpartyContractCode(contractCodes); // Устанавливаем список в DTO
		}
		return roles;
	}

	private static final String queryGetcounterpartyListTO = counterpartyConstruct +
            "FROM Schedule s " +
            "WHERE s.type ='ТО' AND s.counterpartyCode IS NOT NULL " +
            "GROUP BY s.counterpartyCode";
	@Transactional
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
	@Transactional
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
	@Transactional
	@Override
	public List<CounterpartyDTO> getUnicCodeContractTO() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<CounterpartyDTO> theRole = currentSession.createQuery(queryGetUnicCodeContractTO, CounterpartyDTO.class);
		List <CounterpartyDTO> roles = theRole.getResultList();
		return roles;
	}

	private static final String queryUpdateScheduleBycounterpartyCodeHascodeNameOfQuantumCounterparty = "UPDATE Schedule s SET s.codeNameOfQuantumCounterparty = :newCodeName WHERE s.counterpartyCode = :counterpartyCode";
	@Transactional
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

	@Transactional
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
	private static final String queryGetListTOWithTemp = "from Schedule where type='ТО' AND ((startDateTemp IS NOT NULL AND endDateTemp IS NOT NULL AND CURRENT_DATE BETWEEN startDateTemp AND endDateTemp) or (startDateTemp IS NULL AND endDateTemp IS NULL))";
	@Transactional
	@Override
	public List<Schedule> getSchedulesListTOWithTemp() {
		Session currentSession = sessionFactory.getCurrentSession();
		Query<Schedule> schedule = currentSession.createQuery(queryGetListTOWithTemp, Schedule.class);
		List <Schedule> schedules = schedule.getResultList();

		return schedules;
	}

//	/**
//	 * @author Ira
//	 * @param num
//	 * @return
//	 */
//	private static final String queryGetSchedulesByTOTypeWithTemp = "from Schedule where toType=:toType AND ((startDateTemp IS NOT NULL AND endDateTemp IS NOT NULL AND CURRENT_DATE BETWEEN startDateTemp AND endDateTemp) or (startDateTemp IS NULL AND endDateTemp IS NULL))";
//	@Transactional
//	@Override
//	public List<Schedule> getSchedulesByTOTypeWithTemp(String toType) {
//		Session currentSession = sessionFactory.getCurrentSession();
//		Query<Schedule> theObject = currentSession.createQuery(queryGetSchedulesByTOTypeWithTemp, Schedule.class);
//		theObject.setParameter("toType", toType.trim());
//		List<Schedule> trucks = theObject.getResultList();
//		if(trucks.isEmpty()) {
//			return null;
//		}
//		return trucks;
//	}


}
