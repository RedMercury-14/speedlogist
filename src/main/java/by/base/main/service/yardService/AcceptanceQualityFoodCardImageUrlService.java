package by.base.main.service.yardService;

import by.base.main.model.yard.Acceptance;
import by.base.main.model.yard.AcceptanceQualityFoodCard;
import by.base.main.model.yard.AcceptanceQualityFoodCardImageUrl;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public interface AcceptanceQualityFoodCardImageUrlService{

    List<AcceptanceQualityFoodCardImageUrl> getAllByAcceptanceQualityFoodCard(AcceptanceQualityFoodCard acceptanceQualityFoodCard);

    AcceptanceQualityFoodCardImageUrl getByIdAcceptanceQualityFoodCardImageUrl(Long idAcceptanceQualityFoodCardImageUrl);

    List<String> getUrls(AcceptanceQualityFoodCard acceptanceQualityFoodCard, HttpServletRequest request);

    String getUrlImage(Long idAcceptanceQualityFoodCardImageUrl);

    Resource getFile(Long idFile);

}
