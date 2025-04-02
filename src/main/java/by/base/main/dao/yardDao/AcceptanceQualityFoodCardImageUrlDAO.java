package by.base.main.dao.yardDao;

import by.base.main.model.yard.AcceptanceQualityFoodCard;
import by.base.main.model.yard.AcceptanceQualityFoodCardImageUrl;

import java.util.List;
import java.util.Map;

public interface AcceptanceQualityFoodCardImageUrlDAO {

    List<AcceptanceQualityFoodCardImageUrl> getAllByAcceptanceQualityFoodCard(AcceptanceQualityFoodCard acceptanceQualityFoodCard);

    AcceptanceQualityFoodCardImageUrl getByIdAcceptanceQualityFoodCardImageUrl(Long idAcceptanceQualityFoodCardImageUrl);
    
    /**
     * Возвращает мапу с AcceptanceQualityFoodCardImageUrl, где ключь - это <b>idAcceptanceQualityFoodCard</b>
     * @param idAcceptanceQualityFoodCard
     * @return
     */
    Map<Long, List<AcceptanceQualityFoodCardImageUrl>> getMapCardImage (List<Long> idAcceptanceQualityFoodCard);


}