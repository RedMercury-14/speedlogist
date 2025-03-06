package by.base.main.dao;

import by.base.main.model.Review;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;

public interface ReviewDAO {

    /**
     * @param review
     * <br>Метод сохраняет объект обратной связи</br>
     * @author Ira
     */
    Long saveReview(Review review);

    /**
     * @param review
     * <br>Метод обновляет объект обратной связи</br>
     * @author Ira
     */
    void updateReview(Review review);

    /**
     * @param id
     * <br>Метод получает объект обратной связи по id</br>
     * @author Ira
     */
    Review getReviewById(Long id);

    /**
     * @param dateStart
     * @param dateEnd
     * <br>Метод получает список объектов обратной связи за указанный период</br>
     * @author Ira
     */
    List<Review> getReviewsByDates(Date dateStart, Date dateEnd);
}
