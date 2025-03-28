package by.base.main.dao.yardDao;

import by.base.main.model.yard.AcceptanceFoodQuality;
import by.base.main.model.yard.AcceptanceQualityFoodCard;

import java.util.List;

public interface AcceptanceQualityFoodCardDAO {

    List<AcceptanceQualityFoodCard> getAllByAcceptanceFoodQuality(AcceptanceFoodQuality acceptanceFoodQuality);

    AcceptanceQualityFoodCard getByIdAcceptanceQualityFoodCard(Long idAcceptanceQualityFoodCard);

}
