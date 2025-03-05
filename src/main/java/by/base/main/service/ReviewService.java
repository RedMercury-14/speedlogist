package by.base.main.service;

import by.base.main.model.Review;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;

public interface ReviewService {

    /**
     * @param review
     * <br>Метод сохраняет или обновляет объект обратной связи</br>
     * @author Ira
     */
    @Transactional
    void saveOrUpdateReview(Review review);

    /**
     * @param dateStart
     * @param dateEnd
     * <br>Метод получает список объектов обратной связи за указанный период</br>
     * @author Ira
     */
    @Transactional
    List<Review> getReviewsByDates(Date dateStart, Date dateEnd);

    /**
     * @param id
     * <br>Метод получает объект обратной связи по id</br>
     * @author Ira
     */
    Review getReviewById(Long id);
}
