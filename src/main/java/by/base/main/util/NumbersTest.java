package by.base.main.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Collections;
import java.math.BigDecimal;

import com.ibm.icu.text.RuleBasedNumberFormat;

import by.base.main.service.util.FwMoney;

public class NumbersTest {

	public static void main(String[] args) {
		RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.forLanguageTag("ru"),
	            RuleBasedNumberFormat.SPELLOUT);
		double test = 7852359663.32;
		System.out.println(nf.format(test));
		
		FwMoney mo = new FwMoney(test, "BYN");
		String money_as_string = mo.num2str();
		System.out.println(money_as_string);
		
		LocalDate testDate = LocalDate.parse("2022-12-19");
		System.out.println(testDate);
	}

	
	
}


