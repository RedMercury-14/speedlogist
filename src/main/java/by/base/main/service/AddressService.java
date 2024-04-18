package by.base.main.service;

import java.sql.Date;
import java.util.List;

import by.base.main.model.Address;

public interface AddressService {

	Address getAddressById(Integer id);
	
	List<Address> getAddressByDate(Date date);
	
	Integer saveAddress(Address address);
	
	void updateAddress(Address address);
}
