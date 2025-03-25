package by.base.main.dao.yardDao.yardImpl;

import by.base.main.dao.yardDao.AcceptanceQualityFoodCardDAO;
import by.base.main.model.yard.AcceptanceFoodQuality;
import by.base.main.model.yard.AcceptanceQualityFoodCard;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.hibernate.query.Query;
import java.util.List;

@Repository
public class AcceptanceQualityFoodCardDAOImpl implements AcceptanceQualityFoodCardDAO {

    @Autowired
    private SessionFactory sessionFactoryYard;


    private static final String GET_ALL_BY_ACCEPTANCE_FOOD_QUAILITY = "SELECT DISTINCT card FROM AcceptanceQualityFoodCard card " +
            "LEFT JOIN FETCH card.internalDefectsQualityCardList " +
            "LEFT JOIN FETCH card.lightDefectsQualityCardList " +
            "LEFT JOIN FETCH card.totalDefectQualityCardList " +
            "WHERE card.acceptanceFoodQuality = :acceptanceFoodQuality";

    @Transactional(transactionManager = "myTransactionManagerYard")
    public List<AcceptanceQualityFoodCard> getAllByAcceptanceFoodQuality(AcceptanceFoodQuality acceptanceFoodQuality) {
        Session session = sessionFactoryYard.getCurrentSession();

        Query<AcceptanceQualityFoodCard> query = session.createQuery(GET_ALL_BY_ACCEPTANCE_FOOD_QUAILITY, AcceptanceQualityFoodCard.class);
        query.setParameter("acceptanceFoodQuality",acceptanceFoodQuality);
        return query.getResultList();
    }


    String GET_BY_ID_ACCEPTANCE_QUALITY_FOOD_CARD = "SELECT DISTINCT a FROM AcceptanceQualityFoodCard a " +
            "LEFT JOIN FETCH a.internalDefectsQualityCardList " +
            "LEFT JOIN FETCH a.lightDefectsQualityCardList " +
            "LEFT JOIN FETCH a.totalDefectQualityCardList " +
            "WHERE a.idAcceptanceQualityFoodCard = :idAcceptanceQualityFoodCard";

    @Transactional(transactionManager = "myTransactionManagerYard")
    public AcceptanceQualityFoodCard getByIdAcceptanceQualityFoodCard(Long idAcceptanceQualityFoodCard) {
        Session session = sessionFactoryYard.getCurrentSession();
        Query<AcceptanceQualityFoodCard> query = session.createQuery(GET_BY_ID_ACCEPTANCE_QUALITY_FOOD_CARD, AcceptanceQualityFoodCard.class);

        query.setParameter("idAcceptanceQualityFoodCard",idAcceptanceQualityFoodCard);

        return query.uniqueResult();
    }

}

