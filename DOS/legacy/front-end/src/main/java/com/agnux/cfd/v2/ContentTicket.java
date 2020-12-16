/********************************************/
/**Written by Arturo Plauchu*****************/
/*****************************Agnux Mexico***/
/**January 7th 2011**************************/
/********************************************/
package com.agnux.cfd.v2;

import java.util.ArrayList;

public class ContentTicket {
        
        private String oldInvoice;
	private String companyRFC;
	private String proposito;
	
	private String numberApproveDate;
	private String numberCertificate;
	private String serie;
	private String folio;
	private String order;
	private String salesman;
	
	private ContentCustomer customerInfo;
	private ContentInvoice invoiceInfo;
	private ArrayList<ContentConcept> conceptList;
	
	private String totalWord;
	private String paymentDue;
	
	private String subTotalTicket;
	private String taxTicket;
	private String totalTicket;
	
	private String originalString;
	private String digitalSign;

        private String compoundKey;
	
	public String getNumberApproveDate() {
		return numberApproveDate;
	}
        
	public void setNumberAproveDate(String approveDate) {
		numberApproveDate = approveDate;
	}
	
	public String getNumberCertificate() {
		return numberCertificate;
	}
        
	public void setNumberCertificate(String certificate) {
		numberCertificate = certificate;
	}
	
	public String getOrder() {
		return order;
	}
        
	public void setOrder(String orderNumber) {
		order = orderNumber;
	}
	
	public String getSalesman() {
		return salesman;
	}
        
	public void setSalesman(String salesMan) {
		salesman = salesMan;
	}
	
	public ContentCustomer getCustomerInfo() {
		return customerInfo;
	}
        
	public void setCustomerInfo(ContentCustomer customer) {
		customerInfo = customer;
	}
	
	public ContentInvoice getInvoiceInfo() {
		return invoiceInfo;
	}
        
	public void setInvoiceInfo(ContentInvoice invoice) {
		invoiceInfo = invoice;
	}
	
	public void setConcept(ContentConcept concept) {
		if(conceptList == null){
			conceptList = new ArrayList<ContentConcept>();
		}
		conceptList.add(concept);
	}
	
	public ArrayList<ContentConcept> getConcepts() {
		return conceptList;
	}
	
	public ContentConcept getConcept(int position) {
		return conceptList.get(position);
	}
	
	public String getTotalWord() {
		return totalWord;
	}
        
	public void setTotalWord(String totalword) {
		totalWord = totalword;
	}
	
	public String getPaymentDue() {
		return paymentDue;
	}
        
	public void setPaymentDue(String paymentdue) {
		paymentDue = paymentdue;
	}
	
	public String getSubTotal() {
		return subTotalTicket;
	}
        
	public void setSubTotal(String string) {
		subTotalTicket = string;
	}
	
	public String getTax() {
		return taxTicket;
	}
        
	public void setTax(String string) {
		taxTicket = string;
	}
	
	public String getTotal() {
		return totalTicket;
	}
        
	public void setTotal(String string) {
		totalTicket = string;
	}
	
	public String getOriginalString() {
		return originalString;
	}
        
	public void setOriginalString(String originalstring) {
		originalString = originalstring;
	}
        
	public String getDigitalSign() {
		return digitalSign;
	}
        
	public void setDigitalSign(String digitalsign) {
		digitalSign = digitalsign;
	}
        
	public void setSerie(String serie) {
		this.serie = serie;
	}
	
	public String getSerie() {
		return serie;
	}
        
	public void setFolio(String folio) {
		this.folio = folio;
	}
	
	public String getFolio() {
		return folio;
	}
        
	public void setCompanyRFC(String companyRFC) {
		this.companyRFC = companyRFC;
	}
	
	public String getCompanyRFC() {
		return companyRFC;
	}
        
	public String getProposito() {
		return proposito;
	}
        
	public void setProposito(String string) {
		proposito = string;
	}
        
    public String getCompoundKey() {
        return compoundKey;
    }
    
    public void setCompoundKey(String compound_key) {
        compoundKey = compound_key;
    }
    
    public String getOldInvoice() {
        return oldInvoice;
    }
    
    public void setOldInvoice(String oldInvoice) {
        this.oldInvoice = oldInvoice;
    }
    
}
