package by.base.main.service.impl;

import by.base.main.dao.ReviewDAO;
import by.base.main.model.Review;
import by.base.main.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewDAO reviewDAO;

    @Transactional
    @Override
    public void saveOrUpdateReview(Review review) {
        reviewDAO.saveOrUpdateReview(review);
    }

    @Transactional
    @Override
    public List<Review> getReviewsByDates(Date dateStart, Date dateEnd){
        return reviewDAO.getReviewsByDates(dateStart, dateEnd);
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewDAO.getReviewById(id);
    }
}
