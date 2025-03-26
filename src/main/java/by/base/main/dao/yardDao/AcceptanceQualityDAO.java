package by.base.main.dao.yardDao;

import by.base.main.model.yard.AcceptanceFoodQuality;

import java.time.LocalDateTime;
import java.util.List;

public interface AcceptanceQualityDAO {

    List<AcceptanceFoodQuality> getAllByStatus(int status);

    List<AcceptanceFoodQuality> getAllByStatuses(List<Integer> statuses);

    List<AcceptanceFoodQuality> getAllByStatusAndDates(int status, LocalDateTime start, LocalDateTime end);

    AcceptanceFoodQuality getByIdAndStatus(Long id, int status);

    AcceptanceFoodQuality getByIdAndStatusLessThan(Long id, int status);

    AcceptanceFoodQuality getByIdAcceptanceFoodQuality(Long id);

}
