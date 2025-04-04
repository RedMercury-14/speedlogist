package by.base.main.util.hcolossus.service;

import java.util.Comparator;

import by.base.main.model.Shop;

public class ComparatorShopsTEST implements Comparator<Shop>{

	@Override
	public int compare(Shop o1, Shop o2) {
		return new org.apache.commons.lang3.builder.CompareToBuilder()
				.append(o2.getDistanceFromStock()*o2.getNeedPall(), o1.getDistanceFromStock()*o1.getNeedPall())
				.append(o2.getNeedPall(), o1.getNeedPall())
				.build();
	}

}
