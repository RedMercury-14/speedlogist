package by.base.main.service.yardService.yardServiceImpl;

import by.base.main.dao.yardDao.AcceptanceQualityFoodCardDAO;
import by.base.main.dto.yard.AcceptanceQualityFoodCardDTO;
import by.base.main.model.yard.AcceptanceFoodQuality;
import by.base.main.model.yard.AcceptanceQualityFoodCard;
import by.base.main.model.yard.TtnIn;
import by.base.main.service.yardService.AcceptanceFoodQualityService;
import by.base.main.service.yardService.AcceptanceQualityFoodCardImageUrlService;
import by.base.main.service.yardService.AcceptanceQualityFoodCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

@Service
public class AcceptanceQualityFoodCardServiceImpl implements AcceptanceQualityFoodCardService {

    @Autowired
    private AcceptanceQualityFoodCardDAO acceptanceQualityFoodCardDAO;

    @Autowired
    private AcceptanceFoodQualityService acceptanceFoodQualityService;

    @Autowired
    private AcceptanceQualityFoodCardService acceptanceQualityFoodCardService;

    @Autowired
    private AcceptanceQualityFoodCardImageUrlService acceptanceQualityFoodCardImageUrlService;

    @Override
    public List<AcceptanceQualityFoodCard> getAllByAcceptanceFoodQuality(AcceptanceFoodQuality acceptanceFoodQuality) {
        return acceptanceQualityFoodCardDAO.getAllByAcceptanceFoodQuality(acceptanceFoodQuality);
    }

    @Override
    public AcceptanceQualityFoodCard getByIdAcceptanceQualityFoodCard(Long idAcceptanceQualityFoodCard) {
        return acceptanceQualityFoodCardDAO.getByIdAcceptanceQualityFoodCard(idAcceptanceQualityFoodCard);
    }

    @Override
    public List<AcceptanceQualityFoodCardDTO> getAllAcceptanceQualityFoodCard(Long idAcceptanceFoodQuality, HttpServletRequest request) {
        AcceptanceFoodQuality acceptanceFoodQuality = acceptanceFoodQualityService.getByIdAcceptanceFoodQuality(idAcceptanceFoodQuality);

        List<AcceptanceQualityFoodCardDTO> acceptanceQualityFoodCardDTOList = new ArrayList<>();
        List<AcceptanceQualityFoodCard> allData = acceptanceQualityFoodCardService.getAllByAcceptanceFoodQuality(acceptanceFoodQuality);

        allData.forEach(acceptanceQualityFoodCard -> {
            AcceptanceQualityFoodCardDTO acceptanceQualityFoodCardDTO = new AcceptanceQualityFoodCardDTO();
            acceptanceQualityFoodCardDTO.setFirmNameAccept(acceptanceFoodQuality.getAcceptance().getFirmNameAccept());
            acceptanceQualityFoodCardDTO.setCarNumber(acceptanceFoodQuality.getAcceptance().getCarNumber());
            acceptanceQualityFoodCardDTO.setTtn(
                    acceptanceFoodQuality.getAcceptance().getTtnInList().stream()
                            .map(TtnIn::getTtnName) // Получаем name из каждого элемента
                            .collect(Collectors.joining(", ")) // Объединяем в строку через запятую
            );
            acceptanceQualityFoodCardDTO.setIdAcceptanceFoodQuality(acceptanceFoodQuality.getIdAcceptanceFoodQuality());

            acceptanceQualityFoodCardDTO.setIdAcceptanceQualityFoodCard(acceptanceQualityFoodCard.getIdAcceptanceQualityFoodCard());
            acceptanceQualityFoodCardDTO.setCargoWeightCard(acceptanceQualityFoodCard.getCargoWeightCard());
            acceptanceQualityFoodCardDTO.setSampleSize(acceptanceQualityFoodCard.getSampleSize());
            acceptanceQualityFoodCardDTO.setProductName(acceptanceQualityFoodCard.getProductName());
            acceptanceQualityFoodCardDTO.setProductType(acceptanceQualityFoodCard.getProductType());
            acceptanceQualityFoodCardDTO.setClassType(acceptanceQualityFoodCard.getClassType());
            acceptanceQualityFoodCardDTO.setNumberOfBrands(acceptanceQualityFoodCard.getNumberOfBrands());
            acceptanceQualityFoodCardDTO.setQualityOfProductPackaging(acceptanceQualityFoodCard.getQualityOfProductPackaging());
            acceptanceQualityFoodCardDTO.setCardStatus(acceptanceQualityFoodCard.getCardStatus());
            acceptanceQualityFoodCardDTO.setCardInfo(acceptanceQualityFoodCard.getCardInfo());
            acceptanceQualityFoodCardDTO.setThermogram(acceptanceQualityFoodCard.getThermogram());
            acceptanceQualityFoodCardDTO.setBodyTemp(acceptanceQualityFoodCard.getBodyTemp());
            acceptanceQualityFoodCardDTO.setFruitTemp(acceptanceQualityFoodCard.getFruitTemp());
            acceptanceQualityFoodCardDTO.setAppearanceEvaluation(acceptanceQualityFoodCard.getAppearanceEvaluation());
            acceptanceQualityFoodCardDTO.setAppearanceDefects(acceptanceQualityFoodCard.getAppearanceDefects());
            acceptanceQualityFoodCardDTO.setMaturityLevel(acceptanceQualityFoodCard.getMaturityLevel());
            acceptanceQualityFoodCardDTO.setTasteQuality(acceptanceQualityFoodCard.getTasteQuality());
            acceptanceQualityFoodCardDTO.setCaliber(acceptanceQualityFoodCard.getCaliber());
            acceptanceQualityFoodCardDTO.setStickerDescription(acceptanceQualityFoodCard.getStickerDescription());
            acceptanceQualityFoodCardDTO.setDateCard(acceptanceQualityFoodCard.getDateCard());

            acceptanceQualityFoodCardDTO.setInternalDefectsQualityCardList(
                    new ArrayList<>(acceptanceQualityFoodCard.getInternalDefectsQualityCardList()));
            acceptanceQualityFoodCardDTO.setLightDefectsQualityCardList(
                    new ArrayList<>(acceptanceQualityFoodCard.getLightDefectsQualityCardList()));
            acceptanceQualityFoodCardDTO.setTotalDefectQualityCardList(
                    new ArrayList<>(acceptanceQualityFoodCard.getTotalDefectQualityCardList()));

            try {
                List<String> urlList = acceptanceQualityFoodCardImageUrlService.getUrls(acceptanceQualityFoodCard, request);

                acceptanceQualityFoodCardDTO.setImages(urlList);
            } catch (Exception e) {
                e.printStackTrace();
            }


            acceptanceQualityFoodCardDTOList.add(acceptanceQualityFoodCardDTO);
        });


        return acceptanceQualityFoodCardDTOList;
    }


}
