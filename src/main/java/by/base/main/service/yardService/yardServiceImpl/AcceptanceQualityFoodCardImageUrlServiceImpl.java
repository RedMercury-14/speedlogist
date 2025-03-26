package by.base.main.service.yardService.yardServiceImpl;

import by.base.main.dao.yardDao.AcceptanceQualityFoodCardImageUrlDAO;
import by.base.main.model.yard.AcceptanceQualityFoodCard;
import by.base.main.model.yard.AcceptanceQualityFoodCardImageUrl;
import by.base.main.service.yardService.AcceptanceQualityFoodCardImageUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

@Service
public class AcceptanceQualityFoodCardImageUrlServiceImpl implements AcceptanceQualityFoodCardImageUrlService {

    private static final String ROOT_DIRECTORY = "D:/";
//	private static final String ROOT_DIRECTORY = "//fs/Dobronom/СКЛАД ДОБРОНОМ/СКЛАД 1300/200ФРУКТЫ/Карточка товара/";

    @Autowired
    private AcceptanceQualityFoodCardImageUrlDAO acceptanceQualityFoodCardImageUrlDAO;

    @Override
    public List<AcceptanceQualityFoodCardImageUrl> getAllByAcceptanceQualityFoodCard(AcceptanceQualityFoodCard acceptanceQualityFoodCard) {
        return acceptanceQualityFoodCardImageUrlDAO.getAllByAcceptanceQualityFoodCard(acceptanceQualityFoodCard);
    }

    @Override
    public AcceptanceQualityFoodCardImageUrl getByIdAcceptanceQualityFoodCardImageUrl(Long idAcceptanceQualityFoodCardImageUrl) {
        return acceptanceQualityFoodCardImageUrlDAO.getByIdAcceptanceQualityFoodCardImageUrl(idAcceptanceQualityFoodCardImageUrl);
    }

    @Override
    public List<String> getUrls(AcceptanceQualityFoodCard acceptanceQualityFoodCard, HttpServletRequest request) {
        List<AcceptanceQualityFoodCardImageUrl> acceptanceQualityFoodCardImageUrlList = getAllByAcceptanceQualityFoodCard(acceptanceQualityFoodCard);

        String url = request.getRequestURL().toString();

        System.err.println(url);
        String urlPart = "http://localhost:8080/speedlogist_war/tsd/files/";

        List<String> urlList = new ArrayList<>();
        acceptanceQualityFoodCardImageUrlList.forEach(item->{
            urlList.add( urlPart + item.getIdAcceptanceQualityFoodCardImageUrl());
        });


        return urlList;
    }

    @Override
    public String getUrlImage(Long idAcceptanceQualityFoodCardImageUrl) {
        return acceptanceQualityFoodCardImageUrlDAO.getByIdAcceptanceQualityFoodCardImageUrl(idAcceptanceQualityFoodCardImageUrl).getUrl();
    }

    @Override
    public Resource getFile(Long idFile) {
        String urlPart = getUrlImage(idFile);

        if (urlPart == null || urlPart.isEmpty()) {
            return null;
        }

        Path filePath = Paths.get(ROOT_DIRECTORY, urlPart);
        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            return null;
        }

        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка при создании ресурса: " + e.getMessage(), e);
        }
    }
}
