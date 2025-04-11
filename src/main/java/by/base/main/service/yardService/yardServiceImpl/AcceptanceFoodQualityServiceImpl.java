package by.base.main.service.yardService.yardServiceImpl;

import by.base.main.dao.yardDao.AcceptanceQualityDAO;
import by.base.main.model.yard.AcceptanceFoodQuality;
import by.base.main.service.yardService.AcceptanceFoodQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AcceptanceFoodQualityServiceImpl implements AcceptanceFoodQualityService {

    @Autowired
    private AcceptanceQualityDAO acceptanceQualityDAO;


    @Override
    public List<AcceptanceFoodQuality> getAllByStatus(Integer status) {
        return acceptanceQualityDAO.getAllByStatus(status);
    }

    @Override
    public List<AcceptanceFoodQuality> getAllByStatuses(List<Integer> statuses) {
        // Реализация метода, возможно, нужно адаптировать под ваш запрос
        return acceptanceQualityDAO.getAllByStatuses(statuses);
    }

    @Override
    public List<AcceptanceFoodQuality> getAllByStatusAndDates(int status, LocalDate startDate, LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return acceptanceQualityDAO.getAllByStatusAndDates(status, startDateTime, endDateTime);
    }

    @Override
    public AcceptanceFoodQuality getByIdAcceptanceFoodQuality(Long idAcceptanceFoodQuality) {
        return acceptanceQualityDAO.getByIdAcceptanceFoodQuality(idAcceptanceFoodQuality);
    }
}


