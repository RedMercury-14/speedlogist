package by.base.main.dao;

import java.sql.Date;
import java.util.List;

import by.base.main.model.Address;

public interface AddressDAO {
	
	Address getAddressById(Integer id);
	
	List<Address> getAddressByDate(Date date);
	
	Integer saveAddress(Address address);
	
	void updateAddress(Address address);
	
}