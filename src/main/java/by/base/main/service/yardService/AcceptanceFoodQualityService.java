package by.base.main.service.yardService;

import by.base.main.model.yard.AcceptanceFoodQuality;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AcceptanceFoodQualityService {

    List<AcceptanceFoodQuality> getAllByStatus(Integer status);

    List<AcceptanceFoodQuality> getAllByStatuses(List<Integer> statuses);

    List<AcceptanceFoodQuality> getAllByStatusAndDates(int status, LocalDate startDateTime, LocalDate endDateTime);

    AcceptanceFoodQuality getByIdAcceptanceFoodQuality(Long idAcceptanceFoodQuality);

    void update(AcceptanceFoodQuality acceptanceFoodQuality);


}
