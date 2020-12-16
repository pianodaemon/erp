/********************************************/
/**Written by Arturo Plauchu*****************/
/*****************************Agnux Mexico***/
/**January 7th 2011**************************/
/********************************************/
package com.agnux.cfd.v2;

public class ContentCustomer {
	
	private String customerName;
	private String customerAddress;
	private String customerRFC;
	private String customerNumber;
	
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customer) {
		customerName = customer;
	}
	
	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String address) {
		customerAddress = address;
	}
	
	public String getCustomerRFC() {
		return customerRFC;
	}

	public void setCustomerRFC(String rfc) {
		customerRFC = rfc;
	}
	
	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customernumber) {
		customerNumber = customernumber;
	}
}
