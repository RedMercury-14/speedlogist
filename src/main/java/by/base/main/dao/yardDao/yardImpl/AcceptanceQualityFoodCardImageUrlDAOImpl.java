package by.base.main.dao.yardDao.yardImpl;

import by.base.main.dao.yardDao.AcceptanceQualityFoodCardImageUrlDAO;
import by.base.main.model.yard.AcceptanceQualityFoodCard;
import by.base.main.model.yard.AcceptanceQualityFoodCardImageUrl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public class AcceptanceQualityFoodCardImageUrlDAOImpl implements AcceptanceQualityFoodCardImageUrlDAO {


    @Autowired
    @Qualifier("sessionFactoryYard")
    private SessionFactory sessionFactoryYard;

    private static final String GET_ALL_IMAGES_BY_ACCEPTANCE_QUALITY_FOOD_CARD =
            "SELECT image FROM AcceptanceQualityFoodCardImageUrl image " +
                    "WHERE image.acceptanceQualityFoodCard = :acceptanceQualityFoodCard";

    @Transactional(transactionManager = "myTransactionManagerYard")
    public List<AcceptanceQualityFoodCardImageUrl> getAllByAcceptanceQualityFoodCard(AcceptanceQualityFoodCard acceptanceQualityFoodCard) {
        Session session = sessionFactoryYard.getCurrentSession();

        Query<AcceptanceQualityFoodCardImageUrl> query = session.createQuery(GET_ALL_IMAGES_BY_ACCEPTANCE_QUALITY_FOOD_CARD, AcceptanceQualityFoodCardImageUrl.class);
        query.setParameter("acceptanceQualityFoodCard",acceptanceQualityFoodCard);
        return query.getResultList();
    }



    private static final String GET_BY_ID_IMAGE_URL =
            "SELECT image from AcceptanceQualityFoodCardImageUrl image " +
            "where image.idAcceptanceQualityFoodCardImageUrl = :idAcceptanceQualityFoodCardImageUrl";

    @Transactional(transactionManager = "myTransactionManagerYard")
    public AcceptanceQualityFoodCardImageUrl getByIdAcceptanceQualityFoodCardImageUrl(Long idAcceptanceQualityFoodCardImageUrl) {
        Session session = sessionFactoryYard.getCurrentSession();

        Query<AcceptanceQualityFoodCardImageUrl> query = session.createQuery(GET_BY_ID_IMAGE_URL, AcceptanceQualityFoodCardImageUrl.class);
        query.setParameter("idAcceptanceQualityFoodCardImageUrl", idAcceptanceQualityFoodCardImageUrl);
        return query.getSingleResult();
    }

    
    private static final String queryGetMapCardImage = "SELECT image from AcceptanceQualityFoodCardImageUrl image "
			+ "where ol.goodsId IN (:goodsIds)"
			+ "and o.dateDelivery is not null "
			+ "ORDER BY o.dateDelivery DESC";
	@Override
	public Map<Long, List<AcceptanceQualityFoodCardImageUrl>> getMapCardImage(List<Long> idAcceptanceQualityFoodCard) {
		// TODO Auto-generated method stub
		return null;
	}
}
