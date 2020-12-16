/********************************************/
/**Written by Arturo Plauchu*****************/
/*****************************Agnux Mexico***/
/**January 7th 2011**************************/
/********************************************/
package com.agnux.cfd.v2;

public class ContentInvoice {

	private String numberInvoice;
	private String dateInvoice;
	private String hourInvoice;
	private String paymentTerms;
	
	public String getDateInvoice() {
		return dateInvoice;
	}

	public void setDateInvoice(String date) {
		dateInvoice = date;
	}
	
	public String getHourInvoice() {
		return hourInvoice;
	}

	public void setHourInvoice(String hour) {
		hourInvoice = hour;
	}
	
	public String getPaymentTerms() {
		return paymentTerms;
	}

	public void setPaymentTerms(String terms) {
		paymentTerms = terms;
	}
	
	public String getNumberInvoice() {
		return numberInvoice;
	}

	public void setNumberInvoice(String number) {
		numberInvoice = number;
	}
}
