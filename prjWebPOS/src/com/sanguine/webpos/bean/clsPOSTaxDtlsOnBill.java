package com.sanguine.webpos.bean;

public class clsPOSTaxDtlsOnBill {

private String taxCode;
    
    private String taxName;
    
    private String taxCalculationType;
    
    private double taxableAmount;
    
    private double taxAmount;

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public String getTaxName() {
		return taxName;
	}

	public void setTaxName(String taxName) {
		this.taxName = taxName;
	}

	public String getTaxCalculationType() {
		return taxCalculationType;
	}

	public void setTaxCalculationType(String taxCalculationType) {
		this.taxCalculationType = taxCalculationType;
	}

	public double getTaxableAmount() {
		return taxableAmount;
	}

	public void setTaxableAmount(double taxableAmount) {
		this.taxableAmount = taxableAmount;
	}

	public double getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(double taxAmount) {
		this.taxAmount = taxAmount;
	}
    
    
    
	
	
}
