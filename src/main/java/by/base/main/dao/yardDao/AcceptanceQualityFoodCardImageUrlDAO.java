package by.base.main.dao.yardDao;

import by.base.main.model.yard.AcceptanceQualityFoodCard;
import by.base.main.model.yard.AcceptanceQualityFoodCardImageUrl;

import java.util.List;

public interface AcceptanceQualityFoodCardImageUrlDAO {

    List<AcceptanceQualityFoodCardImageUrl> getAllByAcceptanceQualityFoodCard(AcceptanceQualityFoodCard acceptanceQualityFoodCard);

    AcceptanceQualityFoodCardImageUrl getByIdAcceptanceQualityFoodCardImageUrl(Long idAcceptanceQualityFoodCardImageUrl);


}