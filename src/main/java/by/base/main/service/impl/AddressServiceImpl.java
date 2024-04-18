package by.base.main.service.impl;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.dao.AddressDAO;
import by.base.main.model.Address;
import by.base.main.service.AddressService;

@Service
public class AddressServiceImpl implements AddressService{
	
	@Autowired
	private AddressDAO addressDAO;

	@Override
	public Address getAddressById(Integer id) {
		return addressDAO.getAddressById(id);
	}

	@Override
	public List<Address> getAddressByDate(Date date) {
		return addressDAO.getAddressByDate(date);
	}

	@Override
	public Integer saveAddress(Address address) {
		return addressDAO.saveAddress(address);
	}

	@Override
	public void updateAddress(Address address) {
		addressDAO.updateAddress(address);
		
	}

}
