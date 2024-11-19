package com.dto;

import java.util.List;
import java.util.Objects;

public class CounterpartyDTO {

    private Long counterpartyCode;
    private String name;
    private List<Long> counterpartyContractCode;
    private Long counterpartyContractCodeUnic;
	/**
	 * @param counterpartyCode
	 * @param name
	 */
	public CounterpartyDTO(Long counterpartyCode, String name) {
		super();
		this.counterpartyCode = counterpartyCode;
		this.name = name;
	}
	
	
	
	/**
	 * @param counterpartyCode
	 * @param name
	 * @param counterpartyContractCode
	 */
	public CounterpartyDTO(Long counterpartyCode, String name, Long counterpartyContractCode) {
		super();
		this.counterpartyCode = counterpartyCode;
		this.name = name;
		this.counterpartyContractCodeUnic = counterpartyContractCode;
	}



	public Long getCounterpartyCode() {
		return counterpartyCode;
	}
	public void setCounterpartyCode(Long counterpartyCode) {
		this.counterpartyCode = counterpartyCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Long> getCounterpartyContractCode() {
		return counterpartyContractCode;
	}
	public void setCounterpartyContractCode(List<Long> counterpartyContractCode) {
		this.counterpartyContractCode = counterpartyContractCode;
	}
	
	
	
	public Long getCounterpartyContractCodeUnic() {
		return counterpartyContractCodeUnic;
	}



	public void setCounterpartyContractCodeUnic(Long counterpartyContractCodeUnic) {
		this.counterpartyContractCodeUnic = counterpartyContractCodeUnic;
	}



	@Override
	public int hashCode() {
		return Objects.hash(counterpartyCode, name);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CounterpartyDTO other = (CounterpartyDTO) obj;
		return Objects.equals(counterpartyCode, other.counterpartyCode) && Objects.equals(name, other.name);
	}
	@Override
	public String toString() {
		return "CounterpartyDTO [counterpartyCode=" + counterpartyCode + ", name=" + name
				+ ", counterpartyContractCode=" + counterpartyContractCode + "]";
	}
	
    

}
