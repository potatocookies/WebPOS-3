package com.sanguine.webpos.bean;

public class clsPOSCustomerDtlsOnBill {

	private String customerCode;
	
	private String mobileNo;
	
	private String billTransType;
	
	private String remarks;

	private String strCustomerName;
	
	private String strCustomerType;
	
	private String custMobileNo;
	
	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getBillTransType() {
		return billTransType;
	}

	public void setBillTransType(String billTransType) {
		this.billTransType = billTransType;
	}

	public String getStrCustomerName() {
		return strCustomerName;
	}

	public void setStrCustomerName(String strCustomerName) {
		this.strCustomerName = strCustomerName;
	}

	public String getStrCustomerType() {
		return strCustomerType;
	}

	public void setStrCustomerType(String strCustomerType) {
		this.strCustomerType = strCustomerType;
	}

	public String getCustMobileNo() {
		return custMobileNo;
	}

	public void setCustMobileNo(String custMobileNo) {
		this.custMobileNo = custMobileNo;
	}
	
	
}
