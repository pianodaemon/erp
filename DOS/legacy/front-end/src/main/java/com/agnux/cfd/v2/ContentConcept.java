/********************************************/
/**Written by Arturo Plauchu*****************/
/*****************************Agnux Mexico***/
/**January 7th 2011**************************/
/********************************************/
package com.agnux.cfd.v2;

public class ContentConcept {
	
	private String conceptCode;
	private String conceptDescp;
	private String conceptLotNumber;
	private String conceptTotalKgs;
	private String conceptPriceKg;
	private String conceptCurrency;
	private String conceptExtendedValue;
	
	public String getConceptCode() {
		return conceptCode;
	}

	public void setConceptCode(String code) {
		conceptCode = code;
	}
	
	public String getConceptDescp() {
		return conceptDescp;
	}

	public void setConceptDescp(String description) {
		conceptDescp = description;
	}
	
	public String getConceptLotNumber() {
		return conceptLotNumber;
	}

	public void setConceptLotNumber(String lotnumber) {
		conceptLotNumber = lotnumber;
	}
	
	public String getConceptTotalKgs() {
		return conceptTotalKgs;
	}

	public void setConceptTotalKgs(String totalkgs) {
		conceptTotalKgs = totalkgs;
	}
	
	public String getConceptPriceKg() {
		return conceptPriceKg;
	}

	public void setConceptPriceKg(String pricekgs) {
		conceptPriceKg = pricekgs;
	}
	
	public String getConceptCurrency() {
		return conceptCurrency;
	}

	public void setConceptCurrency(String currency) {
		conceptCurrency = currency;
	}
	
	public String getConceptExtendedValue() {
		return conceptExtendedValue;
	}

	public void setConceptExtendedValue(String string) {
		conceptExtendedValue = string;
	}
	
}
