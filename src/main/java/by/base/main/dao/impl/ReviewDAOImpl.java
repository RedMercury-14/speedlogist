package by.base.main.dao.impl;

import by.base.main.dao.ReviewDAO;
import by.base.main.model.Review;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.TemporalType;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.sql.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ReviewDAOImpl implements ReviewDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Long saveReview(Review review) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.save(review);
        return Long.parseLong(currentSession.getIdentifier(review).toString());
    }

    @Override
    public void updateReview(Review review) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.update(review);
    }

    private static final String queryGetObjByIdOrder = "from Review where idReview=:idReview";
    @Override
    public Review getReviewById(Long id) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<Review> theObject = currentSession.createQuery(queryGetObjByIdOrder, Review.class);
        theObject.setParameter("idReview", id);
        List<Review> reviews = theObject.getResultList();
        if(reviews.isEmpty()) {
            return null;
        }
        return reviews.stream().findFirst().get();
    }

    private static final String queryGetReviewByReviewDate = "from Review r where r.reviewDate BETWEEN :dateStart and :dateEnd";
    @Override
    public List<Review> getReviewsByDates(Date dateStart, Date dateEnd){
        Session currentSession = sessionFactory.getCurrentSession();
        Timestamp dateStartFinal = Timestamp.valueOf(LocalDateTime.of(dateStart.toLocalDate(), LocalTime.of(00, 00)));
        Timestamp dateEndFinal = Timestamp.valueOf(LocalDateTime.of(dateEnd.toLocalDate(), LocalTime.of(23, 59)));
        Query<Review> theObject = currentSession.createQuery(queryGetReviewByReviewDate, Review.class);
        theObject.setParameter("dateStart", dateStartFinal, TemporalType.TIMESTAMP);
        theObject.setParameter("dateEnd", dateEndFinal, TemporalType.TIMESTAMP);
        Set<Review> trucks = theObject.getResultList().stream().collect(Collectors.toSet());
        return trucks.stream().collect(Collectors.toList());
    }
}
