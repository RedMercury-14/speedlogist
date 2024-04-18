package by.base.main.service.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import by.base.main.model.Currency;

@Service
public class CurrencyService {

	private Gson gson = new Gson();

	private static Map<String, Currency> currencyMap = new HashMap<String, Currency>();

	private static Properties properties = new Properties();

	public CurrencyService() {
		// TODO Auto-generated constructor stub
	}

	public Currency getCurrencyFromBank(String url) throws IOException {
		Currency currency = new Currency();
		final URL urlTarget = new URL(url);
		final HttpURLConnection con = (HttpURLConnection) urlTarget.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Content-Type", "application/json");
		try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			String inputLine;
			final StringBuilder content = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			currency = gson.fromJson(content.toString(), Currency.class);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return currency;
	}

	public Map<String, Currency> getCurrencyMap() {
		return currencyMap;
	}

	public void setCurrencyMap(Map<String, Currency> currencyMap) {
		this.currencyMap = currencyMap;
	}

	public void loadCurrencyMap(HttpServletRequest request) {
		if (currencyMap.isEmpty() || !currencyMap.get("RUB").getDate().split("T")[0].equals(LocalDate.now().toString())) {
			currencyMap.clear();
			try {
				String appPath = request.getServletContext().getRealPath("");
				FileInputStream fileInputStream;
				fileInputStream = new FileInputStream(appPath + "resources/properties/currency.properties");
				properties.load(fileInputStream);
				Currency USD = getCurrencyFromBank(properties.getProperty("currency.USD"));
				Currency RUB = getCurrencyFromBank(properties.getProperty("currency.RUB"));
				Currency EUR = getCurrencyFromBank(properties.getProperty("currency.EUR"));
				Currency KZT = getCurrencyFromBank(properties.getProperty("currency.KZT"));
				currencyMap.put(USD.getCur_Abbreviation(), USD);
				currencyMap.put(RUB.getCur_Abbreviation(), RUB);
				currencyMap.put(EUR.getCur_Abbreviation(), EUR);
				currencyMap.put(KZT.getCur_Abbreviation(), KZT);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void clearCurrencyMap() {
		currencyMap.clear();
	}

}
