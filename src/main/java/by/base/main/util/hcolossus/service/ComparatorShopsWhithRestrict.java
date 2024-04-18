package by.base.main.util.hcolossus.service;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;

import by.base.main.model.Shop;

/**
 * Компаратор , который выстраивает по самой дальней точке и одновременно имеющей ограничения
 */
public class ComparatorShopsWhithRestrict implements Comparator<Shop>{

	@Override
	public int compare(Shop o1, Shop o2) {		
		return ComparisonChain.start()
				.compare(o2.getMaxPall() != null ? o2.getMaxPall() : 0, o1.getMaxPall() != null ? o1.getMaxPall() : 0)
				.compare(o2.getDistanceFromStock(), o1.getDistanceFromStock())
				.compare(o2.getNeedPall(), o1.getNeedPall())
				.result();
//		return new org.apache.commons.lang3.builder.CompareToBuilder()
////				.append(o2.getNeedPall(), o1.getNeedPall())
//				.build();
	}

}
