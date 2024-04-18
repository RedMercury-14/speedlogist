package by.base.main.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;

import com.google.gson.Gson;

import by.base.main.model.Currency;

public class JsonAPITest {
	private static Gson gson = new Gson();

	public static void main(String[] args) throws IOException {
		final URL url = new URL("https://www.nbrb.by/api/exrates/rates/456");
		final HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Content-Type", "application/json");
		try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
			String inputLine;
			final StringBuilder content = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			Currency currency = gson.fromJson(content.toString(), Currency.class);
			System.out.println(currency.getCur_Abbreviation());
			System.out.println(currency.getDate().split("T")[0]);
			System.out.println(currency.toString());
			System.out.println(content.toString());
			System.out.println(LocalDate.now().toString());
			System.out.println(currency.getDate().split("T")[0].equals(LocalDate.now().toString()));
		} catch (final Exception ex) {
			ex.printStackTrace();
		}

	}

}
