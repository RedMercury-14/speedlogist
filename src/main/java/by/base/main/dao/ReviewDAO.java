package by.base.main.dao;

import by.base.main.model.Review;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;

public interface ReviewDAO {


    @Transactional
    void saveOrUpdateReview(Review review);

    @Transactional
    Review getReviewById(Long id);

    @Transactional
    List<Review> getReviewsByDates(Date dateStart, Date dateEnd);
}
